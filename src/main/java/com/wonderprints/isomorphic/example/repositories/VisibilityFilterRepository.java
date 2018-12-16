package com.wonderprints.isomorphic.example.repositories;

import com.wonderprints.isomorphic.example.model.VisibilityFilter;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository("visibilityFilter")
public interface VisibilityFilterRepository extends ReactiveMongoRepository<VisibilityFilter, String> {
}
