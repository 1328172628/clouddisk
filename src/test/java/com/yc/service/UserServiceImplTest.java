package com.yc.service;

import com.yc.vo.UserVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-15 14:31
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private UserService userService;


    @Test
    public void insert() {
        UserVO uv = new UserVO();
        uv.setUname("aaaaaa");
        uv.setUpwd("a");
        uv = userService.insert(uv);
        System.out.println(uv);
    }

    @Test
    public void isUnameVaild() {
        Assert.assertTrue(userService.isUnameVaild("smith"));
    }
}
