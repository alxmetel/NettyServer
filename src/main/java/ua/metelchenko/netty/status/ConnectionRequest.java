package ua.metelchenko.netty.status;

import java.util.Date;

public class ConnectionRequest {
    private String ip;
    private Date lastRequest;
    private long count;

    public ConnectionRequest(String ip) {
        this.ip = ip;
        this.lastRequest = new Date();
        this.count++;
    }

    public long getCount() {
        return count;
    }

    public void setCount() {
        this.count++;
    }

    public String getIp() {
        return ip;
    }

    public Date getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(Date lastRequest) {
        this.lastRequest = lastRequest;
    }
}
