package com.lydon.websocket.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TgWebSocketHandshake {
    Class handlerClass() ;
}
