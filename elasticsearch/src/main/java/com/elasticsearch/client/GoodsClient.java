package com.elasticsearch.client;

import com.iteminterfaces.api.GoodsApi;
import com.iteminterfaces.bo.SpuBo;
import com.iteminterfaces.pojo.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("item-services")
public interface GoodsClient extends GoodsApi {

}
