package com.nowcoder.community;

import com.mysql.cj.protocol.WatchableOutputStream;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPSize;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Eugen
 * @creat 2022-05-09 10:47
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString(){
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHashKey(){
        String redisKey = "test:hashkey";
        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "zs");
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));

    }

    @Test
    public void testList(){
        String redisKey = "test:list";
        redisTemplate.opsForList().leftPush(redisKey,  1);
        redisTemplate.opsForList().leftPush(redisKey,  2);
        redisTemplate.opsForList().leftPush(redisKey,  3);
        System.out.println(redisTemplate.opsForList().size(redisKey));
    }

    @Test
    public void testSet(){
        String redisKey = "test:set";
        redisTemplate.opsForSet().add(redisKey,  "a","b","c");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testZset(){
        String redisKey = "test:zset";
        redisTemplate.opsForZSet().add(redisKey,  "aa",10);
        redisTemplate.opsForZSet().add(redisKey,  "ss",20);
        redisTemplate.opsForZSet().add(redisKey,  "dd",30);
        System.out.println(redisTemplate.opsForZSet().size(redisKey));
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
    }

    @Test
    public void testKey(){
        String redisKey = "test:zset";
        redisTemplate.opsForZSet().add(redisKey,  "aa",10);
        redisTemplate.opsForZSet().add(redisKey,  "ss",20);
        redisTemplate.opsForZSet().add(redisKey,  "dd",30);
        System.out.println(redisTemplate.opsForZSet().size(redisKey));
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
    }

    // 绑定key
    @Test
    public void testBoundkey(){
        String redisKey = "test:count";
        BoundValueOperations ops = redisTemplate.boundValueOps(redisKey);
        ops.increment();
        System.out.println(ops.get());
    }

    // 编程式事务
    @Test
    public void testTx(){

        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";
                // 组队阶段
                operations.multi();
                operations.opsForSet().add(redisKey, "zhangsan");
                operations.opsForSet().add(redisKey, "lisi");

                // 执行
                return operations.exec();
            }
        });
        System.out.println(obj);
    }

}
