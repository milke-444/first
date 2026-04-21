package com.example.blog.service;


import com.example.blog.dto.*;
import com.example.blog.entity.BlogCategory;
import com.example.blog.entity.BlogComment;
import com.example.blog.entity.PageResult;
import com.example.blog.result.Result;

import java.util.List;

public interface BlogCommentService {
  PageResult<CommentTreeDto> list(ListCommenDto listCommenDto, Long blogId);

    void commentSave(BlogCommentSaveDto blogCommentSaveDto);

    void commentSaveReply(BlogCommentSaveReply blogCommentSaveReply);

    List<BlogComment> listInto(Long adminId);
}
