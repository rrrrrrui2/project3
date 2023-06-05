package com.elasticsearch.client;

import com.iteminterfaces.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-services")
public interface BrandClient extends BrandApi {
}
