package com.itemservice.controller;

import com.iteminterfaces.pojo.SpecGroup;
import com.iteminterfaces.pojo.SpecParam;
import com.itemservice.service.SpecificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.PUT;
import java.util.List;

@Controller
@RequestMapping("spec")
@Api(value = "品牌管理接口")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;


    /**
     * 根据cid查询参数组
     *
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    @ApiOperation(value = "根据cid查询参数组")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> groups = specificationService.queryGroupsByCid(cid);
        if (CollectionUtils.isEmpty(groups)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    /**
     * 根据Gid查询规格参数
     *
     * @param gid
     * @return
     */
    @GetMapping("params")
    @ApiOperation(value = "根据Gid查询规格参数")
    public ResponseEntity<List<SpecParam>> queryParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching
    ) {
        List<SpecParam> params = this.specificationService.queryParams(gid, cid, generic, searching);
        if (CollectionUtils.isEmpty(params)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }


    /**
     * 根据cid新增分组
     * @param specGroup
     * @return
     */
    @PostMapping("group/add")
    @ApiOperation(value = "根据cid新增分组")
    public ResponseEntity<Void> addGroupByCid(@RequestBody SpecGroup specGroup) {
        specificationService.addGroupByCid(specGroup.getCid(),specGroup.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 根据cid修改分组
     * @param specGroup
     * @return
     */
    @PutMapping("group/edit")
    @ApiOperation(value = "根据cid修改分组")
    public ResponseEntity<Void> editGroupByCid(@RequestBody SpecGroup specGroup) {
        specificationService.editGroupByCid(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据cid删除分组
     * @param cid
     * @return
     */
    @DeleteMapping("group/delete/{cid}")
    @ApiOperation(value = "根据cid删除分组")
    public ResponseEntity<Void> deleteGroupByCid(@PathVariable("cid") Long cid) {
        specificationService.deleteGroupByCid(cid);
        return ResponseEntity.noContent().build();
    }


    /**
     * 添加参数
     * @param specParam
     * @return
     */
    @PostMapping("param/add")
    @ApiOperation(value = "添加参数")
    public ResponseEntity<Void> addParam(@RequestBody SpecParam specParam){
        specificationService.addParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改参数
     * @param specParam
     * @return
     */
    @PutMapping("param/edit")
    @ApiOperation(value = "修改参数")
    public ResponseEntity<Void> editParam(@RequestBody SpecParam specParam){
        specificationService.editParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 删除参数
     * @param pid
     * @return
     */
    @DeleteMapping("param/delete/{pid}")
    @ApiOperation(value = "删除参数")
    public ResponseEntity<Void> deleteParam(@PathVariable Long pid){
        specificationService.deleteParam(pid);
        return ResponseEntity.noContent().build();
    }




    @GetMapping("group/param/{cid}")
    @ApiOperation(value = "通过规格参数id查询规格参数组")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid") Long cid) {
        List<SpecGroup> groups = this.specificationService.queryGroupWithParam(cid);
        if (CollectionUtils.isEmpty(groups)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }






}
