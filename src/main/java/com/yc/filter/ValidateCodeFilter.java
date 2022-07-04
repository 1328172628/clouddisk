package com.yc.filter;

import com.yc.auth.DemoAuthenticationFailureHandler;
import com.yc.execption.ValidateCodeException;
import com.yc.utils.GetUserInfo;
import com.yc.utils.RedisBloom;
import org.springframework.web.filter.OncePerRequestFilter;
import redis.clients.jedis.Jedis;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-15 16:42
 */
public class ValidateCodeFilter extends OncePerRequestFilter {

    //失败处理器
    private DemoAuthenticationFailureHandler demoAuthenticationFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("/yccloud/doLogin.action".equals(request.getRequestURI())&&"post".equalsIgnoreCase(request.getMethod())) {
            try {
                validate(request);
            } catch (ValidateCodeException e) {
                demoAuthenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        filterChain.doFilter(request,response);
    }

    private void validate(HttpServletRequest request) throws ValidateCodeException {
        //String validateCode=(String)request.getSession().getAttribute("validateCode");

        String userIp = GetUserInfo.getIPAddress(request);
        Jedis jedis = RedisBloom.getRedis();

        String userKey = userIp + "-code";

        Long ttl = jedis.ttl(userKey);
        if (ttl == -2L) {
            throw new ValidateCodeException("验证码以过期");
        }

        String validateCode = jedis.get(userKey);

        String imageCode=request.getParameter("yzm");
        if (imageCode==null || "".equalsIgnoreCase(imageCode)){
            throw new ValidateCodeException("验证码的值不能为空");
        }if (!validateCode.equalsIgnoreCase(imageCode)){
            throw new ValidateCodeException("验证码不匹配");
        }
    }


    public DemoAuthenticationFailureHandler getDemoAuthenticationFailureHandler(){
        return demoAuthenticationFailureHandler;
    }

    public void setDemoAuthenticationFailureHandler(DemoAuthenticationFailureHandler demoAuthenticationFailureHandler) {
        this.demoAuthenticationFailureHandler = demoAuthenticationFailureHandler;
    }
}
