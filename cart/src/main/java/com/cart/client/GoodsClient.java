package com.cart.client;

import com.iteminterfaces.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-services")
public interface GoodsClient extends GoodsApi {
}
