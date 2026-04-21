-- 实现点赞/取消的原子切换
-- KEYS[1],keys是顶部的key值，用于快速定位key : 博客的点赞 Set Key，例如 "like:blog:1"
-- KEYS[2] : 全局的点赞排序 ZSet Key，例如 "zset:like:blog"
-- ARGV[1],key中的值 : 当前操作用户的 ID
-- ARGV[2] : 当前时间戳 (ZSet 的 score)
-- ARGV[3] : 博客的 ZSet Member，例如 "blog:1"

local like_key = KEYS[1]
local zset_key = KEYS[2]
local user_id = ARGV[1]
local timestamp = tonumber(ARGV[2])
local zset_member = ARGV[3]

-- 1. 检查用户是否已经点过赞
local is_member = redis.call('SISMEMBER', like_key, user_id)

if is_member == 1 then
    -- 2a. 点过赞：执行取消逻辑
    redis.call('SREM', like_key, user_id)
    redis.call('ZREM', zset_key, zset_member)
    return 0    -- 返回 0 代表最终状态为 未点赞
else
    -- 2b. 未点赞：执行点赞逻辑
    redis.call('SADD', like_key, user_id)
    redis.call('ZADD', zset_key, timestamp, zset_member)
    return 1    -- 返回 1 代表最终状态为 已点赞
end