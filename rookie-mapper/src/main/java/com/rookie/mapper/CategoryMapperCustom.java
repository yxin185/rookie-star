package com.rookie.mapper;


import java.util.List;

public interface CategoryMapperCustom{

    /**
     * 根据父分类的 id 去获取子分类列表
     * @param rootCatId
     * @return
     */
    List getSubCatList(Integer rootCatId);
}