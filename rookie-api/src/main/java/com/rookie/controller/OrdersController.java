package com.rookie.controller;


import com.rookie.enums.PayMethodEnum;
import com.rookie.pojo.bo.SubmitOrderBO;
import com.rookie.service.OrdersService;
import com.rookie.utils.CookieUtils;
import com.rookie.utils.RookieJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "订单相关", tags = "订单相关的api")
@RequestMapping("orders")
@RestController
public class OrdersController extends BaseController{

    @Autowired
    private OrdersService ordersService;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public RookieJsonResult create(@RequestBody SubmitOrderBO submitOrderBO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        System.out.println(submitOrderBO.toString());
        if (submitOrderBO.getPayMethod() != PayMethodEnum.WEIXIN.type
        && submitOrderBO.getPayMethod() != PayMethodEnum.ALIPAY.type) {
            return RookieJsonResult.errorMsg("不支持这种支付方式");
        }

        // 1. 创建订单
        String orderId = ordersService.createOrder(submitOrderBO);
        // 2. 创建订单以后，移除购物车中已结算（已提交）的商品
        /**
         * 1001
         * 2002 -> 用户购买
         * 3003 -> 用户购买
         * 4004
         */
        // TODO 整合redis之后，完善购物车中的已结算商品清除，并且同步到前端的cookie
//        CookieUtils.setCookie(request, response, ROOKIE_SHOPCART, "", true);
        // 3. 向支付中心发送当前订单，用以保存支付中心的订单数据
        return RookieJsonResult.ok(orderId);
    }

}
