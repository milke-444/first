package com.example.blog.service.impl;

import com.alibaba.fastjson2.JSON;
import com.example.blog.entity.AopLog;
import com.example.blog.mapper.AopLogMapper;
import com.example.blog.service.AopStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AopStorageImpl implements AopStorage {
    @Autowired
   private AopLogMapper aopLogMapper;


    @Async("logExecutor")
    @Override
    public void save(AopLog aop) {
        aop.setUserName(aopLogMapper.nameselect(aop.getUserId()));
        String params = aop.getParams();

//        log.info("保存日志的线程: {}", Thread.currentThread().getName());
        if (aop.getParams() != null)
        {
            String maskedParams = params.replaceAll(
                    "(?i)(\"[^\"]*password\"\\s*:\\s*\")([^\"]*)(\")",
                    "$1***$3"
            );
            aop.setParams(maskedParams);
        }
        aopLogMapper.save(aop);

    }
}
