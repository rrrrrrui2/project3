/*
package com.goodsweb.listener;

import com.goodsweb.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    //key用于跟生产方的type进行比较
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ZP.ITEM.SAVE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "ZP.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}
    ))
    public void save(Long id) {
        if (id == null) return;
        this.goodsHtmlService.createHtml(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ZP.ITEM.DELETE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "ZP.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void delete(Long id) {
        if (id == null) return;
        this.goodsHtmlService.deleteHtml(id);
    }
}
*/
