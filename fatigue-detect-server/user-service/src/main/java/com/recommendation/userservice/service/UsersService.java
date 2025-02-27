package com.recommendation.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.recommendation.common.utils.PageUtils;
import com.recommendation.common.entity.UsersEntity;

import java.util.Map;

/**
 * 
 *
 * @author ymr
 * @email u202015453@hust.edu.cn
 * @date 2024-04-01 20:37:48
 */
public interface UsersService extends IService<UsersEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

