package com.example.house.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HouseSubscribe {
    private Integer id;
    private Integer houseId;
    private Integer userId;
    private String desc;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime lastUpdateTime;
    private LocalDateTime orderTime;
    private String telephone;
    private Integer adminId;
}
