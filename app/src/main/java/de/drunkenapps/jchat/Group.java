package de.drunkenapps.jchat;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * @author max
 * @date 1/8/18.
 */

class Group {
    private DatabaseReference databaseReference;
    private String name;
    private String lastMessageUid;
    private String groupId;
    private String lastMessage;
    private long lastTimestamp;
    private ArrayList<Message> messages;

    Group(){
        messages = new ArrayList<>();
    }

    Group(DatabaseReference databaseReference, String name, String groupId, long lastTimestamp){
        this.databaseReference = databaseReference;
        this.name = name;
        this.groupId = groupId;
        this.lastTimestamp = lastTimestamp;
        messages = new ArrayList<>();
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessageUid() {
        return lastMessageUid;
    }

    public void setLastMessageUid(String lastMessageUid) {
        this.lastMessageUid = lastMessageUid;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public String getLastMessage() {
        if (lastMessage == null){
            lastMessage = "empty";
        }
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message){
        messages.add(0, message);
    }
}
