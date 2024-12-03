import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    public static void main(String[] args) {
        String message = "Hello, PC!"; // Message you want to send
        String ipAddress = "192.168.2.9"; // Replace with your PC's IP address
        int port = 12346; // The port number the server is listening on

        try {
            // Create a DatagramSocket to send the message
            DatagramSocket socket = new DatagramSocket();

            // Convert the message to bytes
            byte[] buffer = message.getBytes();

            // Get the InetAddress of the server (PC)
            InetAddress address = InetAddress.getByName(ipAddress);

            // Create a DatagramPacket to send the message
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);

            // Send the packet to the server
            socket.send(packet);

            // Close the socket after sending the message
            socket.close();

            System.out.println("Message sent to PC at " + ipAddress + " on port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}