package com.rookie.enums;

public enum PayMethodEnum {

    WEIXIN(1, "微信支付"),
    ALIPAY(2, "支付宝支付"),
    ;

    public Integer type;
    public String value;

    PayMethodEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
