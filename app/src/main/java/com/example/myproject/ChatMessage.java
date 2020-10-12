package com.example.myproject;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String fromUser;
    private String fromUserUUID;
    private String toUserUUID;
    private long messageTime;

    public ChatMessage(String messageText, String fromUser,String fromUserUUID,String toUserUUID) {
        this.messageText = messageText;
        this.fromUser = fromUser;
        this.fromUserUUID =fromUserUUID;
        this.toUserUUID=toUserUUID;
        // Initialize to current time
        messageTime = new Date().getTime();
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
}
