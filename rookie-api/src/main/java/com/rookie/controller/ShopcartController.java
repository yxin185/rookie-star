package com.rookie.controller;


import com.rookie.pojo.bo.ShopcartBO;
import com.rookie.utils.JsonUtils;
import com.rookie.utils.RedisOperator;
import com.rookie.utils.RookieJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "购物车接口controller", tags = "购物车接口api")
@RequestMapping("shopcart")
@RestController
public class ShopcartController extends BaseController{

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public RookieJsonResult add(
            @RequestParam String userId,
            @RequestBody ShopcartBO shopcartBO,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (StringUtils.isBlank(userId)) {
            return RookieJsonResult.errorMsg("");
        }

        System.out.println(shopcartBO);

        // 前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到 redis 缓存
        // 需要额外判断购物车中包含已经存在的商品，如果存在则累加购买数量
        String shopCartJson = redisOperator.get(ROOKIE_SHOPCART + ":" + userId);
        List<ShopcartBO> shopcartList = null;
        if (StringUtils.isNotBlank(shopCartJson)) {
            // redis 中已经有购物车了
            shopcartList = JsonUtils.jsonToList(shopCartJson, ShopcartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话 counts 累加
            boolean isHaving = false;
            for (ShopcartBO sc : shopcartList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(shopcartBO.getSpecId())) {
                    sc.setBuyCounts(sc.getBuyCounts() + shopcartBO.getBuyCounts());
                    isHaving = true;
                }
            }
            if (!isHaving) {
                shopcartList.add(shopcartBO);
            }
        } else {
            // redis 中没有购物车
            shopcartList = new ArrayList<>();
            shopcartList.add(shopcartBO);
        }
        // 覆盖现有 redis 中的购物车
        redisOperator.set(ROOKIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartList));

        return RookieJsonResult.ok();
    }

    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    @PostMapping("/del")
    public RookieJsonResult del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return RookieJsonResult.errorMsg("参数不能为空");
        }

        // 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除redis购物车中的商品
        String shopcartJson = redisOperator.get(ROOKIE_SHOPCART + ":" + userId);
        if (StringUtils.isNotBlank(shopcartJson)) {
            // redis 中已经有购物车了
            List<ShopcartBO> shopcartBOList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话就删除
            for (ShopcartBO sc : shopcartBOList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(itemSpecId)) {
                    shopcartBOList.remove(sc);
                    break;
                }
            }
            // 覆盖现有 redis 中的购物车
            redisOperator.set(ROOKIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartBOList));
        }

        return RookieJsonResult.ok();
    }
}
