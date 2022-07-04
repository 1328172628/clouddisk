package com.yc.service;

import com.yc.vo.UserVO;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-09 20:19
 */
public interface UserService {

    /**
     * 添加用户
     * @param user
     * @return
     */
    public UserVO insert(UserVO user);

    /**
     * 判断用户是否有效
     * @param name
     * @return
     */
    public boolean isUnameVaild(String name);

}
