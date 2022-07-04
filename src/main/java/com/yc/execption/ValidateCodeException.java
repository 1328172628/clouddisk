package com.yc.execption;


import org.springframework.security.core.AuthenticationException;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-15 16:50
 */
public class ValidateCodeException extends AuthenticationException {

    public ValidateCodeException(String msg){
        super(msg);
    }
}
