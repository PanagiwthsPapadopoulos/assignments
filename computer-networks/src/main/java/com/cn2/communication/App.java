package com.cn2.communication;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.DatagramPacket;
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

	// TODO: Please define and initialize your variables here...

	// Declare the receiver's IP and port as class-level variables
	static String receiverAddressString = "localhost";
	static int receiverPort = 12345;
	static int ownPort = 12346;
	static public boolean outgoingCall = false;
	static public boolean incomingCall = false;
	static boolean activeCall = false;

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

		/*
		 * 2. Adding the components to the GUI
		 */
		add(scrollPane);
		add(inputTextField);
		add(sendButton);
		add(callButton);

		/*
		 * 3. Linking the buttons to the ActionListener
		 */
		sendButton.addActionListener(this);
		callButton.addActionListener(this);
		inputTextField.addActionListener(this);

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

		/*
		 * 2.
		 */
		try {

			// Create a DatagramSocket to listen on the specified port
			DatagramSocket socket = new DatagramSocket(ownPort);

			// Buffer to hold incoming data
			byte[] buffer = new byte[1024];

			// packet
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			// Create the receiver
			Receiver receiver = new Receiver(socket);

			// Create the sender
			Sender sender = new Sender(socket, InetAddress.getByName(receiverAddressString), receiverPort);

			// Audio format configuration
			AudioFormat format = new AudioFormat(16000, 16, 1, true, false);

			// Create and start audio sender and receiver
			AudioRecorder audioRecorder = new AudioRecorder(sender, format, activeCall);
			AudioPlayer audioPlayer = new AudioPlayer(format, activeCall);

			// Start threads for sending and receiving audio
			new Thread(audioRecorder).start();
			new Thread(audioPlayer).start();

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
					System.out.println("Changed active call for audioRecorder");
					audioRecorder.setActiveCall(activeCall);
					audioPlayer.setAudioData(audioData);
					System.out.println("Received audio data of length: " + audioData.length);
					// The audio will now be handled by the AudioReceiver in a separate thread
				}

				System.out.println("Changed active call for audioRecorder");
				audioPlayer.setActiveCall(activeCall);
				audioRecorder.setActiveCall(activeCall);

				// Change Logic for Receiver

				if (incomingCall) {
					callButton.setText("Accept Call");
					callButton.setEnabled(true);
				} else if (outgoingCall) {
					callButton.setText("Waiting...");
					callButton.setEnabled(false);
				} else if (!incomingCall && !outgoingCall && !activeCall) {
					callButton.setText("Call");
					callButton.setEnabled(true);
				} else if (activeCall) {
					callButton.setText("End Call");
					callButton.setEnabled(true);
				}

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

			// The "Call" button was clicked

			// TODO: Your code goes here...
			try {
				DatagramSocket socket = new DatagramSocket(); // Creates a socket for sending
				Sender sender = new Sender(socket, InetAddress.getByName(receiverAddressString), receiverPort);

				// First outgoing Call
				if (!outgoingCall && !incomingCall && !activeCall) {
					// Start the client for initiating a call
					System.out.println("Starting audio call as a client...");

					sender.sendConnectionMessage("call");
					// Send the appropriate message on the other end
					outgoingCall = true;

					textArea.append("You started a call." + newline);

					// Answer incoming call
				} else if (incomingCall) {

					// Start the server for receiving a call
					System.out.println("Waiting for an incoming audio call...");

					sender.sendConnectionMessage("answer");
					// Send the appropriate message on the other end
					incomingCall = false;
					activeCall = true;

					textArea.append("You accepted the call." + newline);

				} else if (outgoingCall) {
					// Cancel call

					// End Call
				} else if (activeCall) {

					System.out.println("Ending active audio call...");

					sender.sendConnectionMessage("end-call");

					activeCall = false;
					outgoingCall = false;
					incomingCall = false;

					textArea.append("You ended the call." + newline);
				}

				socket.close(); // Close the socket

			} catch (Exception exception) {
				exception.printStackTrace();
			}

			// Change Logic for Sender
			if (incomingCall) {
				callButton.setText("Accept Call");
				callButton.setEnabled(true);
			} else if (outgoingCall) {
				callButton.setText("Waiting...");
				callButton.setEnabled(false);
			} else if (!incomingCall && !outgoingCall && !activeCall) {
				callButton.setText("Call");
				callButton.setEnabled(true);
			} else if (activeCall) {
				callButton.setText("End Call");
				callButton.setEnabled(true);
			}

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
