package com.test.chatroomtest.Data;

public class ChatMessage {
    private String message;
    private String user;
    private long time;
    private String type;
    private String photo;
    public ChatMessage(){}

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ChatMessage(String message, String user, long time, String type, String photo) {
        this.message = message;
        this.user = user;
        this.time = time;
        this.type = type;
        this.photo = photo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ChatMessage(String message, String user, long time) {
        this.message = message;
        this.user = user;
        this.time = time;
    }

    public ChatMessage(String message, String user, long time, String type) {
        this.message = message;
        this.user = user;
        this.time = time;
        this.type = type;
    }
}
