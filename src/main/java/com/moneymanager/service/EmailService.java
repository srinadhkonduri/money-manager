package com.moneymanager.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendWelcomeEmail(String to, String fullName){

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Welcome to Money Manager");

            String html = """
                <!DOCTYPE html>
                <html>
                <body>

                    <h2>Welcome to Money Manager, %s!</h2>

                    <p>
                        Your account has been successfully created.
                    </p>

                    <p>
                        Your account is already active.
                        You can log in and start using Money Manager.
                    </p>

                    <p>
                        Thank you for joining us!
                    </p>

                </body>
                </html>
                """.formatted(fullName);

            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Failed to send welcome email",
                    e
            );
        }

    }


    public void sendDailyRemainder(String to, String subject, String body){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        mailSender.send(mailMessage);
    }
}