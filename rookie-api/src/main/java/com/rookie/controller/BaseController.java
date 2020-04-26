package com.rookie.controller;

import org.springframework.stereotype.Controller;

/**
 * 作为所有 Controller 的父类，存放一些常量
 */
@Controller
public class BaseController {

    /**
     * 购物车的 Cookie 名字
     */
    public static final String ROOKIE_SHOPCART = "shopcart";

    public static final Integer COMMENT_PAGE_SIZE = 10;

    /**
     * 搜索商品使得页面数量
     */
    public static final Integer PAGE_SIZE = 20;

    // 支付中心的调用地址
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";		// produce

    // 微信支付成功 -> 支付中心 -> 天天吃货平台
    //                       |-> 回调通知的url
    /**
     * 这个http://39am5i.natappfree.cc地址
     * 因为是免费的内网穿透，所以如果关闭了natapp，需要重新修改
     */
    String payReturnUrl = "http://hyptfc.natappfree.cc/orders/notifyMerchantOrderPaid";

    // 这个看不到回调的状态
//    String payReturnUrl = "http://api.z.mukewang.com/foodie-dev-api/orders/notifyMerchantOrderPaid";

}
