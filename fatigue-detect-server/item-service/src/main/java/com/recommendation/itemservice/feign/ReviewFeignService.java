package com.recommendation.itemservice.feign;

import com.recommendation.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("review-service")
public interface ReviewFeignService {
    @RequestMapping("reviewservice/reviews/movie")
    public R movieReviews(@RequestParam Map<String, Object> params);
    @RequestMapping("reviewservice/reviews/userSeq")
    public R userSeq(@RequestParam String userId);
    @RequestMapping("reviewservice/reviews/reviewExist")
    public boolean reviewExist(@RequestParam String userId,@RequestParam Integer movieId);
}
