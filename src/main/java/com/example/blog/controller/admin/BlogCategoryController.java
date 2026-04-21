package com.example.blog.controller.admin;

import com.example.blog.dto.BlogCategorySaveDto;
import com.example.blog.dto.BlogCategoryUpdateDto;
import com.example.blog.dto.ListCommenDto;
import com.example.blog.dto.ListDto;
import com.example.blog.entity.Blog;
import com.example.blog.entity.BlogCategory;
import com.example.blog.entity.PageResult;
import com.example.blog.result.Result;
import com.example.blog.service.BlogCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/blogCategory")
public class BlogCategoryController {
    @Autowired
    private BlogCategoryService blogCategoryService;
   @GetMapping("/list")
   /**
    * 通过分页获得分类列表
    * @param listDto
    * @return
    */
   public Result list(@Valid @ModelAttribute ListDto listDto){
       PageResult<BlogCategory> pageResult = blogCategoryService.list(listDto);//获取分类列表

       return Result.success(pageResult);
   }
   @PostMapping("/save")
   /**
    * 添加分类
    * @param blogCategorySaveDto
    * @return
    */
   //TODO:后续去除分类图标，增加更具id查询对应分类下的博客。
    public Result save(@Valid @RequestBody BlogCategorySaveDto blogCategorySaveDto){
       blogCategoryService.save(blogCategorySaveDto);
       return Result.success("保存成功");

   }

   @PostMapping("/update")
   /**
    * 修改分类
    * @param blogCategorySaveDto
    * @return
    */
    public Result update(@Valid @RequestBody BlogCategoryUpdateDto blogCategoryUpdateDto){
       blogCategoryService.update(blogCategoryUpdateDto);
       return Result.success("更新成功");
   }
   @GetMapping("/delete/{CategroyId}")
    /**
    * 删除分类(逻辑删除)
    * @param categoryId
    * @return
    */
    public Result delete(@PathVariable("CategroyId") Integer categoryId){
       blogCategoryService.delete(categoryId);
       return Result.success("删除成功");
   }
   //TODO:后续添加分类恢复功能

}
