package com.rookie.service;

import com.rookie.pojo.Users;
import com.rookie.pojo.bo.UserBO;

/**
 * 实现用户相关的操作
 */
public interface UserService {

    public boolean queryUsernameIsExist(String username);

    // UserBO 相当于是根据从前端取到的用户名称、密码构造一个新的用户
    public Users createUser(UserBO userBO);
}
