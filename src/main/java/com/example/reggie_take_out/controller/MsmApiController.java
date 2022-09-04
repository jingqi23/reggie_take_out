package com.example.reggie_take_out.controller;

import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.service.MsmService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msm")
public class MsmApiController {
    @Autowired
    private MsmService msmService;
//    @Autowired
//    private RedisTemplate redisTemplate;
//    @GetMapping("/sendEmail/{email}")
//    public R<String> sendEmailCode(@PathVariable String email){
//        String code=(String) redisTemplate.opsForValue().get(email);
//        if(!StringUtils.isEmpty(code)){
//            return R.success("验证码已发送");
//        }
//        code=msmService.getCode();
//        msmService.sendEmail(email, code);
//        redisTemplate.opsForValue().set(email,code,2, TimeUnit.MINUTES);
//        return R.success("验证码发送成功");
//    }
}
