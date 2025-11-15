package com.wonderprints.isomorphic.example.websocket;

import com.wonderprints.isomorphic.example.services.ClientMessageDecoder;
import com.wonderprints.isomorphic.react.services.RenderingService;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class ReactiveWebSocketHandler extends TextWebSocketHandler {
  private static Map<String, WebSocketSession> sessionsMap = new ConcurrentHashMap<>();
  private ClientMessageDecoder clientMessageDecoder;
  private RenderingService renderingService;
  private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

  public ReactiveWebSocketHandler(ClientMessageDecoder clientMessageDecoder, RenderingService renderingService) {
    this.clientMessageDecoder = clientMessageDecoder;
    this.renderingService = renderingService;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    var sessionId = session.getId();
    sessionsMap.put(sessionId, session);
    System.out.println("WebSocket connection established: " + sessionId);
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    var sessionId = session.getId();
    var payload = message.getPayload();
    
    virtualThreadExecutor.submit(() -> {
      try {
        var processedMessage = payload.replace("\\", "");
        if (processedMessage.length() >= 2) {
          var cleanMessage = processedMessage.substring(1, processedMessage.length() - 1);
          
          var result = clientMessageDecoder.handleMessage(cleanMessage);
          if (result != null) {
            broadcast(result);
            System.out.println("Received Message: " + result);
            
            var stateAsString = renderingService.getCurrentStateAsString();
            renderingService.setCurrentStateAsString(stateAsString);
            
            var now = Instant.now().toEpochMilli();
            renderingService.render();
            System.out.println("rendered in " + (Instant.now().toEpochMilli() - now) + " milliseconds");
          }
        }
      } catch (Exception e) {
        System.err.println("Error handling WebSocket message: " + e.getMessage());
        e.printStackTrace();
      }
    });
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    var sessionId = session.getId();
    System.out.println("Terminating WebSocket Session (client side) sig: [" + status + "] [" + sessionId + "]");
    sessionsMap.remove(sessionId);
  }

  public static void broadcast(String message) {
    sessionsMap.values().forEach(session -> {
      try {
        if (session.isOpen()) {
          session.sendMessage(new TextMessage(message));
        }
      } catch (IOException e) {
        System.err.println("Error broadcasting message: " + e.getMessage());
      }
    });
  }
}
