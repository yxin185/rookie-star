package com.rookie.controller.center;

import com.rookie.pojo.Users;
import com.rookie.pojo.bo.center.CenterUserBO;
import com.rookie.service.center.CenterUserService;
import com.rookie.utils.CookieUtils;
import com.rookie.utils.JsonUtils;
import com.rookie.utils.RookieJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "用户信息接口", tags = "用户信息接口相关")
@RestController
@RequestMapping("userInfo")
public class CenterUserController {

    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("update")
    public RookieJsonResult update(
            @ApiParam(name = "userId", value = "用户 id", required = true)
            @RequestParam String userId,
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult result,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return RookieJsonResult.errorMap(errorMap);
        }
        
        Users userResult = centerUserService.updateUserInfo(userId, centerUserBO);
        // 隐藏用户的一些属性，并更新到 Cookie中
        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userResult), true);

        // TODO 后续要改，增加令牌 token，会整合进 redis，分布式会话
        return RookieJsonResult.ok();

    }

    private Map<String, String> getErrors(BindingResult result) {
        Map<String,String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError error : errorList) {
            // 获得错误的字段
            String field = error.getField();
            // 获得默认的错误信息
            String defaultMessage = error.getDefaultMessage();
            map.put(field, defaultMessage);
        }
        return map;
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }
}
