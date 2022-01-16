package com.example.house.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class House {
    private Long id;
    private String title;
    private Integer price;
    private Integer area;
    private Integer room;
    private Integer floor;
    private Integer totalFloor;
    private Integer watchTimes;
    private Integer buildYear;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime lastUpdateTime;
    private String cityEnName;
    private String regionEnName;
    private String cover;
    private Integer direction;
    private Integer distanceToSubway;
    private Integer parlour;
    private String district;
    private Long adminId;
    private Integer bathroom;
    private String street;
}
