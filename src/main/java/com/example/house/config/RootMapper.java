package com.example.house.config;

import org.elasticsearch.index.mapper.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootMapper {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

}
