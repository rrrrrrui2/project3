package com.authservice.service;

import com.authcommon.pojo.UserInfo;
import com.authcommon.utils.JwtUtils;
import com.authservice.client.UserClient;
import com.authservice.config.JwtProperties;
import com.userinterface.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    public String accredit(String username, String password) {
        //1.根据用户名和密码查询
        User user = userClient.queryUser(username, password);
        //2.判断是否为空
        if (user == null) return null;
        //3.jwtUtils生成
        //用户存在，则根据用户信息生成一个UserInfo对象
        UserInfo userInfo = new UserInfo();//初始化载荷信息
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        try {
            return JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
