package com.example.blog.service;

import com.example.blog.model.dto.BlogCreateDto;
import com.example.blog.model.dto.ListDto;
import com.example.blog.model.dto.UpdateBlogDto;
import com.example.blog.entity.Blog;
import com.example.blog.entity.PageResult;
import com.example.blog.common.result.Result;

public interface BlogService {
    PageResult<Blog> list(ListDto listDto);

    Blog listEdit(Integer currentId);

    void save(BlogCreateDto blogCreateDto);

    void updateBlog(UpdateBlogDto updateBlogDto);

    void logicDelete(Integer blogid);


    Result likeCount(Integer blogid);


    Result selectlike(Integer blogid);


    Result likely(Integer currentId, Integer blogid);

    Result ranking();
}
