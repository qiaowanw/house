package com.example.house.domain;

import lombok.Data;

@Data
public class SupportAddress {
    private Integer id;
    private String belongTo;
    private String enName;
    private String cnName;
    private String level;
    private double baiduMapLng;
    private double baiduMapLat;

    public enum Level{
        CITY("city"),REGION("region");
        private String value;

        Level(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
