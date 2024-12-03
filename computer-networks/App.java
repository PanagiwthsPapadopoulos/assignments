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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class App extends Frame implements WindowListener, ActionListener {

	/*
	 * Definition of the app's fields
	 */
	static TextField inputTextField;		
	static JTextArea textArea;				 
	static JFrame frame;					
	static JButton sendButton;				
	static JTextField messageTextField;		  
	public static Color gray;				
	final static String newline="\n";		
	static JButton callButton;				
	
	// TODO: Please define and initialize your variables here...

	// Declare the receiver's IP and port as class-level variables
    static String receiverAddressString = "192.168.2.9"; 
    static int receiverPort = 12345;
	static int ownPort = 12346;

	
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
		textArea = new JTextArea(10,40);			
		textArea.setLineWrap(true);				
		textArea.setEditable(false);			
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		
		//Setting up the buttons
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
	 * The main method of the application. It continuously listens for
	 * new messages.
	 */
	public static void main(String[] args){
	
		/*
		 * 1. Create the app's window
		 */
		App app = new App("CN2 - AUTH port "+ownPort);  // TODO: You can add the title that will displayed on the Window of the App here																		  
		app.setSize(500,250);				  
		app.setVisible(true);				  

		/*
		 * 2. 
		 */
			try {
            // Create a DatagramSocket to listen on the specified port
            DatagramSocket socket = new DatagramSocket(ownPort);
            byte[] buffer = new byte[1024]; // Buffer to hold incoming data
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            

            // Continuously listen for incoming packets
            while (true) {
                // Receive the packet
                socket.receive(packet);

                // Convert the received data to a string
                String message = new String(packet.getData(), 0, packet.getLength());
				System.out.println("Received message: " + message);

                textArea.append(receiverPort + ": " + message + newline);
            }

            // Close the socket (although this will never be reached in this case)
            // socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
	/**
	 * The method that corresponds to the Action Listener. Whenever an action is performed
	 * (i.e., one of the buttons is clicked) this method is executed. 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		/*
		 * Check which button was clicked.
		 */
		System.out.println(e.getSource());


		if (e.getSource() == sendButton || e.getSource() == inputTextField){
			
			// The "Send" button was clicked
			
			// TODO: Your code goes here...
			try {

				String message = inputTextField.getText(); // Replace with the message to be sent

				if(!"".equals(message)){
					DatagramSocket socket = new DatagramSocket(); // Creates a socket for sending
	
					
					inputTextField.setText(message + " local");
					textArea.append("You: " + message + newline);
					
					byte[] buffer = message.getBytes(); // Convert the message to byte array
	
					// InetAddress receiverAddress = InetAddress.getByName("192.168.2.9"); // Replace with the receiver's IP
					// int receiverPort = 12345; // Replace with the receiver's port
			
					// Encapsule the byte array, destination IP and poet in to a DatagramPacket. This packet will be sent via the socket
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(receiverAddressString), receiverPort);
	
				
					socket.send(packet); // Send the packet
	
	
					socket.close(); // Close the socket
					inputTextField.setText("");
				}
			} catch (Exception exception) {
            	exception.printStackTrace();
				System.out.println(exception);
        }

		}else if(e.getSource() == callButton){
			
			// The "Call" button was clicked
			
			// TODO: Your code goes here...
			
			
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
