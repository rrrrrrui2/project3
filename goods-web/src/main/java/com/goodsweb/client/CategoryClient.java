package com.goodsweb.client;

import com.iteminterfaces.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-services")
public interface CategoryClient extends CategoryApi {
}
