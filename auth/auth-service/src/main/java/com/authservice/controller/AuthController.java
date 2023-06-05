package com.authservice.controller;

import com.authcommon.pojo.UserInfo;
import com.authcommon.utils.JwtUtils;
import com.authservice.config.JwtProperties;
import com.authservice.service.AuthService;
import com.common.utils.CookieUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;
//mvc控制器
@Controller
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    //处理用户登陆认证请求，以用户名和密码为参数
    @PostMapping("accredit")
    @ApiOperation("校验用户名和密码生成cookie信息")
    public ResponseEntity<Void> accredit(@RequestParam("username") String username,
                                         @RequestParam("password") String password,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        //调用service方法生成jwt
        String token = this.authService.accredit(username, password);
        if (StringUtils.isBlank(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //使用cookieUtils.setCookie方法，将jwt类型的token设置给cookie
        CookieUtils.setCookie(request, response, jwtProperties.getCookieName(), token, jwtProperties.getExpire() * 60);
        return ResponseEntity.ok(null);
    }

    /**
     * 返回用户信息
     *
     * @param token
     * @param request
     * @param response
     * @return
     */
    //据cookie信息获取用户信息
    @GetMapping("verify")
    @ApiOperation("根据cookie信息获取用户信息，实现登录功能")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN") String token,
                                           HttpServletRequest request,
                                           HttpServletResponse response
    ) {
        try {
            UserInfo user = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            //刷新jwt中的有效时间
            token = JwtUtils.generateToken(user, jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            //刷新cookie中
            CookieUtils.setCookie(request, response, jwtProperties.getCookieName(), token, jwtProperties.getExpire() * 60);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    /**
     * 退出
     * @param token
     * @param request
     * @param response
     * @return
     */
    @GetMapping("logout")
    @ApiOperation("删除cookie信息，实现退出登录功能")
    public ResponseEntity<Void> logout(@CookieValue("LY_TOKEN") String token,
                                           HttpServletRequest request,
                                           HttpServletResponse response
    )
    {
            //刷新cookie中
            CookieUtils.setCookie(request, response, jwtProperties.getCookieName(), null, 0);
            return ResponseEntity.noContent().build();
    }

}
