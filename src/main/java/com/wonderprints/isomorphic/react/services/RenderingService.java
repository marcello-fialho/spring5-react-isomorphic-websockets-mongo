package com.wonderprints.isomorphic.react.services;

import com.wonderprints.isomorphic.react.model.RenderingData;
import reactor.core.publisher.Mono;
import java.util.Optional;

public interface RenderingService {
    Optional<RenderingData> getRenderingData();
    RenderingData getModelOnly();
    void render();
    void render(String url);
    boolean isRendering();
    boolean tryWaitUntilRendered();
    boolean renderedPageIsStale();
    Mono<String> getCurrentStateAsString$();
    void setCurrentStateAsString(String currentStateAsString);
    void init();
}
