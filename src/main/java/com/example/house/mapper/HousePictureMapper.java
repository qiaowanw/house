package com.example.house.mapper;

import com.example.house.domain.HousePicture;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HousePictureMapper {
    List<HousePicture> findAllByHouseId(Long id);
    int save(List<HousePicture> housePicture);
    HousePicture findOne(Long id);
    int delete(Long id);
}
