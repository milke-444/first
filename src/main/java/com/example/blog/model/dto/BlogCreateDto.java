package com.example.blog.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogCreateDto {

        @NotBlank(message = "博客标题不能为空")
        @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间")
        private String blogName;

        @NotBlank(message = "博客作者不能为空")
        @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间")
        private String blogGgeer;

        @NotBlank(message = "博客内容不能为空")
        @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间")
        private String blogContent;

        @NotBlank(message = "博客封面不能为空")
        private String blogCoverImage;

        private String blogCategoryName;

        @NotBlank(message = "博客标签不能为空")
        @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间")
        private String blogTags;// 标签可为空

        private Integer blogCategoryId;//博客分类id

        public @NotBlank(message = "博客标题不能为空") @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间") String getBlogName() {
                return blogName;
        }

        public void setBlogName(@NotBlank(message = "博客标题不能为空") @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间") String blogName) {
                this.blogName = blogName;
        }

        public @NotBlank(message = "博客作者不能为空") @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间") String getBlogGgeer() {
                return blogGgeer;
        }

        public void setBlogGgeer(@NotBlank(message = "博客作者不能为空") @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间") String blogGgeer) {
                this.blogGgeer = blogGgeer;
        }

        public @NotBlank(message = "博客内容不能为空") @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间") String getBlogContent() {
                return blogContent;
        }

        public void setBlogContent(@NotBlank(message = "博客内容不能为空") @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间") String blogContent) {
                this.blogContent = blogContent;
        }

        public String getBlogCategoryName() {
                return blogCategoryName;
        }

        public void setBlogCategoryName(String blogCategoryName) {
                this.blogCategoryName = blogCategoryName;
        }

        public @NotBlank(message = "博客封面不能为空") String getBlogCoverImage() {
                return blogCoverImage;
        }

        public void setBlogCoverImage(@NotBlank(message = "博客封面不能为空") String blogCoverImage) {
                this.blogCoverImage = blogCoverImage;
        }

        public @NotBlank(message = "博客标签不能为空") @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间") String getBlogTags() {
                return blogTags;
        }

        public void setBlogTags(@NotBlank(message = "博客标签不能为空") @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间") String blogTags) {
                this.blogTags = blogTags;
        }

        public Integer getBlogCategoryId() {
                return blogCategoryId;
        }

        public void setBlogCategoryId(Integer blogCategoryId) {
                this.blogCategoryId = blogCategoryId;
        }
}

