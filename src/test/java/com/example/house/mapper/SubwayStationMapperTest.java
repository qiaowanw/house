package com.example.house.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SubwayStationMapperTest {

    @Autowired
    private SubwayStationMapper subwayStationMapper;
    @Autowired
    private SubwayMapper subwayMapper;

    @Test
    void findAllBySubwayId() {
        subwayStationMapper.findAllBySubwayId(4L).forEach(System.out::println);
    }

    @Test
    void findOne() {
        System.out.println(subwayStationMapper.findOne(6L));
    }

    @Test
    void findAllByCityEnName(){
        subwayMapper.findAllByCityEnName("bj").forEach(System.out::println);
        System.out.println(subwayMapper.findOne(4L));
    }


}