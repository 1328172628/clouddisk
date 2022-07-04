package com.yc.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-15 15:27
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class JsonModel implements Serializable {
    private static final long serialVersionUID = 8236838596228742172L;
    private Integer code;
    private String msg;
    private Object obj;
    private String url;
}
