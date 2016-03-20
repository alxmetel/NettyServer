package ua.metelchenko.netty.responses;

import io.netty.handler.codec.http.FullHttpResponse;

public interface BaseResponse {
    FullHttpResponse response(String uri);
}
