package com.reactive.isomorphic.example.repositories;

import com.reactive.isomorphic.example.model.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("todo")
public interface TodoRepository extends MongoRepository<Todo, String> {
}
