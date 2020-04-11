package com.rookie.enums;


/**
 * 分类级别
 */
public enum CategoryLevelEnum {

    ONE(1, "一级分类"),
    TWO(2, "二级分类"),
    THREE(3, "三级分类"),
    ;

    public Integer type;
    public String value;

    CategoryLevelEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
