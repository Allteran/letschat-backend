package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.UserVerificationCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
    private String VERIFICATION_CONTENT;

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(UserVerificationCode verificationCode, String username) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(EMAIL_ADDRESS_FROM);
        helper.setTo(verificationCode.getUserLogin());
        helper.setSubject(VERIFICATION_SUBJECT);
        String content = VERIFICATION_CONTENT.replace("[[name]]", username);
        content = content.replace("[[VRCD]]", String.valueOf(verificationCode.getVerificationCode()));

        helper.setText(content, true);

        mailSender.send(message);
    }


}
