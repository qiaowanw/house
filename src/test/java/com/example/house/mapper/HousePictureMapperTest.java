package com.example.house.mapper;

import com.example.house.domain.HousePicture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HousePictureMapperTest {
    @Autowired
    private HousePictureMapper housePictureMapper;

    @Test
    void findAllByHouseId() {
        housePictureMapper.findAllByHouseId(27L).forEach(System.out::println);
        //System.out.println(housePictureMapper.findOne(88L));
    }

    @Test
    void save() {
        HousePicture pics = new HousePicture();
        pics.setHouseId(28L);
        pics.setCdnPrefix("eee");
        pics.setWidth(80);
        pics.setHeight(40);
        pics.setLocation("celinelyle");
        pics.setPath("bbbb");

        HousePicture pics1 = new HousePicture();
        pics1.setHouseId(28L);
        pics1.setCdnPrefix("fff");
        pics1.setWidth(80);
        pics1.setHeight(40);
        pics1.setLocation("celinelyle");
        pics1.setPath("bbbb");

        List<HousePicture> housePictures = new ArrayList<>();
        housePictures.add(pics);
        housePictures.add(pics1);

        housePictureMapper.save(housePictures);
    }

    @Test
    void delete() {
        housePictureMapper.delete(90L);
        housePictureMapper.delete(91L);
    }
}