package com.example.blog.controller.admin;

import com.example.blog.result.Result;
import com.example.blog.util.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
public class FileUploadController {
    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传本地
     */
    @PostMapping("upload")
    public Result upload(MultipartFile file) throws IOException {

        String orginalFilename = file.getOriginalFilename();//获取原始文件名
        String fileName = UUID.randomUUID().toString() + orginalFilename.substring(orginalFilename.lastIndexOf("."));//生成新的文件名
       // file.transferTo(new File("C:\\yoxi\\File\\" + fileName));

        String imageUrl = aliOssUtil.upload(file, "images/");//上传文件
        return Result.success(imageUrl);//返回文件名

    }

}
