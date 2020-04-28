package com.rookie.controller;


import com.rookie.pojo.Items;
import com.rookie.pojo.ItemsImg;
import com.rookie.pojo.ItemsParam;
import com.rookie.pojo.ItemsSpec;
import com.rookie.pojo.vo.CommentLevelCountsVO;
import com.rookie.pojo.vo.ItemInfoVO;
import com.rookie.pojo.vo.ShopcartVO;
import com.rookie.service.ItemService;
import com.rookie.utils.PagedGridResult;
import com.rookie.utils.RookieJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品接口", tags = {"商品信息展示的相关接口"})
@RestController
@RequestMapping("items")
public class ItemsController extends BaseController{

    @Autowired
    private ItemService itemService;


    @ApiOperation(value = "查询商品详情", notes = "查询商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public RookieJsonResult info(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @PathVariable String itemId) {

        if (StringUtils.isBlank(itemId)) {
            return RookieJsonResult.errorMsg(null);
        }

        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemImgList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemParams = itemService.queryItemParam(itemId);

        // RookieJsonResult 一次只能返回一个对象，要想把上面4个对象一次返回给前端
        // 那么就引入一个 ItemInfoVO 把四个对象聚合起来
        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(item);
        itemInfoVO.setItemImgList(itemImgList);
        itemInfoVO.setItemSpecList(itemSpecList);
        itemInfoVO.setItemParams(itemParams);

        return RookieJsonResult.ok(itemInfoVO);
    }

    @ApiOperation(value = "查询商品等级评价", notes = "查询商品等级评价", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public RookieJsonResult commentLevel(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId) {

        if (StringUtils.isBlank(itemId)) {
            return RookieJsonResult.errorMsg(null);
        }

        CommentLevelCountsVO countsVO = itemService.queryCommentCounts(itemId);

        return RookieJsonResult.ok(countsVO);
    }

    @ApiOperation(value = "查询商品评论", notes = "查询商品评论", httpMethod = "GET")
    @GetMapping("/comments")
    public RookieJsonResult comments(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level", value = "评价等级", required = false)
            @RequestParam Integer level,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(itemId)) {
            return RookieJsonResult.errorMsg(null);
        }

        // 如果参数不传，我们给这个参数一个默认值
        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = COMMONPAGE_SIZE;
        }

        PagedGridResult gridResult = itemService.queryPagedComments(itemId,
                                                                    level,
                                                                    page,
                                                                    pageSize);

        // 返回给前端的
        return RookieJsonResult.ok(gridResult);
    }

    @ApiOperation(value = "搜索商品列表", notes = "搜索商品列表", httpMethod = "GET")
    @GetMapping("/search")
    public RookieJsonResult search(
            @ApiParam(name = "keywords", value = "商品id", required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort", value = "排序方法", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return RookieJsonResult.errorMsg(null);
        }

        // 如果参数不传，我们给这个参数一个默认值
        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedGridResult gridResult = itemService.searchItems(keywords,
                                                             sort,
                                                             page,
                                                             pageSize);

        // 返回给前端的
        return RookieJsonResult.ok(gridResult);
    }

    @ApiOperation(value = "通过分类id搜索商品列表", notes = "通过分类id搜索商品列表", httpMethod = "GET")
    @GetMapping("/catItems")
    public RookieJsonResult catItems(
            @ApiParam(name = "catId", value = "三级分类id", required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort", value = "排序方法", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (catId == null) {
            return RookieJsonResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedGridResult grid = itemService.searchItemsByThirdCat(catId,
                                                                    sort,
                                                                    page,
                                                                    pageSize);

        return RookieJsonResult.ok(grid);
    }

    @ApiOperation(value = "根据商品规格ids查找最新的商品数据", notes = "根据商品规格ids查找最新的商品数据", httpMethod = "GET")
    @GetMapping("/refresh")
    public RookieJsonResult refresh(
            @ApiParam(name = "itemSpecIds", value = "拼接的规格ids", required = true, example = "1001,1002,1003")
            @RequestParam String itemSpecIds) {

        if (StringUtils.isBlank(itemSpecIds)) {
            return RookieJsonResult.ok();
        }

        List<ShopcartVO> list = itemService.queryItemsBySpecIds(itemSpecIds);

        return RookieJsonResult.ok(list);
    }


}
