package com.example.house.service.house.impl;

import com.example.house.service.house.IQiNiuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QiNiuServiceImplTest {
    @Autowired
    private IQiNiuService qiNiuService;

    @Test
    void testUploadFile() {
        String fileName = "src/1.jpg";
        File file = new File(fileName);

        try {
            Response response = qiNiuService.uploadFile(file);
            System.out.println(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }


}