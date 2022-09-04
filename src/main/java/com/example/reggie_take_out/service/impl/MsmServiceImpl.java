package com.example.reggie_take_out.service.impl;

import com.example.reggie_take_out.service.MsmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MsmServiceImpl implements MsmService {
    @Autowired
    JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sendFrom;

    @Override
    public String getCode() {
        int random=(int) (Math.random()*1000000);
        String code =String.format("%06d", random);
        return code;
    }

    @Override
    public void sendEmail(String email, String code) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("【瑞吉外卖】登录验证码");
        simpleMailMessage.setText("尊敬的："+email+"您的验证码为"+code+"有效期2分钟");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom(sendFrom);
    }
}
