package com.recommendation.common.entity;

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
 * @date 2024-04-03 17:04:17
 */
@Data
@TableName("types")
public class TypesEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 类型id
	 */
	@TableId
	private Integer typeId;
	/**
	 * 类型名
	 */
	private String typeName;

}
