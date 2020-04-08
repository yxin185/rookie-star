package com.rookie.controller;

import com.rookie.pojo.bo.UserBO;
import com.rookie.service.UserService;
import com.rookie.utils.RookieJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户登录相关的控制
 */
@Api(value = "注册登录", tags = {"用户注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "判断用户是否存在", notes = "判断用户是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public RookieJsonResult usernameIsExist(@RequestParam String username) {
        // 1. 判断用户名不能为空
        if (StringUtils.isBlank(username)) {
            return RookieJsonResult.errorMsg("用户名不能为空");
        }

        // 2. 查找注册的用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return RookieJsonResult.errorMsg("用户名已存在");
        }
        // 3. 请求成功，用户名没有重复
        return RookieJsonResult.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/regist")
    public RookieJsonResult regist(@RequestBody UserBO userBO) {
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPwd = userBO.getConfirmPassword();

        // 0. 校验用户名、密码、确认密码不能为空
        if (StringUtils.isBlank(username)
        || StringUtils.isBlank(password)
        || StringUtils.isBlank(confirmPwd)) {
            return RookieJsonResult.errorMsg("用户名或密码为空");
        }
        // 1. 查询用户是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return RookieJsonResult.errorMsg("用户名已存在");
        }
        // 2. 密码长度不能小于6
        if (password.length() < 6) {
            return RookieJsonResult.errorMsg("用户密码长度不能小于6");
        }
        // 3. 校验两次密码是否一致
        if (!password.equals(confirmPwd)) {
            return RookieJsonResult.errorMsg("两次密码不一致");
        }

        // 4. 实现注册
        userService.createUser(userBO);
        // 5. 注册完成
        return RookieJsonResult.ok();
    }
}
