package com.rookie.service;

import com.rookie.pojo.Users;
import com.rookie.pojo.bo.UserBO;

/**
 * 实现用户相关的操作
 */
public interface UserService {

    /**
     * 判断用户名是否存在
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 创建一个用户
     */
    // UserBO 相当于是根据从前端取到的用户名称、密码构造一个新的用户
    public Users createUser(UserBO userBO);

    /**
     * 检索用户名和密码是否匹配，用于登录
     */
    public Users queryUserForLogin(String username, String password);
}
