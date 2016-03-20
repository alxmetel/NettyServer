package ua.metelchenko.netty.status;

import java.util.Date;

public class Connection {
    private String ip;
    private String uri;
    private Date date;
    private long sentBytes;
    private long receivedBytes;
    private long speed;

    public Connection() {
        this.ip = null;
        this.uri = null;
        this.date = new Date();
        this.sentBytes = 0;
        this.receivedBytes = 0;
        this.speed = 0;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getSentBytes() {
        return sentBytes;
    }

    public void setSentBytes(long sentBytes) {
        this.sentBytes = sentBytes;
    }

    public long getReceivedBytes() {
        return receivedBytes;
    }

    public void setReceivedBytes(long receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }
}
