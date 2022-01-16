package com.example.house.mapper;

import com.example.house.domain.HouseTag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HouseTagMapperTest {
    @Autowired
    private HouseTagMapper houseTagMapper;

    @Test
    void findByNameAndHouseId() {
        System.out.println(houseTagMapper.findByNameAndHouseId("独立阳台", 27L));
    }

    @Test
    void findAllByHouseId() {
        houseTagMapper.findAllByHouseId(26L).forEach(System.out::println);
    }

    @Test
    void findAllByHouseIdIn() {
        List<Long> list = new ArrayList<>(List.of(26L,27L));
        houseTagMapper.findAllByHouseIdIn(list).forEach(System.out::println);
    }

    @Test
    void save() {
        HouseTag tag = new HouseTag();
        tag.setHouseId(28L);
        tag.setName("独立阳台");
        HouseTag tag1 = new HouseTag();
        tag1.setHouseId(28L);
        tag1.setName("集体供暖");
        List<HouseTag> tags = new ArrayList<>();
        tags.add(tag);
        tags.add(tag1);
        houseTagMapper.save(tags);
    }

    @Test
    void delete() {
        houseTagMapper.delete(28L);
    }
}