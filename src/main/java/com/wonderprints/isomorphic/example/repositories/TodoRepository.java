package com.wonderprints.isomorphic.example.repositories;

import com.wonderprints.isomorphic.example.model.Todo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository("todo")
public interface TodoRepository extends ReactiveMongoRepository<Todo, String> {
}
