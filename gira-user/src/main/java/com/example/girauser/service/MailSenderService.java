package com.example.girauser.service;

import com.example.girauser.dto.MailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {


    private final JavaMailSender mailSender;

    @Autowired
    public MailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleMail(MailDto dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getAddress());
        message.setSubject(dto.getTitle());
        message.setText(dto.getContent());
        mailSender.send(message);
    }
}
