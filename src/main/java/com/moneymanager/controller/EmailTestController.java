package com.moneymanager.controller;

import com.moneymanager.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class EmailTestController {

    private final EmailService emailService;

    public EmailTestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    public String testEmail() {

        emailService.sendEmail(
                "srinadhkonduri2003@gmail.com",
                "Money Manager Test Email",
                "Gmail SMTP is working successfully!"
        );

        return "Email sent successfully";
    }
}
