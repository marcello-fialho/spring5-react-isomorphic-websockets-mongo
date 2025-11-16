package com.reactive.isomorphic.react.services;

import com.reactive.isomorphic.react.model.RenderingData;
import java.util.Optional;

public interface RenderingService {
    Optional<RenderingData> getRenderingData();
    RenderingData getModelOnly();
    void render();
    void render(String url);
    boolean isRendering();
    boolean tryWaitUntilRendered();
    boolean renderedPageIsStale();
    String getCurrentStateAsString();
    void setCurrentStateAsString(String currentStateAsString);
    void init();
}
