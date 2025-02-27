package com.recommendation.userservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.recommendation.common.constants.UserConstants;
import com.recommendation.common.entity.UsersEntity;
import com.recommendation.common.utils.R;
import com.recommendation.common.utils.RandomString;
import com.recommendation.userservice.feign.ReviewFeignService;
import com.recommendation.userservice.service.UsersService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 *
 * @author ymr
 * @email u202015453@hust.edu.cn
 * @date 2024-04-01 20:37:48
 */
@RestController
@RequestMapping("userservice/users")
public class UsersController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private ReviewFeignService reviewFeignService;
    @RequestMapping("/checkId")
    public R checkUserId(@RequestParam Map<String, Object> params){
        UsersEntity usersEntity =  usersService.getById((Serializable) params.get("userId"));
        if (usersEntity == null){
            return R.fail("用户不存在");
        }
        return R.ok("用户存在");
    }
    @PostMapping("register")
    public R registerUser(@RequestBody UsersEntity user){
        QueryWrapper<UsersEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",user.getId());
        if (usersService.exists(queryWrapper)){
            return  R.fail("账户已存在");
        }
        //MD5 加密 + 加盐处理
        String newPwd = DigestUtils.md5Hex(user.getPassword() + UserConstants.USER_SLAT);
        user.setPassword(newPwd);
        user.setName("user" + RandomString.getRandomString(10));
        //
        if(usersService.save(user)){
            return R.ok("注册成功");
        }
        else
            return R.fail("注册失败，请重试");

    }
    @PostMapping("login")
    public R login(@RequestBody UsersEntity user){
        QueryWrapper<UsersEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",user.getId());
        if (!usersService.exists(queryWrapper)){
            return  R.fail("账户不存在");
        }
        String newPwd = DigestUtils.md5Hex(user.getPassword() + UserConstants.USER_SLAT);
        user.setPassword(newPwd);
        queryWrapper.eq("password",user.getPassword());
        user = usersService.getOne(queryWrapper);
        if (user == null){
            return  R.fail("账户或密码错误");
        }
        user.setPassword(null);
        return R.ok("登陆成功").put("user", user);
    }

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = usersService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }
    /**
    * 删除用户的评论
    */
    @RequestMapping("/info/review_list/del/{movieId}")
    public R delReview(@PathVariable("movieId") Integer movieId,
                       @CookieValue(value = "user_id") String user_id){
        if(usersService.getById(user_id)!=null)
            return reviewFeignService.delReview(user_id,movieId);
        else
            return R.fail("用户不存在");

    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") String id,@CookieValue(value = "user_id") String user_id){
        if(id.equals("-1"))
            id=user_id;
		UsersEntity user = usersService.getById(id);

        if(user == null)
            return R.fail("no such user");
        user.setPassword(null);

//        LinkedHashMap linkedHashMap = (LinkedHashMap) r.get("review_page");
//        ArrayList<MoviesEntity> list = (ArrayList<MoviesEntity>) linkedHashMap.get("list");

        R r = R.ok().put("user",user);
        return r.put("selfPage",id.equals(user_id));
    }
    @RequestMapping("/info/review_list/{id}")
    public R userReviewPage(@PathVariable("id") String id,
                            @RequestParam(value = "page",defaultValue = "1") int page,
                            @CookieValue(value = "user_id") String user_id){
        if(id.equals("-1"))
            id=user_id;
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("user_id",id);
        hashMap.put("page",page);
        return  reviewFeignService.userReviews(hashMap);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody UsersEntity users){
		usersService.save(users);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody UsersEntity users){
		usersService.updateById(users);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody String[] ids){
		usersService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
