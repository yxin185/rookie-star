package com.rookie.controller;


import com.rookie.pojo.UserAddress;
import com.rookie.pojo.bo.AddressBO;
import com.rookie.service.AddressService;
import com.rookie.utils.MobileEmailUtils;
import com.rookie.utils.RookieJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "地址相关", tags = "地址相关的api")
@RequestMapping("address")
@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 用户在确认订单页面，可以针对收货地址做如下操作：
     * 1. 查询用户的所有收货地址列表
     * 2. 新增收货地址
     * 3. 删除收货地址
     * 4. 修改收货地址
     * 5. 设置默认地址
     */
    @ApiOperation(value = "根据用户id查询收货地址列表", notes = "根据用户id查询收货地址列表", httpMethod = "POST")
    @PostMapping("/list")
    public RookieJsonResult list(
            @RequestParam String userId) {

        if (StringUtils.isBlank(userId)) {
            return RookieJsonResult.errorMsg("");
        }

        List<UserAddress> userAddressList = addressService.queryAll(userId);
        return RookieJsonResult.ok(userAddressList);
    }

    @ApiOperation(value = "用户新增地址", notes = "用户新增地址", httpMethod = "POST")
    @PostMapping("/add")
    public RookieJsonResult add(@RequestBody AddressBO addressBO) {

        RookieJsonResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }

        addressService.addNewUserAddress(addressBO);
        return RookieJsonResult.ok();
    }

    private RookieJsonResult checkAddress(AddressBO addressBO) {
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return RookieJsonResult.errorMsg("收货人不能为空");
        }
        if (receiver.length() > 12) {
            return RookieJsonResult.errorMsg("收货人姓名太长啦");
        }
        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return RookieJsonResult.errorMsg("收货人手机号不能为空");
        }
        if (mobile.length() != 11) {
            return RookieJsonResult.errorMsg("收货人手机号长度不正确");
        }
        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk) {
            return RookieJsonResult.errorMsg("收货人手机号格式不正确");
        }

        /**
         * 判断相关的信息是否符合要求
         */
        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province) ||
                StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) ||
                StringUtils.isBlank(detail)) {
            return RookieJsonResult.errorMsg("收货地址信息不能为空");
        }

        return RookieJsonResult.ok();
    }

    @ApiOperation(value = "用户修改地址", notes = "用户修改地址", httpMethod = "POST")
    @PostMapping("/update")
    public RookieJsonResult update(@RequestBody AddressBO addressBO) {

        if (StringUtils.isBlank(addressBO.getAddressId())) {
            return RookieJsonResult.errorMsg("修改地址错误：addressId为空");
        }

        RookieJsonResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }

        addressService.updateUserAddress(addressBO);
        return RookieJsonResult.ok();
    }

    @ApiOperation(value = "用户删除地址", notes = "用户删除地址", httpMethod = "POST")
    @PostMapping("/delete")
    public RookieJsonResult delete(
            @RequestParam String userId,
            @RequestParam String addressId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return RookieJsonResult.errorMsg("");
        }

        addressService.deleteUserAddress(userId, addressId);
        return RookieJsonResult.ok();
    }

    /**
     * 前端这个setDefault之前写成了 setDefalut
     * @param userId
     * @param addressId
     * @return
     */
    @ApiOperation(value = "用户设置默认地址", notes = "用户设置默认地址", httpMethod = "POST")
    @PostMapping("/setDefault")
    public RookieJsonResult setDefault(
            @RequestParam String userId,
            @RequestParam String addressId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return RookieJsonResult.errorMsg("");
        }

        addressService.updateUserAddressToBeDefault(userId, addressId);
        return RookieJsonResult.ok();
    }
}
