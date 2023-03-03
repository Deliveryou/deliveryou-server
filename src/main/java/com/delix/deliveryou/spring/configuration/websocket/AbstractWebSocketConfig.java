package com.delix.deliveryou.spring.configuration.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.AbstractMessageBrokerConfiguration;
import org.springframework.messaging.simp.user.SimpUserRegistry;


public class AbstractWebSocketConfig extends AbstractMessageBrokerConfiguration {
    @Override
    protected SimpUserRegistry createLocalUserRegistry(Integer order) {
        return null;
    }
}
