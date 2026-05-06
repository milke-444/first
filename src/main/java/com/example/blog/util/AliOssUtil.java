package com.example.blog.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Component
public class AliOssUtil {

    // 从配置文件读取配置（推荐）
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.url-prefix}")
    private String urlPrefix;

    /**
     * 上传文件到阿里云OSS
     * @param file 上传的文件
     * @param folder 存储的文件夹路径（如：images/）
     * @return 文件访问URL
     */
    public String upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 生成唯一文件名，防止覆盖
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = folder + UUID.randomUUID().toString() + fileExtension;

        OSS ossClient = null;
        try {
            // 创建OSSClient实例
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 上传文件流
            InputStream inputStream = file.getInputStream();
            ossClient.putObject(new PutObjectRequest(bucketName, fileName, inputStream));

            // 返回文件的访问URL
            return urlPrefix + fileName;

        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 上传字节数组到阿里云OSS
     * @param bytes 文件字节数组
     * @param fileName 文件名（可包含路径）
     * @return 文件访问URL
     */
    public String upload(byte[] bytes, String fileName) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("文件内容不能为空");
        }

        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 创建字节输入流
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

            // 上传文件
            ossClient.putObject(new PutObjectRequest(bucketName, fileName, inputStream));

            // 返回访问URL
            return urlPrefix + fileName;

        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 删除OSS上的文件
     * @param fileUrl 文件的完整URL
     */
    public void delete(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(urlPrefix)) {
            return;
        }

        // 从URL中提取objectName
        String objectName = fileUrl.substring(urlPrefix.length());

        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.deleteObject(bucketName, objectName);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
