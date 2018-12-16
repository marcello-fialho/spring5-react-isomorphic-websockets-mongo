package com.wonderprints.isomorphic.react.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wonderprints.isomorphic.example.repositories.TodoRepository;
import com.wonderprints.isomorphic.example.repositories.VisibilityFilterRepository;
import com.wonderprints.isomorphic.react.renderer.React;
import com.wonderprints.isomorphic.react.model.RenderingData;
import java.util.function.BiFunction;
import lombok.val;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;


public final class RenderingServiceImpl implements RenderingService {
    private final TodoRepository todoRepository;
    private final VisibilityFilterRepository visibilityFilterRepository;
    private RenderingData cache;
    private React react = new React();
    private ObjectMapper mapper = new ObjectMapper();
    private BiFunction<TodoRepository,VisibilityFilterRepository,Mono<Map<String,Object>>> stateGetter$;
    private volatile String lastRenderedStateAsString = "";
    private volatile String currentStateAsString = "";
    private Semaphore sem = new Semaphore(1);

    public synchronized void setCurrentStateAsString(String currentStateAsString) {
        this.currentStateAsString = currentStateAsString;
    }

    @Override
    public void init() {
        this.currentStateAsString = getCurrentStateAsStringSync();
    }

    public Mono<String> getCurrentStateAsString$() {
      try {
        return stateGetter$.apply(todoRepository, visibilityFilterRepository).flatMap((Map<String,Object> state) -> {
            try {
                val currentStateAsString = mapper.writeValueAsString(state);
                return Mono.just(currentStateAsString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return Mono.just("");
            }
        });
      } catch (Exception e) {
         e.printStackTrace();
         return Mono.empty();
      }
    }

    private String getCurrentStateAsStringSync() {
        val sem = new Semaphore(1);
        sem.acquireUninterruptibly();
        val currentStateAsStringBuilder = new StringBuilder();
        getCurrentStateAsString$().subscribe((String stateAsString) -> {
            currentStateAsStringBuilder.append(stateAsString);
            sem.release();
        }, (error) -> {
            error.printStackTrace();
            sem.release();
        }, sem::release);
        sem.acquireUninterruptibly();
        sem.release();
        return currentStateAsStringBuilder.toString();
    }

    public RenderingServiceImpl(BiFunction<TodoRepository,VisibilityFilterRepository,Mono<Map<String, Object>>> stateGetter$, TodoRepository todoRepositoru, VisibilityFilterRepository visibilityFilterRepository) {
        this.todoRepository = todoRepositoru;
        this.visibilityFilterRepository = visibilityFilterRepository;
        this.stateGetter$ = stateGetter$;
    }

    public void setRenderingWaitTimeout(int renderingWaitTimeout) {
        this.renderingWaitTimeout = renderingWaitTimeout;
    }

    private int renderingWaitTimeout;

    private volatile boolean rendering = false;

    @Override
    public Optional<RenderingData> getRenderingData() {
        if (cache == null) return Optional.empty();
        try {
            val acquired = sem.tryAcquire(renderingWaitTimeout, TimeUnit.MILLISECONDS);
            if (acquired) {
                sem.release();
                return Optional.of(cache);
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException e) {
            return Optional.empty();
        }
    }

    @Override
    public RenderingData getModelOnly() {
        return new RenderingData(currentStateAsString);
    }

    @Override
    public boolean tryWaitUntilRendered() {
        try {
            val acquired = sem.tryAcquire(renderingWaitTimeout, TimeUnit.MILLISECONDS);
            if (acquired) {
                sem.release();
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean renderedPageIsStale() {
        return !currentStateAsString.equals(lastRenderedStateAsString);
    }

    @Override
    public void render() {
        render("/");
    }

    @Override
    public void render(String url) {
        sem.acquireUninterruptibly();
        rendering = true;
        val req = new HashMap<String, Object>();
        req.put("location", url);
        try {
            val requestAsString = mapper.writeValueAsString(req);
            val initialStateAsString = currentStateAsString;
            lastRenderedStateAsString = initialStateAsString;
            val content = react.render(initialStateAsString, requestAsString);
            cache = new RenderingData(initialStateAsString, content);
            System.out.println("Template rendered...");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to parse json input(s)", e);
        } finally {
            sem.release();
            rendering = false;
        }
    }

    @Override
    public boolean isRendering() {
        return rendering;
    }
}
