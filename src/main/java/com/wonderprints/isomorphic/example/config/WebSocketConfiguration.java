package com.wonderprints.isomorphic.example.config;

import com.wonderprints.isomorphic.example.services.ClientMessageDecoder;
import com.wonderprints.isomorphic.example.websocket.ReactiveWebSocketHandler;
import com.wonderprints.isomorphic.react.services.RenderingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    private final ClientMessageDecoder clientMessageDecoder;
    private final RenderingService renderingService;

    @Autowired
    public WebSocketConfiguration(ClientMessageDecoder clientMessageDecoder, RenderingService renderingService) {
        this.clientMessageDecoder = clientMessageDecoder;
        this.renderingService = renderingService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ReactiveWebSocketHandler(clientMessageDecoder, renderingService), "/react")
                .setAllowedOrigins("*");
    }
}
