package com.elasticsearch;

import com.common.pojo.PageResult;
import com.elasticsearch.client.GoodsClient;
import com.elasticsearch.pojo.Goods;
import com.elasticsearch.service.SearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.iteminterfaces.bo.SpuBo;
/*import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.SearchContextAggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;*/
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
/*import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;*/
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchApplicationTests {

   /* @Autowired
    private GoodsRepository goodsRepository;*/

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsClient goodsClient;

   /* @Test
    public void test() {
        Integer page = 1;
        Integer rows = 100;

        do {
            //分页查询spu,获取分页结果集
            PageResult<SpuBo> pageResultResponseEntity = this.goodsClient.querySpuByPage(null, null, page, rows);

            //获取当前页的数据
            List<SpuBo> items = pageResultResponseEntity.getItems();

            //处理List<SpuBo>
            List<Goods> collect = items.stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods(spuBo);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());

            //执行新增数据的方法
            this.goodsRepository.saveAll(collect);
            rows = items.size();
            page++;
        } while (rows == 100);


    }*/

 /*  public void contextLoads(){
        List<Item> list = new ArrayList<>();
        list.add(new Item(5L,"努基亚手机","手机","华为",3499.0,"10.PNG"));
        list.add(new Item(4L,"锤子手机","手机","华为",3499.0,"10.PNG"));
       this.itemRepository.saveAll(list);
        Optional<Item> optionalItem =  this.itemRepository.findById(1L);
        System.out.println(optionalItem.get());
       this.elasticsearchTemplate.createIndex(Item.class);
       this.elasticsearchTemplate.putMapping(Item.class);
    }*/


    /*public void testSearch(){
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title","小米手机");
        Iterable<Item> items = this.itemRepository.search(matchQueryBuilder);
        items.forEach(System.out::println);
    }*/

    /*public void testAggs(){
       //初始化自定义查询构建器
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //添加聚合
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("brandAgg").field("brand.keyword"));
        //添加结果集过滤
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        //执行聚合查询
        AggregatedPage<Item> itemPage = (AggregatedPage<Item>) itemRepository.search(nativeSearchQueryBuilder.build());
        //解析聚合结果集
        StringTerms brandAgg = (StringTerms) itemPage.getAggregation("brandAgg");
        //获取捅集合
        List<StringTerms.Bucket> buckets = brandAgg.getBuckets();
        buckets.forEach(bucket -> {
            System.out.println(bucket.getKeyAsString());
            System.out.println(bucket.getDocCount());
        });
   }*/


    /*public void testSubAggs(){
        //初始化自定义查询构建器
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //添加聚合
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("brandAgg").field("brand.keyword").subAggregation(AggregationBuilders.avg("price_avg").field("price")));
        //添加结果集过滤
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        //执行聚合查询
        AggregatedPage<Item> itemPage = (AggregatedPage<Item>) itemRepository.search(nativeSearchQueryBuilder.build());
        //解析聚合结果集
        StringTerms brandAgg = (StringTerms) itemPage.getAggregation("brandAgg");
        //获取捅集合
        List<StringTerms.Bucket> buckets = brandAgg.getBuckets();
        buckets.forEach(bucket -> {
            System.out.println(bucket.getKeyAsString());
            System.out.println(bucket.getDocCount());
            //获取字聚合的map集合：key--聚合名称，value--对应的子聚合对象
           Map<String, Aggregation> stringAggregationMap  = bucket.getAggregations().asMap();
           InternalAvg price_avg = (InternalAvg) stringAggregationMap.get("price_avg");
           System.out.println(price_avg.getValue());
        });
    }*/


}
