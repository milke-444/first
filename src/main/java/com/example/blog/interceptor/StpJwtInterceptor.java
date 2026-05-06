package com.example.blog.interceptor;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StpJwtInterceptor implements StpInterface {
    // 返回一个账号所拥有的权限码集合
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (loginType.equals("admin")){
            List<String> list = new ArrayList<>();
            list.add("*");
            return list;
        }

        if (loginType.equals("user")){
            List<String> list = new ArrayList<>();
            list.add("101");
            list.add("user.add");
            list.add("user.update");
            list.add("user.get");
            // list.add("user.delete");
            list.add("art.*");
            return list;
        }
        return List.of();
    }

    // 返回一个账号所拥有的角色标识集合
    @Override
    public List<String> getRoleList(Object loginID, String loginType) {
        if (loginType.equals("admin")){
            List<String> list = new ArrayList<>();
            list.add("admin");
            return list;
        }
        if (loginType.equals("user")){
            List<String> list = new ArrayList<>();
            list.add("user");
            return list;
        }
        return List.of();
    }
}
