package ua.metelchenko.netty.responses;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class HelloResponse implements BaseResponse {
    private static final String HELLO = "Hello World";

    @Override
    public FullHttpResponse response(String uri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(HELLO, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        return response;
    }
}
