package com.wonderprints.isomorphic.example.repositories.util;

import com.wonderprints.isomorphic.example.model.VisibilityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class VisibilityFilterRepositoryInitializer implements CommandLineRunner {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public VisibilityFilterRepositoryInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void run(String... args) {
        mongoTemplate.dropCollection("visibilityFilter");
        mongoTemplate.insert(new VisibilityFilter("show_all"), "visibilityFilter");
    }
}
