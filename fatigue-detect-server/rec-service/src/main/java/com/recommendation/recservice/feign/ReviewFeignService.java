package com.recommendation.recservice.feign;

import com.recommendation.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient("review-service")
public interface ReviewFeignService {
    @RequestMapping("reviewservice/reviews/setSeq")
    public R setSeq(@RequestParam String userId, @RequestParam List<String> seq);
    @RequestMapping("reviewservice/reviews/user")
    public R userReviews(@RequestParam Map<String, Object> params);
    @RequestMapping("reviewservice/reviews/delete")
    public R delReview(@RequestParam String userId,@RequestParam Integer movieId);
    @RequestMapping("reviewservice/reviews/userSeq")
    public R userSeq(@RequestParam String userId);
}
