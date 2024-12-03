
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;

public class AudioServer {
    static public int port;

    // Constructor that requires a port number
    public AudioServer(int port) {
        AudioServer.port = port;
    }
    public static void main(String[] args) {
        AudioFormat format = AudioCall.getAudioFormat();
        int clientPort = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(clientPort)) {
            System.out.println("Waiting for a client to connect...");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected!");

            // Start threads for sending and receiving audio
            new Thread(new AudioSender(socket, format)).start();
            new Thread(new AudioReceiver(socket, format)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}