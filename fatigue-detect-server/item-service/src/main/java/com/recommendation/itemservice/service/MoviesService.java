package com.recommendation.itemservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.recommendation.common.utils.PageUtils;
import com.recommendation.common.entity.MoviesEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author ymr
 * @email u202015453@hust.edu.cn
 * @date 2024-04-01 16:48:09
 */
public interface MoviesService extends IService<MoviesEntity> {

    PageUtils queryPage(Map<String, Object> params);
    public List<MoviesEntity> selectMoviesByListOder(List<Integer> ids);
}

