package com.recommendation.itemservice.service.impl;

import com.recommendation.itemservice.dao.TypesDao;
import com.recommendation.common.entity.TypesEntity;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.recommendation.common.utils.PageUtils;
import com.recommendation.common.utils.Query;

import com.recommendation.itemservice.service.TypesService;


@Service("typesService")
public class TypesServiceImpl extends ServiceImpl<TypesDao, TypesEntity> implements TypesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<TypesEntity> page = this.page(
                new Query<TypesEntity>().getPage(params),
                new QueryWrapper<TypesEntity>()
        );

        return new PageUtils(page);
    }

}