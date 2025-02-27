package com.recommendation.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author ymr
 * @email u202015453@hust.edu.cn
 * @date 2024-04-01 20:51:55
 */
@Data
@TableName("reviews")
public class ReviewsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@TableId
	private String userId;
	/**
	 * 电影id
	 */
	@TableField
	private Integer movieId;
	/**
	 * 用户评分
	 */
	private Integer score;
	/**
	 * 评论时间
	 */
	private Date time;
	/**
	 * 用户评价具体内容
	 */
	private String content;

	public final static Long maxSeqLen = 50L;

}
