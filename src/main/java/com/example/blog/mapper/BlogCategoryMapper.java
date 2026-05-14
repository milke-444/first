package com.example.blog.mapper;

import com.example.blog.model.dto.ListDto;
import com.example.blog.entity.BlogCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BlogCategoryMapper {
    @Select("select * from blog_category where category_id = #{categoryId} and is_deleted = 0")
    BlogCategory selectById(Integer categoryId);

    @Select("select count(*) from blog_category where is_deleted = 0")
    long count();

    List<BlogCategory> list(ListDto listDto);

    //后续增加索引来优化查询
    @Select("select * from blog_category where category_name = #{categoryName} and is_deleted = 0")
    BlogCategory selectName(String categoryName);

    void save(BlogCategory blogCategory);

    void update(BlogCategory blogCategory);


    @Update("update blog_category set is_deleted = 1 where category_id = #{categoryId}")
    void delete(Integer categoryId);




}
