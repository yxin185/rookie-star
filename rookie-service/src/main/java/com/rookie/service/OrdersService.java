package com.rookie.service;

import com.rookie.pojo.OrderStatus;
import com.rookie.pojo.bo.ShopcartBO;
import com.rookie.pojo.bo.SubmitOrderBO;
import com.rookie.pojo.vo.OrderVO;

import java.util.List;

public interface OrdersService {

    /**
     * 用户创建订单相关信息
     * @param submitOrderBO
     */
    public OrderVO createOrder(List<ShopcartBO> shopcartBOList, SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    public OrderStatus queryOrderStatusInfo(String orderId);

    /**
     * 关闭超时未支付的订单
     */
    public void closeOrder();
}