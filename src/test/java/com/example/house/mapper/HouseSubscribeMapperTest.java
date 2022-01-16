package com.example.house.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HouseSubscribeMapperTest {
    @Autowired
    private HouseSubscribeMapper houseSubscribeMapper;

    @Test
    void findByHouseIdAndUserId() {
        System.out.println(houseSubscribeMapper.findByHouseIdAndUserId(26L, 2L));
    }

    @Test
    void findAllByUserIdAndStatus() {
        System.out.println(houseSubscribeMapper.findAllByUserIdAndStatus(2L, 1));
    }

    @Test
    void findByHouseIdAndAdminId() {
       houseSubscribeMapper.findByHouseIdAndAdminId(27L, 3L).forEach(System.out::println);
    }

    @Test
    void updateStatus() {
        houseSubscribeMapper.updateStatus(13L,1);
    }
}