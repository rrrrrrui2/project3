package com.iteminterfaces.api;

import com.common.pojo.PageResult;
import com.iteminterfaces.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RequestMapping("brand/")
public interface BrandApi {

    @GetMapping("{id}")
    abstract Brand queryBrandById(@PathVariable("id") Long id);


}
