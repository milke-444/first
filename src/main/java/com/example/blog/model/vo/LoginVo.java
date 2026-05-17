package com.example.blog.model.vo;

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

        public Long getAdminId() {
            return adminId;
        }

        public void setAdminId(Long adminId) {
            this.adminId = adminId;
        }

        public String getAdminName() {
            return adminName;
        }

        public void setAdminName(String adminName) {
            this.adminName = adminName;
        }

        public String getAdminAccount() {
            return adminAccount;
        }

        public void setAdminAccount(String adminAccount) {
            this.adminAccount = adminAccount;
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
