package com.yc.auth;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-15 16:36
 */
@Component
public class DemoAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private Logger logger = LoggerFactory.getLogger(DemoAuthenticationFailureHandler.class);

//    @Autowired
//    private ObjectMapper objectMapper;  //json框架

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        logger.info("登录失败");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        //将异常输出成json
        //String jsonResult = objectMapper.writeValueAsString(exception.getMessage());
        //out.write(jsonResult);
        out.write(exception.getMessage());
        out.flush();
    }
}
