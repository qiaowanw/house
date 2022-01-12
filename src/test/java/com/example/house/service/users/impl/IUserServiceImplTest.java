package com.example.house.service.users.impl;

import com.example.house.domain.User;
import com.example.house.service.users.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IUserServiceImplTest {
    @Autowired
    private IUserService iUserService;

    @Test
    void findUserByName() {
       // System.out.println(iUserService.findUserByName("admin"));
      // System.out.println(iUserService.findUserByTelephone("13466677777"));
        System.out.println(iUserService.findById(4L));
       //User user = iUserService.addUserByPhone("13051350718");
        iUserService.modifyUserProfile("email","qiaowan@google.com");
    }
}