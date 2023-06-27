package io.allteran.letschatbackend.repo;

import io.allteran.letschatbackend.domain.UserVerificationCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVerificationCodeRepo extends MongoRepository<UserVerificationCode, String> {
    UserVerificationCode findByUserLogin(String login);
    void deleteByUserLogin(String login);
}
