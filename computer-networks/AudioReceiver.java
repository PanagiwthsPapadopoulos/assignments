
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioReceiver implements Runnable {

    private AudioFormat format;
    private byte[] audioData;
    private boolean ongoingCall;
    private SourceDataLine sourceLine;

    public AudioReceiver(AudioFormat format, boolean ongoingCall, byte[] audioData) {
        this.format = format;
        this.audioData = audioData;
        setOngoingCall(ongoingCall);
    }

    public void setOngoingCall(boolean ongoingCall) {
        this.ongoingCall = ongoingCall;
    }

    private void initializePlayback() {
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(format);
            sourceLine.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            byte[] buffer = audioData;

            initializePlayback();

            while (ongoingCall) {
                // Write the audio data to the SourceDataLine
                sourceLine.write(buffer, 0, buffer.length);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sourceLine != null) {
                sourceLine.drain();
                sourceLine.close();
            }
        }
    }
}