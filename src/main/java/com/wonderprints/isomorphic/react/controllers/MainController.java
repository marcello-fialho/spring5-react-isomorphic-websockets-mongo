package com.wonderprints.isomorphic.react.controllers;

import com.wonderprints.isomorphic.react.services.RenderingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class MainController {

    private final RenderingService renderingService;

    @Autowired
    public MainController(RenderingService renderingService) {
        this.renderingService = renderingService;
    }

    // @GetMapping("/{path:(?!react|.*.js|.*.css|.*.jpg|api).*$}")
    @RequestMapping("/")
    public String index(Map<String, Object> model)  {

        // in case we are still rendering, proceed with non-isomorphic rendering
        if (renderingService.isRendering() || renderingService.renderedPageIsStale()) {
            var rd = renderingService.getModelOnly();
            model.put("content", "");
            model.put("data", rd.data());
            model.put("spinner", " .loader { display: block } ");
            return "index";
        }

        var rd = renderingService.getRenderingData();
        if (!rd.isPresent()) { // The cache should never be empty as we trigger rendering at startup, so this is a sanity check
            return "error";
        }

        model.put("content", rd.get().content());
        model.put("data", rd.get().data());
        model.put("spinner", " .loader { display: none } ");
        return "index";
    }
}
