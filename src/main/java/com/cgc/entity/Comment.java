package com.cgc.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

@Setter
@Getter
@ToString
public class Comment {
    private Integer id;
    private Integer userId;
    private Integer entityType;
    private Integer entityId;
    private Integer targetId;
    private String content;
    private Integer status;
    private Date createTime;
}
