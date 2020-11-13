package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.item.common.domain.PageResult;
import com.leyou.item.domain.Brand;
import com.leyou.item.domain.CategoryAndBrand;
import com.leyou.mapper.BrandMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据查询条件分页并排序查询品牌信息
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {

        // 初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        // 根据name模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }

        // 添加分页条件
        PageHelper.startPage(page, rows);

        // 添加排序条件
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ? "desc":"asc"));
        }

        List<Brand> brandList = this.brandMapper.selectByExample(example);

        // 包装成pageInfo对象
        PageInfo<Brand> pageInfo = new PageInfo<>(brandList);

        // 包装成分页结果集返回
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());

    }

    /**
     * 品牌新增
     *
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {

        // 首先新增表tb_brand
        this.brandMapper.insertSelective(brand);

        // 再新增中间表tb_category_brand
        cids.forEach(cid -> {
            this.brandMapper.insertCategoryAndBrand(cid, brand.getId());
        });

    }


    /**
     * 修改品牌
     *
     * @param brand
     * @param cids
     * @return
     */
    @Transactional
    public void updateBrand(Brand brand, List<Long> cids) {

        // 判断数据库是否有该品牌，如果有则直接删除
        Brand localBrand = this.brandMapper.selectByPrimaryKey(brand.getId());

        if (localBrand != null) {
            this.brandMapper.deleteByPrimaryKey(brand.getId());
        }

        // 判断数据库是否有品牌与分类的关联数据，如果有直接删除
        List<CategoryAndBrand> categoryAndBrandList = this.brandMapper.selectCategoryAndBrandByBid(brand.getId());

        if (!CollectionUtils.isEmpty(categoryAndBrandList)) {
            this.brandMapper.deleteCategoryAndBrandByBid(brand.getId());
        }

        // 最后再对品牌数据进行添加
        saveBrand(brand, cids);

    }

    /**
     * 根据商品主键查询品牌信息
     *
     * @param cid
     * @return
     */
    public List<Brand> queryBrandByCid(Long cid) {

        return this.brandMapper.selectBrandByCid(cid);
    }

}
