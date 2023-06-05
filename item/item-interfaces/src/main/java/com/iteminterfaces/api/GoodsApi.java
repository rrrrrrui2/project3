package com.iteminterfaces.api;

import com.common.pojo.PageResult;
import com.iteminterfaces.bo.SpuBo;
import com.iteminterfaces.pojo.Sku;
import com.iteminterfaces.pojo.Spu;
import com.iteminterfaces.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {
    @GetMapping("spu/detail/{spuId}")
    SpuDetail querySpuDetailBySpuId(@PathVariable("spuId") Long spuId);

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
    public PageResult<SpuBo> querySpuByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer row
    );

    @GetMapping("sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id") Long spuId);

    @GetMapping("{id}")
    public Spu querySpuById(@PathVariable("id") Long id);

    @GetMapping("sku/{skuId}")
    public Sku querySkuBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("spu/title")
    public List<Spu> querySpuByTitle(@RequestParam("title") String title,
                                      @RequestParam(value = "brandId", required = false) Long brandId);
}
