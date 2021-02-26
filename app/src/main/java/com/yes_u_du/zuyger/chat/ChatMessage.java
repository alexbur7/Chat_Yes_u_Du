package com.yes_u_du.zuyger.chat;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String fromUser;
    private String fromUserUUID;
    private String toUserUUID;
    private long messageTime;
    private String firstSeen;
    private String secondSeen;
    private String image_url;
    private String firstDelete;
    private String secondDelete;
    private String edited;

    public ChatMessage(String messageText, String fromUser, String fromUserUUID, String toUserUUID,
                       String firstSeen, String secondSeen, String image_url, String firstDelete, String secondDelete, String edited) {
        this.messageText = messageText;
        this.fromUser = fromUser;
        this.fromUserUUID =fromUserUUID;
        this.toUserUUID=toUserUUID;
        messageTime = new Date().getTime();
        this.firstSeen = firstSeen;
        this.secondSeen = secondSeen;
        this.image_url = image_url;
        this.firstDelete = firstDelete;
        this.secondDelete = secondDelete;
        this.edited=edited;
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

    public String getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(String firstSeen) {
        this.firstSeen = firstSeen;
    }

    public String getSecondSeen() {
        return secondSeen;
    }

    public void setSecondSeen(String secondSeen) {
        this.secondSeen = secondSeen;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getFirstDelete() {
        return firstDelete;
    }

    public void setFirstDelete(String firstDelete) {
        this.firstDelete = firstDelete;
    }

    public String getSecondDelete() {
        return secondDelete;
    }

    public void setSecondDelete(String secondDelete) {
        this.secondDelete = secondDelete;
    }

    public String getEdited() {
        return edited;
    }

    public void setEdited(String edited) {
        this.edited = edited;
    }
}

