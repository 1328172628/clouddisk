package com.yc.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-09 20:05
 */
@Entity     //对应的数据表 user
@Data       // lombok
@AllArgsConstructor     //生成全参数构造方法
@NoArgsConstructor      //生成无参数构造方法
@ToString               //生成toString方法
@JsonIgnoreProperties(ignoreUnknown = true)      //产生json时是否忽略那些字段
public class User {
    @Id
    /** 主键生成策略 自增  */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uid;
    private String uname;
    private String upwd;
    private String role;
}
