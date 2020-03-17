package com.lydon.websocket.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lydon.websocket.normal.WebSocketHandler;
import com.lydon.websocket.annotation.TgWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@TgWebSocketHandler(endPoint = "/normal/demo")
public class HandlerDemo implements WebSocketHandler {

     //为了保存在线用户信息，在方法中新建一个list存储一下【实际项目依据复杂度，可以存储到数据库或者缓存】


    private final static List<WebSocketSession> SESSIONS = Collections.synchronizedList(new ArrayList<>());


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("链接成功......");
        SESSIONS.add(session);
        String userName = (String) session.getAttributes().get("WEBSOCKET_USERNAME");
        if (userName != null) {
            JSONObject obj = new JSONObject();
            // 统计一下当前登录系统的用户有多少个
            obj.put("count", SESSIONS.size());
            users(obj);
            session.sendMessage(new TextMessage(obj.toJSONString()));
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("处理要发送的消息");
        JSONObject msg = JSON.parseObject(message.getPayload().toString());
        System.out.println(msg);

        if(msg.containsKey("token")){
            System.out.println("验证是否包含token:" + msg.getString("token"));
        }else{

            JSONObject obj = new JSONObject();
            if (msg.getInteger("type") == 1) {
                //给所有人
                obj.put("msg", msg.getString("msg"));
                sendMessageToUsers(new TextMessage(obj.toJSONString()));
            } else {
                //给个人
                String to = msg.getString("to");
                obj.put("msg", msg.getString("msg"));
                sendMessageToUser(to, new TextMessage(obj.toJSONString()));
            }
        }

    }


    /**
     *  链接出错时的操作
     * @param session
     * @param exception
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        log.info("链接出错，关闭链接......");
        SESSIONS.remove(session);
    }

    /**
     * 链接关闭时做的操作
     * @param session
     * @param closeStatus
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("链接关闭......" + closeStatus.toString());
        SESSIONS.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /*
 * 给所有在线用户发送消息
 *
 * @param message
  */
    private void sendMessageToUsers(TextMessage message) {
        for (WebSocketSession user : SESSIONS) {
            try {
                if (user.isOpen()) {
                    user.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

/*
     * 给某个用户发送消息
     *
     * @param userName
     * @param message
 */

    private void sendMessageToUser(String userName, TextMessage message) {
        for (WebSocketSession user : SESSIONS) {
            if (user.getAttributes().get("WEBSOCKET_USERNAME").equals(userName)) {
                try {
                    if (user.isOpen()) {
                        user.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

/*
     * 将系统中的用户传送到前端
     *
     * @param obj
 */

    private void users(JSONObject obj) {
        List<String> userNames = new ArrayList<>();
        for (WebSocketSession webSocketSession : SESSIONS) {
            userNames.add((String) webSocketSession.getAttributes().get("WEBSOCKET_USERNAME"));
        }
        obj.put("users", userNames);
    }
}
