package com.example.house.dto;

public class HouseBucketDTO {
    /**
     * 聚合bucket的key
     */
    private String key;

    /**
     * 聚合结果值
     */
    private long count;

    public HouseBucketDTO(String key, long count) {
        this.key = key;
        this.count = count;
    }
}
