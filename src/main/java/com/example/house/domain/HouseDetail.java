package com.example.house.domain;

import lombok.Data;

@Data
public class HouseDetail {
    private Integer id;
    private String description;
    private String layoutDesc;
    private String traffic;
    private String roundService;
    private Integer rentWay;
    private String address;
    private Integer subwayLineId;
    private String subwayLineName;
    private Integer subwayStationId;
    private String subwayStationName;
    private Integer houseId;
}
