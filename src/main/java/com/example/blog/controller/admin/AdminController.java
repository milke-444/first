package com.example.blog.controller.admin;

import com.example.blog.common.context.BaseContext;
import com.example.blog.model.dto.*;
import com.example.blog.entity.Admin;
import com.example.blog.common.result.Result;
import com.example.blog.service.AdminService;
import com.example.blog.model.vo.LoginVo;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminController {
    @Autowired
    private AdminService adminService;
    private static Logger log = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private ModelMapper modelMapper;//创建modelMapper，使用第三方要映射第三方，配置类的目的是把第三发功能映射到spring容器中，然后通过@Autowired来调用
    @Autowired
    private StringRedisTemplate stringRedisTemplate;//使用redistribution缓存，用于存储token
    /**后续增加功能
     * 1. 增加头像的存入
     * 2. 增加图片验证
     * 3. 完善博客评论功能
     * 4. 增加事务的管理
     * 5. aop统一日志
     * 6. 捋清楚多表的关联，如评论表后序的删除什么的都需要把关联表删除，后续画图理解。
     *
     * **/
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginDto loginDto) {

        //增加登录限流功能
       //登录成功，返回用户信息通过用户信息来生成jwt临牌
//        if(login != null){
//            Map<String,Object> claims = new HashMap<>();
//            claims.put("adminId",login.getAdminId());
//            claims.put("adminAccount",login.getAdminAccount());
//            claims.put("adminName",login.getAdminName());
//            String jwt = JwtUtil.generateJwt(claims);
//            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();//创建redis操作对象
//            operations.set(jwt,jwt,1, TimeUnit.HOURS);//
//            return Result.success("登录成功",jwt);
//
//        }
//
//        return  login!=null?Result.success(login):Result.failure("用户名或密码错误");

        //controller中不写任何的业务逻辑，只做数据接收和返回，使用trycatch来捕获异常，然后返回结果,方便后续使用统一的异常处理器处理异常
        try{
            LoginVo loginVo = adminService.adminlogin(loginDto);
            return Result.success("登录成功",loginVo);


        }catch (Exception e){
            return Result.failure("登录失败");

        }

//
    }

    @PostMapping("/register")
    public Result register(@Valid @RequestBody ResigerDto admin ){
//        log.info("用户注册:{}", admin);
        if (admin ==  null){
//            log.info("用户不能为空");
            return Result.failure("用户不能为空");

        }
       // TODO:注册功能后面要增加名称限制，不允许重复，增加全局异常处理用于处理注册失败的异常

        adminService.adminRegister(admin);

        return Result.success("注册成功");
    }
//TODO 登录后通过解析jwt来获取id然后进行各种操作，使用解析可以避免直接获取id，防止用户直接通过url进行操作，通过dto来获取数据，不用修改实体类
    @GetMapping("/getInfo")
    public Result getInfo(){
//        log.info("获取用户信息");
        Admin admin = adminService.getById();
        SelectDto selectDto = modelMapper.map(admin,SelectDto.class);//使用modelmapper映射，映射实体类到dto
        return Result.success("获取成功",selectDto);//修改为vo
    }

    @PostMapping("/UpdatePassword")
    public Result getInfoPassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto,@RequestHeader("Authorization") String  token){
        log.info("修改密码");

        Admin adminuopdate = adminService.updatePassword(updatePasswordDto.getNewPassword(),updatePasswordDto.getOldPassword());
        //修改token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String value = operations.get(token);
        if (value != null) {
            stringRedisTemplate.delete(token);
        }//删除redis中的 token
//

        SelectDto selectDto = modelMapper.map(adminuopdate,SelectDto.class);


        return Result.success("修改密码成功",selectDto);


    }

    @PostMapping("/updateName")
    public Result updateName(@Valid @RequestBody UpdateNameDto updateNameDto){
//        log.info("修改用户名");
      Admin adminuopdate =  adminService.updateName(BaseContext.getCurrentId(),updateNameDto.getNewName(),updateNameDto.getOldName());
        SelectDto selectDto = modelMapper.map(adminuopdate,SelectDto.class);
        return Result.success("修改名字成功",selectDto);



    }
    //TODO:状态的相关功能，包括禁用，启用，删除，修改，查询






}
