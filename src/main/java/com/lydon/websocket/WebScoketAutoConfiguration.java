package com.lydon.websocket;

import com.lydon.websocket.example.WebSocketInterceptorDemo;
import com.lydon.websocket.subscribe.WebSocketInterceptor;
import com.lydon.websocket.util.BeanUtils;
import com.lydon.websocket.util.WebSocketRegister;
import com.lydon.websocket.properties.WebsocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.Resource;
import java.util.Map;

/**
 * author liuyd 2020/03/04
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(WebsocketProperties.class)
@ConditionalOnProperty(prefix = "spring.websocket",value = "enabled",matchIfMissing = true)
public class WebScoketAutoConfiguration {
    @Autowired
    WebsocketProperties websocketProperties;

    @Bean
    @DependsOn(value={"beanUtils"})
    public WebSocketRegister webSocketRegister(){
        return new WebSocketRegister();
    }

    @Bean
    public WebsocketProperties websocketProperties() {
        return new WebsocketProperties();
    }

    @Bean
    public BeanUtils beanUtils(){
        return new BeanUtils();
    }

    @Bean
    public WebSocketInterceptorDemo webSocketInterceptorDemo(){
        return new WebSocketInterceptorDemo();
    }


    /**
     *EnableWebSocketMessageBroker 注解表明： 这个配置类不仅配置了 WebSocket，还配置了基于代理的 STOMP 消息；
     *registerStompEndpoints() 方法：添加一个服务端点，来接收客户端的连接。将 “/chat” 路径注册为 STOMP 端点。这个路径与之前发送和接收消息的目的路径有所不同， 这是一个端点，客户端在订阅或发布消息到目的地址前，要连接该端点，即用户发送请求 ：url=’/127.0.0.1:8080/chat’ 与 STOMP server 进行连接，之后再转发到订阅url；
     *configureMessageBroker() 方法：配置了一个 简单的消息代理，通俗一点讲就是设置消息连接请求的各种规范信息。
     *
     */
    @Configuration
    @EnableWebSocketMessageBroker
    protected class WebStompConfig implements WebSocketMessageBrokerConfigurer {

        @Resource
        private WebSocketRegister webSocketRegister;
        @Resource
        private WebsocketProperties websocketProperties;

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            String endPointsStr=websocketProperties.getEndpoint();

            if(StringUtils.isEmpty(endPointsStr)){
                log.info("没有配置endpoints");
                return;
            }

            String[] endPoints=endPointsStr.split(",");
            for (String endPoint : endPoints) {
                //添加一个/chat端点，客户端就可以通过这个端点来进行连接；withSockJS作用是添加SockJS支持
                registry.addEndpoint(endPoint).setAllowedOrigins("*").withSockJS();
            }
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            String subscribePrefixsStr=websocketProperties.getSubscribePrefixs();
            if(StringUtils.isEmpty(subscribePrefixsStr)){
                log.info("没有配置subscribePrefixsStr");
                return;
            }

            if(!StringUtils.isEmpty(subscribePrefixsStr)){
                //定义了两个客户端订阅地址的前缀信息，也就是客户端接收服务端发送消息的前缀信息
                String[] subscribePrefixs=subscribePrefixsStr.split(",");
                registry.enableSimpleBroker(subscribePrefixs);
            }

            String destinationPrefixesStr=websocketProperties.getDestinationPrefixes();
            if(!StringUtils.isEmpty(destinationPrefixesStr)){
                //定义了服务端接收地址的前缀，也即客户端给服务端发消息的地址前缀
                String[] destinationPrefixes=destinationPrefixesStr.split(",");
                registry.setApplicationDestinationPrefixes(destinationPrefixes);
            }
        }

        @Override
        public void configureClientInboundChannel(ChannelRegistration registration) {
            //注册了一个接受客户端消息通道拦截器
            Map<String, WebSocketInterceptor> handlerBeanContainer= webSocketRegister.getInterceptorsBeanContainer();
            for(String key:handlerBeanContainer.keySet()){
                WebSocketInterceptor webSocketInterceptor=handlerBeanContainer.get(key);
                registration.interceptors(webSocketInterceptor);
            }
        }
    }
}
