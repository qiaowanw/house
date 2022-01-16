package com.example.house.mapper;

import com.example.house.domain.HouseDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HouseDetailMapper {
    HouseDetail findByHouseId(Long houseId);
    List<HouseDetail> findAllByHouseIdIn(List<Long> houseIds);
    void save(HouseDetail houseDetail);
}
