package com.example.house.mapper;

import com.example.house.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void findOne(){
        System.out.println(userMapper.findOne(2L));
    }

    @Test
    void findByName(){
        System.out.println(userMapper.findByName("ben"));
        System.out.println(userMapper.findUserByPhoneNumber("13466677777"));
    }

    @Test
    void updateUserName(){
        userMapper.updateUsername(2L,"rose");
        userMapper.updateEmail(2L,"rose@google.com");
        userMapper.updatePassword(2L,"xxx");
    }
    @Test
    void save() {
        User user = new User();
        user.setName("ben");
        user.setEmail("ben@google.com");
        user.setPhoneNumber("1359999333");
        user.setPassword(passwordEncoder.encode("345678"));
        user.setStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastUpdateTime(LocalDateTime.now());
        user.setAvatar("ben.jpg");
        userMapper.save(user);
    }
}