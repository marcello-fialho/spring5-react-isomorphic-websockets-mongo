package com.wonderprints.isomorphic.example.websocket;

import com.wonderprints.isomorphic.example.services.ClientMessageDecoder;
import com.wonderprints.isomorphic.react.services.RenderingService;
import lombok.val;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReactiveWebSocketHandler implements WebSocketHandler {
  private static Map<String, FluxSink<String>> sessionsMap = new ConcurrentHashMap<>();
  private ClientMessageDecoder clientMessageDecoder;
  private RenderingService renderingService;

  public ReactiveWebSocketHandler(ClientMessageDecoder clientMessageDecoder, RenderingService renderingService) {
    this.clientMessageDecoder = clientMessageDecoder;
    this.renderingService = renderingService;
  }

  @Override
  public Mono<Void> handle(WebSocketSession webSocketSession) {
    val sessionId = webSocketSession.getId();
    // Subscribe to the inbound message flux
    webSocketSession.receive().doFinally(sig -> {
      System.out.println("Terminating WebSocket Session (client side) sig: [" + sig.name() + "] [" + sessionId + "]");
      webSocketSession.close();
      sessionsMap.remove(sessionId);  // remove the stored session id
    }).map(inMsg -> inMsg.getPayloadAsText().replace("\\", ""))
        .filter(message -> message.length() >= 2)
        .map(message -> message.substring(1, message.length() - 1))
        .flatMap(message -> clientMessageDecoder.handleMessage(message).subscribeOn(Schedulers.elastic()))
        .flatMap(message -> {
          broadcast(message);
          return Mono.just(message);
        })
        .doOnNext(message -> System.out.println("Received Message: " + message))
        .onBackpressureLatest()
        .flatMap(message -> renderingService.getCurrentStateAsString$().flatMap((String stateAsString) -> {
          renderingService.setCurrentStateAsString(stateAsString);
          return Mono.just(message);
        }))
        .flatMap(message -> {
          val now = Instant.now().toEpochMilli();
          renderingService.render();
          System.out.println("rendered in " + (Instant.now().toEpochMilli() - now) + " milliseconds");
          return Mono.just(message);
        })
        .subscribe($ -> {
        });
    return webSocketSession.send(getFlux(sessionId).map(webSocketSession::textMessage));
  }

  private Flux<String> getFlux(String sessionId) {
    return Flux.create(sink -> {
      sessionsMap.put(sessionId, sink);
      System.out.println("added one session");
    });
  }

  public static void broadcast(String message) {
    sessionsMap.values().forEach(sink -> sink.next(message));
  }
}
