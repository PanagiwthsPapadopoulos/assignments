import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorderAndPlayer {
    // Βασικές ρυθμίσεις για τον ήχο
    private static final float SAMPLE_RATE = 8000.0f; // Συχνότητα δειγματοληψίας
    private static final int SAMPLE_SIZE_IN_BITS = 8; // Μέγεθος δείγματος (8 bits)
    private static final int CHANNELS = 1; // Μονοφωνικός ήχος
    private static final boolean SIGNED = true; // Signed samples
    private static final boolean BIG_ENDIAN = false; // Little-endian δεδομένα

    public static void main(String[] args) {
        // Δημιουργία μορφής ήχου
        AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);

        // Εκκίνηση καταγραφής και αναπαραγωγής σε διαφορετικά νήματα
        Thread recorderThread = new Thread(new AudioRecorder1(audioFormat));
        Thread playerThread = new Thread(new AudioPlayer1(audioFormat));

        recorderThread.start();
        playerThread.start();
    }
}

// Νήμα για καταγραφή ήχου
class AudioRecorder1 implements Runnable {
    private final AudioFormat audioFormat;

    public AudioRecorder1(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
    }

    @Override
    public void run() {
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open(audioFormat);
            targetLine.start();

            System.out.println("Recording...");

            byte[] buffer = new byte[1024];
            while (true) { // Συνεχής καταγραφή
                targetLine.read(buffer, 0, buffer.length);
                AudioBuffer.getInstance().addData(buffer); // Αποθήκευση δεδομένων για αναπαραγωγή
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Νήμα για αναπαραγωγή ήχου
class AudioPlayer1 implements Runnable {
    private final AudioFormat audioFormat;

    public AudioPlayer1(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
    }

    @Override
    public void run() {
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
            sourceLine.start();

            System.out.println("Playing...");

            while (true) { // Συνεχής αναπαραγωγή
                byte[] data = AudioBuffer.getInstance().getData();
                if (data != null) {
                    sourceLine.write(data, 0, data.length);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Buffer για καταγραφή και αναπαραγωγή ήχου
class AudioBuffer {
    private static final AudioBuffer instance = new AudioBuffer();
    private byte[] data;

    private AudioBuffer() {}

    public static AudioBuffer getInstance() {
        return instance;
    }

    public synchronized void addData(byte[] newData) {
        this.data = newData.clone(); // Αποθήκευση νέων δεδομένων
    }

    public synchronized byte[] getData() {
        return data; // Επιστροφή δεδομένων για αναπαραγωγή
    }
}