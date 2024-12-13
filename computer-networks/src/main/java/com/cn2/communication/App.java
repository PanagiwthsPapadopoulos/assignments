package com.cn2.communication;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class App extends Frame implements WindowListener, ActionListener {

	/*
	 * Definition of the app's fields
	 */
	static TextField inputTextField;
	static JTextArea textArea;
	static JButton sendButton;
	static JTextField messageTextField;
	public static Color gray;
	final static String newline = "\n";
	static JButton callButton;
	static JButton testMicButton;

	// TODO: Please define and initialize your variables here...

	// Declare the receiver's IP and port as class-level variables

	// static String receiverAddressString = "192.168.2.9";

	static String receiverAddressString = "192.168.124.227";
	static String ownAddressString = "192.168.2.9";

	static int receiverPort = 12345;
	static int ownPort = 12345;
	static public boolean outgoingCall = false;
	static public boolean incomingCall = false;
	static boolean activeCall = false;
	static boolean activeTest = false;
	

    // Local Recorder and Player initialization for microphone testing
    private static AudioRecorder audioRecorderLocal;
    private static AudioPlayer audioPlayerLocal;
    
    
    // End microphone testing

	/**
	 * Construct the app's frame and initialize important parameters
	 */
	public App(String title) {
		
		/*
		 * 1. Defining the components of the GUI
		 */

		// Setting up the characteristics of the frame
		super(title);
		gray = new Color(254, 254, 254);
		setBackground(gray);
		setLayout(new FlowLayout());
		addWindowListener(this);

		// Setting up the TextField and the TextArea
		inputTextField = new TextField();
		inputTextField.setColumns(20);

		// Setting up the TextArea.
		textArea = new JTextArea(10, 40);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// Setting up the buttons
		sendButton = new JButton("Send");
		callButton = new JButton("Call");
		testMicButton = new JButton("Test mic - OFF");

		/*
		 * 2. Adding the components to the GUI
		 */
		add(scrollPane);
		add(inputTextField);
		add(sendButton);
		add(callButton);
		add(testMicButton);

		/*
		 * 3. Linking the buttons to the ActionListener
		 */
		sendButton.addActionListener(this);
		callButton.addActionListener(this);
		inputTextField.addActionListener(this);
		testMicButton.addActionListener(this); 
	    
	}

	/**
	 * The main method of the application. It continuously listens for new messages.
	 */
	public static void main(String[] args) {

		/*
		 * 1. Create the app's window
		 */
		App app = new App("CN2 - AUTH port " + ownPort); // TODO: You can add the title that will displayed on the
		
		// Window of the App here
		app.setSize(500, 250);
		app.setVisible(true);

		// String serverAddress = "127.0.0.1"; // Replace with the server's IP address
		// int port = 5000;
		// receiverAddressString = "192.168.2.9";
		/*
		 * 2.
		 */
		try {
			
			// Create a DatagramSocket to listen on the specified port
			DatagramSocket socket = new DatagramSocket(ownPort);

			// Create the receiver
			Receiver receiver = new Receiver(socket);

			// Create the sender
			Sender sender = new Sender(socket, InetAddress.getByName(receiverAddressString), receiverPort);

			// Audio format configuration
			AudioFormat audioFormat = new AudioFormat(
    AudioFormat.Encoding.PCM_SIGNED, // Signed PCM encoding
    8000,                         // Sample rate: 8000 Hz
    8,                               // Sample size in bits: 8 bits
    1,                               // Channels: Mono
    1,                               // Frame size (1 byte per frame)
	8000,                         // Frame rate matches sample rate
    false                            // Little-endian byte order
);
			// AudioFormat format = new AudioFormat(8000.0f, 8, 1, true, false);

			// Create and start audio sender and receiver
			AudioRecorder audioRecorderCall = new AudioRecorder(sender, audioFormat, activeCall, false);
			AudioPlayer audioPlayerCall = new AudioPlayer(audioFormat, activeCall, false);

			// Start threads for sending and receiving audio
			new Thread(audioRecorderCall).start();
			new Thread(audioPlayerCall).start();
			
			// Microphone check
		    audioRecorderLocal = new AudioRecorder(audioFormat, activeTest);
		    new Thread(audioRecorderLocal).start();
		    
		    audioPlayerLocal = new AudioPlayer(audioFormat, activeTest, true);
		    new Thread(audioPlayerLocal).start();
	        
	        
	        

			// Continuously listen for incoming packets
			while (true) {

				// Receive the packet
				// socket.receive(packet);

				ReceivedData data = receiver.receiveData();

				// Continuously receive and process data

				if (data.getType().equals("message")) {

					// Handle received message
					textArea.append(receiverPort + ": " + data.getMessage() + newline);

					System.out.println("Received message: " + data.getMessage());
				} else if (data.getType().equals("connection")) {

					switch (data.getMessage()) {
					case "call":
						incomingCall = true;
						textArea.append(receiverPort + " started a call. " + newline);
						break;
					case "answer":
						incomingCall = false;
						outgoingCall = false;
						activeCall = true;
						textArea.append(receiverPort + " accepted the call. " + newline);
						break;
					case "end-call":
						incomingCall = false;
						outgoingCall = false;
						activeCall = false;
						textArea.append(receiverPort + " ended the call. " + newline);
						break;
					default:
						textArea.append(receiverPort + ": " + data.getMessage() + newline);
						break;
					}
				} else if (data.getType().equals("audio")) {
					// Handle received audio
					byte[] audioData = data.getAudio();
					audioRecorderCall.setActive(activeCall);
					audioPlayerCall.setAudioData(audioData);
					System.out.println("Received audio data of length: " + audioData.length);
					// The audio will now be handled by the AudioReceiver in a separate thread
				}

				audioPlayerCall.setActive(activeCall);
				audioRecorderCall.setActive(activeCall);

				// Change Logic for Receiver

				buttonLogic();

			}

			// Close the socket (although this will never be reached in this case)
			// socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * The method that corresponds to the Action Listener. Whenever an action is
	 * performed (i.e., one of the buttons is clicked) this method is executed.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		/*
		 * Check which button was clicked.
		 */
		System.out.println(e.getSource());

		if (e.getSource() == sendButton || e.getSource() == inputTextField) {

			// The "Send" button was clicked

			// TODO: Your code goes here...
			if (!inputTextField.getText().isEmpty()) {
				try {
					DatagramSocket socket = new DatagramSocket(); // Creates a socket for sending
					Sender sender = new Sender(socket, InetAddress.getByName(receiverAddressString), receiverPort);

					String message = inputTextField.getText();
					sender.sendMessage(message);
					textArea.append("You: " + message + newline);
					socket.close(); // Close the socket
					inputTextField.setText("");
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

		} else if (e.getSource() == callButton) {
			try {
				DatagramSocket socket = new DatagramSocket(); // Creates a socket for sending
				Sender sender = new Sender(socket, InetAddress.getByName(receiverAddressString), receiverPort);

				// First outgoing Call
				if (!outgoingCall && !incomingCall && !activeCall) {
					// Start the client for initiating a call
					sender.sendConnectionMessage("call");
					
					// Locally handle outgoing call
					outgoingCall = true;
					textArea.append("You started a call." + newline);

				// Answer incoming call
				} else if (incomingCall) {
					// Send the appropriate message on the other end
					sender.sendConnectionMessage("answer");
					
					// Locally handle incoming call
					incomingCall = false;
					activeCall = true;
					textArea.append("You accepted the call." + newline);

				// End Call
				} else if (activeCall) {
					// Send the appropriate message on the other end
					sender.sendConnectionMessage("end-call");

					// Locally handle cancelling the call
					activeCall = false;
					outgoingCall = false;
					incomingCall = false;
					textArea.append("You ended the call." + newline);
				}
				socket.close(); // Close the socket

			} catch (Exception exception) {
				exception.printStackTrace();
			}
			
		} else if (e.getSource() == testMicButton) {
			// Open/close microphone
			activeTest = !activeTest;
			System.out.println("Mic status: " + activeTest);
			
			// sinePlayerThread.setActive(activeTest);
			audioRecorderLocal.setActive(activeTest);
			audioPlayerLocal.setActive(activeTest);
			
			if(activeTest) {
				textArea.append("Microphone Active" + newline);
			} else {
				textArea.append("Microphone Inactive" + newline);
			}	
		}
		
		buttonLogic();

	}

	public static void buttonLogic(){

		if(outgoingCall){
			callButton.setText("Waiting...");
			callButton.setEnabled(false);
			testMicButton.setEnabled(false);
		}

		if (incomingCall) {
			callButton.setText("Accept Call");
		}

		if(activeCall){
			callButton.setText("End Call");
			callButton.setEnabled(true);
			testMicButton.setEnabled(false);
		}

		if (!incomingCall && !outgoingCall && !activeCall) {
			callButton.setText("Call");
			callButton.setEnabled(true);
			testMicButton.setEnabled(true);
		} 

		// if mic test is active, call button does not work
		if (activeTest) {
			testMicButton.setText("Test mic - ON");
			callButton.setEnabled(false);
		}

		// If mic test is inactive, call button works
		if(!activeTest) {
			testMicButton.setText("Test mic - OFF");
		}
	}

	/**
	 * These methods have to do with the GUI. You can use them if you wish to define
	 * what the program should do in specific scenarios (e.g., when closing the
	 * window).
	 */
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		dispose();
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}
}
