package com.rookie.controller;


import com.rookie.enums.YesOrNoEnum;
import com.rookie.pojo.Carousel;
import com.rookie.pojo.Category;
import com.rookie.service.CarouselService;
import com.rookie.service.CategoryService;
import com.rookie.utils.RookieJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public RookieJsonResult carousel() {

        List<Carousel> result = carouselService.queryAll(YesOrNoEnum.YES.type);
        return RookieJsonResult.ok(result);
    }

    @ApiOperation(value = "获取商品分类(一级分类)", notes = "获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public RookieJsonResult cats() {
        List<Category> result = categoryService.queryAllRootLevelCat();
        return RookieJsonResult.ok(result);
    }
}
