package com.example.reggie_take_out.controller;

import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.service.DishService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String s = UUID.randomUUID().toString()+suffix;
        File dir=new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(basePath+s));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(s);
    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("/image/jpeg");
            int len=0;
            byte[] bytes=new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
