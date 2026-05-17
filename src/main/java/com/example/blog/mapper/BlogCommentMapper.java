package com.example.blog.mapper;


import com.example.blog.entity.BlogComment;
import com.example.blog.model.dto.BlogCommentSaveDto;
import com.example.blog.model.dto.BlogCommentSaveReply;
import com.example.blog.model.dto.CommentTreeDto;
import com.example.blog.model.dto.ListCommenDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BlogCommentMapper {
    @Select("select count(*) from blog_category where is_deleted = 0")
    long count();


    List<CommentTreeDto> list(ListCommenDto listCommenDto);

    void saveComment(BlogCommentSaveDto blogCommentSaveDto);

    @Select("select * from blog_comment where comment_id = #{commentId}")
    BlogComment listEdit(Byte ParentId);

    void saveCommentReply(BlogCommentSaveReply blogCommentSaveReply);

    @Select("select * from blog_comment where parent_id = #{parentId}")
    List<CommentTreeDto> getChildren(Long parentId);

    @Select("select admin_name from admin where admin_id = #{adminId} ")
    List<BlogComment> listInto(Long adminId);
}
