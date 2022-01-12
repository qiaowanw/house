package com.example.house.service.users.impl;

import com.example.house.service.users.ISmsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ISmsServiceImplTest {

    @Autowired
    private ISmsService iSmsService;

    @Test
    void sendSms() throws IOException {
        iSmsService.sendSms("13051350718");

    }
}