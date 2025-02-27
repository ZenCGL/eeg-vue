package com.recommendation.itemservice.controller;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.recommendation.common.entity.TypesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recommendation.itemservice.service.TypesService;
import com.recommendation.common.utils.R;



/**
 * 
 *
 * @author ymr
 * @email u202015453@hust.edu.cn
 * @date 2024-04-03 17:04:17
 */
@RestController
@RequestMapping("itemservice/types")
public class TypesController {
    @Autowired
    private TypesService typesService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(){
        QueryWrapper<TypesEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("type_name");

        List<TypesEntity> list = typesService.list(queryWrapper);

        return R.ok().put("data", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{typeId}")
    public R info(@PathVariable("typeId") Integer typeId){
		TypesEntity types = typesService.getById(typeId);

        return R.ok().put("types", types);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody TypesEntity types){
		typesService.save(types);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody TypesEntity types){
		typesService.updateById(types);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] typeIds){
		typesService.removeByIds(Arrays.asList(typeIds));

        return R.ok();
    }

}
