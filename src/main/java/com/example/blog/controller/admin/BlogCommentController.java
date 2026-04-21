package com.example.blog.controller.admin;

import com.example.blog.dto.*;
import com.example.blog.entity.BlogCategory;
import com.example.blog.entity.BlogComment;
import com.example.blog.entity.PageResult;
import com.example.blog.result.Result;
import com.example.blog.service.BlogCommentService;
import jakarta.validation.Valid;
import org.apache.catalina.WebResourceRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/Comment")
public class BlogCommentController {
    @Autowired
    private BlogCommentService blogCommentService;
    /**
     * 通过分页获得评论列表
     * @param \
     * @return
     */
    @GetMapping("/list/{blogId}")

        public Result list(@Valid @ModelAttribute ListCommenDto listCommenDto, @PathVariable("blogId") Long blogId){

        PageResult<CommentTreeDto> pageResult = blogCommentService.list(listCommenDto,blogId);
        return Result.success(pageResult);


        }

      //博客评论功能

    @PostMapping("/commentSave")
    public Result commentSave(@Valid @RequestBody BlogCommentSaveDto blogCommentSaveDto){
        blogCommentService.commentSave(blogCommentSaveDto);
        return Result.success("保存成功");
    }

    //评论回复功能
    @PostMapping("/commentReply")
    public Result commentReply(@Valid @RequestBody BlogCommentSaveReply blogCommentSaveReply){
        blogCommentService.commentSaveReply(blogCommentSaveReply);
        return Result.success("保存成功");
    }

    @GetMapping("/listInto/{adminId}")
    public Result listInto(@PathVariable("adminId") Long adminId){
        List<BlogComment> list = blogCommentService.listInto(adminId);
        return Result.success(list);
    }




}

