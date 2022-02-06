package com.example.house.form;

import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatatableSearch {
    /**
     * 下面的三个属性是Datatables要求回显字段，从第几条开始显示多少条内容，前台需要，不得不加
     */
    private int draw;
    private int start;
    private int length;

    private Integer status;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createTimeMin;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createTimeMax;
    private String city;
    private String title;

    private String direction;
    //private String orderBy;
}
