package com.FlatNotifier.FlatNotifier.Email;

import com.FlatNotifier.FlatNotifier.FlatOffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private static final String EMAIL_SUBJECT = "🏡Nowa oferta z OLX🏡";
    @Value("${spring.mail.username}")
    private String myEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmail(List<FlatOffer> flatOfferList) {
        log.info("starting sending mail ...");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(myEmail);
        message.setFrom(myEmail);
        message.setSubject(EMAIL_SUBJECT);

        StringBuilder emailContent = new StringBuilder();
        for (FlatOffer offer : flatOfferList) {
            emailContent.append(offer.toString()).append("\n");
        }
        message.setText(emailContent.toString());

        mailSender.send(message);
    }
}
