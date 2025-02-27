package com.recommendation.itemservice;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.recommendation.common.entity.MoviesEntity;
import com.recommendation.itemservice.service.MoviesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = ItemServiceApplication.class)
class ItemServiceApplicationTests {
    @Autowired
    MoviesService moviesService;

    @Test
    void contextLoads() {

        List<MoviesEntity>list = moviesService.list(new QueryWrapper<MoviesEntity>().like("tags","Children's"));
        list.forEach(System.out::println);
    }

}
