package com.example.blog.service.impl;

import com.example.blog.config.RedisScriptConfig;
import com.example.blog.contest.BaseContext;
import com.example.blog.dto.BlogCreateDto;
import com.example.blog.dto.ListDto;
import com.example.blog.dto.updateBlogDto;
import com.example.blog.entity.Blog;
import com.example.blog.entity.BlogCategory;
import com.example.blog.entity.PageResult;
import com.example.blog.mapper.BlogCategoryMapper;
import com.example.blog.mapper.BlogMapper;
import com.example.blog.result.Result;
import com.example.blog.service.BlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
public class BlogServiceImpl implements BlogService {
   @Autowired
   private BlogMapper blogMapper;
   @Autowired
   private BlogCategoryMapper blogCategoryMapper;
   @Autowired
   private StringRedisTemplate stringRedisTemplate;

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisScript<Long> toggleLikeScript;  // 注入刚才定义的 Bean

    @Override
    public PageResult<Blog> list(ListDto listDto) {
        long count = blogMapper.count();
        List<Blog> list  = blogMapper.list(listDto);
        PageResult<Blog> pageResult = new PageResult<>(list,count,listDto.getPage(),listDto.getPageSize());
        return pageResult;
    }

    @Override
    public Blog listEdit(Integer currentId) {
        Blog blog = blogMapper.listEdit(currentId);
        if (blog == null){
            log.info("此博客以失效");
            throw new RuntimeException("查询用户失败,请重新登录");

        }
        return blog;
    }

    @Override
    public void save(BlogCreateDto blogCreateDto) {
        BlogCategory blogCategory = blogCategoryMapper.selectById(blogCreateDto.getBlogCategoryId());//传入前端的分类id。查询分类实体中存在对应的分类名称吗
        //TODO:修改为用户不用输入分类名称
        if (blogCategory == null){
            blogCreateDto.setBlogCategoryName("默认分类");
            blogCreateDto.setBlogCategoryId(0);
        }
        else {
            blogCreateDto.setBlogCategoryName(blogCategory.getCategoryName());//把分类实体中的分类名称设置给博客实体
            //分类的排序值加1
            blogCategory.setCategoryRank(blogCategory.getCategoryRank() + 1);//分类排序值加1
        }

        Blog blog = new Blog();
        BeanUtils.copyProperties(blogCreateDto,blog);
        blog.setBlogStatus((byte) 1);
        blog.setEnableComment((byte) 1);
        blog.setIsDeleted((byte) 0);
        blog.setBlogViews(0L);
        blogMapper.save(blog);
        log.info("保存成功");

    }

    @Override
    public void updateBlog(updateBlogDto updateBlogDto) {
        Blog existingBlog = blogMapper.getid(updateBlogDto.getBlogId());
        if (existingBlog == null){
            log.info("此博客已失效");
            throw new RuntimeException("查询用户失败,请重新登录");

        }
        BlogCategory blogCategory = blogCategoryMapper.selectById(updateBlogDto.getBlogCategoryId());//传入前端的分类id。查询分类实体中存在对应的分类名称吗
        if (blogCategory == null){
            updateBlogDto.setBlogCategoryName("默认分类");
            updateBlogDto.setBlogCategoryId(0);

        }
        else {
            updateBlogDto.setBlogCategoryName(blogCategory.getCategoryName());//把分类实体中的分类名称设置给博客实体
            //分类的排序值加1
            blogCategory.setCategoryRank(blogCategory.getCategoryRank() + 1);//分类排序值加1
        }

        Blog blog = new Blog();
        BeanUtils.copyProperties(updateBlogDto,blog);//拷贝属性
//        blog.setBlogId(updateBlogDto.getBlogId());
        blog.setBlogUpdateTime(new Date());
        blogMapper.update(blog);
    }

    @Override
    public void logicDelete(Integer blogid) {
        if (blogid == null){
            log.info("删除失败");
            throw new RuntimeException("删除失败");
        }
        blogMapper.logicDelete(blogid);
    }

    //rediszset的使用，如何避免高并发，如何异步配置数据库，使用注解或连接池
    //增加lua脚本保持原子性，解决高并发和高线程下的问题，将功能分割为两个不同的模块，可以更进一步避免高并发下的问题。
    @Override
    public Result likeCount(Integer blogid) {

        Integer adminId = BaseContext.getCurrentId();
        log.info("点赞请求 - 当前线程: {}, 用户ID: {}", Thread.currentThread().getName(), adminId);
        String likeKey = "like" + blogid;           // KEYS[1]
        String ZSET_KEY = "zsetlikevalue";        // KEYS[2]// ARGV[3]
        String userId = adminId.toString();         // ARGV[1]
        String timestamp = String.valueOf(System.currentTimeMillis()); // ARGV[2]
        String zsetMember = "zsetlike" + blogid;
        Long status = stringRedisTemplate.execute(
                toggleLikeScript,
                Arrays.asList(likeKey, ZSET_KEY),     // KEYS 列表（只有2个！）
                userId, timestamp, zsetMember           // ARGV 列表（3个参数）
        );

        return status == 1 ? Result.success("点赞成功", 1)
                : Result.success("取消点赞成功", 0);

        //下面是旧的点赞功能代码，没有保证原子性，上述是用lua脚本实现的点赞代码，保证了原子性，避免了在高并发下的问题
//        Boolean likeCount = stringRedisTemplate.opsForSet().isMember(likeKey,adminId.toString());//使用redis的set集合(有序)的score方法，查看用户是否点赞
//        if (Boolean.FALSE.equals(likeCount)){
//            log.info("用户未点赞");
//           // boolean isSuccess = update().setsql("like_count = like_count + 1").eq("blogid", blogid).update();
//            stringRedisTemplate.opsForSet().add(likeKey,adminId.toString());//通过集合存储用户信息，可以避免用户重复点赞，加赞功能中没有直接操作数据库，所以没出现重复加赞的问题
//            stringRedisTemplate.opsForZSet().incrementScore(ZSET_KEY,zsetMember,System.currentTimeMillis());//添加用户点赞，使用有序集合，方便排名，按时间顺序牌
//            log.info("点赞请求 - 当前线程: {}, 用户ID: {}", Thread.currentThread().getName(), adminId);
//            return Result.success("点赞成功",1);
//
//
//        }
//        else {
//            log.info("用户已点赞");
//            //查询点赞数，不要通过数据库查数据，在多线程下计时器还没来的及写入点赞，而用户点赞被其他线程截断，就出现数据库中无数据，而进行减赞的操作，测试数据库中如果有多余的赞，会导致一个用户的点赞请求可以修改其他用户的点赞数
//            stringRedisTemplate.opsForSet().remove(likeKey,adminId.toString());
//            stringRedisTemplate.opsForZSet().remove(ZSET_KEY,zsetMember);
////            blogMapper.DeleteLikeCount(blogid);//不能直接去操作数据库，如这次，线程的互相截断导致，一个条件下多次修改数据库值，出现重复减赞,直接用redis的缓存去替换数据库的点赞值
//            log.info("点赞请求 - 当前线程: {}, 用户ID: {}", Thread.currentThread().getName(), adminId);
//            return Result.success("取消点赞成功",0);
//        }



    }

    @Override
    public Result selectlike(Integer blogid) {
        String likekey = "like" + blogid;  // 更规范的key命名

        try {
            // 1. 从 Redis 获取点赞数
            Long likeCount = stringRedisTemplate.opsForSet().size(likekey);

            // 2. 如果缓存不存在，从数据库加载
            if (likeCount == null) {
                log.info("缓存未命中，从数据库加载点赞数，blogid: {}", blogid);

                // 从数据库查询
                Long dbLikeCount = blogMapper.selectLikeCount(blogid);

                // 重要：将数据库值写入缓存（设置过期时间防止数据永久占用内存）
                if (dbLikeCount != null) {
                    // 注意：Set的size不能直接设置，需要初始化Set中的元素
                    // 方式1：如果使用String存储计数
                    stringRedisTemplate.opsForValue().set(likekey, String.valueOf(dbLikeCount), 1, TimeUnit.HOURS);

                    // 方式2：如果使用Set存储用户ID，需要从数据库加载点赞用户列表
                    // loadLikeUsersToRedis(blogid);

                    return Result.success(dbLikeCount);
                } else {
                    // 博客不存在的情况
                    return Result.failure("博客不存在");
                }
            }

            return Result.success(likeCount);

        } catch (Exception e) {
            log.error("查询点赞数失败，blogid: {}", blogid, e);
            // 降级处理：直接查数据库
            Long dbLikeCount = blogMapper.selectLikeCount(blogid);
            return Result.success(dbLikeCount);
        }
    }

    @Override
    public Result likely(Integer currentId, Integer blogid) {
        Integer adminId = BaseContext.getCurrentId();
        String likeKey = "like" +  blogid;
        Boolean likeCount = stringRedisTemplate.opsForSet().isMember(likeKey,adminId.toString());
        if (Boolean.FALSE.equals(likeCount)){
            log.info("用户未点赞");
            return Result.success("点赞成功",false);

        }
        else {
            log.info("用户已点赞");
            return Result.success("取消点赞成功",true);
        }

    }

   //TODO: 2023/9/20 创建一个redis的zset，存储点赞数，使用有序集合，方便排名,去理解其意义
    @Override
    public Result ranking() {
        // 获取前5名博客（带点赞数）
        Set<ZSetOperations.TypedTuple<String>> ranking = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores("zsetlikevalue", 0, 4);

        if (ranking == null || ranking.isEmpty()) {
            return Result.success("暂无排行榜数据");
        }

        // 提取博客ID列表（去掉前缀）
        List<Integer> blogIds = new ArrayList<>();
        Map<String, Double> scoreMap = new HashMap<>();

        for (ZSetOperations.TypedTuple<String> tuple : ranking) {
            String value = tuple.getValue();  // "zsetlike9"
            Double likes = tuple.getScore();

            // 去掉前缀，提取纯ID
            String blogIdStr = value.replace("zsetlike", "");
            Integer blogId = Integer.parseInt(blogIdStr);

            blogIds.add(blogId);
            scoreMap.put(blogIdStr, likes);
        }


        // 构建返回结果
        List<Map<String, Object>> rankingList = new ArrayList<>();
        int rank = 1;

        for (ZSetOperations.TypedTuple<String> tuple : ranking) {
            String value = tuple.getValue();
            Double likes = tuple.getScore();

            // 提取ID
            String blogIdStr = value.replace("zsetlike", "");
            Integer blogId = Integer.parseInt(blogIdStr);
            Blog blog = blogMapper.selectblogname(blogId);
            Map<String, Object> item = new HashMap<>();
            item.put("rank", rank++);;
            item.put("blogTitle", blog.getBlogName());  // 博客名称
            item.put("totalLikes", likes != null ? likes.longValue() : 0);

            rankingList.add(item);
        }

        return Result.success(rankingList);
    }

    @Scheduled(fixedDelay = 60000)//间隔1分钟执行一次
    public void chunmysql(){
        log.info("开始执行定时任务");

      Set<String> keys = stringRedisTemplate.keys("like*");//获取所有以like开头的key
        if (keys == null || keys.isEmpty()) {
            log.info("没有需要同步的数据");
            blogMapper.updateLikeCountNull();//因为我的项目不会清空redis的缓存，如果清空这个方法会导致点赞数丢失，后续优化
            return;
        }
        for (String key : keys) {
            Integer blogId = Integer.parseInt(key.substring(4));
            Long likeCount = stringRedisTemplate.opsForSet().size(key);


            log.info("博客ID：{}，点赞数：{}",blogId);
            if (blogId != null) {
                log.info("同步博客{}点赞数：{}", blogId);
                blogMapper.updateLikeCount(blogId, likeCount);

            }

        }
        log.info("定时任务结束");



    }



}
