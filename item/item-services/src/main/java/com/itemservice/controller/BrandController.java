package com.itemservice.controller;

import com.common.pojo.PageResult;
import com.github.pagehelper.PageInfo;
import com.iteminterfaces.pojo.Brand;
import com.iteminterfaces.pojo.Category;
import com.itemservice.service.BrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("brand")
@Api(value = "品牌管理接口")
public class BrandController {

    @Autowired
    private BrandService brandService;


    /**
     * 根据查询条件分页查询品牌信息
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    //?key=&page=1&rows=5&sortBy=id&desc=false
    @GetMapping("page")
    @ApiOperation(value = "根据查询条件分页查询品牌信息")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", required = false) Boolean desc,
            HttpServletRequest request
    ) {
        //对搜索词进行检测，发现改变立即返回第一页
        if (key == null) {
            key = "";
        }
        ;

        ServletContext servletContext = request.getServletContext();
        String key1 = (String) servletContext.getAttribute("key");
        if (!key.equals(key1)) {
            page = 1;
        }
        servletContext.setAttribute("key", key);

        PageResult<Brand> result = brandService.queryBrandByPage(key, page, rows, sortBy, desc);
        /*if (CollectionUtils.isEmpty(result.getItems())) {
            return ResponseEntity.notFound().build();
        }*/

        return ResponseEntity.ok(result);
    }


    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping("add")
    @ApiOperation(value = "新增品牌")
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 修改品牌
     * @param brand
     * @param cids
     * @return
     */
    @PutMapping("edit")
    @ApiOperation(value = "修改品牌")
    public ResponseEntity<Void> editBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.editBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }




    /**
     * 根据分类id查询品牌列表
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    @ApiOperation(value = "根据分类id查询品牌列表")
    public ResponseEntity<List<Brand>> queryBrandsByCid(@PathVariable("cid") Long cid) {
        List<Brand> brands = this.brandService.queryBrandByCid(cid);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("{id}")
    @ApiOperation(value = "根据品牌id查询品牌信息")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id) {
        Brand brand = this.brandService.queryBrandById(id);
        if (brand == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }

    /**
     * 根据品牌id即bid修改品牌信息
     * @param bid
     * @return
     */

    @GetMapping("bid/{bid}")
    @ApiOperation(value = "根据品牌id即bid修改品牌信息")
    public ResponseEntity<List<Category>> queryCategoryByBid(@PathVariable("bid") Long bid) {
        List<Category> cids = this.brandService.queryCategoryByBid(bid);
        if (CollectionUtils.isEmpty(cids)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cids);
    }


    /**
     * 根据品牌id即bid删除品牌
     * @param bid
     * @return
     */
    @GetMapping("delete/{bid}")
    @ApiOperation(value = "根据品牌id即bid删除品牌")
    public ResponseEntity<Void> deleteBrandByBid(@PathVariable("bid") Long bid) {
        this.brandService.deleteBrandByBid(bid);
        return ResponseEntity.noContent().build();
    }


}
