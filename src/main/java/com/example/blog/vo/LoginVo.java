package com.example.blog.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class LoginVo {
    private String token;
    private UserInfo userInfo;


    @Data
    @Getter
    @Setter
    public static class UserInfo{
        private Long adminId;
        private String adminName;
        private String adminAccount;
    }

}
