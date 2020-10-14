package com.example.myproject;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String fromUser;
    private String fromUserUUID;
    private String toUserUUID;
    private long messageTime;
    private String firstKey;
    private String secondKey;

    public ChatMessage(String messageText, String fromUser,String fromUserUUID,String toUserUUID, String firstKey, String secondKey) {
        this.messageText = messageText;
        this.fromUser = fromUser;
        this.fromUserUUID =fromUserUUID;
        this.toUserUUID=toUserUUID;
        messageTime = new Date().getTime();
        this.firstKey=firstKey;
        this.secondKey = secondKey;
    }

    public ChatMessage(){}

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getFromUserUUID() {
        return fromUserUUID;
    }

    public void setFromUserUUID(String fromUserUUID) {
        this.fromUserUUID = fromUserUUID;
    }

    public String getToUserUUID() {
        return toUserUUID;
    }

    public void setToUserUUID(String toUserName) {
        this.toUserUUID = toUserName;
    }

    public String getFirstKey() {
        return firstKey;
    }

    public void setFirstKey(String firstKey) {
        this.firstKey = firstKey;
    }

    public String getSecondKey() {
        return secondKey;
    }

    public void setSecondKey(String secondKey) {
        this.secondKey = secondKey;
    }
}

