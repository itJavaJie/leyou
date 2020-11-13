package com.leyou.controller;

import com.leyou.item.domain.Category;
import com.leyou.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据子节点chaxun
     *
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam(value = "pid", defaultValue = "0")Long pid) {

        if (pid == null || pid < 0) {
            // 400：参数错误
            return ResponseEntity.badRequest().build();
        }

        List<Category> categoryList = this.categoryService.queryCategoriesByPid(pid);

        if (CollectionUtils.isEmpty(categoryList)) {
            // 404：资源服务器未找到
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoryList);

    }

    /**
     * 根据bid修改品牌信息
     *
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> modifyBand(@PathVariable("bid")Long bid) {

        List<Category> categoryList = this.categoryService.queryBrandByBid(bid);

        if (CollectionUtils.isEmpty(categoryList)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoryList);

    }

}
