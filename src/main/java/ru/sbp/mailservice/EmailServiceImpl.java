package ru.sbp.mailservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.sbp.controllers.NotificationController;

@Service
public class EmailServiceImpl implements EmailService {
    private Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String sender;

    public String sendSimpleMail(EmailDetails details)
    {
        try {
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

           // javaMailSender.send(mailMessage);
            logger.info("Mail to " + details.getRecipient() + " sent successfully");
            return "DONE";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            logger.info("Error sending message to " + details.getRecipient());
            return "ERROR";
        }
    }
}

