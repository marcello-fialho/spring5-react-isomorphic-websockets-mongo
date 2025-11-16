package com.wonderprints.isomorphic.example.repositories.util;

import com.wonderprints.isomorphic.example.model.Todo;
import com.wonderprints.isomorphic.example.model.VisibilityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class RepositoryInitializer implements CommandLineRunner {
    private final MongoTemplate mongoTemplate;

    @Value("${emptyDB:false}")
    private boolean emptyDB;

    @Autowired
    public RepositoryInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) {
        System.out.println("RepositoryInitializer: Starting database initialization...");
        // Initialize Todo collection
        mongoTemplate.dropCollection("todo");
        System.out.println("RepositoryInitializer: Dropped todo collection");
        if (!emptyDB) {
            var todos = Arrays.asList(
                new Todo("1", "Learn JavaScript", false),
                new Todo("2", "Learn React", false),
                new Todo("3", "Learn React Router", false),
                new Todo("4", "Learn Redux", false),
                new Todo("5", "Learn RxJS", false)
            );
            var ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Todo.class);
            ops.insert(todos);
            ops.execute();
            System.out.println("RepositoryInitializer: Inserted " + todos.size() + " todos");
        }

        // Initialize VisibilityFilter collection
        mongoTemplate.dropCollection("visibilityFilter");
        mongoTemplate.insert(new VisibilityFilter("show_all"), "visibilityFilter");
        System.out.println("RepositoryInitializer: Database initialization complete");
    }
}
