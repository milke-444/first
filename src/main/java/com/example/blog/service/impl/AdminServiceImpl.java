package com.example.blog.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.example.blog.common.context.BaseContext;
import com.example.blog.common.globalException.UserException;
import com.example.blog.model.dto.LoginDto;
import com.example.blog.entity.Admin;
import com.example.blog.mapper.AdminMapper;
import com.example.blog.model.dto.ResigerDto;
import com.example.blog.model.vo.common.ExceptionCommon;
import com.example.blog.service.AdminService;
import com.example.blog.util.JwtUtil;
import com.example.blog.util.RedisUtil;
import com.example.blog.model.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public LoginVo adminlogin(LoginDto loginDto) {
        String md5Password = DigestUtils.md5DigestAsHex(loginDto.getAdminPassword().getBytes()).toUpperCase();//密码加密,注意数据库中是大写，记得加上touppercase
        loginDto.setAdminPassword(md5Password);
        String adminPassword = loginDto.getAdminPassword();
        String adminAccount = loginDto.getAdminAccount();
        Admin loginadmin =  adminMapper.adminlogin(adminAccount, adminPassword);
        if (loginadmin == null) {
            log.warn("登录失败，账号或密码错误: {}", adminAccount);
            throw new UserException(ExceptionCommon.USER_NOT_EXIST);
        }//查询用户
        //设置key,使用简短的key可以减少内存的消耗
        String key = "adminLogin:" + (loginadmin.getAdminId());
        //生成jwt
        Map<String,Object> claims = new HashMap<>();
        claims.put("adminId",loginadmin.getAdminId());
        claims.put("adminAccount",loginadmin.getAdminAccount());

//        String role = loginadmin.getRole();
//        StpUtil.login(loginadmin.getAdminId(),role);
        String jwt = JwtUtil.generateJwt(claims);
        //创建redis用于进行jwt的存储
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();//创建redis操作对象
        operations.set(key,jwt,1, TimeUnit.HOURS);
        //返回数据
        LoginVo loginVo = new LoginVo();
        LoginVo.UserInfo userInfo = new LoginVo.UserInfo();
        userInfo.setAdminId(loginadmin.getAdminId());
        userInfo.setAdminName(loginadmin.getAdminName());
        userInfo.setAdminAccount(loginadmin.getAdminAccount());
        loginVo.setUserInfo(userInfo);
        loginVo.setToken(jwt);
        return loginVo;

    }

    @Override
    public void adminRegister(ResigerDto admin) {
        if (admin ==  null){
            log.info("用户不能为空");
            throw new UserException(ExceptionCommon.PARAM_ERROR);//这段异常信息只有后端才能看见，使用异常处理的原因是为了让这个异常可以别用户正确看到
        }

        admin.setExitTime(new  Date());
        String md5Password = DigestUtils.md5DigestAsHex(admin.getAdminPassword().getBytes()).toUpperCase();
        admin.setAdminPassword(md5Password);
        adminMapper.adminRegister(admin);
        log.info("用户注册成功:{}", admin);




    }

    @Override
    public Admin getById() {
        //TODO:后面改进使用aop来获取id，不用每次都获取id，但是目前没有使用aop，所以还是使用这个方法，但是这个方法有漏洞，如果用户id被修改，那么就会出错，所以这个方法要加上权限控制，但是权限控制要加上aop，所以这个方法要加上权限控制，但是权限控制要加上aop，所以这个方法要加上权限控制，但是权限控制要加上aop，所以这个方法要加上权限控制
        Integer currentId = BaseContext.getCurrentId();
        if ( currentId == null){
            log.info("查询用户失败,请重新登录");
            throw new RuntimeException("查询用户失败,请重新登录");

        }

        //TODO：增加用户信息发生修改后会清除缓存
        String key = "admin:" + currentId;//拼接生成key值，更好区分
        String json = redisUtil.get(key);
        if (json == null) {
            // 缓存未命中，查询数据库
            Admin admin = adminMapper.getById(BaseContext.getCurrentId());

            if (admin == null) {
                // 用户不存在，缓存空值防止缓存穿透
                redisUtil.set(key, "NULL", 5, TimeUnit.MINUTES);
                return null;
            }

            // 缓存用户数据
            redisUtil.set(key, JSON.toJSONString(admin), 30, TimeUnit.MINUTES);
            return admin;
        } else {
            // 缓存命中，检查是否为空值缓存
            if ("NULL".equals(json)) {
                return null;
            }
            // 将JSON转换成Admin对象
            return JSON.parseObject(json, Admin.class);
        }


    }


    @Override
    public Admin updatePassword(String newPassword, String oldPassword) {
        if (BaseContext.getCurrentId() == null){
            log.info("修改密码失败,请重新登录");
            throw new UserException("修改密码失败,请重新登录",400);
        }
        Integer adminId = Math.toIntExact(BaseContext.getCurrentId());
        String md5newPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes()).toUpperCase();
        String md5oldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes()).toUpperCase();
        String selectpassword = adminMapper.selectPassword(Math.toIntExact(adminId));
        if (!md5oldPassword.equals(selectpassword)){
            log.info("修改密码失败,请重新输入原密码");
            throw new UserException("修改密码失败,请重新输入原密码",400);

        }
        if (md5newPassword.equals(md5oldPassword)){
            log.info("修改密码失败,请重新输入新密码");
            throw new UserException("修改密码失败,新旧密码不能相同",400);

        }
        adminMapper.updatePassword( adminId,md5newPassword);
        log.info("修改密码成功");
        return adminMapper.getById(adminId);


    }

    //修改了用户名称，后由于redis缓存的存在，导致修改的信息没有被显示
    @Override
    public Admin updateName(Integer currentId, String newName, String oldName) {
        if (BaseContext.getCurrentId() == null){
            log.info("修改名称失败,请重新登录");
            throw new UserException("修改名称失败,请重新登录",400);

        }
        if (newName.equals(oldName)){
            log.info("修改名称失败,请重新输入名称");
            throw new UserException("修改名称失败，新名称不能等于旧名称",400);

        }
        adminMapper.updateName(currentId,newName);
        log.info("修改名称成功");
        String key = "admin:" + currentId;//拼接生成key值，更好区分
        redisUtil.delete(key);
        return adminMapper.getById(currentId);
    }


}
