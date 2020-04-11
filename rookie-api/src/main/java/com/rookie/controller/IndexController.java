package com.rookie.controller;


import com.rookie.enums.YesOrNoEnum;
import com.rookie.pojo.Carousel;
import com.rookie.pojo.Category;
import com.rookie.pojo.vo.CategoryVO;
import com.rookie.pojo.vo.NewItemsVO;
import com.rookie.service.CarouselService;
import com.rookie.service.CategoryService;
import com.rookie.utils.RookieJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

        List<CategoryVO> result = categoryService.getSubCatList(rootCatId);
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
