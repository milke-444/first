package com.example.blog.service;

import com.example.blog.dto.BlogCategorySaveDto;
import com.example.blog.dto.BlogCategoryUpdateDto;
import com.example.blog.dto.ListCommenDto;
import com.example.blog.dto.ListDto;
import com.example.blog.entity.Blog;
import com.example.blog.entity.BlogCategory;
import com.example.blog.entity.PageResult;

public interface BlogCategoryService {
    PageResult<BlogCategory> list(ListDto listDto);

    void save(BlogCategorySaveDto blogCategorySaveDto);

    void update(BlogCategoryUpdateDto blogCategoryUpdateDto);

    void delete(Integer categoryId);
}
