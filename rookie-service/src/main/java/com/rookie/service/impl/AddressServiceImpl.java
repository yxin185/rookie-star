package com.rookie.service.impl;

import com.rookie.enums.YesOrNoEnum;
import com.rookie.mapper.UserAddressMapper;
import com.rookie.pojo.UserAddress;
import com.rookie.pojo.bo.AddressBO;
import com.rookie.service.AddressService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAll(String userId) {

        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addNewUserAddress(AddressBO addressBO) {
        // 1. 判断当前用户是否存在地址，如果不存在，就将新创建的地址设置为默认地址
        Integer isDefault = 0;
        List<UserAddress> addressList = this.queryAll(addressBO.getUserId());
        if (addressList == null || addressList.isEmpty() || addressList.size() == 0) {
            isDefault = 1;
        }

        String addressId = sid.nextShort();

        // 2. 保存地址到数据库
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, userAddress);

        userAddress.setId(addressId);
        userAddress.setIsDefault(isDefault);
        userAddress.setCreatedTime(new Date());
        userAddress.setUpdatedTime(new Date());

        userAddressMapper.insert(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddress(AddressBO addressBO) {
        String addressId = addressBO.getAddressId();
        UserAddress updateAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, updateAddress);

        updateAddress.setId(addressId);
        updateAddress.setUpdatedTime(new Date());

        userAddressMapper.updateByPrimaryKeySelective(updateAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserAddress(String userId, String addressId) {

        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setId(addressId);

        userAddressMapper.delete(address);
    }

    @Override
    public void updateUserAddressToBeDefault(String userId, String addressId) {
        // 1. 查找默认地址，设置为不默认
        UserAddress queryAddress = new UserAddress();
        queryAddress.setUserId(userId);
        queryAddress.setIsDefault(YesOrNoEnum.YES.type);
        List<UserAddress> list  = userAddressMapper.select(queryAddress);
        for (UserAddress ua : list) {
            ua.setIsDefault(YesOrNoEnum.NO.type);
            userAddressMapper.updateByPrimaryKeySelective(ua);
        }

        // 2. 根据地址id修改为默认的地址
        UserAddress defaultAddress = new UserAddress();
        defaultAddress.setId(addressId);
        defaultAddress.setUserId(userId);
        defaultAddress.setIsDefault(YesOrNoEnum.YES.type);
        userAddressMapper.updateByPrimaryKeySelective(defaultAddress);
    }
}
