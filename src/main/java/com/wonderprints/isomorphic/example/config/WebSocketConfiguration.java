package com.wonderprints.isomorphic.example.config;

import com.wonderprints.isomorphic.example.services.ClientMessageDecoder;
import com.wonderprints.isomorphic.example.websocket.ReactiveWebSocketHandler;
import com.wonderprints.isomorphic.react.services.RenderingService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import java.util.HashMap;

@Configuration
public class WebSocketConfiguration {
    private final ClientMessageDecoder clientMessageDecoder;

    private final RenderingService renderingService;

    @Autowired
    public WebSocketConfiguration(ClientMessageDecoder clientMessageDecoder, RenderingService renderingService) {
        this.clientMessageDecoder = clientMessageDecoder;
        this.renderingService = renderingService;
    }

    @Bean
    WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public WebSocketHandler wsh() {
        return new ReactiveWebSocketHandler(clientMessageDecoder, renderingService);
    }

    @Bean
    public HandlerMapping hm() {
        val handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(10);
        val map = new HashMap<String, WebSocketHandler>();
        map.put("/react", wsh());
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }
}
