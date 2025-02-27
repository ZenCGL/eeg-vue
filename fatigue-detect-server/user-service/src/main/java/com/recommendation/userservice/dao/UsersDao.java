package com.recommendation.userservice.dao;

import com.recommendation.common.entity.UsersEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author ymr
 * @email u202015453@hust.edu.cn
 * @date 2024-04-01 20:37:48
 */
@Mapper
public interface UsersDao extends BaseMapper<UsersEntity> {
	
}
