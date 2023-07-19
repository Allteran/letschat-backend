package io.allteran.letschatbackend.repo;

import io.allteran.letschatbackend.domain.ChatCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatCategoryRepo extends MongoRepository<ChatCategory, String> {
    ChatCategory findByName(String name);
}
