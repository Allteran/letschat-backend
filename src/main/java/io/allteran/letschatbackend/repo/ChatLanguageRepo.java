package io.allteran.letschatbackend.repo;

import io.allteran.letschatbackend.domain.ChatLanguage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatLanguageRepo extends MongoRepository<ChatLanguage, String> {
    ChatLanguage findByCode(String code);
    ChatLanguage findByName(String name);
}
