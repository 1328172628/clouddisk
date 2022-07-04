package com.yc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-12 15:54
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HdfsServiceImplTest {

    @Autowired
    private HdfsService hdfsService;

    @Test
    public void testGetConfigurationInfoAsMap() throws Exception {
        Map<String, String> map = hdfsService.getConfigurationInfoAsMap();
        map.forEach((key,value)->{
            System.out.println(key + " : " + value);
        });
    }

    @Test
    public void testMkdir() throws Exception {
        boolean b = hdfsService.mkdir("/b");
        System.out.println(b);
    }

    @Test
    public void testExist() throws Exception {
        System.out.println(hdfsService.existFile("/test"));
    }

    @Test
    public void testReadPathInfo() throws Exception {
        List<Map<String, Object>> list = hdfsService.readPathInfo("/");
        list.forEach(System.out::println);
    }

    @Test
    public void testListStatus() throws Exception {
        List<Map<String, String>> maps = hdfsService.liatStatus("/");
        maps.forEach(System.out::println);
    }

}
