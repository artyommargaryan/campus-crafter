package com.inter.campuscrafter.repositories.mongo;

import com.inter.campuscrafter.entities.Course;
import com.inter.campuscrafter.repositories.interfaces.CourseRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseMongoRepository extends CourseRepository, MongoRepository<Course, String> {
}
