package com.rookie.enums;

/**
 * 定于用户性别的枚举
 */
public enum YesOrNoEnum {

    NO(0, "否"),
    YES(1, "是");

    public Integer type;
    public String value;

    YesOrNoEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
