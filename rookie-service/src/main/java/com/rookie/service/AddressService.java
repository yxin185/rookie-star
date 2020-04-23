package com.rookie.service;

import com.rookie.pojo.Carousel;
import com.rookie.pojo.UserAddress;
import com.rookie.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {

    /**
     * 根据用户id获得用户的地址列表
     * @param userId
     * @return
     */
    public List<UserAddress> queryAll(String userId);

    /**
     * 用户新增地址
     * @param addressBO
     */
    public void addNewUserAddress(AddressBO addressBO);

    /**
     * 用户修改地址
     * @param addressBO
     */
    public void updateUserAddress(AddressBO addressBO);

    /**
     * 用户删除地址
     * @param userId
     * @param addressId
     */
    public void deleteUserAddress(String userId, String addressId);

    /**
     * 修改默认地址
     * @param userId
     * @param addressId
     */
    public void updateUserAddressToBeDefault(String userId, String addressId);
}
