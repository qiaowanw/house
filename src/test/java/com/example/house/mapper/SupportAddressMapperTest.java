package com.example.house.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SupportAddressMapperTest {
    @Autowired
    private SupportAddressMapper supportAddressMapper;

    @Test
    void findAllByLevel() {
        supportAddressMapper.findAllByLevel("city").forEach(System.out::println);
        supportAddressMapper.findAllByLevelAndBelongTo("city","hb").forEach(System.out::println);
        System.out.println(supportAddressMapper.findByEnNameAndLevel("bj", "city"));
        System.out.println(supportAddressMapper.findByEnNameAndBelongTo("sjz", "hb"));
    }
}