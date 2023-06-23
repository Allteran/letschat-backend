package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.UserVerificationCode;
import io.allteran.letschatbackend.repo.UserVerificationCodeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserVerificationCodeService {
    @Value("${verification.expiration}")
    private String EXPIRATION_TIME;

    private final UserVerificationCodeRepo repo;

    private UserVerificationCode generateCode(String userLogin) {
        Random r = new Random(System.currentTimeMillis());
        long code = 100000 + r.nextInt(200000);

        UserVerificationCode verificationCode = new UserVerificationCode();
        verificationCode.setUserLogin(userLogin);
        verificationCode.setVerificationCode(code);
        verificationCode.setAttemptsCount(0);
        verificationCode.setCreationDate(new Date(System.currentTimeMillis()));

        return verificationCode;
    }

    @Transactional
    public UserVerificationCode createCode(String userEmail) {
        UserVerificationCode generatedCode = generateCode(userEmail);
        UserVerificationCode existingCode = repo.findByUserLogin(userEmail);

        long expirationSeconds = Long.parseLong(EXPIRATION_TIME);
        Date expirationDate = new Date(System.currentTimeMillis() + expirationSeconds * 1000);

        if(existingCode != null) {
            //in case when we already have user registered, but not active - regenerate verification code
            if(!new Date().before(expirationDate)) {
                existingCode.setVerificationCode(generatedCode.getVerificationCode());
                existingCode.setCreationDate(generatedCode.getCreationDate());
                return repo.save(existingCode);
            }
            return existingCode;
        }
        return repo.save(generatedCode);
    }

    @Transactional
    public boolean verify(String userLogin, long code) {
        UserVerificationCode verificationCode = repo.findByUserLogin(userLogin);
        if(verificationCode == null) {
            return false;
        }
        if(code != verificationCode.getVerificationCode()) {
            int count = verificationCode.getAttemptsCount() + 1;
            verificationCode.setAttemptsCount(count);
            return false;
        }
//        repo.deleteByUserLogin(userLogin);
        return true;
    }
}
