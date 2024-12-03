
import java.io.InputStream;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioReceiver implements Runnable {
    private final Socket socket;
    private final AudioFormat format;

    public AudioReceiver(Socket socket, AudioFormat format) {
        this.socket = socket;
        this.format = format;
    }

    @Override
    public void run() {
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine speaker = (SourceDataLine) AudioSystem.getLine(info);
            speaker.open(format);
            speaker.start();

            InputStream in = socket.getInputStream();
            byte[] buffer = new byte[1024];
            System.out.println("Receiving audio...");

            while (true) {
                int bytesRead = in.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    speaker.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}