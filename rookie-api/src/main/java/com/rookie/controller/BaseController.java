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

}
