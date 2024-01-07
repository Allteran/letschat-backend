package io.allteran.letschatbackend.repo;

import io.allteran.letschatbackend.domain.Interest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestRepo extends MongoRepository<Interest, String> {
    Optional<Interest> findByName(String name);

    Optional<Interest> findByIdOrName(String id, String name);
}
