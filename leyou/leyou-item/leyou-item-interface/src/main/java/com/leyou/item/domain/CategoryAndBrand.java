package com.leyou.item.domain;

import javax.persistence.Table;

@Table(name = "tb_category_brand")
public class CategoryAndBrand {

    private Long categoreId;

    private Long brandId;

    public Long getCategoreId() {
        return categoreId;
    }

    public void setCategoreId(Long categoreId) {
        this.categoreId = categoreId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }
}
