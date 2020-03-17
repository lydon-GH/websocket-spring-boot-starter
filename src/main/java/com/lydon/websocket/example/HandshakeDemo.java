package com.lydon.websocket.example;

import com.lydon.websocket.annotation.TgWebSocketHandshake;
import com.lydon.websocket.normal.WebSocketHandShake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Map;



@Slf4j
@TgWebSocketHandshake(handlerClass= HandlerDemo.class)
public class HandshakeDemo implements WebSocketHandShake {

    /**
     * 握手前做的事情
     * @param request
     * @param response
     * @param webSocketHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("........beforeHandshake.......");
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

            // 获取请求连接之前的token参数.
            Enumeration enu = servletRequest.getParameterNames();
            while (enu.hasMoreElements()) {
                String paraName = (String) enu.nextElement();
                if(paraName.equalsIgnoreCase("name")){
                    attributes.put("WEBSOCKET_USERNAME", servletRequest.getParameter(paraName));
                }
                System.out.println(paraName + ": " + servletRequest.getParameter(paraName));
            }
        }
        return true;
    }

    /**
     * 握手后做的事情
     * @param request
     * @param response
     * @param handler
     * @param e
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Exception e) {
        System.out.println("........afterHandshake.......");
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
            HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
            if (!StringUtils.isEmpty(req.getHeader("sec-normal-protocol"))) {
                try {
                    System.out.println(URLDecoder.decode(req.getHeader("sec-normal-protocol"),"utf-8"));
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                resp.addHeader("sec-normal-protocol", req.getHeader("sec-normal-protocol"));
            }
        }
    }
}
