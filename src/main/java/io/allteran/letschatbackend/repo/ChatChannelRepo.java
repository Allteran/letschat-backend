package io.allteran.letschatbackend.repo;

import io.allteran.letschatbackend.domain.ChatChannel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatChannelRepo extends MongoRepository<ChatChannel, String> {
    ChatChannel findByName(String name);

}
