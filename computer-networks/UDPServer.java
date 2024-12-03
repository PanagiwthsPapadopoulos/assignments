import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {
    public static void main(String[] args) {
        int port = 12345; // The port number where the server listens

        try {
            // Create a DatagramSocket to listen on the specified port
            DatagramSocket socket = new DatagramSocket(port);
            byte[] buffer = new byte[1024]; // Buffer to hold incoming data
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            System.out.println("Waiting for messages on port " + port + "...");

            // Continuously listen for incoming packets
            while (true) {
                // Receive the packet
                socket.receive(packet);

                // Convert the received data to a string
                String message = new String(packet.getData(), 0, packet.getLength());

                // Print the received message
                System.out.println("Received message: " + message);
            }

            // Close the socket (although this will never be reached in this case)
            // socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}