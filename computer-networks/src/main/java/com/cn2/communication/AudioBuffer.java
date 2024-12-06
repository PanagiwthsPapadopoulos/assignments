package com.cn2.communication;

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