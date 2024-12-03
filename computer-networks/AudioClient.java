import java.io.IOException;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;

public class AudioClient {
    static public int port;
    static public String serverAddress;

    // Constructor that requires a port number
    public AudioClient(int port, String serverAddress) {
        AudioClient.port = port;
        AudioClient.serverAddress = serverAddress;
    }
    public static void main(String[] args) {
        AudioFormat format = AudioCall.getAudioFormat();
        int serverPort = Integer.parseInt(args[0]);
        

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            System.out.println("Connected to the server!");

            // Start threads for sending and receiving audio
            new Thread(new AudioSender(socket, format)).start();
            new Thread(new AudioReceiver(socket, format)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}