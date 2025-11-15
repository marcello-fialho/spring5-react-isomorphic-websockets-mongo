package com.wonderprints.isomorphic.example.repositories;

import com.wonderprints.isomorphic.example.model.VisibilityFilter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("visibilityFilter")
public interface VisibilityFilterRepository extends MongoRepository<VisibilityFilter, String> {
}
