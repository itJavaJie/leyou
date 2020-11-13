package com.leyou.controller;

import com.leyou.item.domain.SpecGroup;
import com.leyou.item.domain.SpecParam;
import com.leyou.service.SpecificationService;
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
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id查询规格参数组
     *
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupByCid(@PathVariable("cid")Long cid) {

        List<SpecGroup> specGroupList = this.specificationService.querySpecGroupByCid(cid);

        if (CollectionUtils.isEmpty(specGroupList)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(specGroupList);

    }

    /**
     * 根据规格组id查询规格参数信息
     *
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParamsByGid(
            @RequestParam(value = "gid", required = false)Long gid,
            @RequestParam(value = "cid", required = false)Long cid,
            @RequestParam(value = "generic", required = false)Boolean generic,
            @RequestParam(value = "searching", required = false)Boolean searching) {

        List<SpecParam> specParamList = this.specificationService.querySpecParamsByGid(gid, cid, generic, searching);

        if (CollectionUtils.isEmpty(specParamList)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(specParamList);

    }

}
