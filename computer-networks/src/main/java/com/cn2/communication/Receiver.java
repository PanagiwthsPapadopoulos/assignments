package com.cn2.communication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver {
    private final DatagramSocket socket;

    public Receiver(DatagramSocket socket) {
        this.socket = socket;
    }

    public ReceivedData receiveData() throws Exception {
        byte[] header = new byte[8]; // Fixed header size (4 bytes for type and 4 bytes for length)
        DatagramPacket packet = new DatagramPacket(header, header.length);
        socket.receive(packet);

        // Read and parse the header
        String type = new String(header, 0, 4); // "MSG " or "CALL" or "CONN"
        int length = Integer.parseInt(new String(header, 4, 4).trim()); // Length of the data

        // Receive the payload data
        byte[] payload = new byte[length];
        packet = new DatagramPacket(payload, payload.length);
        socket.receive(packet);

        // Return the data based on the type
        if (type.equals("MSG ")) {
            String message = new String(payload);
            return new ReceivedData("message", message, null);
        } else if (type.equals("CALL")) {
            return new ReceivedData("audio", null, payload);
        } else if (type.equals("CONN")) {
            String message = new String(payload);
            return new ReceivedData("connection", message, null);
        } else {
            throw new Exception("Unknown data type: " + type);
        }
    }
}