package com.recommendation.itemservice.dao;

import com.recommendation.common.entity.MoviesEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * 
 * @author ymr
 * @email u202015453@hust.edu.cn
 * @date 2024-04-01 16:48:09
 */
@Mapper
@Repository
public interface MoviesDao extends BaseMapper<MoviesEntity> {
    List<MoviesEntity> oderByList(List<Integer> ids);
}
