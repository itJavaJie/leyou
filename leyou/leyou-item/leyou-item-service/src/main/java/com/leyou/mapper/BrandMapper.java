package com.leyou.mapper;

import com.leyou.item.domain.Brand;
import com.leyou.item.domain.CategoryAndBrand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    /**
     * 品牌新增,添加品牌与商品之间关联数据
     *
     * @param cid
     * @param bid
     */
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid}, #{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    /**
     * 根据商品主键查询品牌信息
     *
     * @param cid
     * @return
     */
    @Select("SELECT * FROM tb_brand WHERE id IN (SELECT brand_id FROM tb_category_brand WHERE category_id = #{cid})")
    List<Brand> selectBrandByCid(@Param("cid") Long cid);

    /**
     * 根据bid查询品牌与分类关联表数据
     *
     * @param bid
     * @return
     */
    @Select("SELECT * FROM `tb_category_brand` WHERE brand_id = #{bid}")
    List<CategoryAndBrand> selectCategoryAndBrandByBid(@Param("bid") Long bid);

    /**
     * 根据bid删除品牌与分类关联表数据
     *
     * @param id
     */
    @Delete("DELETE FROM `tb_category_brand` WHERE brand_id = #{bid}")
    void deleteCategoryAndBrandByBid(@Param("bid") Long bid);
}
