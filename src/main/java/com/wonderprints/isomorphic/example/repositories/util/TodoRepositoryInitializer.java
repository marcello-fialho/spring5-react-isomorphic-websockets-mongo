package com.wonderprints.isomorphic.example.repositories.util;

import com.wonderprints.isomorphic.example.model.Todo;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class TodoRepositoryInitializer implements CommandLineRunner {
    private final MongoTemplate mongoTemplate;

    @Value("${emptyDB:false}")
    private boolean emptyDB;

    @Autowired
    public TodoRepositoryInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void run(String... args) {
        mongoTemplate.dropCollection("todo");
        if (!emptyDB) {
            val todos = Arrays.asList(new Todo("1", "Learn JavaScript", false),
                new Todo("2", "Learn React", false),
                new Todo("3", "Learn React Router", false),
                new Todo("4", "Learn Redux", false),
                new Todo("5", "Learn RxJS", false));
            val ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Todo.class);
            ops.insert(todos);
            ops.execute();
        }
    }
}
