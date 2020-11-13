package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.item.common.domain.PageResult;
import com.leyou.item.domain.Sku;
import com.leyou.item.domain.Spu;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.domain.SpuDetail;
import com.leyou.item.domain.Stock;
import com.leyou.mapper.BrandMapper;
import com.leyou.mapper.SkuMapper;
import com.leyou.mapper.SpuDetailMapper;
import com.leyou.mapper.SpuMapper;
import com.leyou.mapper.StockMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    /**
     * 按照条件查询商品列表
     *
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        // 判断搜索条件
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        // 分页条件
        PageHelper.startPage(page, rows);

        // 执行查询
        List<Spu> spuList = this.spuMapper.selectByExample(example);
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spuList);

        List<SpuBo> spuBoList = new ArrayList<>();

        spuList.forEach(spu -> {

            SpuBo spuBo = new SpuBo();

            // 赋值共同属性到新的对象
            BeanUtils.copyProperties(spu, spuBo);

            // 查询分类名称
            List<String> nameList = this.categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(nameList, "/"));

            // 查询品牌名称
            String brandName = this.brandMapper.selectByPrimaryKey(spu.getBrandId()).getName();

            spuBo.setBname(brandName);


            spuBoList.add(spuBo);

        });

        return new PageResult<>(spuPageInfo.getTotal(), spuBoList);

    }

    /**
     * 新增商品信息
     *
     * @param spuBo
     */
    @Transactional
    public void saveGoods(SpuBo spuBo) {

        // 首先新增spu
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insert(spuBo);

        // 再新增SpuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);

        // 最后新增Sku和Stock信息
        saveSkuAndStock(spuBo);

    }

    /**
     * 存储Sku和Stock信息
     *
     * @param spuBo
     */
    private void saveSkuAndStock(SpuBo spuBo) {

        spuBo.getSkus().forEach(sku -> {

            // 新增sku
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            // 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);

        });

    }

    /**
     * 修改商品信息
     *
     * @param spuBo
     * @return
     */
    @Transactional
    public void updateGoods(SpuBo spuBo) {

        // 根据spuId查询历史Sku信息
        Sku recond = new Sku();
        recond.setSpuId(spuBo.getId());
        List<Sku> skuList = this.skuMapper.select(recond);

        // 判断当历史sku不为空时
        if (!CollectionUtils.isEmpty(skuList)) {

            // 遍历删除所有的库存信息
            skuList.forEach(sku -> {

                this.stockMapper.deleteByPrimaryKey(sku.getStock());

            });

            // 删除历史所有的Sku
            Sku sku = new Sku();
            sku.setSpuId(spuBo.getId());
            this.skuMapper.delete(sku);

            //List<Long> idList = skuList.stream().map(sku -> sku.getId()).collect(Collectors.toList());
            //
            //Example example = new Example(Stock.class);
            //example.createCriteria().andIn("skuId", idList);
            //this.stockMapper.deleteByExample(example);
            //
            //// 删除以前的sku
            //Sku sku = new Sku();
            //sku.setSpuId(spuBo.getId());
            //this.skuMapper.delete(sku);

        }

        // 新增sku和stock库存
        saveSkuAndStock(spuBo);

        // 更新spu
        spuBo.setLastUpdateTime(new Date());
        spuBo.setCreateTime(null);
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);

        // 更新spuDetail
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

    }

    /**
     * 根据spuId查找SpuDetail信息
     *
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spuId查找sku集合信息，并同时将库存信息返回
     *
     * @param id
     * @return
     */
    public List<Sku> querySkuBySpuId(Long id) {

        Sku sku = new Sku();
        sku.setSpuId(id);

        List<Sku> skuList = this.skuMapper.select(sku);

        skuList.forEach(s -> {

            Stock stock = this.stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());

        });

        return skuList;

    }

}
