package ua.metelchenko.netty.responses;

import io.netty.handler.codec.http.*;

import java.util.List;


public class RedirectResponse implements BaseResponse {

    @Override
    public FullHttpResponse response(String uri) {

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        List<String> redirectUrl = queryStringDecoder.parameters().get("url");
        String direction = "http://" + redirectUrl.get(0);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION, direction);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        return response;
    }
}
