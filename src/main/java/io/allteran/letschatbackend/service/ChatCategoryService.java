package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.ChatCategory;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.repo.ChatCategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatCategoryService {
    private final ChatCategoryRepo repo;

    public List<ChatCategory> findAll() {
        return repo.findAll();
    }

    public ChatCategory findById(String id) {
        Optional<ChatCategory> result = repo.findById(id);
        if(result.isEmpty()) {
            throw new NotFoundException("ChatCategory not found [ID = " + id + "]");
        }
        return result.get();
    }

    public ChatCategory findByName(String name) {
        ChatCategory result = repo.findByName(name);
        if(result == null) {
            throw new NotFoundException("ChatCategory with given NAME not found [NAME = " + name + "]");
        }
        return result;
    }

    @Transactional
    public ChatCategory create(ChatCategory body) {
        ChatCategory existed = repo.findByName(body.getName().toLowerCase());
        if(existed != null) {
            throw new EntityFieldException("Name of ChatCategory should be unique");
        }
        return repo.save(body);
    }

    @Transactional
    public void delete(String id) {
        if(repo.findById(id).isEmpty()) {
            throw new NotFoundException("ChatCategory not found [ID = " + id + "]");
        }
        repo.deleteById(id);
    }


}
