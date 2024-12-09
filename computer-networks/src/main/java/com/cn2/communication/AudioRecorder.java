package com.cn2.communication;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder implements Runnable {
    public final AudioFormat audioFormat;
    public final Sender sender;
    public boolean active;
    public boolean local;

    public AudioRecorder(Sender sender, AudioFormat audioFormat, boolean active, boolean local) {
        this.audioFormat = audioFormat;
        this.active = active;
        this.sender = sender;
        this.local = local;
    }

    // Constructor for local Audio Recorder
    public AudioRecorder(AudioFormat audioFormat, boolean active) {
        this.audioFormat = audioFormat;
        this.active = active;
        this.local = true;
        this.sender = null;
    }

    // Set call status
    public void setActive(boolean active){
        this.active = active;
    }

    @Override
    public void run() {
        try {
            


            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open(audioFormat);
            targetLine.start();
            File outputFile = new File("record.wav");
            System.out.println("Recording...");

            AudioInputStream recordingStream = new AudioInputStream(targetLine);
            byte[] buffer = new byte[2048];
            while (true) { // Συνεχής καταγραφή
                if(active) {
                    int bytesRead = targetLine.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        // Here, you can process or store the recorded audio data as needed
                        
                        // AudioSystem.write(recordingStream, AudioFileFormat.Type.WAVE, outputFile);


                        if (local){
                            AudioBuffer.getInstance().addData(buffer);
                        } else {
                            sender.sendAudio(buffer);
                        }
                        // Αποθήκευση δεδομένων για αναπαραγωγή
                        
                        
                       System.out.println("Recording audio with " + bytesRead + " bytes read");
                    }
                } 
                AudioBuffer.getInstance().addData(null);
                
                targetLine.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}