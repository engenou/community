package com.nowcoder.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Date;

/**
 * @author Eugen
 * @creat 2022-05-02 16:14
 */
@Data
@TableName("discuss_post")
@Document(indexName = "discusspost", type = "_doc", shards = 6, replicas = 3)
public class DiscussPost {

    // 帖子 id
    @Id
    private Integer id;

    // 用户 id
    @Field(type = FieldType.Integer)
    private Integer userId;

    // 标题
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    // 内容
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    // 分类  0-普通    1-置顶
    @Field(type = FieldType.Integer)
    private Integer type;

    // 状态  0-正常    1-精华    2-拉黑
    @Field(type = FieldType.Integer)
    private Integer status;

    // 评论数（冗余的写在这里，正确做法应该是在 comment 表里，但效率低）
    @Field(type = FieldType.Integer)
    private Integer commentCount;

    // 分数 用于计算热贴排行
    @Field(type = FieldType.Double)
    private Double score;

    // 创建时间
    @Field(type = FieldType.Date)
    private Date createTime;
}
