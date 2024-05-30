package com.pawar.todo.register.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@ComponentScan
@Service
public class MailService {
	
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    

    private final JavaMailSender javaMailSender;
    
    
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        logger.info("Sending Mail : {}",to);
        javaMailSender.send(message);
        logger.info("Mail Sent To : {}",to);
    }
}