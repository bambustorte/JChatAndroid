package de.drunkenapps.jchat;

/**
 * @author max
 * @date 1/6/18.
 */

public class Message {

    private String message;
    private String uid;
    private String username;
    private String mid;
    private long timestamp;

    Message(){}

    Message(String message, String uid, long timestamp, String username){
        this.message = message;
        this.uid = uid;
        this.timestamp = timestamp;
        this.username = username;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
