package com.wonderprints.isomorphic.react.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wonderprints.isomorphic.example.repositories.TodoRepository;
import com.wonderprints.isomorphic.example.repositories.VisibilityFilterRepository;
import com.wonderprints.isomorphic.react.renderer.React;
import com.wonderprints.isomorphic.react.model.RenderingData;
import java.util.function.BiFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;

public final class RenderingServiceImpl implements RenderingService {
    private final TodoRepository todoRepository;
    private final VisibilityFilterRepository visibilityFilterRepository;
    private RenderingData cache;
    private React react = new React();
    private ObjectMapper mapper = new ObjectMapper();
    private BiFunction<TodoRepository,VisibilityFilterRepository,Map<String,Object>> stateGetter;
    private volatile String lastRenderedStateAsString = "";
    private volatile String currentStateAsString = "";
    private Semaphore sem = new Semaphore(1);
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private int renderingWaitTimeout;
    private volatile boolean rendering = false;

    public synchronized void setCurrentStateAsString(String currentStateAsString) {
        this.currentStateAsString = currentStateAsString;
    }

    @Override
    public void init() {
        this.currentStateAsString = getCurrentStateAsStringSync();
    }

    public String getCurrentStateAsString() {
        try {
            var state = stateGetter.apply(todoRepository, visibilityFilterRepository);
            return mapper.writeValueAsString(state);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getCurrentStateAsStringSync() {
        return CompletableFuture.supplyAsync(() -> getCurrentStateAsString(), virtualThreadExecutor).join();
    }

    public RenderingServiceImpl(BiFunction<TodoRepository,VisibilityFilterRepository,Map<String, Object>> stateGetter, TodoRepository todoRepository, VisibilityFilterRepository visibilityFilterRepository) {
        this.todoRepository = todoRepository;
        this.visibilityFilterRepository = visibilityFilterRepository;
        this.stateGetter = stateGetter;
    }

    public void setRenderingWaitTimeout(int renderingWaitTimeout) {
        this.renderingWaitTimeout = renderingWaitTimeout;
    }

    @Override
    public Optional<RenderingData> getRenderingData() {
        if (cache == null) return Optional.empty();
        try {
            var acquired = sem.tryAcquire(renderingWaitTimeout, TimeUnit.MILLISECONDS);
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
        return RenderingData.of(currentStateAsString);
    }

    @Override
    public boolean tryWaitUntilRendered() {
        try {
            var acquired = sem.tryAcquire(renderingWaitTimeout, TimeUnit.MILLISECONDS);
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
        try {
            // Refresh current state from database before rendering
            this.currentStateAsString = getCurrentStateAsStringSync();
            var req = new HashMap<String, Object>();
            req.put("location", url);
            var requestAsString = mapper.writeValueAsString(req);
            var initialStateAsString = currentStateAsString;
            lastRenderedStateAsString = initialStateAsString;
            var content = react.render(initialStateAsString, requestAsString);
            cache = RenderingData.of(initialStateAsString, content);
            System.out.println("Template rendered with " + mapper.readTree(initialStateAsString).get("todos").size() + " todos");
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
