package com.example.house.mapper;

import com.example.house.domain.HouseDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HouseDetailMapperTest {
    @Autowired
    private HouseDetailMapper houseDetailMapper;

    @Test
    void findByHouseId() {
        System.out.println(houseDetailMapper.findByHouseId(26L));
    }

    @Test
    void findAllByHouseIdIn() {
        List<Long> list = new ArrayList<>();
        list.add(houseDetailMapper.findByHouseId(26L).getHouseId());
        list.add(houseDetailMapper.findByHouseId(27L).getHouseId());
        houseDetailMapper.findAllByHouseIdIn(list).forEach(System.out::println);
    }

    @Test
    void save() {
        HouseDetail houseDetail = new HouseDetail();
        houseDetail.setDescription("阳面大两居室");
        houseDetail.setLayoutDesc("2室1厅");
        houseDetail.setTraffic("近地铁公交");
        houseDetail.setRoundService("大卖场");
        houseDetail.setRentWay(2);
        houseDetail.setAddress("三元桥");
        houseDetail.setSubwayLineId(4);
        houseDetail.setSubwayLineName("10号线");
        houseDetail.setSubwayStationId(51);
        houseDetail.setSubwayStationName("三元桥");
        houseDetail.setHouseId(28L);
        houseDetailMapper.save(houseDetail);
    }
}