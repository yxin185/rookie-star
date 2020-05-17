package com.rookie.controller;

import com.rookie.pojo.Users;
import com.rookie.pojo.bo.ShopcartBO;
import com.rookie.pojo.bo.UserBO;
import com.rookie.service.UserService;
import com.rookie.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户登录相关的控制
 */
@Api(value = "注册登录", tags = {"用户注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

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
    public RookieJsonResult regist(@RequestBody UserBO userBO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
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
        Users userResult = userService.createUser(userBO);
        // 隐藏用户的部分属性
        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userResult), true);

        // TODO 生成用户token，存入redis会话
        // 同步购物车数据
        syncShopcartData(userResult.getId(), request, response);


        // 5. 注册完成
        return RookieJsonResult.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public RookieJsonResult login(@RequestBody UserBO userBO,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        String username = userBO.getUsername();
        String password = userBO.getPassword();

        // 0. 校验用户名、密码、确认密码不能为空
        if (StringUtils.isBlank(username)
                || StringUtils.isBlank(password)) {
            return RookieJsonResult.errorMsg("用户名或密码为空");
        }

        Users userResult = userService.queryUserForLogin(username,
                MD5Utils.getMD5Str(password));
        // 没查到对应的用户
        if (userResult == null) {
            return RookieJsonResult.errorMsg("用户名或密码不正确");
        }
        // 隐藏用户的部分属性
        userResult = setNullProperty(userResult);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userResult), true);

        // TODO 生成用户token，存入redis会话
        // 同步购物车数据
        syncShopcartData(userResult.getId(), request, response);


        return RookieJsonResult.ok(userResult);
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public RookieJsonResult logout(@RequestParam String userId,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, "user");


        return RookieJsonResult.ok();
    }

    /**
     * 注册登录成功后，同步 cookie 和 Redis 中的购物车数据
     */
    private void syncShopcartData(String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        /**
         * 1. redis 中无数据，如果 cookie 中的购物车为空，那么这个时候不做任何处理
         *                  如果 cookie 中的购物车不为空，此时直接放入 Redis 中
         * 2. Redis 中有数据，如果 cookie 中的购物车为空，那么直接把 Redis 的购物车覆盖到本地 cookie
         *                  如果 cookie 中的购物车不为空，
         *                      如果 cookie 中的某个商品在 Redis 中存在，
         *                      则以 cookie 为主，删除 Redis 中的，
         *                      把 cookie 中的商品直接覆盖掉 Redis 中（参考京东）
         * 3. 同步到 Redis 中去了以后，覆盖掉本地 cookie 购物车中的数据，保证本地购物车的数据是同步的
         */

        // 从 Redis 中获取购物车
        String shopcartJsonRedis = redisOperator.get(ROOKIE_SHOPCART + ":" + userId);

        // 从 cookie 中获取购物车
        String shopcartStrCookie = CookieUtils.getCookieValue(request, ROOKIE_SHOPCART, true);

        if (StringUtils.isBlank(shopcartJsonRedis)) {
            // redis 为空，cookie 不为空，直接把 cookie 中的数据放到 Redis 中
            if (StringUtils.isNotBlank(shopcartStrCookie)) {
                redisOperator.set(ROOKIE_SHOPCART + ":" + userId, shopcartStrCookie);
            }
        } else {
            // redis 不为空， cookie 不为空，合并 cookie 和 Redis 中购物车中的商品数据（同一商品则合并）
            if (StringUtils.isNotBlank(shopcartStrCookie)) {
                /**
                 * 1. 已经存在的，把 cookie 中对应的数量，覆盖 Redis
                 * 2. 该项商品标记为待删除，统一放入一个待删除的 list 中
                 * 3. 从 cookie 清理所有的待删除 list
                 * 4. 合并 Redis 和 cookie 中的数据
                 * 5. 更新到 cookie 和 Redis 中
                 */
                List<ShopcartBO> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopcartListCookie = JsonUtils.jsonToList(shopcartStrCookie, ShopcartBO.class);
                // 定义一个待删除列表
                List<ShopcartBO> pendingDeleteList = new ArrayList<>();
                for (ShopcartBO redisShopcart : shopcartListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();

                    for (ShopcartBO cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();
                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖购买数量，不累加，参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            // 把 cookieShopcart 放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }
                    }
                }
                // 从现有 cookie 中删除对应的覆盖过的商品数据
                shopcartListCookie.removeAll(pendingDeleteList);
                // 合并两个 list
                shopcartListRedis.addAll(shopcartListCookie);
                // 更新到 Redis 和 cookie
                CookieUtils.setCookie(request, response, ROOKIE_SHOPCART, JsonUtils.objectToJson(shopcartListRedis), true);
                redisOperator.set(ROOKIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartListRedis));

            } else {
                // redis 不为空，cookie 为空，直接 Redis 覆盖 cookie
                CookieUtils.setCookie(request, response, ROOKIE_SHOPCART, shopcartJsonRedis, true);
            }
        }
    }

}
