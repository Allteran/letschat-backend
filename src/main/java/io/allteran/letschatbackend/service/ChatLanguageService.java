package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.ChatLanguage;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.repo.ChatLanguageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatLanguageService {
    private final ChatLanguageRepo repo;

    public Optional<ChatLanguage> findById(String id) {
        return repo.findById(id);
    }
    public List<ChatLanguage> findAll() {
        return repo.findAll();
    }

    @Transactional
    public ChatLanguage create(ChatLanguage language) {
        if(language.getName().isBlank()) {
            throw new EntityFieldException("ChatLanguage.name not provided");
        }
        if(language.getCode().isBlank() || language.getCode().length() != 3) {
            throw new EntityFieldException("ChatLanguage.code should fit requirements of ISO 639-2: 3 symbols");
        }
        if(repo.findByName(language.getName().toLowerCase()) != null) {
            throw new EntityFieldException("ChatLanguage.name should be unique");
        }
        if(repo.findByCode(language.getCode().toUpperCase()) != null) {
            throw new EntityFieldException("ChatLanguage.code should be unique");
        }

        language.setName(language.getName());
        language.setCode(language.getCode().toUpperCase());

        return repo.save(language);
    }

}
