package ua.metelchenko.netty.server;

import ua.metelchenko.netty.responses.*;
import ua.metelchenko.netty.status.Connection;
import ua.metelchenko.netty.status.RedirectionRequest;
import ua.metelchenko.netty.status.ConnectionRequest;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.net.InetSocketAddress;
import java.util.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final HelloResponse HELLO        = new HelloResponse();
    private static final RedirectResponse REDIRECT  = new RedirectResponse();
    private static final StatusResponse STATUS      = new StatusResponse();
    private static final NotFoundResponse NOT_FOUND = new NotFoundResponse();

    private static List<ConnectionRequest> uniqueConnectionsList =
            Collections.synchronizedList(new ArrayList<ConnectionRequest>());
    private static List<RedirectionRequest> redirectionsList =
            Collections.synchronizedList(new ArrayList<RedirectionRequest>());
    private static List<Connection> lastConnectionsList =
            Collections.synchronizedList(new ArrayList<Connection>());

    private Connection connection = new Connection();

    private static long countOfRequests;
    private static long countOfUniqueRequests;
    private static long countOfActiveChannels;
    private double time;
    private int receivedBytes;
    private int sentBytes;
    private long speed;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        boolean flag = false;

        receivedBytes += msg.toString().length();
        connection.setReceivedBytes(receivedBytes);

        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            if (HttpHeaders.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }

            boolean keepAlive = HttpHeaders.isKeepAlive(req);
            // Getting URI of request
            String uri = req.getUri();
            if (uri.matches("/redirect[?]url=.*")) {
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
                List<String> redirectUrl = queryStringDecoder.parameters().get("url");
                connection.setUri(redirectUrl.get(0));
            } else {
                connection.setUri(uri);
            }

            String IP = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
            connection.setIp(IP);
            connection.setDate(new Date());

            ConnectionRequest connectionRequest = new ConnectionRequest(IP);
            synchronized (uniqueConnectionsList) {
                for (ConnectionRequest r : uniqueConnectionsList) {
                    if (r.getIp().equals(IP)) {
                        r.setCount();
                        r.setLastRequest(new Date());
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    uniqueConnectionsList.add(connectionRequest);
                }
            }
            countOfRequests++;
            countOfUniqueRequests = uniqueConnectionsList.size();

            time = (System.nanoTime() - time)/1000000000;
            sentBytes = getSentBytes(uri);
            connection.setSentBytes(sentBytes);
            speed = (long)((receivedBytes + sentBytes)/time);
            connection.setSpeed(speed);
            addToListConnections();

            FullHttpResponse response = getResponse(uri);

            if (uri.equals("/hello")) {
                Thread.currentThread().sleep(10000);
            }

            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                ctx.write(response);
            }
        }
    }

    private int getSentBytes(String uri) {
        if (uri.equals("/hello")) {
            return HELLO.response(uri).content().writerIndex() +
                    HELLO.response(uri).headers().toString().length();
        }
        else if (uri.matches("/redirect[?]url=.*")) {
            return REDIRECT.response(uri).content().writerIndex() +
                    REDIRECT.response(uri).headers().toString().length();
        }
        else if (uri.equals("/status")) {
            return STATUS.response(uri).content().writerIndex() +
                    STATUS.response(uri).headers().toString().length();
        }
        else {
            return NOT_FOUND.response(uri).content().writerIndex() +
                    NOT_FOUND.response(uri).headers().toString().length();
        }
    }

    private FullHttpResponse getResponse(String uri) {
        BaseResponse response;
        if (uri.equals("/hello")) {
            response = HELLO;
        }
        else if (uri.matches("/redirect[?]url=.*")) {
            response = REDIRECT;
            boolean flag = false;

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
            List<String> redirectUrl = queryStringDecoder.parameters().get("url");
            String direction = redirectUrl.get(0);

            RedirectionRequest redirectionRequest = new RedirectionRequest(direction);
            for (RedirectionRequest r : redirectionsList) {
                if (r.getRedirectionUrl().equals(direction)) {
                    r.setCountOfRedirections();
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                redirectionsList.add(redirectionRequest);
            }
        } else if (uri.equals("/status")) {
            response = STATUS;
        }
        else response = NOT_FOUND;
        return response.response(uri);
    }

    public static synchronized void dropActiveConnections() {
        countOfActiveChannels--;
    }

    public static synchronized void addActiveConnections() {
        countOfActiveChannels++;
    }

    public synchronized static long getActiveChannels() {
        return countOfActiveChannels;
    }

    private synchronized void addToListConnections() {
        lastConnectionsList.add(connection);
        if (lastConnectionsList.size() > 16) {
            lastConnectionsList.remove(0);
        }
    }

    public synchronized static ListIterator<Connection> getConnectionListIterator() {
        List<Connection> l = new ArrayList<Connection>(lastConnectionsList);
        return l.listIterator(l.size());
    }

    public synchronized static List<ConnectionRequest> getUniqueConnectionsList() {
        return uniqueConnectionsList;
    }

    public synchronized static long getCountOfRequests() {
        return countOfRequests;
    }

    public synchronized static long getCountOfUniqueRequests() {
        return countOfUniqueRequests;
    }

    public synchronized static List<RedirectionRequest> getRedirectionsList() {
        return redirectionsList;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        time = System.nanoTime();
        addActiveConnections();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        dropActiveConnections();
        super.channelInactive(ctx);
    }
}
