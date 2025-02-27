package com.recommendation.itemservice.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommendation.itemservice.feign.ReviewFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.recommendation.common.entity.MoviesEntity;
import com.recommendation.itemservice.service.MoviesService;
import com.recommendation.common.utils.PageUtils;
import com.recommendation.common.utils.R;
import org.springframework.web.client.RestTemplate;


/**
 * 
 *
 * @author ymr
 * @email u202015453@hust.edu.cn
 * @date 2024-04-01 16:48:09
 */

@RefreshScope
@RestController
@RequestMapping("itemservice/movies")
public class MoviesController {
    @Autowired
    private MoviesService moviesService;
    @Autowired
    ReviewFeignService reviewFeignService;



    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("itemservice:movies:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("size",100);
        PageUtils page = moviesService.queryPage(params);

        return R.ok().put("page", page);
    }
//    获取电影list
    @RequestMapping("/getMoviesByList")
    public R MovieInfoByList(@RequestBody List<Integer> list){
        List<MoviesEntity> moviesEntities = moviesService.selectMoviesByListOder(list);
        return R.ok().put("movie_list",moviesEntities);
    }
    @RequestMapping("/recommendation")
    public R recommendation(@CookieValue(value = "user_id") String user_id){
        RestTemplate restTemplate = new RestTemplate();
//        user_id = "1";
        List<String> seq = (List<String>) reviewFeignService.userSeq(user_id).get("seq");
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("seq", seq);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> r = new HttpEntity<Map<String, Object>>(requestBody, requestHeaders);
        //TODO
        String url = "http://localhost:24000/recommendation";
        String result = restTemplate.postForObject(url, r, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNde;
        try {
            Map<String, Object> responseMap = mapper.readValue(result, new TypeReference<Map<String, Object>>(){});
            List<Integer> recList = (List<Integer>) responseMap.get("rec");
            List<MoviesEntity> moviesEntities = moviesService.selectMoviesByListOder(recList);
            return R.ok().put("movie_list",moviesEntities);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{movieId}")
//    @RequiresPermissions("itemservice:movies:info")
    public R info(@PathVariable("movieId") Integer movieId){
		MoviesEntity movie = moviesService.getById(movieId);
        if(movie == null)
            return R.fail("no such movie");

        return R.ok().put("movie",movie);
    }
    @RequestMapping("/info/review_list/{movieId}")
    public R movieReviewPage(@PathVariable("movieId") Integer movieId,
                             @RequestParam(value = "page",defaultValue = "1") int page,
                             @CookieValue(value = "user_id") String userId){
        MoviesEntity movie = moviesService.getById(movieId);
        if(movie == null)
            return R.fail("no such movie");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("movie_id",movieId);
        hashMap.put("page",page);
        boolean reviewed = reviewFeignService.reviewExist(userId,movieId);
        return reviewFeignService.movieReviews(hashMap).put("reviewed",reviewed);
    }


    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("itemservice:movies:save")
    public R save(@RequestBody MoviesEntity movies){
		moviesService.save(movies);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("itemservice:movies:update")
    public R update(@RequestBody MoviesEntity movies){
		moviesService.updateById(movies);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("itemservice:movies:delete")
    public R delete(@RequestBody Integer[] movieIds){
		moviesService.removeByIds(Arrays.asList(movieIds));

        return R.ok();
    }

}
