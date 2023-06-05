package com.itemservice.controller;

import com.iteminterfaces.pojo.Category;
import com.itemservice.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
@Api(value = "分类接口")
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点查询子节点
     * @param pid
     * @return
     */
    @GetMapping("list")
    @ApiOperation(value = "根据父节点查询子节点")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam(value = "pid", defaultValue = "0") Long pid) {
        if (pid == null || pid < 0) {
            //400参数不合法
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            return ResponseEntity.badRequest().build();
        }
        List<Category> categories = categoryService.queryCategoriesByPid(pid);
        if (CollectionUtils.isEmpty(categories)) {
            //404未找到
            return ResponseEntity.notFound().build();
        }
        //200 查询成功
        return ResponseEntity.ok(categories);

        /*//500：服务器内部错误
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();*/
    }

    @GetMapping
    @ApiOperation(value = "根据节点id查询节点名称")
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("id") List<Long> ids) {
        List<String> names = categoryService.queryNamesByIds(ids);
        if (CollectionUtils.isEmpty(names)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }


    /**
     * 增加节点
     * @param node
     * @return
     */
    @GetMapping("add")
    @ApiOperation(value = "新增节点")
    public ResponseEntity<Void> addNode(@RequestParam(value = "node") Object node){
        categoryService.saveNode(node);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 修改节点
     * @param id
     * @param name
     * @return
     */
    @GetMapping("edit")
    @ApiOperation(value = "修改节点")
    public ResponseEntity<Void> updateNode(@RequestParam(value = "id") Long id,
                                           @RequestParam(value = "name") String name){
        categoryService.updateNode(id,name);
        return ResponseEntity.noContent().build();
    }


    /**
     * 删除节点
     * @param id
     * @return
     */
    @GetMapping("delete")
    @ApiOperation(value = "删除节点")
    public ResponseEntity<Void> deleteNode(@RequestParam(value = "id") Long id){
        categoryService.deleteNode(id);
        return ResponseEntity.noContent().build();
    }

}
