package ua.metelchenko.netty.responses;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class NotFoundResponse implements BaseResponse {
    private static final String NOT_FOUND = "Error 404";

    @Override
    public FullHttpResponse response(String uri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(NOT_FOUND, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        return response;
    }
}
