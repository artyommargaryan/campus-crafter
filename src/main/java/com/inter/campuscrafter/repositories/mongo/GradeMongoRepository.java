package com.inter.campuscrafter.repositories.mongo;

import com.inter.campuscrafter.entities.Grade;
import com.inter.campuscrafter.repositories.interfaces.GradeRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeMongoRepository extends GradeRepository, MongoRepository<Grade, String> {
}
