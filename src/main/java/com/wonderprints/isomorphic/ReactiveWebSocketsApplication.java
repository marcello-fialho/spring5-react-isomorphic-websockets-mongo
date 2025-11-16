package com.wonderprints.isomorphic;


import com.wonderprints.isomorphic.react.services.RenderingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;



@SpringBootApplication
public class ReactiveWebSocketsApplication {
	private final RenderingService renderingService;

	@Autowired
	public ReactiveWebSocketsApplication(RenderingService renderingService) {
		this.renderingService = renderingService;
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactiveWebSocketsApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
  	public void render() {
		// Wait a bit to ensure RepositoryInitializer has completed
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		renderingService.init();
		renderingService.render();
	}
}
