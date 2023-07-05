package io.allteran.letschatbackend.repo;

import io.allteran.letschatbackend.domain.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepo extends MongoRepository<PasswordResetToken, String> {
    PasswordResetToken findByUserLogin(String login);
    PasswordResetToken findByToken(String token);
}
