package com.recommendation.authserver.feign;

import com.recommendation.common.entity.UsersEntity;
import com.recommendation.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("user-service")
public interface UserFeignService {
    @RequestMapping("userservice/users/checkId")
    R checkUserId(@RequestParam Map<String, Object> params);
    @PostMapping("userservice/users/register")
    R registerUser(@RequestBody UsersEntity user);
    @PostMapping("userservice/users/login")
    R login(@RequestBody UsersEntity user);
}
