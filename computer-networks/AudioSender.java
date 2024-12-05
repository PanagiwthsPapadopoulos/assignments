
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioSender implements Runnable {

    private DatagramSocket socket;
    private InetAddress receiverAddress;
    private int receiverPort;
    private AudioFormat format;
    private boolean ongoingCall;
    private TargetDataLine microphone;

    public AudioSender(DatagramSocket socket, InetAddress receiverAddress, int receiverPort, AudioFormat format, boolean ongoingCall) {
        this.socket = socket;
        this.receiverAddress = receiverAddress;
        this.receiverPort = receiverPort;
        this.format = format;
        setOngoingCall(ongoingCall);
    }

    public void setOngoingCall(boolean ongoingCall) {
        this.ongoingCall = ongoingCall;
    }

    private void initializeMicrophone() {
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            Sender sender = new Sender(socket, receiverAddress, receiverPort);
            initializeMicrophone();
            byte[] buffer = new byte[1024];  // Adjust buffer size as needed

            while (ongoingCall) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    // Send the audio data over the network
                    
                    sender.sendAudio(buffer);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            microphone.close();
        }
    }
}