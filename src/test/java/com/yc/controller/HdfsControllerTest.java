package com.yc.controller;

import com.yc.vo.JsonModel;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class HdfsControllerTest {

    @Test
    void getFiles() {
        HdfsController hdfsController = new HdfsController();
        hdfsController.getFiles(new JsonModel(),"/");
    }
}