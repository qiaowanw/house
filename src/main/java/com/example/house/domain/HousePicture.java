package com.example.house.domain;

import lombok.Data;

@Data
public class HousePicture {
    private Long id;
    private Long houseId;
    private String cdnPrefix;
    private Integer width;
    private Integer height;
    private String location;
    private String path;
}
