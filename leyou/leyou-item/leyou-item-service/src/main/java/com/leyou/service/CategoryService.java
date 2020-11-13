package com.leyou.service;

import com.leyou.item.domain.Category;
import com.leyou.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父节点查询子节点信息
     *
     * @param pid
     * @return
     */
    public List<Category> queryCategoriesByPid(Long pid) {

        Category record = new Category();
        record.setParentId(pid);

        return this.categoryMapper.select(record);

    }

    /**
     * 根据bid修改品牌信息
     *
     * @param bid
     * @return
     */
    public List<Category> queryBrandByBid(Long bid) {
        return this.categoryMapper.queryBrandByBid(bid);
    }

    /**
     * 按照商品分类主键集合查询结果
     *
     * @param longs
     * @return
     */
    public List<String> queryNameByIds(List<Long> cidList) {

        List<Category> categoryList = this.categoryMapper.selectByIdList(cidList);

        List<String> categoryNameList = new ArrayList<>();
        categoryList.forEach(category -> {
            categoryNameList.add(category.getName());
        });

        return categoryNameList;
    }
}
