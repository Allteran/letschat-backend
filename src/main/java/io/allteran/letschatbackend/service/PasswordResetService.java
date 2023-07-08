package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.PasswordResetToken;
import io.allteran.letschatbackend.dto.payload.ChangePasswordRequest;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.NotFoundException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserService userService;
    private final PasswordResetTokenService passwordResetTokenService;

    public String resetPassword(String userLogin) throws MessagingException {
        if(userService.findByEmail(userLogin) == null) {
            throw new NotFoundException("Reset password error: user not found");
        }
        PasswordResetToken token = passwordResetTokenService.findByUser(userLogin);
        if(token == null || !passwordResetTokenService.validateToken(token)) {
            token = passwordResetTokenService.generateToken(userLogin);
        }
        passwordResetTokenService.sendResetLink(token);
        return "SUCCESS";
    }

    public boolean validateResetPasswordToken(String token, String userLogin) {
        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);
        //in case when token is null OR not null, but user is wrong - token isn't valid
        if(passwordResetToken == null || !passwordResetToken.getUserLogin().equals(userLogin)) {
            return false;
        }
        return passwordResetTokenService.validateToken(passwordResetToken);
    }

    public boolean changePassword(ChangePasswordRequest request) throws NotFoundException, EntityFieldException {
        if(!validateResetPasswordToken(request.getPasswordResetToken(), request.getUserLogin())) {
            return false;
        }
        return userService.changePasswordFromReset(request.getUserLogin(), request.getPassword(), request.getPasswordConfirm());
    }
}
