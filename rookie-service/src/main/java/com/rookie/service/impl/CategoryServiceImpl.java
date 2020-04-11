package com.rookie.service.impl;

import com.rookie.enums.CategoryLevelEnum;
import com.rookie.mapper.CategoryMapper;
import com.rookie.pojo.Category;
import com.rookie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> queryAllRootLevelCat() {

        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", CategoryLevelEnum.ONE.type);

        List<Category> result = categoryMapper.selectByExample(example);

        return result;
    }


}
