package com.yc.controller;

import com.yc.service.UserService;
import com.yc.utils.GetUserInfo;
import com.yc.utils.RedisBloom;
import com.yc.vo.JsonModel;
import com.yc.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-15 15:25
 */
@RestController
public class ResuserController {

    private Logger logger = LoggerFactory.getLogger(ResuserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/getUname", method = {RequestMethod.GET, RequestMethod.POST})
    public JsonModel getUname(JsonModel jm, HttpServletRequest req){
        //TODO: 分布式场景下  多个服务端 负载均衡调用是 可能导致 session 丢失（第二次访问到另一个服务端所以没有session了）
        //      采用 redis 缓存
        String ip = GetUserInfo.getIPAddress(req);
        Jedis jedis = RedisBloom.getRedis();
        if (!jedis.exists(ip)) {
            jm.setCode(0);
            jm.setMsg("用户未登录。。。");
        }else {
            jm.setCode(1);
            jm.setObj( jedis.get(ip) );
        }

        //String uname = String.valueOf(session.getAttribute("uname"));


//        if ("".equals(uname)) {
//            jm.setCode(0);
//            jm.setMsg("用户未登录。。。");
//        }else {
//            jm.setCode(1);
//            jm.setObj(uname);
//        }
        return jm;
    }

    @RequestMapping(value = "/isUnameValid.action", method = {RequestMethod.GET, RequestMethod.POST})
    public JsonModel isUnameValid(JsonModel jm, String uname) {
        String regExp = "^\\w{6,10}$";
        System.out.println(uname);
        if (!uname.matches(regExp)) {
            jm.setCode(0);
            jm.setMsg("用户名必须为6-10位以上的数字字母下划线组成...");
            return jm;
        }
        boolean flag = userService.isUnameVaild(uname);
        if (flag) {
            jm.setCode(1);
        } else {
            jm.setCode(0);
            jm.setMsg("用户名重复");
        }
        return jm;
    }

    @RequestMapping(value = "/reg.action", method = {RequestMethod.POST})
    public JsonModel reg(JsonModel jm, UserVO userVO) {
        userVO = userService.insert(userVO);
        jm.setCode(1);
        return jm;
    }
}
