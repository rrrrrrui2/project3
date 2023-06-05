package com.userservice.service;

import com.common.utils.NumberUtils;
import com.userinterface.pojo.*;
import com.userservice.mapper.UserMapper;
import com.userservice.utils.CodecUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String key_prefix = "user:verify:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 校验数据是否可用
     *
     * @param data
     * @param type
     * @return
     */
    public Boolean checkUser(String data, Integer type) {
        User record = new User();
        if (type == 1) {
            record.setUsername(data);
        } else if (type == 2) {
            record.setPhone(data);
        } else {
            return null;
        }

        return this.userMapper.selectCount(record) == 0;
    }

    public void sendVerifyCode(String phone) {
        //生成验证码
        if (StringUtils.isBlank(phone)) return;
        String code = NumberUtils.generateCode(6);

        //发送消息到rabbitMQ
        HashMap<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        amqpTemplate.convertAndSend("ZP.SMS.EXCHANGE", "verifycode.sms", msg);
        //把验证码保存到redis中
        redisTemplate.opsForValue().set(key_prefix + phone, code, 5, TimeUnit.MINUTES);
    }


    public void register(User user/*, String code*/) {
        /*//校验验证码
        String redisCode = this.redisTemplate.opsForValue().get(key_prefix + user.getPhone());
        if (!StringUtils.equals(code, redisCode)) return;*/
        // 根据工具类生成随机盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);  //保存生成的盐之后登陆时还需要用到
        //根据工具类将加密后的密码放进去
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        //新增用户  将用户加入到用户表
        //用户的电话，密码，用户名均由前端传过来
        user.setId(null);  //id是自动加一的，在原有基础数据数量上
        user.setCreated(new Date()); //当前注册时间
        userMapper.insertSelective(user);

    }

    public User queryUser(String username, String password) {
        User record = new User();
        //1.现根据用户名查询用户
        //2.对用户输入的密码进行佳哑膜加密
        //3判断用户输入的密码是否正确
        record.setUsername(username);
        User user = userMapper.selectOne(record);
        if (user == null) return null;
        //用原来的盐，和当前用户输入的密码进行相同的加密，看最终的密码是否相同
        password = CodecUtils.md5Hex(password, user.getSalt());
        if (StringUtils.equals(password, user.getPassword())) {
            return user;
        }
        return null;

    }
}
