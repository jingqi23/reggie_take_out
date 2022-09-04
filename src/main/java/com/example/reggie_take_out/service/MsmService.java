package com.example.reggie_take_out.service;

public interface MsmService {
    String getCode();
    void sendEmail(String email,String code);
}
