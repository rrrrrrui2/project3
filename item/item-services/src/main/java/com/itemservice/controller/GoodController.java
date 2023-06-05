package com.itemservice.controller;

import com.common.pojo.PageResult;
import com.github.pagehelper.Page;
import com.iteminterfaces.bo.SpuBo;
import com.iteminterfaces.pojo.Sku;
import com.iteminterfaces.pojo.Spu;
import com.iteminterfaces.pojo.SpuDetail;
import com.itemservice.service.GoodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Column;
import javax.ws.rs.Path;
import javax.xml.bind.ValidationEvent;
import java.util.List;

@Controller
@Api(value = "商品接口")
public class GoodController {

    @Autowired
    private GoodService goodService;

    /**
     * 根据条件分页查询SPU
     *
     * @param key
     * @param saleable
     * @param page
     * @param row
     * @return
     */
    @GetMapping("spu/page")
    @ApiOperation(value = "根据条件分页查询SPU")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer row
    ) {
        PageResult<SpuBo> result = this.goodService.querySpuByPage(key, saleable, page, row);
        return ResponseEntity.ok(result);
    }


    /**
     * 新增商品
     * @param spuBo
     * @return
     */
    @PostMapping("goods")
    @ApiOperation(value = "新增商品")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo) {
        this.goodService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改商品
     * @param spuBo
     * @return
     */
    @PutMapping("goods")
    @ApiOperation(value = "修改商品")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo) {
        this.goodService.updateGoods(spuBo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除商品
     * @param spuId
     * @return
     */
    @DeleteMapping("spu/delete/{spuId}")
    @ApiOperation(value = "删除商品")
    public ResponseEntity<Void> deleteGoods(@PathVariable("spuId") Long spuId) {
        this.goodService.deleteGoods(spuId);
        return ResponseEntity.noContent().build();
    }


    /**
     * 根据spuId查询spuDetail
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    @ApiOperation(value = "根据spuId查询spuDetail")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId") Long spuId) {
        SpuDetail spuDetail = this.goodService.querySpuDetailBySpuId(spuId);
        if (spuDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }


    @GetMapping("sku/list")
    @ApiOperation(value = "根据spuId查询对应的sku")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id") Long spuId) {
        List<Sku> skus = this.goodService.querySkusBySpuId(spuId);
        if (CollectionUtils.isEmpty(skus)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }

    /**
     * 根据spu的id查询spu
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation(value = "根据spu的id查询spu")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
        Spu spu = goodService.querySpuById(id);
        if (spu == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);
    }

    @GetMapping("sku/{skuId}")
    @ApiOperation(value = "根据sku的id查询sku")
    public ResponseEntity<Sku> querySkuBySkuId(@PathVariable("skuId") Long skuId) {
        Sku sku = goodService.querySkusBySkuId(skuId);
        if (sku == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sku);
    }

    /**
     * 根据商品id下架商品
     * @param spuId
     * @return
     */
    @GetMapping("spu/off/{spuId}")
    @ApiOperation(value = "根据商品id下架商品")
    public ResponseEntity<Void> offGoods(@PathVariable("spuId") Long spuId) {
        goodService.offGoods(spuId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据商品id上架商品
     * @param spuId
     * @return
     */
    @GetMapping("spu/up/{spuId}")
    @ApiOperation(value = "根据商品id上架商品")
    public ResponseEntity<Void> upGoods(@PathVariable("spuId") Long spuId) {
        goodService.upGoods(spuId);
        return ResponseEntity.noContent().build();
    }

    //根据title和brand模糊查询Spu
    @GetMapping("spu/title")
    @ApiOperation(value = "根据title和brand模糊查询Spu")
    public ResponseEntity<List<Spu>> querySpuByTitle(@RequestParam("title") String title,
                                                     @RequestParam(value = "brandId",required = false) Long brandId) {
        List<Spu> spuList =  goodService.querySpuByTitle(title,brandId);
        return ResponseEntity.ok(spuList);
    }


}
