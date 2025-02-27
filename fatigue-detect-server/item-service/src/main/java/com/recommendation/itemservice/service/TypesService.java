package com.recommendation.itemservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.recommendation.common.utils.PageUtils;
import com.recommendation.common.entity.TypesEntity;

import java.util.Map;

/**
 * 
 *
 * @author ymr
 * @email u202015453@hust.edu.cn
 * @date 2024-04-03 17:04:17
 */
public interface TypesService extends IService<TypesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

