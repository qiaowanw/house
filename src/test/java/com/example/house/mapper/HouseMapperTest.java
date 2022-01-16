package com.example.house.mapper;

import com.example.house.domain.House;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HouseMapperTest {
    @Autowired
    private HouseMapper houseMapper;

    @Test
    void updateCover() {
        houseMapper.updateCover(26L,"sjfslfj");
    }

    @Test
    void updateStatus() {
        houseMapper.updateStatus(26L,1);
    }

    @Test
    void updateWatchTimes() {
        houseMapper.updateWatchTimes(26L);
    }

    @Test
    void save() {
        House house = new House();
        house.setTitle("锦绣家园");
        house.setPrice(3200);
        house.setArea(3);
        house.setRoom(2);
        house.setFloor(4);
        house.setTotalFloor(10);
        house.setBuildYear(2012);
        house.setCityEnName("sh");
        house.setRegionEnName("ptq");
        house.setCover("aaaa");
        house.setDirection(2);
        house.setDistrict("putuo");
        house.setAdminId(3L);
        house.setStreet("Dafo");
        house.setStatus(1);
        house.setCreateTime(LocalDateTime.now());
        house.setLastUpdateTime(LocalDateTime.now());
        house.setDistanceToSubway(234);
        house.setParlour(0);
        house.setBathroom(1);
        houseMapper.save(house);
    }

    @Test
    void findOne() {
        System.out.println(houseMapper.findOne(27L));
    }

    @Test
    void findAll() {
        houseMapper.findAll().forEach(System.out::println);
    }
}