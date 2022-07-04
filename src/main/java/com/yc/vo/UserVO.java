package com.yc.vo;

import lombok.Data;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-15 14:05
 */
@Data
public class UserVO {

    private Integer id;
    private String uname;
    private String upwd;
    private String role;

    private String imageCode;
}
