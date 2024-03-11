package io.allteran.letschatbackend.repo;

import io.allteran.letschatbackend.dto.payload.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepo extends MongoRepository<ChatMessage, String> {
}
