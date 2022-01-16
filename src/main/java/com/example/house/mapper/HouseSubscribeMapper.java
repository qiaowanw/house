package com.example.house.mapper;

import com.example.house.domain.HouseSubscribe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HouseSubscribeMapper {
    HouseSubscribe findByHouseIdAndUserId(@Param("houseId") Long houseId, @Param("userId") Long userId);
    List<HouseSubscribe> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") int status);
    List<HouseSubscribe> findByHouseIdAndAdminId(@Param("houseId") Long houseId, @Param("adminId") Long adminId);
    void updateStatus(@Param("id") Long id, @Param("status") int status);
}
