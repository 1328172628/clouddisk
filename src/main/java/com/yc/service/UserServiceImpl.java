package com.yc.service;

import com.yc.bean.User;
import com.yc.dao.UserDao;
import com.yc.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-15 14:08
 */
@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserDao userDao;

    //spring security 提供的一个密码加密的类
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserVO insert(UserVO user) {
        User u = new User();
        u.setUname(user.getUname());
        u.setUpwd(passwordEncoder.encode(user.getUpwd()));
        u.setRole("ROLE_ADMIN");
        u = userDao.save(u);
        user.setId(u.getUid());
        return user;
    }

    @Override
    public boolean isUnameVaild(String name) {
        User u = new User();
        u.setUname(name);
        Example<User> example = Example.of(u);
        Optional<User> optional = userDao.findOne(example);
        u = optional.orElseGet(new Supplier<User>() {
            @Override
            public User get() {
                return null;
            }
        });
        if (u == null) {
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String uname) throws UsernameNotFoundException {
        User u = new User();
        u.setUname(uname);
        Example<User> example = Example.of(u);
        Optional<User> optional = userDao.findOne(example);
        u = optional.orElseGet(new Supplier<User>() {
            @Override
            public User get() {
                return null;
            }
        });
        if (u == null) {
            return null;
        }
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(u.getRole()));
        org.springframework.security.core.userdetails.User user2 = new org.springframework.security.core.userdetails.User(u.getUname(), u.getUpwd(), authorities);
        return user2;
    }
}
