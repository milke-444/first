package com.example.blog.until;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtil {

    // 升级到0.11.x的写
        // 直接使用固定的密钥字符串
        private static final String SECRET = "lkgjjggjghhggjghjjgjlhkjhjkhkkhkhkhkhkjjkjhkhkhkhkhkj";

        // 将字符串转换为Key对象（这是关键！）
        private static final Key KEY = new SecretKeySpec(
                SECRET.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        public static String generateJwt(Map<String, Object> claims) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1小时
                    .signWith(KEY, SignatureAlgorithm.HS256)  // 使用Key对象
                    .compact();
        }

        public static Claims parseJwt(String jwt) {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)  // 使用同一个Key对象
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        }
    }
