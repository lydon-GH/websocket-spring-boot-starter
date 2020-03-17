package com.lydon.websocket.util;

import com.lydon.websocket.annotation.TgWebSocketHandler;
import com.lydon.websocket.annotation.TgWebSocketHandshake;
import com.lydon.websocket.annotation.TgWebSocketInterceptor;
import com.lydon.websocket.entity.HandlerEntity;
import com.lydon.websocket.subscribe.WebSocketInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.*;
@Slf4j

public class WebSocketRegister implements BeanFactoryAware {
    private static final Map<String, HandlerEntity> handlerBeanContainer = new HashMap<String,HandlerEntity>();
    private static final Map<String,WebSocketInterceptor> interceptorsBeanContainer = new HashMap<String,WebSocketInterceptor>();
    private final String packname="com.teligen";
    public  Map<String,HandlerEntity> getHandlerBeanContainer(){
        return handlerBeanContainer;
    }
    public Map<String,WebSocketInterceptor> getInterceptorsBeanContainer(){
        return interceptorsBeanContainer;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Reflections f=new Reflections(packname);
        //普通方式
        //dealHandlerAndHandshake(f);
        //stomp方式
        dealInterceptor(f);
    }

    /**
     * //扫描出websocket自定义注解handler与handshake
     * @param f
     */
    private void dealHandlerAndHandshake(Reflections f) {
        Set<Class<?>> handlers=f.getTypesAnnotatedWith(TgWebSocketHandler.class);
        Set<Class<?>> handshakes=f.getTypesAnnotatedWith(TgWebSocketHandshake.class);
        //手动通过spring实例化handler
        for (Class<?> c : handlers) {
            try {
                WebSocketHandler webSocketHandler= (WebSocketHandler) registerBeanDefinition(c);
                TgWebSocketHandler annotation=c.getAnnotation(TgWebSocketHandler.class);
                if(StringUtils.isEmpty(annotation.endPoint())){
                    log.error(c.getName()+"类配置的TgWebSocketHandler注解缺失endPoint属性,获取出错,不能实例化websocket!!");
                    break;
                }
                HandlerEntity handlerEntity=new HandlerEntity();
                handlerEntity.setEndpoint(annotation.endPoint());
                handlerEntity.setWebSocketHandler(webSocketHandler);
                handlerBeanContainer.put(c.getName(),handlerEntity);
                //list.add(bean);
            } catch (Exception e) {
                e.printStackTrace();}
        }

        //手动通过spring实例化handshake
        for (Class<?> c : handshakes) {
            try {
                HandshakeInterceptor handshakeInterceptor= (HandshakeInterceptor) registerBeanDefinition(c);
                TgWebSocketHandshake annotation=c.getAnnotation(TgWebSocketHandshake.class);
                Class handlerClass=annotation.handlerClass();
                if(handlerClass==null){
                    log.error(c.getName()+"类配置的TgWebSocketHandshake注解缺失handlerClass属性,获取出错,不能实例化websocket!!");
                    break;
                }

                HandlerEntity handlerEntity=handlerBeanContainer.get(handlerClass.getName());
                if(handlerEntity==null){
                    log.error(c.getName()+"类配置的TgWebSocketHandshake注解handlerClass属性为"+annotation+"的实例获取出错,不能实例化websocket!!");
                    break;
                }
                handlerEntity.setInterceptor((HandshakeInterceptor) handshakeInterceptor);
                handlerBeanContainer.put(handlerClass.getName(),handlerEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dealInterceptor(Reflections f) {
        Set<Class<?>> interceptors=f.getTypesAnnotatedWith(TgWebSocketInterceptor.class);

        //手动通过spring实例化interceptors
        for (Class<?> c : interceptors) {
            try {
                if(c.getSimpleName().equals("WebSocketInterceptorDemo")){
                    continue;
                }

                WebSocketInterceptor webSocketInterceptor= (WebSocketInterceptor) registerBeanDefinition(c);
                interceptorsBeanContainer.put(c.getName(),webSocketInterceptor);
            } catch (Exception e) {
                e.printStackTrace();}
        }
    }

    private Object registerBeanDefinition(Class<?> c) {
        BeanDefinitionBuilder beanDefinitionBuilder=BeanDefinitionBuilder.genericBeanDefinition(c.getName());
        BeanDefinition beanDefinition=beanDefinitionBuilder.getBeanDefinition();
        String name=c.getSimpleName();
        //首字母变小写
        name=Character.toLowerCase(name.charAt(0))+name.substring(1);
        //手动注册实例
        BeanUtils.registerBeanDefinition(name,beanDefinition);
        return BeanUtils.getBean(name);
    }
}
