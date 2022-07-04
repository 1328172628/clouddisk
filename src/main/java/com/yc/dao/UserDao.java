package com.yc.dao;

import com.yc.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-09 20:17
 */
public interface UserDao extends JpaRepository<User,Integer> {
}
