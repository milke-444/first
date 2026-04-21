package com.example.blog.service.impl;

import com.example.blog.dto.BlogCategorySaveDto;
import com.example.blog.dto.BlogCategoryUpdateDto;
import com.example.blog.dto.ListDto;
import com.example.blog.entity.Blog;
import com.example.blog.entity.BlogCategory;
import com.example.blog.entity.PageResult;
import com.example.blog.mapper.BlogCategoryMapper;
import com.example.blog.service.BlogCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class BlogCategoryServiceImpl implements BlogCategoryService {
    @Autowired
    private BlogCategoryMapper blogCategoryMapper;
    @Override
    public PageResult<BlogCategory> list(ListDto listDto) {
        long count = blogCategoryMapper.count();//查询总记录数
        List<BlogCategory> list  = blogCategoryMapper.list(listDto);
        PageResult<BlogCategory> pageResult = new PageResult<>(list,count,listDto.getPage(),listDto.getPageSize());


        return pageResult;
    }

    @Override
    public void save(BlogCategorySaveDto blogCategorySaveDto) {
        BlogCategory blogPanCategory = blogCategoryMapper.selectName(blogCategorySaveDto.getCategoryName());
        if (!ObjectUtils.isEmpty(blogPanCategory)){
            log.info("此分类已存在");
            throw new RuntimeException("此分类已存在");

        }
        BlogCategory blogCategory = new BlogCategory();
        BeanUtils.copyProperties(blogCategorySaveDto,blogCategory);
        blogCategory.setCategoryRank(0);
        blogCategory.setIsDeleted((byte) 0);
        blogCategory.setCreateTime(new Date());
        blogCategoryMapper.save(blogCategory);
    }

    @Override
    public void update(BlogCategoryUpdateDto blogCategoryUpdateDto) {
        BlogCategory blogPanCategory = blogCategoryMapper.selectById(blogCategoryUpdateDto.getCategoryId());
        if (blogPanCategory == null){
            log.info("此分类不存在");
            throw new RuntimeException("此分类不存在");
        }
        BlogCategory blogCategory = new BlogCategory();
        BeanUtils.copyProperties(blogCategoryUpdateDto,blogCategory);
        blogCategoryMapper.update(blogCategory);

    }

    @Override
    public void delete(Integer categoryId) {
        //TODO:后续修改为逻辑批量删除
        BlogCategory blogCategory = blogCategoryMapper.selectById(categoryId);
        if (blogCategory == null){
            log.info("此分类不存在");
            throw new RuntimeException("此分类不存在");

        }
        blogCategoryMapper.delete(categoryId);
    }
}
