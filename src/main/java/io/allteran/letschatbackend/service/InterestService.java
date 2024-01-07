package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.Interest;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.repo.InterestRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@DependsOn("interestRepo")
@RequiredArgsConstructor
public class InterestService {
    private final InterestRepo repo;

    public List<Interest> findAll() {
        return repo.findAll();
    }

    public Optional<Interest> findById(String id) {
        return repo.findById(id);
    }

    public Interest create(Interest i) {
        if (repo.findByName(i.getName()).isPresent()) {
            throw new EntityFieldException(String.format("Interest with name [%s] is already exist", i.getName()));
        }
        return repo.save(i);
    }

    public Optional<Interest> find(Interest interest) {
        return repo.findByIdOrName(interest.getId(), interest.getName());
    }
}
