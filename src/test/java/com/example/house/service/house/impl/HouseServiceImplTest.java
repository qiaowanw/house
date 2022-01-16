package com.example.house.service.house.impl;

import com.example.house.domain.HousePicture;
import com.example.house.form.HouseForm;
import com.example.house.form.PhotoForm;
import com.example.house.service.house.IHouseService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HouseServiceImplTest {
    @Autowired
    private IHouseService houseService;
    @Autowired
    private ModelMapper modelMapper;

    @Test
    void save() {
        HouseForm houseForm = new HouseForm();
        houseForm.setTitle("精装好房");
        houseForm.setCityEnName("bj");
        houseForm.setRegionEnName("dcq");
        houseForm.setStreet("东城区某街道");
        houseForm.setDistrict("城市花园");
        houseForm.setDetailAddress("东城区城市花园111号");
        houseForm.setRoom(2);
        houseForm.setParlour(2);
        houseForm.setFloor(10);
        houseForm.setTotalFloor(20);
        houseForm.setDirection(1);
        houseForm.setBuildYear(2020);
        houseForm.setArea(100);
        houseForm.setPrice(10000);
        houseForm.setRentWay(0);
        houseForm.setSubwayLineId(1L);
        houseForm.setSubwayStationId(5L);
        houseForm.setDistanceToSubway(1000);
        houseForm.setLayoutDesc("大2居室");
        houseForm.setRoundService("近超市");
        houseForm.setTraffic("13号线");
        houseForm.setDescription("好房待租");
        houseForm.setCover("fsafa");
        houseForm.setTags(List.of("集体供暖","独立卫生间"));

        List<PhotoForm> photos = new ArrayList<>();
        HousePicture housePicture = new HousePicture();
        housePicture.setPath("http://r5mj0bzjl.hb-bkt.clouddn.com/Fk5vj5XIpikcf12kaDuExbZ3tTKw");
        housePicture.setWidth(100);
        housePicture.setHeight(50);
        PhotoForm photo1 = modelMapper.map(housePicture, PhotoForm.class);

        HousePicture housePicture1 = new HousePicture();
        housePicture1.setPath("http://r5mj0bzjl.hb-bkt.clouddn.com/Fr1BIhjGAf9P_CIA2nMuWC-np81q");
        housePicture1.setWidth(100);
        housePicture1.setHeight(50);
        PhotoForm photo2 = modelMapper.map(housePicture1, PhotoForm.class);

        photos.add(photo1);
        photos.add(photo2);
        houseForm.setPhotos(photos);
        System.out.println(houseService.save(houseForm).getResult());
    }
}