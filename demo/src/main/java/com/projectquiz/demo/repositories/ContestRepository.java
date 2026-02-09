package com.projectquiz.demo.repositories;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.projectquiz.demo.models.Contest;

public interface ContestRepository extends MongoRepository<Contest, String> {
    List<Contest> findByIsActiveTrue();
}
