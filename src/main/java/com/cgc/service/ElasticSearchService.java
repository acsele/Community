package com.cgc.service;

import com.cgc.entity.DiscussPost;
import org.springframework.data.domain.Page;

import java.util.List;


public interface ElasticSearchService {

    /**
     * 向ElasticSearch中添加帖子
     *
     * @param discussPost 帖子实体对象
     */
    void saveDiscussPost(DiscussPost discussPost);

    /**
     * 通过id删除ElasticSearch中的帖子
     *
     * @param id 要删除的帖子id
     */
    void deleteDiscussPost(int id);

    /**
     * 通过关键字查询帖子
     *
     * @param keyword 关键字
     * @return 包含分页信息的帖子对象集合
     */
    List<DiscussPost> search(String keyword, int limit, int offset);
}
