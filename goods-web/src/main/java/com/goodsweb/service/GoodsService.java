package com.goodsweb.service;

import com.goodsweb.client.BrandClient;
import com.goodsweb.client.CategoryClient;
import com.goodsweb.client.GoodsClient;
import com.goodsweb.client.SpecificationClient;
import com.iteminterfaces.pojo.*;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    public Map<String, Object> loadData(Long spuId) {
        Map<String, Object> model = new HashMap<>();
        //查询spuId查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        model.put("spu", spu);
        //查询wpuDetail
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spuId);
        model.put("spuDetail", spuDetail);
        //查询分类：Map<String,Object>
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = categoryClient.queryNamesByIds(cids);
        //初始化一个分类的map
        List<Map<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }
        model.put("categories", categories);
        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        model.put("brand", brand);
        //skus
        List<Sku> skus = goodsClient.querySkusBySpuId(spuId);
        model.put("skus", skus);

        //查询规格参数组
        List<SpecGroup> groups = specificationClient.queryGroupsWithParam(spu.getCid3());
        model.put("groups", groups);

        //查询特殊的规格参数
        List<SpecParam> params = specificationClient.queryParams(null, spu.getCid3(), false, null);
        //初始化特殊规格参数的map
        Map<Long, String> paramMap = new HashMap<>();
        params.forEach(param -> {
            paramMap.put(param.getId(), param.getName());
        });
        model.put("paramMap", paramMap);
        return model;

    }
}
