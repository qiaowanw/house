package com.example.house.service.house.impl;

import com.example.house.mapper.SupportAddressMapper;
import com.example.house.service.house.IAddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AddressServiceImplTest {
    @Autowired
    private IAddressService addressService;

    @Test
    void findAllCities() {
        System.out.println(addressService.findAllCities());
    }

    @Test
    void findAllRegionsByCityName() {
        System.out.println(addressService.findAllRegionsByCityName("sjz"));
    }

    @Test
    void findAllSubwayByCity() {
        addressService.findAllSubwayByCity("bj").forEach(System.out::println);
    }

    @Test
    void findAllStationBySubway() {
        addressService.findAllStationBySubway(4L).forEach(System.out::println);
    }

    @Test
    void findByCNR(){
        System.out.println(addressService.findCityAndRegion("bj", "cyq"));
    }
}