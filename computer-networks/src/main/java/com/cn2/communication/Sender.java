package com.cn2.communication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sender {
    private final DatagramSocket socket;
    private final InetAddress receiverAddress;
    private final int receiverPort;

    public Sender(DatagramSocket socket, InetAddress receiverAddress, int receiverPort) {
        this.socket = socket;
        this.receiverAddress = receiverAddress;
        this.receiverPort = receiverPort;
    }


    // Sends a text message with a header
   
    public void sendMessage(String message) {
        try {
            // Convert the message to a byte array
            byte[] messageBytes = message.getBytes();

            // Create the header: "MSG " + 4-digit length
            String header = "MSG " + String.format("%04d", messageBytes.length);
            byte[] headerBytes = header.getBytes();

            // Send the header first
            DatagramPacket headerPacket = new DatagramPacket(headerBytes, headerBytes.length, receiverAddress, receiverPort);
            socket.send(headerPacket);

            // Send the actual message data
            DatagramPacket messagePacket = new DatagramPacket(messageBytes, messageBytes.length, receiverAddress, receiverPort);
            socket.send(messagePacket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    // Sends a connection message with a header

    public void sendConnectionMessage(String message) {
        try {
            // Convert the message to a byte array
            byte[] messageBytes = message.getBytes();

            // Create the header: "MSG " + 4-digit length
            String header = "CONN" + String.format("%04d", messageBytes.length);
            byte[] headerBytes = header.getBytes();

            // Send the header first
            DatagramPacket headerPacket = new DatagramPacket(headerBytes, headerBytes.length, receiverAddress, receiverPort);
            socket.send(headerPacket);

            // Send the actual message data
            DatagramPacket messagePacket = new DatagramPacket(messageBytes, messageBytes.length, receiverAddress, receiverPort);
            socket.send(messagePacket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sends an audio message with a header 
   
    public void sendAudio(byte[] audioData) {
        try {
            // Create the header: "CALL" + 4-digit length
            String header = "CALL" + String.format("%04d", audioData.length);
            byte[] headerBytes = header.getBytes();

            // Send the header first
            DatagramPacket headerPacket = new DatagramPacket(headerBytes, headerBytes.length, receiverAddress, receiverPort);
            socket.send(headerPacket);

            // Send the actual audio data
            DatagramPacket audioPacket = new DatagramPacket(audioData, audioData.length, receiverAddress, receiverPort);
            socket.send(audioPacket);
            System.out.println("Sent audio Packet");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}