package com.example.blog.controller.admin;

import com.example.blog.context.BaseContext;
import com.example.blog.dto.BlogCreateDto;
import com.example.blog.dto.ListDto;
import com.example.blog.dto.updateBlogDto;
import com.example.blog.entity.Blog;
import com.example.blog.entity.PageResult;
import com.example.blog.result.Result;
import com.example.blog.service.BlogService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/blogs")
public class BlogController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/list")
    /**
     * 通过分页获得博客列表
     * 获取博客列表
     * @return
     */
    public Result list(@Valid @ModelAttribute ListDto listDto){
        PageResult<Blog> pageResult = blogService.list(listDto);

       return Result.success(pageResult);
    }

    @GetMapping("/list/edit/{blogid}")
    /**
     * 获取博客列表（编辑自己的博客）
     * @return
     */
    //TODO:后续需要加上权限验证，获取当前用户权限，然后判断当前用户权限是否可以修改该博客
    //TODO:后续为blog表增加作者id与用户id一致，方便后面的个人博客列表获取等
    public Result listEdit(@PathVariable("blogid") Integer blogid){
       Blog blog = blogService.listEdit(blogid);//通过要修改的博客id，获取该博客的修改权限
        BlogCreateDto blogCreateDto = modelMapper.map(blog,BlogCreateDto.class);
        return Result.success(blogCreateDto);
    }

    @PostMapping("/save")
    /**
     * 增加博客
     * 保存博客
     * @param blogCreateDto
     * @return
     */
    public Result save(@Valid @RequestBody BlogCreateDto blogCreateDto){
        blogService.save(blogCreateDto);
        return Result.success("保存成功");
    }

    @PostMapping ("/update")
    /**
     * 修改博客
     * @param blogCreateDto
     * @return
     */
    public Result update(@Valid @RequestBody updateBlogDto updateBlogDto){
        blogService.updateBlog(updateBlogDto);
        return Result.success("修改成功");
    }

    @DeleteMapping("/delete/{blogid}")
    /**
     * 删除博客
     * @param blogid
     * @return
     */
    public Result delete(@PathVariable("blogid") Integer blogid){
        if (blogid == null){
            return Result.failure("参数有误");
        }
        blogService.logicDelete(blogid);//逻辑删除,标记已删除，但没彻底删除数据
        return Result.success("删除成功");
    }

   @PostMapping("/likeCount/{blogid}")
    public Result likeCount(@PathVariable Integer blogid){
        return blogService.likeCount(blogid);
    }

    @GetMapping("/likeSelect/{blogid}")
    public Result selectlikeCount(@PathVariable Integer blogid){
        return blogService.selectlike(blogid);
    }


    // 后续增加点赞状态获取功能，解决前端的页面刷新重复点赞问题
    @GetMapping("/likely/{blogid}")
    public Result likely(@PathVariable Integer blogid){
      return blogService.likely(BaseContext.getCurrentId(),blogid);

    }

    //博客排行榜功能
    @GetMapping("/ranking")
    public Result ranking(){
        return blogService.ranking();
    }








}
