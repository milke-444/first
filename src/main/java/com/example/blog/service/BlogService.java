package com.example.blog.service;

import com.example.blog.dto.BlogCreateDto;
import com.example.blog.dto.ListDto;
import com.example.blog.dto.updateBlogDto;
import com.example.blog.entity.Blog;
import com.example.blog.entity.PageResult;
import com.example.blog.result.Result;

public interface BlogService {
    PageResult<Blog> list(ListDto listDto);

    Blog listEdit(Integer currentId);

    void save(BlogCreateDto blogCreateDto);

    void updateBlog(updateBlogDto updateBlogDto);

    void logicDelete(Integer blogid);


    Result likeCount(Integer blogid);


    Result selectlike(Integer blogid);


    Result likely(Integer currentId, Integer blogid);

    Result ranking();
}
