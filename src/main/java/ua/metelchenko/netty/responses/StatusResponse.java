package ua.metelchenko.netty.responses;

import ua.metelchenko.netty.server.ServerHandler;
import ua.metelchenko.netty.status.Connection;
import ua.metelchenko.netty.status.RedirectionRequest;
import ua.metelchenko.netty.status.ConnectionRequest;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.ListIterator;

public class StatusResponse implements BaseResponse {

    @Override
    public FullHttpResponse response(String uri) {

        final StringBuilder buff = new StringBuilder();

        List<ConnectionRequest> connectionsList = ServerHandler.getUniqueConnectionsList();
        List<RedirectionRequest> redirectionsList = ServerHandler.getRedirectionsList();

        buff.append("<!DOCTYPE html><html><head>");
        buff.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n");
        buff.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css\">\n");
        buff.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap-theme.min.css\">\n");
        buff.append("</head>");
        buff.append("<body>");
        buff.append("<div style=\"margin: 0 0 0 20px  \">");

        buff.append("<h4>Request count: ").append(ServerHandler.getCountOfRequests()).append("</h4>");
        buff.append("<h4>Unique request count: ").append(ServerHandler.getCountOfUniqueRequests()).append("</h4>");
        buff.append("<h4>Open connections: ").append(ServerHandler.getActiveChannels()).append("</h4>");

        buff.append("<br><br>");
        buff.append("<h4>Table 1: Requests:</h4>");
        buff.append("<div>\n" +
                "<table width=\"60%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">")
                .append("<tbody>\n" +
                        "<tr>\n" +
                        "<td style=\"text-align: center;\"><b>&nbsp;IP</b></td>\n" +
                        "<td style=\"text-align: center;\"><b>&nbsp;Count</b></td>\n" +
                        "<td style=\"text-align: center;\"><b>Last Request&nbsp;</b></td></tr>");
        synchronized (connectionsList) {
            for (ConnectionRequest record : connectionsList) {
                buff.append("<tr><td style=\"text-align: center;\">")
                        .append(record.getIp())
                        .append("</td><td style=\"text-align: center;\">")
                        .append(record.getCount())
                        .append("</td><td style=\"text-align: center;\">")
                        .append(record.getLastRequest()).append("</td></tr>");
            }
        }
        buff.append("</tbody></table></div> <br /> <br />");

        buff.append("<br><br>");
        buff.append("<h4>Table 2: Redirection requests:</h4>");
        buff.append(
                "<div>\n" +
                        "<table width=\"60%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">")
                .append("<tbody>\n" +
                        "<tr>\n" +
                        "<td style=\"text-align: center;\"><b>&nbsp;URL</b></td>\n" +
                        "<td style=\"text-align: center;\"><b>&nbsp;Count</b></td></tr>");
        synchronized (redirectionsList) {
            for (RedirectionRequest record : redirectionsList) {
                buff.append("<tr><td style=\"text-align: center;\">").append(record.getRedirectionUrl())
                        .append("</td><td style=\"text-align: center;\">").append(record.getCountOfRedirections())
                        .append("</td>");
            }
        }
        buff.append("</tbody></table></div><br /> <br />");

        buff.append("<br><br>");
        buff.append("<h4>Table 3: Last 16 connections:</h4>");
        buff.append(
                "<div>\n" +
                        "<table width=\"60%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">")
                .append("<tbody>\n" +
                        "<tr>\n" +
                        "<td style=\"text-align: center;\"><b>&nbsp;IP</b></td>\n" +
                        "<td style=\"text-align: center;\"><b>&nbsp;URI</b></td>\n" +
                        "<td style=\"text-align: center;\"><b>&nbsp;Date and Time</b></td>\n" +
                        "<td style=\"text-align: center;\"><b>&nbsp;Sent</b></td>\n" +
                        "<td style=\"text-align: center;\"><b>&nbsp;Received</b></td>\n" +
                        "<td style=\"text-align: center;\"><b>&nbsp;Speed (bytes/sec)</b></td>\n</tr>");
        ListIterator<Connection> iterator = ServerHandler.getConnectionListIterator();
        synchronized (iterator) {
            while (iterator.hasPrevious()) {
                Connection connection = iterator.previous();
                buff.append("<tr><td style=\"text-align: center;\">").append(connection.getIp())
                        .append("</td><td style=\"text-align: left;\">").append(connection.getUri())
                        .append("</td><td style=\"text-align: center;\">").append(connection.getDate())
                        .append("</td><td style=\"text-align: right;\">").append(connection.getSentBytes())
                        .append("</td><td style=\"text-align: right;\">").append(connection.getReceivedBytes())
                        .append("</td><td style=\"text-align: right;\">").append(connection.getSpeed()).append("</td></tr>");
            }
        }
        buff.append("</tbody></table></div>");

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(buff, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        return  response;
    }
}
