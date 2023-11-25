package com.inter.campuscrafter.repositories.mongo;

import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.repositories.interfaces.UserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMongoRepository extends UserRepository, MongoRepository<User, String> {
}
