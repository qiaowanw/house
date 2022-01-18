package com.example.house.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentSearch {
    private int draw;
    private int start;
    private int size;
    private int length;
    private String keywords;
    private String cityEnName;
    private String regionEnName;
    private Integer room;
    private Integer direction;
    private Integer rentWay;
    private String orderBy;
    private Integer orderDirection;

}
