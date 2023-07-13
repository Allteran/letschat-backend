package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.PasswordResetToken;
import io.allteran.letschatbackend.domain.UserVerificationCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service("emailService")
@RequiredArgsConstructor
public class EmailService {
    @Value("${spring.mail.username}")
    private String EMAIL_ADDRESS_FROM;
    @Value("${email.company}")
    private String COMPANY_NAME;
    @Value("${email.verification.subject}")
    private String VERIFICATION_SUBJECT;
    @Value("${email.verification.content}")
    private Resource EMAIL_VERIFICATION_CONTENT_RESOURCE;
    @Value("${email.forgot.content}")
    private Resource FORGOT_PASSWORD_CONTENT_RESOURCE;

    @Value("${email.forgot.subject}")
    private String FORGOT_PASSWORD_SUBJECT;
    @Value("${forgot.reset.url}")
    private String PASSWORD_RESET_URL;

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(UserVerificationCode verificationCode, String username) throws MessagingException, IOException {

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(EMAIL_VERIFICATION_CONTENT_RESOURCE.getInputStream()));){
            reader.lines().forEach(builder::append);
        } catch (IOException e) {
            throw new IOException(e);
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(EMAIL_ADDRESS_FROM);
        helper.setTo(verificationCode.getUserLogin());
        helper.setSubject(VERIFICATION_SUBJECT);
        String content = builder.toString().replace("[[name]]", username);
        content = content.replace("[[VRCD]]", String.valueOf(verificationCode.getVerificationCode()));

        helper.setText(content, true);

        mailSender.send(message);
    }
    @Async
    public void sendResetPasswordLink(PasswordResetToken token, String username) throws MessagingException, IOException {
        String link = PASSWORD_RESET_URL.replace("[[EML]]", token.getUserLogin()).replace("[[TKN]]", token.getToken());

        String content = readStringFromFile(FORGOT_PASSWORD_CONTENT_RESOURCE).toString()
                .replace("[[name]]", username)
                .replace("[[LNK]]", link);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(EMAIL_ADDRESS_FROM);
        helper.setTo(token.getUserLogin());
        helper.setSubject(FORGOT_PASSWORD_SUBJECT);

        helper.setText(content, true);

        mailSender.send(message);
    }

    private StringBuilder readStringFromFile(Resource resource) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            reader.lines().forEach(builder::append);
        } catch (IOException ex) {
            throw new IOException(ex);
        }
        return builder;
    }


}
