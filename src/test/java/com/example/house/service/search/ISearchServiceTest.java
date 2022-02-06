package com.example.house.service.search;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ISearchServiceTest {

    @Autowired
    private ISearchService searchService;

    @Test
    void index() {
        searchService.index(27L);
    }

    @Test
    void remove(){
        searchService.remove(26L);
    }
}