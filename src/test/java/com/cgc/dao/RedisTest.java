package com.cgc.dao;

import com.cgc.CommunityApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    //编程式事务
    @Test
    public void redisTest() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                //标志事务开始
                operations.multi();

                //事务相关操作
                operations.opsForValue().set("k3", "lucy");
                operations.opsForSet().add("name", "张飞");
                operations.opsForSet().add("name", "李白");
                operations.opsForSet().add("name", "关羽");
                operations.opsForSet().add("name", "项羽");
                operations.opsForSet().add("age", 100);
                operations.opsForSet().add("sex", "男");

                //事务执行过程中执行查询，为了保证数据一致性，并不会立即执行，而是会等事务执行结束后再执行查询
                System.out.println(operations.opsForSet().members("name"));

                //标志事务提交
                return operations.exec();
            }
        });

        System.out.println(obj);
    }
}

