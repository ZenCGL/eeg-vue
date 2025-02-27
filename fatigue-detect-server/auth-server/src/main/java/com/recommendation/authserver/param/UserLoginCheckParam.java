package com.recommendation.authserver.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserLoginCheckParam {
    @Length(min = 8,max= 30,message = "用户账号应在{min}-{max}之间")
    private String userId;
    @Length(min = 8, max = 30, message = "密码长度应在{min}-{max}之间")
    private String userPassWord;
}
