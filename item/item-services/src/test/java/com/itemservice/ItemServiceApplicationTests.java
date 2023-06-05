package com.itemservice;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iteminterfaces.pojo.Brand;
import com.iteminterfaces.pojo.Spu;
import com.itemservice.mapper.BrandMapper;
import com.itemservice.service.BrandService;
import com.itemservice.service.GoodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
class ItemServiceApplicationTests {
    @Autowired
    GoodService goodService;

    @Test
    void contextLoads() {
    }

}
