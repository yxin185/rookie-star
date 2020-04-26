package com.rookie.controller;


import com.rookie.enums.OrderStatusEnum;
import com.rookie.enums.PayMethodEnum;
import com.rookie.pojo.OrderStatus;
import com.rookie.pojo.bo.SubmitOrderBO;
import com.rookie.pojo.vo.MerchantOrdersVO;
import com.rookie.pojo.vo.OrderVO;
import com.rookie.service.OrdersService;
import com.rookie.utils.RookieJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "订单相关", tags = "订单相关的api")
@RequestMapping("orders")
@RestController
public class OrdersController extends BaseController{

    final static Logger logger = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public RookieJsonResult create(@RequestBody SubmitOrderBO submitOrderBO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

//        System.out.println(submitOrderBO.toString());
        if (submitOrderBO.getPayMethod() != PayMethodEnum.WEIXIN.type
        && submitOrderBO.getPayMethod() != PayMethodEnum.ALIPAY.type) {
            return RookieJsonResult.errorMsg("不支持这种支付方式");
        }

        // 1. 创建订单
        OrderVO orderVO = ordersService.createOrder(submitOrderBO);
        String orderId = orderVO.getOrderId();
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
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);

        // 为了方便测试购买，所以所有的支付金额都统一改为1分钱
        merchantOrdersVO.setAmount(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        /**
         * 密码账号来自于 github，时间长了之后可能会失效
         */
        headers.add("imoocUserId","6567325-1528023922");
        headers.add("password","342r-t450-gr4r-456y");

        HttpEntity<MerchantOrdersVO> entity =
                new HttpEntity<>(merchantOrdersVO, headers);

        ResponseEntity<RookieJsonResult> responseEntity =
                restTemplate.postForEntity(paymentUrl,
                        entity,
                        RookieJsonResult.class);

        RookieJsonResult paymentResult = responseEntity.getBody();
        if (paymentResult.getStatus() != 200) {
            logger.error("发送错误：{}", paymentResult.getMsg());
            return RookieJsonResult.errorMsg("支付中心订单创建失败，请联系管理员！");
        }
        return RookieJsonResult.ok(orderId);
    }

    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        ordersService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @PostMapping("getPaidOrderInfo")
    public RookieJsonResult getPaidOrderInfo(String orderId) {

        OrderStatus orderStatus = ordersService.queryOrderStatusInfo(orderId);
        return RookieJsonResult.ok(orderStatus);
    }

}
