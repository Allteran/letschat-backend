package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.PasswordResetToken;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.repo.PasswordResetTokenRepo;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {
    @Value("${forgot.token.expiration}")
    private String EXPIRATION_TIME;
    private final PasswordResetTokenRepo repo;
    private final EmailService emailService;

    public PasswordResetToken generateToken(String userLogin) {
        PasswordResetToken resetToken = findByUser(userLogin);
        if(resetToken != null && validateToken(resetToken)) {
            return resetToken;
        }
        resetToken = new PasswordResetToken();
        String token = UUID.randomUUID().toString();


        resetToken.setToken(token);
        resetToken.setUserLogin(userLogin);

        long expirationSeconds = Long.parseLong(EXPIRATION_TIME);
        Date expirationDate = new Date(System.currentTimeMillis() + expirationSeconds * 1000);;
        resetToken.setExpireDate(expirationDate);

        return repo.save(resetToken);
    }

    public PasswordResetToken findByToken(String token) {
        PasswordResetToken resetToken = repo.findByToken(token);
        if(resetToken == null) {
            throw new NotFoundException("Search token error: can't find by given data [token=" + token + "]");
        }
        return resetToken;
    }

    public PasswordResetToken findByUser(String userLogin) {
        return repo.findByUserLogin(userLogin);
    }

    public boolean validateToken(PasswordResetToken token) {
        return new Date().before(token.getExpireDate());
    }

    public void sendResetLink(PasswordResetToken token) throws MessagingException {
        emailService.sendResetPasswordLink(token);
    }
}
