package com.cn2.communication;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;



// Νήμα για καταγραφή ήχου
class AudioRecorder1 implements Runnable {
    private final AudioFormat audioFormat;
    private boolean active;
    private TargetDataLine targetLine;

    public AudioRecorder1(AudioFormat audioFormat, boolean active) {
        this.audioFormat = audioFormat;
        this.active = active;
    }
    
    public void setStatus (boolean active) {
    	this.active = active;
    }

    @Override
    public void run() {
        try {
        	// Create a DataLine.Info object with the desired format
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

            // Get the TargetDataLine for capturing audio
            targetLine = (TargetDataLine) AudioSystem.getLine(info);

            // Open and start the line
            targetLine.open(audioFormat);
            targetLine.start();

            System.out.println("Recording from AudioRecorder 1...");

            byte[] buffer = new byte[1024];
            while (true) { // Συνεχής καταγραφή
            	
            	// targetLine.read(buffer, 0, buffer.length);
                // AudioBuffer.getInstance().addData(buffer);
            	
            	
                if(active) {
                    int bytesRead = targetLine.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        // Here, you can process or store the recorded audio data as needed
                        
                        
                        // Αποθήκευση δεδομένων για αναπαραγωγή
                        AudioBuffer.getInstance().addData(buffer);
                        
                    //    System.out.println("Recording audio with " + bytesRead + " bytes read");
                    }
                } 
                
                targetLine.flush();
                    //Thread.sleep(2000);
            }
            
        } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Close the line when done
                if (targetLine != null && targetLine.isOpen()) {
                    targetLine.stop();
                    targetLine.close();
                    System.out.println("Recording stopped.");
            }
        }
    }
}

// Νήμα για αναπαραγωγή ήχου
class AudioPlayer1 implements Runnable {
    private final AudioFormat audioFormat;
    private boolean active;
    private SourceDataLine line; // Declare line as an instance variable

    public AudioPlayer1(AudioFormat audioFormat, boolean active) {
        this.audioFormat = audioFormat;
        this.active = true;
    }
    
    public void setStatus (boolean active) {
    	this.active = active;
    }

    @Override
    public void run() {
        try {
        	// Get the SourceDataLine to play the sound
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
            line.start();

            System.out.println("Playing...");
            byte[] buffer = new byte[2]; // 16-bit samples (2 bytes)
            double angle = 0.0; // The starting angle for the sine wave
            final float frequency = 440.0f; // Frequency of the sine wave (440 Hz is the standard A note)
            final float sampleRate = audioFormat.getSampleRate(); // Sample rate from the audio format

           
            
            
            while (true) { // Συνεχής αναπαραγωγή

            	byte[] data = AudioBuffer.getInstance().getData();

            	// byte[] data = AudioBuffer.getInstance().getData();
            	// if(data != null) {
            	// 	line.write(data, 0, data.length);
            	// } else {
            	// 	System.out.println("Null data incoming");
            	// 	Thread.sleep(500);
            	// }

                if (active){
                    if (data == null) {
                        // System.out.println("Player: Null Data received");
                    
                    } else if (data != null) {
                        
                        line.write(data, 0, data.length);
                        // System.out.println("Player: Actual Data received");
                    
                        
                        // AudioBuffer.getInstance().nullData();
                    }
                }
                line.flush();
                // line.stop();
              
                //Thread.sleep(2000);
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

    public void nullData() {
    	this.data = null;
    }
    
    public synchronized void addData(byte[] newData) {
        this.data = newData; // Αποθήκευση νέων δεδομένων
    }

    public synchronized byte[] getData() {
        return data; // Επιστροφή δεδομένων για αναπαραγωγή
    }
}


class SinePlayer implements Runnable {
    private final AudioFormat audioFormat; // AudioFormat passed as an argument
    private boolean active;
    private SourceDataLine line; // Declare line as an instance variable


    // Constructor to initialize the player with the audio format and active state
    public SinePlayer(AudioFormat audioFormat, boolean active) {
        this.audioFormat = audioFormat;
        this.active = active;
    }

    // Method to change the active state
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void run() {
        try {
            // Get the SourceDataLine to play the sound
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
            line.start();

            byte[] buffer = new byte[2]; // 16-bit samples (2 bytes)
            double angle = 0.0; // The starting angle for the sine wave
            final float frequency = 440.0f; // Frequency of the sine wave (440 Hz is the standard A note)
            final float sampleRate = audioFormat.getSampleRate(); // Sample rate from the audio format

            while (true) {
                if (active) {
                    // Generate sine wave samples
                    for (int i = 0; i < buffer.length; i += 2) {
                        short sample = (short) (Math.sin(angle) * Short.MAX_VALUE); // Generate sine wave sample
                        buffer[i] = (byte) (sample & 0xFF); // Low byte
                        buffer[i + 1] = (byte) ((sample >> 8) & 0xFF); // High byte
                        angle += 2.0 * Math.PI * frequency / sampleRate; // Increment angle for next sample

                        if (angle >= 2.0 * Math.PI) {
                            angle -= 2.0 * Math.PI; // Keep the angle within one cycle
                        }
                    }

                    // Play the sine wave sample
                    line.write(buffer, 0, buffer.length);
                } else {
                    // If active is false, stop the sound by not writing anything to the line
                    line.flush(); // Clear the audio buffer
                }

                // Sleep for the time it takes to play one buffer worth of audio
                try {
                    Thread.sleep(20); // Sleep for 20 ms to generate and play the sine wave in real-time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } finally {
        	if (line != null && line.isOpen()) {
                System.out.println("Shutting down the audio line...");
                line.stop();
                line.flush();  // Make sure any leftover audio is cleared
                line.close();
            }
        }
    }
}