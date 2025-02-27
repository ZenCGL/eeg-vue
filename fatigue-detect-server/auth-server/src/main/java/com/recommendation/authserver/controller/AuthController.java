package com.recommendation.authserver.controller;

import com.recommendation.authserver.feign.UserFeignService;
import com.recommendation.authserver.param.UserLoginCheckParam;
import com.recommendation.authserver.param.UserRegisterCheckParam;
import com.recommendation.authserver.service.AuthService;
import com.recommendation.common.entity.UsersEntity;
import com.recommendation.common.utils.R;
import com.recommendation.common.utils.TokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.mail.Session;
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;
    @Autowired
    UserFeignService userFeignService;

    private boolean userIdExist(String userId){
        HashMap<String, Object> param = new HashMap<>();
        param.put("userId",userId);
        R r = userFeignService.checkUserId(param);
        return (int) r.get("code") != 400;
    }
    @RequestMapping("sendEmail")
    public R sendRegisterEmail(@RequestBody @Validated UserRegisterCheckParam userRegisterCheckParam, BindingResult result){

    }
    public static boolean sendMail(String to, String code) {

        try {
            Properties props = new Properties();
            props.put("username", "huyuyang6688@163.com");
            props.put("password", "123456");
            props.put("mail.transport.protocol", "smtp" );
            props.put("mail.smtp.host", "smtp.163.com");
            props.put("mail.smtp.port", "25" );

            Session mailSession = Session.getDefaultInstance(props);

            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress("huyuyang6688@163.com"));
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject("激活邮件");
            msg.setContent("<h1>此邮件为官方激活邮件！请点击下面链接完成激活操作！</h1><h3><a href='http://localhost:8080/SendMail/servlet/ActiveServlet?code="+code+"'>http://localhost:8080/SendMail/servlet/ActiveServlet</a></h3>","text/html;charset=UTF-8");
            msg.saveChanges();

            Transport transport = mailSession.getTransport("smtp");
            transport.connect(props.getProperty("mail.smtp.host"), props
                    .getProperty("username"), props.getProperty("password"));
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return false;
        }
        return true;
    }
    @RequestMapping("register")
    public R Register(@RequestBody @Validated UserRegisterCheckParam userRegisterCheckParam, BindingResult result){
        if (result.hasErrors()){
            List<String> msg = result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
            return R.fail(msg.toString());
        }

        if (!userRegisterCheckParam.getUserPassWord().equals(userRegisterCheckParam.getRepeatPassWord())){
            return R.fail("两次密码不一致");
        }

        if (!userIdExist(userRegisterCheckParam.getUserId())){
            // do Register
            UsersEntity user = new UsersEntity();
            user.setPassword(userRegisterCheckParam.getUserPassWord());
            user.setId(userRegisterCheckParam.getUserId());
            return userFeignService.registerUser(user);
        }
        else
            return R.fail("用户已存在");

    }
    @RequestMapping("login")
    public R login(@RequestBody @Validated UserLoginCheckParam userLoginCheckParam, BindingResult result, HttpServletResponse response){
        if (result.hasErrors()){
            List<String> msg = result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
            return R.fail(msg.toString());
        }
        if (userIdExist(userLoginCheckParam.getUserId())){
            // do login

            UsersEntity user = new UsersEntity();

            user.setPassword(userLoginCheckParam.getUserPassWord());
            user.setId(userLoginCheckParam.getUserId());
            R r = userFeignService.login(user);
            if((Integer) r.get("code") == 0){
                Cookie cookie = authService.generateToken(userLoginCheckParam.getUserId());
                response.addCookie(cookie);
            }
            return r;
        }
        else
            return R.fail("用户不存在");

    }
    @RequestMapping("exit")
    public R exit(@CookieValue(value = "token") String token, HttpServletResponse response){
        String userId = authService.getIdFromToken(token);
        if(userIdExist(userId)){
            Cookie cookie = new Cookie("token", "");
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            return R.ok("注销成功");
        }
        return R.fail("用户不存在");
    }
}
