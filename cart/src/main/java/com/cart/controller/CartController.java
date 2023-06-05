package com.cart.controller;

import com.cart.pojo.Cart;
import com.cart.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(value = "购物车接口")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    @ApiOperation(value = "加入购物车")
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @ApiOperation(value = "查看购物车")
    public ResponseEntity<List<Cart>> queryCarts() {
        List<Cart> carts = cartService.queryCarts();
        if (CollectionUtils.isEmpty(carts)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carts);
    }

    @PutMapping
    @ApiOperation(value = "更新购物车")
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart) {
        cartService.updateNum(cart);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("delete")
    @ApiOperation(value = "删除购物车")
    public ResponseEntity<Void> deleteNum(@RequestBody Cart cart) {
        cartService.deleteCart(cart);
        return ResponseEntity.noContent().build();
    }
}
