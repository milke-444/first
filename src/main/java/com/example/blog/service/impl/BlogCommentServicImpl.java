package com.example.blog.service.impl;

import com.example.blog.contest.BaseContext;
import com.example.blog.dto.*;
import com.example.blog.entity.*;
import com.example.blog.mapper.AdminMapper;
import com.example.blog.mapper.BlogCommentMapper;
import com.example.blog.mapper.BlogMapper;
import com.example.blog.result.Result;
import com.example.blog.service.BlogCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BlogCommentServicImpl implements BlogCommentService {
    @Autowired
    private BlogCommentMapper blogCommentMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private AdminMapper adminMapper;
    @Override
    public PageResult<CommentTreeDto> list(ListCommenDto listCommenDto ,Long blogId) {
        //TODO:后续使用vo接收 数据返回给前端，避免数据返回给前端时，字段过多，和返回隐私数据
        // list查询的mapper可能写错了，只查询顶级评论
        log.info("开始查询第{}条数据");
        listCommenDto.setBlogId(blogId);
        List<CommentTreeDto> list  = blogCommentMapper.list(listCommenDto);
        for (CommentTreeDto commentTreeDto : list) {
            commentTreeDto.setChildren(getChildren(commentTreeDto.getCommentId()));
        }

        PageResult<CommentTreeDto> pageResult = new PageResult<>(list,blogCommentMapper.count(),listCommenDto.getPage(),listCommenDto.getPageSize());
        return pageResult;




    }

    private List<CommentTreeDto> getChildren(Long parentId){
        List<CommentTreeDto> children = blogCommentMapper.getChildren(parentId);
        for (CommentTreeDto commentTreeDto : children) {
            commentTreeDto.setChildren(getChildren(commentTreeDto.getCommentId()));
        }
        return children;
    }




    @Override
    public void commentSave(BlogCommentSaveDto blogCommentSaveDto) {
        Blog blog = blogMapper.getid(blogCommentSaveDto.getBlogId());
        if (blog ==  null){
            log.info("查询博客失败");
            throw new RuntimeException("要评论博客不存在");
        }
        Long adminId = Long.valueOf(BaseContext.getCurrentId());
        if (adminId == null) {
            throw new RuntimeException("用户未登录");
        }
        blogCommentSaveDto.setAdminId(adminId);


        blogCommentMapper.saveComment(blogCommentSaveDto);

    }

    @Override
    public void commentSaveReply(BlogCommentSaveReply blogCommentSaveReply) {
        BlogComment blogComment = blogCommentMapper.listEdit(blogCommentSaveReply.getParentId());
        log.info("查询原评论");
        if (blogComment == null){
            log.info("回复评论失败");
            throw new RuntimeException("原评论不存在");
        }
        Long adminId = Long.valueOf(BaseContext.getCurrentId());
        if (adminId == null) {
            throw new RuntimeException("用户未登录");
        }
        blogCommentSaveReply.setAdminId(adminId);
        blogCommentMapper.saveCommentReply(blogCommentSaveReply);
    }

    @Override
    public List<BlogComment> listInto(Long adminId) {
        return blogCommentMapper.listInto(adminId);

    }
}
