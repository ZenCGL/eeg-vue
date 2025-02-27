package com.recommendation.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 
 * 
 * @author ymr
 * @email u202015453@hust.edu.cn
 * @date 2024-04-01 20:37:48
 */
@Data
@TableName("users")
public class UsersEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@TableId
	@NotBlank
	private String id;
	/**
	 * 用户昵称
	 */
	private String name;
	/**
	 * 用户性别
	 */
	private String gender;
	/**
	 * 用户职业
	 */
	private String occupation;
	/**
	 * 用户年龄
	 */
	private String age;
	/**
	 * 用户密码
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Size(min = 8, max = 30)
	private String password;
	/**
	 * 用户头像
	 */
	private byte[] avatar;

}
