package com.rookie.service;

import com.rookie.pojo.bo.SubmitOrderBO;

public interface OrdersService {

    /**
     * 用户创建订单相关信息
     * @param submitOrderBO
     */
    public String createOrder(SubmitOrderBO submitOrderBO);
}