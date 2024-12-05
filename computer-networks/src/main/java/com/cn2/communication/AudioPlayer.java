package com.cn2.communication;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayer implements Runnable{
    private final AudioFormat format;
    private byte[] audioData;
    private SourceDataLine speakers;
    private boolean activeCall;

    // Initialize the AudioPlayer with a given AudioFormat
    public AudioPlayer(AudioFormat format, boolean activeCall) {
        this.format = format;
        this.activeCall = activeCall;
    }

    // Set Call Status
    public void setActiveCall(boolean activeCall){
        this.activeCall = activeCall;
    }

    // Set audio data
    public void setAudioData(byte[] audioData){
        this.audioData = audioData;
    }

    // Method to close the audio line when done
    public void close() {
        if (speakers != null) {
            speakers.drain();  // Finish playing any remaining data
            speakers.close();  // Close the audio line
        }
    }

     @Override
    public void run() {
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(format);
            sourceLine.start();

            System.out.println("Playing...");

            while (true) { // Συνεχής αναπαραγωγή
                if (audioData != null && activeCall) {
                    sourceLine.write(audioData, 0, audioData.length);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}


