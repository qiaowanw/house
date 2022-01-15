package com.example.house.domain;

import lombok.Data;

@Data
public class HousePicture {
    private Integer id;
    private Integer houseId;
    private String cdnPrefix;
    private Integer width;
    private Integer height;
    private String location;
    private String path;
}
