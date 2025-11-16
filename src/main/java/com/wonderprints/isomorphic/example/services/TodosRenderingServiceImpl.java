package com.wonderprints.isomorphic.example.services;

import com.wonderprints.isomorphic.example.model.Todo;
import com.wonderprints.isomorphic.example.model.VisibilityFilter;
import com.wonderprints.isomorphic.example.repositories.TodoRepository;
import com.wonderprints.isomorphic.example.repositories.VisibilityFilterRepository;
import com.wonderprints.isomorphic.react.model.RenderingData;
import com.wonderprints.isomorphic.react.services.RenderingService;
import com.wonderprints.isomorphic.react.services.RenderingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Service("renderingService")
public class TodosRenderingServiceImpl implements RenderingService {
  private final TodoRepository todoRepository;
  private final VisibilityFilterRepository visibilityFilterRepository;

  @Value("${rendering-wait-timeout}")
  private String renderingWaitTimeoutStr;

  private BiFunction<TodoRepository, VisibilityFilterRepository, Map<String, Object>> stateGetter = (TodoRepository todoRepository, VisibilityFilterRepository visibilityFilterRepository) -> {
    var initialState = new HashMap<String, Object>();
    var todosList = new ArrayList<Todo>(todoRepository.findAll());
    initialState.put("todos", todosList);
    var vList = new ArrayList<VisibilityFilter>(visibilityFilterRepository.findAll());
    if (!vList.isEmpty()) {
      initialState.put("visibilityFilter", vList.get(0).value());
    } else {
      initialState.put("visibilityFilter", "show_all");
    }
    return initialState;
  };

  private RenderingServiceImpl renderingServiceImpl;

  @Autowired
  public TodosRenderingServiceImpl(TodoRepository todoRepository, VisibilityFilterRepository visibilityFilterRepository) {
    this.todoRepository = todoRepository;
    this.visibilityFilterRepository = visibilityFilterRepository;
  }

  @Override
  public Optional<RenderingData> getRenderingData() {
    return renderingServiceImpl.getRenderingData();
  }

  @Override
  public RenderingData getModelOnly() {
    return renderingServiceImpl.getModelOnly();
  }

  @Override
  public boolean renderedPageIsStale() {
    return renderingServiceImpl.renderedPageIsStale();
  }

  @Override
  public String getCurrentStateAsString() {
    return renderingServiceImpl.getCurrentStateAsString();
  }

  @Override
  public void setCurrentStateAsString(String currentStateAsString) {
    renderingServiceImpl.setCurrentStateAsString(currentStateAsString);
  }

  @Override
  public void init() {
    renderingServiceImpl.init();
  }

  private BiFunction<Supplier<String>, Integer, Integer> getPropsIntegerValue = (Supplier<String> propsGetter, Integer defaultValue) -> {
    try {
      return Integer.parseInt(propsGetter.get());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  };

  private Integer getRenderingWaitTimeout() {
    return getPropsIntegerValue.apply(() -> renderingWaitTimeoutStr, 10000);
  }

  @PostConstruct
  private void start() {
    renderingServiceImpl = new RenderingServiceImpl(stateGetter, todoRepository, visibilityFilterRepository);
    renderingServiceImpl.setRenderingWaitTimeout(getRenderingWaitTimeout());
  }

  @Override
  public void render() {
    renderingServiceImpl.render();
  }

  @Override
  public void render(String url) {
    renderingServiceImpl.render(url);
  }

  @Override
  public boolean isRendering() {
    return renderingServiceImpl.isRendering();
  }

  @Override
  public boolean tryWaitUntilRendered() {
    return renderingServiceImpl.tryWaitUntilRendered();
  }

}
