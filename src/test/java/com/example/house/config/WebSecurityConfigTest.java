package com.example.house.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebSecurityConfigTest {
    @Autowired
    private WebSecurityConfig webSecurityConfig;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    void passwordEncoder() {
       /* System.out.println(webSecurityConfig.passwordEncoder().encode("123456"));
        System.out.println(webSecurityConfig.passwordEncoder().encode("234567"));
        System.out.println(passwordEncoder.encode("123456"));*/
        System.out.println(passwordEncoder.encode("234567"));
      /*  BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123456"));
        System.out.println(bCryptPasswordEncoder.encode("234567"));*/
    }
}