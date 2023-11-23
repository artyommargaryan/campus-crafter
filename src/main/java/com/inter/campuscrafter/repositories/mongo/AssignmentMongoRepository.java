package com.inter.campuscrafter.repositories.mongo;

import com.inter.campuscrafter.entities.Assignment;
import com.inter.campuscrafter.repositories.interfaces.AssignmentRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentMongoRepository extends AssignmentRepository, MongoRepository<Assignment, String> {
}
