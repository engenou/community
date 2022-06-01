package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import io.netty.channel.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Eugen
 * @creat 2022-05-30 12:47
 */
@Service
public class DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    // 1. 将指定ip统计进UV
    public void recordUV(String ip){
        String redisKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    // 2. 统计指定日期范围的UV  :union
    public long calculateUV(Date start, Date end){
        if(start == null || end == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        // 每一天的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(key);
            // 日期 + 1
            calendar.add(Calendar.DATE, 1);
        }

        // 合并每一天的UV
        String redisKey = RedisKeyUtil.getUVKey(df.format(start), df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());

        // 返回统计的结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }


    // 3. 将指定userid计入DAU
    public void recordDAU(int userId){
        String redisKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    // 4. 统计指定日期范围内的DAU  :or运算
    public long calculateDAU(Date start, Date end){
        if(start == null || end == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        // 每一天的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String redisKey = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(redisKey.getBytes());
            // 日期 + 1
            calendar.add(Calendar.DATE, 1);
        }

        // 进行or运算，统计DAU
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(df.format(start), df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }

}
