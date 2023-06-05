package com.elasticsearch.service;

import com.common.pojo.PageResult;
/*import com.elasticsearch.GoodsRepository;*/
import com.elasticsearch.client.BrandClient;
import com.elasticsearch.client.CategoryClient;
import com.elasticsearch.client.GoodsClient;
import com.elasticsearch.client.SpecificationClient;
import com.elasticsearch.pojo.Goods;
import com.elasticsearch.pojo.SearchRequest;
import com.elasticsearch.pojo.SearchResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteminterfaces.bo.SpuBo;
import com.iteminterfaces.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
/*import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;*/
import org.springframework.beans.factory.annotation.Autowired;
/*import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;*/
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    /*@Autowired
    private GoodsRepository goodsRepository;*/

    static Long brandId = null;


    private static final ObjectMapper MAPPER = new ObjectMapper();


    /**
     * 通过spu构建goods，然后注入到es数据库
     *
     * @param spu
     * @return
     * @throws JsonProcessingException
     */
//  存储商品相关信息
    public Goods buildGoods(Spu spu) throws JsonProcessingException {
        Goods goods = new Goods();
        //根据分类id查询分类名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //根据品牌id查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //根据spuId查询所有的sku
        List<Sku> skus = goodsClient.querySkusBySpuId(spu.getId());
        List<Long> prices = new ArrayList<>();
        //收集sku的必要字段信息
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            //获取sku中的图片，数据库的
            map.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuMapList.add(map);
        });

        //根据分类id获取搜索参数
        List<SpecParam> params =  this.specificationClient.queryParams(null, spu.getCid3(), null, true);

        //根据spuID查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        //通用参数反序列化
        Map<String, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        //特殊参数反序列化
        Map<String, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {
        });

        Map<String, Object> specs = new HashMap<>();
        params.forEach(param -> {
            //判断规格参数的类型，是否是通用的规格参数
            if (param.getGeneric()) {
                String value = genericSpecMap.get(param.getId().toString()).toString();
                if (param.getNumeric()) {
                    value = chooseSegment(value, param);
                }
                specs.put(param.getName(), value);
            } else {
                List<Object> value = specialSpecMap.get(param.getId().toString());
                specs.put(param.getName(), value);
            }
        });

        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //拼接all字段，需要分类名称及品牌名称
        goods.setAll(spu.getSubTitle() + " " + StringUtils.join(names, " ") + " " + brand.getName());
        //获取spu下的所有sku的价格
        goods.setPrice(prices);
        //获取sku下的所有spu
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        //获取所有查询的规格参数{name:value}
        goods.setSpecs(specs);

        return goods;
    }
//  根据给定的数值和规格参数，选择对应的数值段
    public String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其他";
        //保存数据值
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            //获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            //判断范围
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                }
            } else if (begin == 0) {
                result = segs[1] + p.getUnit() + "以下";
            } else {
                result = segment + p.getUnit();
            }
            break;
        }
        return result;
    }


    /**
     * 从MySql数据库查询
     * @param request
     * @return
     */
//    根据关键词和过滤条件进行模糊查询，并返回相应的搜索结果
    public SearchResult search1(SearchRequest request) {
        String key = request.getKey();
        //根据搜索关键词模糊查询
        Object getBrandId = request.getFilter().get("品牌");
        if (getBrandId !=null){
            Long parseBrandId = Long.parseLong(getBrandId.toString());
            if (brandId==null || parseBrandId.longValue()!=brandId.longValue()){
                //检测到品牌发生改变，立刻回到第一页
                request.setPage(1);
                brandId = parseBrandId;
            }
        }else{
            if (brandId!=null){
                request.setPage(1);
                brandId = null;
            }
        }
        List<Spu> spuList = goodsClient.querySpuByTitle(key, brandId);
        int size = spuList.size();
        long sizeLong = size;
        //添加分类和品牌的聚合
        List<Long> ids = new ArrayList<>();
        List<Map<String, Object>> categories = new ArrayList<>();
        spuList.stream().map(spu -> {
            Long id = spu.getCid3();
            if (!ids.contains(id)){
                Map<String, Object> map = new HashMap<>();
                List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(id));
                map.put("id", id);
                map.put("names", names.get(0));
                ids.add(id);
                categories.add(map);
            }
            return categories;
        }).collect(Collectors.toList());

        List<Brand> Brands = new ArrayList<>();
        spuList.stream().map(spu -> {
            if (!ids.contains(spu.getBrandId())){
                Brand brand = brandClient.queryBrandById(spu.getBrandId());
                Brands.add(brand);
                ids.add(spu.getBrandId());
            }
            return Brands;
        }).collect(Collectors.toList());


        if (size >=request.getSize()*request.getPage()){
            spuList = spuList.subList((request.getPage() - 1) * request.getSize(), request.getPage() * request.getSize());
        }
        else{
            spuList = spuList.subList((request.getPage() - 1) * request.getSize(), size);
        }

        List<Goods> goods = spuList.stream().map(spu -> {
            try {
                return buildGoods(spu);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());

        return new SearchResult(sizeLong, (size /request.getSize()+1), goods, categories, Brands, request.getPage());
    }


    /**
     * 从es数据库查询数据
     *
     * @param request
     * @return
     */
    /*public SearchResult search(SearchRequest request) {
        //自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
        *//* MatchQueryBuilder basicQuery = QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND);*//*
        BoolQueryBuilder basicQuery = buildBoolQueryBuilder(request);
        queryBuilder.withQuery(basicQuery);
        //添加分页
        queryBuilder.withPageable(PageRequest.of(request.getPage() - 1, request.getSize()));
        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));
        //添加分类和品牌的聚合
        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //执行获取结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        //获取聚合结果集并解析
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        //判断是否只有一个分类，只有一个分类时才做规格参数聚合
        List<Map<String, Object>> spec = null;
        if (!CollectionUtils.isEmpty(categories) && categories.size() == 1) {
            //对规格参数进行聚合
            spec = getParamAggResult((Long) categories.get(0).get("id"), basicQuery);
        }

        return new SearchResult(goodsPage.getTotalElements(), goodsPage.getTotalPages(), goodsPage.getContent(), categories, brands, spec);
    }*/

    /**
     * 构建布尔查询
     *
     * @param request
     * @return
     */
   /* private BoolQueryBuilder buildBoolQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //给布尔查询添加基本查询
        if (request.getKey().equals("null")||StringUtils.isBlank(request.getKey())){
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        }else{
            boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        }
        //添加过滤条件
        //获取用户过滤信息
        Map<String, Object> filter = request.getFilter();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                key = "cid3";
            } else {
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }
        return boolQueryBuilder;
    }*/

    /**
     * 根据查询条件聚合规格参数
     *
     * @param cid
     * @param basicQuery
     * @return
     */
   /* private List<Map<String, Object>> getParamAggResult(Long cid, BoolQueryBuilder basicQuery) {
        //自定义查询对象构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
        queryBuilder.withQuery(basicQuery);
        //查询聚合的规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, cid, null, true);
        //添加规格参数的聚合
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });

        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));

        //执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        List<Map<String, Object>> spec = new ArrayList<>();

        //解析聚合结果集，key-聚合名称(规格参数名) 聚合对象
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            //初始化一个map
            Map<String, Object> map = new HashMap<>();
            map.put("k", entry.getKey());
            //初始化一个options集合,收集桶中的key
            List<String> options = new ArrayList<>();
            //获取聚合
            StringTerms terms = (StringTerms) entry.getValue();
            //获取桶集合
            terms.getBuckets().forEach(bucket -> {
                options.add(bucket.getKeyAsString());
            });
            map.put("options", options);
            spec.add(map);
        }
        return spec;
    }*/

    /**
     * 解析品牌的聚合结果集
     *
     * @param aggregation
     * @return
     */
    /*private List<Brand> getBrandAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        return terms.getBuckets().stream().map(bucket -> {
            return this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());
    }*/

    /**
     * 解析分类的聚合结果集
     *
     * @param aggregation
     * @return
     */
   /* private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        return terms.getBuckets().stream().map(bucket -> {
            Map<String, Object> map = new HashMap<>();
            Long id = bucket.getKeyAsNumber().longValue();
            List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(id));
            map.put("id", id);
            map.put("names", names.get(0));
            return map;
        }).collect(Collectors.toList());
    }*/

    /*public void save(Long id) throws JsonProcessingException {
        Spu spu = this.goodsClient.querySpuById(id);
        Goods goods = this.buildGoods(spu);
        goodsRepository.save(goods);
    }*/

   /* public void delete(Long id) {
        goodsRepository.deleteById(id);
    }*/
}
