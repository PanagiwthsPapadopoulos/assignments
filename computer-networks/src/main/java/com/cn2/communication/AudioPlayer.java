package com.cn2.communication;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

class AudioPlayer implements Runnable {
    private final AudioFormat audioFormat;
    private byte[] audioData;
    boolean activeCall;
    boolean local;

    public AudioPlayer(AudioFormat audioFormat, boolean activeCall, boolean local) {
        this.audioFormat = audioFormat;
        this.activeCall = activeCall;
        this.local = local;
    }

    public byte[] getData() {
    	// System.out.println("Returned Audio Data within class");
    	return audioData;
    }
    
    public void setAudioData(byte[] incomingData) {
    	System.out.println("Set Data");
    	this.audioData = incomingData;
    }
    
    public void setActive(boolean activeCall) {
    	this.activeCall = activeCall;
    }
    
    @Override
    public void run() {
        try {

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
            sourceLine.start();
            
            if (!sourceLine.isOpen() || !sourceLine.isActive()) {
            	System.out.println("SourceLine open: " + sourceLine.isOpen() );
            	System.out.println("SourceLine active: " + sourceLine.isActive() );
                System.err.println("SourceDataLine is not properly initialized.");
            }
            
            byte[] data = new byte[1024];
            System.out.println("Playing...");
            
            while (true) { // Συνεχής αναπαραγωγή
                
                
                if (activeCall){
                    
                        
                    if(local){
                        data = AudioBuffer.getInstance().getData();
                    } else {
                        data = getData();
                    }
                    if(data != null) {
                        sourceLine.write(data, 0, data.length);
                        // System.out.println("Player: Actual Data received");
                    }
                    
                    // AudioBuffer.getInstance().nullData();
                    
                }
                sourceLine.flush();
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}