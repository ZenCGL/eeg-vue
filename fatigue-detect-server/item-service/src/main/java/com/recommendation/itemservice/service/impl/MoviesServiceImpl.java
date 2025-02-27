package com.recommendation.itemservice.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.recommendation.common.utils.PageUtils;
import com.recommendation.common.utils.Query;

import com.recommendation.itemservice.dao.MoviesDao;
import com.recommendation.common.entity.MoviesEntity;
import com.recommendation.itemservice.service.MoviesService;


@Service("moviesService")
public class MoviesServiceImpl extends ServiceImpl<MoviesDao, MoviesEntity> implements MoviesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        int current = Integer.parseInt((String) params.get("page"));
        IPage<MoviesEntity> page = new Page<>(current,100);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("movie_id","title","tags");
        page = baseMapper.selectPage(page,queryWrapper);

        return new PageUtils(page);
    }
    @Override
    public List<MoviesEntity> selectMoviesByListOder(List<Integer> ids){
        return baseMapper.oderByList(ids);
    }
}