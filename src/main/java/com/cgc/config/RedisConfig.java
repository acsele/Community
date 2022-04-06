package com.cgc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 其实SpringBoot默认已经在RedisAutoConfiguration类中对redis进行了配置，但是其中有些配置不方便，所以在这里
     * 对一些不方便的配置做自定义配置
     *
     * @param redisConnectionFactory 是用来创建连接对象的，就像mysql中的数据库连接对象一样，只有先连接上数据库才能进行数据库相关操作
     * @return 使用自定义配置的redisTemplate模板
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //设置序列化方式
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        //使自定义配置生效
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
