package com.rookie.controller;


import com.rookie.enums.YesOrNoEnum;
import com.rookie.pojo.Carousel;
import com.rookie.pojo.Category;
import com.rookie.pojo.vo.CategoryVO;
import com.rookie.pojo.vo.NewItemsVO;
import com.rookie.service.CarouselService;
import com.rookie.service.CategoryService;
import com.rookie.utils.JsonUtils;
import com.rookie.utils.RedisOperator;
import com.rookie.utils.RookieJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public RookieJsonResult carousel() {

        List<Carousel> result = new ArrayList<>();

        String carouselStr = redisOperator.get("carousel");
        if (StringUtils.isBlank(carouselStr)) {
            result = carouselService.queryAll(YesOrNoEnum.YES.type);
            // 需要把拿到的结果存到redis中
            redisOperator.set("carousel", JsonUtils.objectToJson(result));
        } else {
            result = JsonUtils.jsonToList(carouselStr, Carousel.class);
        }

        return RookieJsonResult.ok(result);
    }

    /**
     * 1. 后台运营系统，一旦广告（轮播图）发生更改，就可以删除缓存，然后重置
     * 2. 定时重置，比如每天凌晨三点重置
     * 3. 每个轮播图都有可能是一个广告，每个广告都会有一个过期时间，过期了，再重置
     */


    @ApiOperation(value = "获取商品分类(一级分类)", notes = "获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public RookieJsonResult cats() {
        List<Category> result = new ArrayList<>();
        String catsStr = redisOperator.get("cats");
        if (StringUtils.isBlank(catsStr)) {
            result = categoryService.queryAllRootLevelCat();
            redisOperator.set("cats", JsonUtils.objectToJson(result));
        } else {
            result = JsonUtils.jsonToList(catsStr, Category.class);
        }

        return RookieJsonResult.ok(result);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public RookieJsonResult subCat(
                @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
                @PathVariable Integer rootCatId) {
        /**
         * 需要手动进行校验 rootCatId 是否为空，避免恶意攻击
         */
        if (rootCatId == null) {
            return RookieJsonResult.errorMsg("分类不存在");
        }

        List<CategoryVO> result = new ArrayList<>();
        String subCatsStr = redisOperator.get("subCats:" + rootCatId);
        if (StringUtils.isBlank(subCatsStr)) {
            result = categoryService.getSubCatList(rootCatId);
            redisOperator.set("subCats:" + rootCatId, JsonUtils.objectToJson(subCatsStr));
        } else {
            result = JsonUtils.jsonToList(subCatsStr, CategoryVO.class);
        }

        return RookieJsonResult.ok(result);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes = "查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public RookieJsonResult sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {
        /**
         * 需要手动进行校验 rootCatId 是否为空，避免恶意攻击
         */
        if (rootCatId == null) {
            return RookieJsonResult.errorMsg("分类不存在");
        }

        List<NewItemsVO> result = categoryService.getSixNewItemsLazy(rootCatId);
        return RookieJsonResult.ok(result);
    }

}
