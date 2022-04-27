package com.test.chatroomtest.Data;

public class MessageData {
    private String message;
    private String user;
    private long time;
    private String type;

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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public MessageData(String message, String user, long time) {
        this.message = message;
        this.user = user;
        this.time = time;
    }
}
