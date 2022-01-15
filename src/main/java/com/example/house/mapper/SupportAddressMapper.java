package com.example.house.mapper;

import com.example.house.domain.SupportAddress;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SupportAddressMapper {
    List<SupportAddress> findAllByLevel(String level); //所有城市，所有区
    SupportAddress findByEnNameAndLevel(@Param("enName") String enName, @Param("level") String level);//具体某个城市，或者某个区
    SupportAddress findByEnNameAndBelongTo(@Param("enName") String enName, @Param("belongTo") String belongTo);//某省的某城市，或某城市的某个区
    //某省份有哪些城市，或者某城市有哪些区
    List<SupportAddress> findAllByLevelAndBelongTo(@Param("level") String level,@Param("belongTo") String belongTo);
}
