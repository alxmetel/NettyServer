package ua.metelchenko.netty.status;

public class RedirectionRequest {

    private String redirectionUrl;
    private long countOfRedirections;

    public RedirectionRequest(String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
        this.countOfRedirections++;
    }

    public String getRedirectionUrl() {
        return redirectionUrl;
    }

    public long getCountOfRedirections() {
        return countOfRedirections;
    }

    public void setCountOfRedirections() {
        this.countOfRedirections++;
    }
}
