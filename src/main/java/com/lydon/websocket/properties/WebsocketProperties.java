package com.lydon.websocket.properties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * author liuyd 2020/03/04
 */
@ConfigurationProperties("spring.websocket")
@ConditionalOnProperty(prefix = "spring.websocket", name = "enabled", havingValue = "true")
public class WebsocketProperties {
    private String endpoint;
    private String subscribePrefixs;
    private String destinationPrefixes;

    public String getDestinationPrefixes() {
        return destinationPrefixes;
    }

    public void setDestinationPrefixes(String destinationPrefixes) {
        this.destinationPrefixes = destinationPrefixes;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getSubscribePrefixs() {
        return subscribePrefixs;
    }

    public void setSubscribePrefixs(String subscribePrefixs) {
        this.subscribePrefixs = subscribePrefixs;
    }
}
