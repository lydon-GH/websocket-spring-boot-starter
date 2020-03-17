package com.lydon.websocket.entity;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class HandlerEntity {
    private String endpoint;
    private WebSocketHandler webSocketHandler;
    private HandshakeInterceptor interceptor;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public WebSocketHandler getWebSocketHandler() {
        return webSocketHandler;
    }

    public void setWebSocketHandler(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    public HandshakeInterceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(HandshakeInterceptor interceptor) {
        this.interceptor = interceptor;
    }
}
