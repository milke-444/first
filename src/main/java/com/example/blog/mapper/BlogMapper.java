package com.example.blog.mapper;

import com.example.blog.dto.BlogCreateDto;
import com.example.blog.dto.ListDto;
import com.example.blog.entity.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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


    @Select("select blog_name from blog where blog_id in (#{blogIds})")
    Blog selectblogname(Integer blogIds);

    @Update("update blog set like_count = 0 ")
    void updateLikeCountNull();
}
