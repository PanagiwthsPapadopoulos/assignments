package com.cn2.communication;

public class ReceivedData {
    private String type; // "message" or "audio"
    private String message; // Text message (if applicable)
    private byte[] audio; // Audio data (if applicable)

    public ReceivedData(String type, String message, byte[] audio) {
        this.type = type;
        this.message = message;
        this.audio = audio;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getAudio() {
        return audio;
    }
}