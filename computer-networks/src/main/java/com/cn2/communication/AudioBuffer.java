package com.cn2.communication;

// Audio buffer for local voice storage

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
    
    // Update Data
    public synchronized void addData(byte[] newData) {
        this.data = newData; 
    }

    // Return Data
    public synchronized byte[] getData() {
        return data; 
    }
}