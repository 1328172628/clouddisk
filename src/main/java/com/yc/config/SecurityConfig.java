package com.yc.config;

import com.google.gson.Gson;
import com.yc.auth.DemoAuthenticationFailureHandler;
import com.yc.filter.ValidateCodeFilter;
import com.yc.service.UserServiceImpl;
import com.yc.utils.GetUserInfo;
import com.yc.utils.RedisBloom;
import com.yc.vo.JsonModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-15 16:13
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private DemoAuthenticationFailureHandler demoAuthenticationFailureHandler;


    //配置用户信息(用户名/密码，权限)
    // 该方法的主要用法
    // 1.通过Java代码的方式配置用户名/密码
    // 2.在这里完成获得数据库中的用户信息->dao类->面向接回
    // 3.密码一定要加密（加密的方式一定要和注册是加密的方式一致）
    // /4.登录认证
    @Override
    protected void configure(AuthenticationManagerBuilder auth)throws Exception {
        //Spring Security提供了BCryptPasswordEncoder类，实现Spring的PasswordEncoder接口使用BCrypt强哈希方法来加密密码。
        // auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder()
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }


    @Override //配置拦戴摸式(哪些资源震要验证)
    protected void configure(HttpSecurity http)throws Exception {
        //http.addFilterBefore(verifyCodeFilter,UsernamePasswordAuthenticationFilter.class);
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        //错误处理组件
        validateCodeFilter.setDemoAuthenticationFailureHandler(demoAuthenticationFailureHandler);
        http.headers().frameOptions().disable();
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class).formLogin()
                //定义登录页面，未登录时，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
                .loginPage("/login.html")//以默认跳装的是springsecurityl自带的意录界▣
                //默认是/Login,但是当配置了.LoginPage("/login.html"),默认值就变成了/login.html
                .loginProcessingUrl("/doLogin.action")
                 //设置登陆成功页
                .defaultSuccessUrl("/index.html")
                //定义登录时，用户名的key,默认为username
                .usernameParameter("uname")
                //定义登录时，用户密码的key,默认为password
                .passwordParameter("upwd")
                //登录成功的处理器
                .successHandler(new AuthenticationSuccessHandler(){
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException {
                        //TODO: 使用redis  存放uname 实现用户状态保持  可不可以采用布隆过滤器 防止重复存放？（问题 用户退出无法删除节点）
                        //      redis 简单实现  set ip uname
                        String uname = req.getParameter("uname");
                        String ip = GetUserInfo.getIPAddress(req);
                        Jedis jedis = RedisBloom.getRedis();
                        //jedis.set(ip, uname);
                        // 过期时间 一天
                        // TODO: 可修改 若信任 可持续保持登录状态 或者设定 状态保持时间
                        //  登录后 不应该直接设置为 期限节点  如果用户一直处于登录状态 但是节点却过期了 这是不行的
                        //                               也不是在用户退出后设置  用户主动退出 要清除缓存

                        jedis.setex(ip, 3600 * 24,uname);
                        jedis.close();
//                      HttpSession session = req.getSession();
//                      session.setAttribute("uname",uname);
                        resp.setContentType("application/json;charset=utf-8");
                        //TODO: url 一定不要写死 否则容易访问不到
                        //  这里我用frp内网穿透 代理 本地服务
                        //  公网：frp.mufeng.ml  内网：mfclouddisk  nginx ip: 192.168.2.2:80
                        //   由于 resp.sendRedirect("http:mfclouddisk/......")  写了具体地址
                        //   公网访问 orgin：frp.mufeng.ml   访问地址： http:mfclouddisk    域名不同 跨域 始终无法访问
                        //          或者出现跨域 交由前端 和 nginx 处理
                        //          前端 利用 axios.defaults.baseurl 确定访问 地址和路径
                        //          nginx 解决跨域 多服务间跨域   本地服务负载均衡
                        resp.sendRedirect("/index.html");
                        PrintWriter out = resp.getWriter();
                        out.write("success");
                        out.flush();
                    }
                })
                //登录失败的处理器
                .failureHandler(new AuthenticationFailureHandler(){
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp,  AuthenticationException exception) throws IOException {
                        exception.printStackTrace();
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        JsonModel jm = new JsonModel();
                        jm.setCode(0);
                        jm.setMsg("登录失败，用户名或密码错误");
                        Gson gson = new Gson();
                        out.write(gson.toJson(jm));
                        out.flush();
                    }
                })
                //和表单登录相关的接口统统都直接通过
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/Logout.action")
                .logoutSuccessHandler(new LogoutSuccessHandler(){
                    @Override
                    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException {

                        String ip = GetUserInfo.getIPAddress(req);
                        Jedis jedis = RedisBloom.getRedis();
                        jedis.del(ip);
                        jedis.close();

//                        HttpSession session = req.getSession();
//                        session.removeAttribute("uname");
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.write("Logout success");
                        out.flush();
                    }
                })
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .authorizeRequests()
                //开启登录配置/如果有允许匿名的urL,填在下面，这些UrL可以不经过过滤
                .antMatchers("/reg.html","/login.html","//VerifyCode",
                        "/isUnameValid.action","/reg.action","/getUname","/getFiles",
                        "/downLoadDirectory","/downLoadFile","/uploadData","/mkdir",
                        "/delete","/rename","/moveTo").permitAll()
                        // downLoad 这里一定要加进来
                //注意该处要与数据库的R0LE_后面部分保持一致，大小写也要一致
                .antMatchers("/back").hasRole("ADMIN") //表示访问/hello这个接口，需要具备ADMIN这
                .anyRequest().authenticated()//表示剩余的其他接口，任何用户登录之后就能访问
                .and()
                .csrf().disable();//关闭CsRF跨域
    }


    //直接过滤掉该地址，即该地址不走Spring Security过滤器链
    @Override       //配置Spring Security的Filter链
    public void configure(WebSecurity web)throws Exception {
        //设置拦截忽略文件夹，可以对静态资源放行
        web.ignoring().antMatchers("/css/**","/js/**","/pics/**");
    }
}
