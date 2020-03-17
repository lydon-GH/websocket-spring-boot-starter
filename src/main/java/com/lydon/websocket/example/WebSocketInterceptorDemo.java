package com.lydon.websocket.example;

import com.sun.security.auth.*;
import com.lydon.websocket.annotation.TgWebSocketInterceptor;
import com.lydon.websocket.subscribe.WebSocketInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.*;
import org.springframework.util.*;

import java.security.*;
import java.util.*;

/**
 * @author liuyd
 */
@TgWebSocketInterceptor
public class WebSocketInterceptorDemo implements WebSocketInterceptor {

    /**
     * 绑定user到websocket conn上
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println("----------------------:");
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            if (raw instanceof Map) {
                System.out.println(raw);
            }
            System.out.println(">>>>>>>>>>>>>>");
            String username = accessor.getFirstNativeHeader("username");
            String token = accessor.getFirstNativeHeader("token");

            System.out.println("token:" + token);

            if (StringUtils.isEmpty(username)) {
                return null;
            }
            // 绑定user
            Principal principal = new UserPrincipal(username);
            accessor.setUser(principal);
        }
        return message;
    }
}
