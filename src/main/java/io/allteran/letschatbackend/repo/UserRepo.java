package io.allteran.letschatbackend.repo;

import io.allteran.letschatbackend.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    Boolean existsByEmail(String email);
    User findByEmail(String email);
}
