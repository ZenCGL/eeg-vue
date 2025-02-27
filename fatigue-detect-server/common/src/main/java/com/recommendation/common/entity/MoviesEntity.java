package com.recommendation.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 
 * 
 * @author ymr
 * @email u202015453@hust.edu.cn
 * @date 2024-04-01 16:48:09
 */
@Data
@TableName("movies")
public class MoviesEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 电影id
	 */
	@TableId(type = IdType.AUTO)
	private Integer movieId;
	/**
	 * 电影名
	 */
	private String title;
	/**
	 * 电影类别，可能有多个类别，用,划分
	 */
	private String tags;
	/**
	 * 电影简介
	 */
	private String introduction;

}
