package com.cn2.communication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sender {
    private DatagramSocket socket;
    private InetAddress receiverAddress;
    private int receiverPort;

    public Sender(DatagramSocket socket, InetAddress receiverAddress, int receiverPort) {
        this.socket = socket;
        this.receiverAddress = receiverAddress;
        this.receiverPort = receiverPort;
    }

    /**
     * Sends a message with a header
     * @param message The message to be sent
     */
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

    /**
     * Sends a connection message with a header
     * @param message The message to be sent
     */
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

    /**
     * Sends audio data with a header
     * @param audioData The audio data to be sent
     */
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}