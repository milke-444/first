package com.example.blog.mapper;

import com.example.blog.model.dto.ListDto;
import com.example.blog.entity.Blog;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BlogMapper {

    List<Blog> list(ListDto listDto);

    @Select("select count(*) from blog where is_deleted = 0")
    long count();

    @Select("select * from blog where blog_id = #{currentId}")
    Blog listEdit(Integer currentId);

    void save(Blog blog);

    void update(Blog blog);

    void logicDelete(Integer blogid);

    @Select("select * from blog where blog_id = #{blogId}")
    Blog getid(Long blogId);


    @Update("update blog set like_count = #{likeCount} where blog_id = #{blogId}")
    void updateLikeCount(Integer blogId, Long likeCount);


    @Update("update blog set like_count = like_count - 1 where blog_id = #{blogid}")
    void DeleteLikeCount(Integer blogid);

    @Select("select like_count from blog where blog_id = #{blogid}")
    Long selectLikeCount(Integer blogid);




    @Update("update blog set like_count = 0 ")
    void updateLikeCountNull();

    // BlogMapper.java
    @MapKey("id")   // 指定 Map 的 key 为 blog 的 id 字段
    Map<Integer, Blog> selectBlogMapByIds(@Param("ids") List<Integer> ids);

    @Select("select * from blog where blog_id = #{blogId}")
    Blog selectById(Integer blogId);
}
