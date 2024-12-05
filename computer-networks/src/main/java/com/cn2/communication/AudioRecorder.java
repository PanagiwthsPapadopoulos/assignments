package com.cn2.communication;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder implements Runnable {
    public final AudioFormat audioFormat;
    public final Sender sender;
    public  boolean activeCall;

    public AudioRecorder(Sender sender, AudioFormat audioFormat, boolean activeCall) {
        this.audioFormat = audioFormat;
        this.activeCall = activeCall;
        this.sender = sender;
    }

    // Set call status
    public void setActiveCall(boolean activeCall){
        this.activeCall = activeCall;
        System.out.println("ActiveCall var: " + activeCall);
        return;
    }

    @Override
    public void run() {
        try {
            


            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open(audioFormat);
            targetLine.start();

            System.out.println("Recording...");
            System.out.println("Active Call: " + activeCall);
            byte[] buffer = new byte[1024];
            while (true) { // Συνεχής καταγραφή
            	System.out.println("Thread is running...");
                if(activeCall){
                	System.out.println("Active Call and transmitting with mic");
                    targetLine.read(buffer, 0, buffer.length);
                    sender.sendAudio(buffer);
                }
                Thread.sleep(1500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}