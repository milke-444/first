package com.example.blog.service.impl;

import com.example.blog.common.context.BaseContext;
import com.example.blog.entity.*;
import com.example.blog.mapper.AdminMapper;
import com.example.blog.mapper.BlogCommentMapper;
import com.example.blog.mapper.BlogMapper;
import com.example.blog.service.BlogCommentService;
import com.example.blog.model.dto.BlogCommentSaveDto;
import com.example.blog.model.dto.BlogCommentSaveReply;
import com.example.blog.model.dto.CommentTreeDto;
import com.example.blog.model.dto.ListCommenDto;
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
    public PageResult<CommentTreeDto> list(ListCommenDto listCommenDto , Long blogId) {
        //TODO:后续使用vo接收 数据返回给前端，避免数据返回给前端时，字段过多，和返回隐私数据
        // list查询的mapper可能写错了，只查询顶级评论
        log.info("开始查询第{}条数据");
        listCommenDto.setBlogId(blogId);//设置查询的博客id,使用分页的形式查询
        List<CommentTreeDto> list  = blogCommentMapper.list(listCommenDto);//将查询所有的顶级评论
        //遍历全部的评论，获取其中评论的子评论
        for (CommentTreeDto commentTreeDto : list) {
            //获取所有顶层评论的id，传给获取子评论的方法，获取子评论
            commentTreeDto.setChildren(getChildren(commentTreeDto.getCommentId()));
        }

        //创建分页结果，分页显示评论数据
        PageResult<CommentTreeDto> pageResult = new PageResult<>(list,blogCommentMapper.count(),listCommenDto.getPage(),listCommenDto.getPageSize());
        return pageResult;




    }

    private List<CommentTreeDto> getChildren(Long parentId){
        //查询所有的
        List<CommentTreeDto> children = blogCommentMapper.getChildren(parentId);//查询所有的子评论
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
