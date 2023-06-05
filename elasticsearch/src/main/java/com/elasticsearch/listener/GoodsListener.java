/*
package com.elasticsearch.listener;

import com.elasticsearch.service.SearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ZP.SEARCH.SAVE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "ZP.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}
    ))
    public void save(Long id) throws JsonProcessingException {
        if (id == null) return;
        System.out.println("search开始同步");
        this.searchService.save(id);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "ZP.SEARCH.DELETE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "ZP.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}
    ))
    public void delete(Long id) {
        if (id == null) return;
        this.searchService.delete(id);

    }
}
*/
