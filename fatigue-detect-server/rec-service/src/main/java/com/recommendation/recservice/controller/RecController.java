package com.recommendation.recservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommendation.common.constants.UserConstants;
import com.recommendation.common.entity.MoviesEntity;
import com.recommendation.common.entity.UsersEntity;
import com.recommendation.common.utils.R;
import com.recommendation.common.utils.RandomString;
import com.recommendation.recservice.feign.MovieFeignService;
import com.recommendation.recservice.feign.ReviewFeignService;
import com.recommendation.recservice.service.RecService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.*;


/**
 * 
 *
 * @author ymr
 * @email u202015453@hust.edu.cn
 * @date 2024-04-01 20:37:48
 */
@RestController
@RequestMapping("recservice")
public class RecController {
    @Autowired
    private RecService recService;
    @Autowired
    private ReviewFeignService reviewFeignService;
    @Autowired
    private MovieFeignService movieFeignService;
    @RequestMapping("rec")
    public R recommendation(@CookieValue(value = "user_id") String user_id){
        RestTemplate restTemplate = new RestTemplate();
//        user_id = "1";
        List<String> seq = (List<String>) reviewFeignService.userSeq(user_id).get("seq");
        if (seq == null|| seq.size()==0 || seq.get(0).equals("")){
            List<Integer> recList = new ArrayList<>();
            Random random = new Random();
            for(int i = 0;i < 100;i++){
                recList.add(random.nextInt(3000));
            }
            return movieFeignService.MovieInfoByList(recList).put("seqisnull",true);
        }
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
            return movieFeignService.MovieInfoByList(recList).put("seqisnull",false);
//            return R.ok().put("movie_list",moviesEntities);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @RequestMapping("setSeq")
    public R getFakeSeq(@CookieValue(value = "user_id") String user_id,@RequestBody ArrayList<String> seq){
        RestTemplate restTemplate = new RestTemplate();
        List<String> user_seq = (List<String>) reviewFeignService.userSeq(user_id).get("seq");
        if (user_seq == null|| user_seq.size()==0 || user_seq.get(0).equals(""))
            return reviewFeignService.setSeq(user_id,seq);
        return R.fail("用户序列不为空");
    }

}
