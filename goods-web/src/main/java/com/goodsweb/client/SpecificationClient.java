package com.goodsweb.client;

import com.iteminterfaces.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-services")
public interface SpecificationClient extends SpecificationApi {
}
