package com.example.blog.service;

import com.example.blog.model.dto.BlogCategorySaveDto;
import com.example.blog.model.dto.BlogCategoryUpdateDto;
import com.example.blog.model.dto.ListDto;
import com.example.blog.entity.BlogCategory;
import com.example.blog.entity.PageResult;

public interface BlogCategoryService {
    PageResult<BlogCategory> list(ListDto listDto);

    void save(BlogCategorySaveDto blogCategorySaveDto);

    void update(BlogCategoryUpdateDto blogCategoryUpdateDto);

    void delete(Integer categoryId);
}
