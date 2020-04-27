package com.rookie.service.center;

import com.rookie.pojo.Users;
import com.rookie.pojo.bo.UserBO;
import com.rookie.pojo.bo.center.CenterUserBO;

/**
 * 实现用户中心相关的操作
 */
public interface CenterUserService {

    /**
     * 根据用户 id 查询用户信息
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);

    /**
     * 根据用户 id 去更新用户信息
     * @param userId
     * @param centerUserBO
     * @return
     */
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO);
}
