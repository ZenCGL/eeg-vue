package com.recommendation.recservice.feign;

import com.recommendation.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("item-service")
public interface MovieFeignService {
    @RequestMapping("itemservice/movies/getMoviesByList")
    public R MovieInfoByList(@RequestBody List<Integer> recList);
}
