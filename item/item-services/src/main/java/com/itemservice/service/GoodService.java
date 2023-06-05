package com.itemservice.service;

import com.common.pojo.PageResult;
import com.iteminterfaces.bo.SpuBo;
import com.iteminterfaces.pojo.*;
import com.itemservice.mapper.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer row) {

        if (key == null) {
            key = "";
        }

        String sale = "";
        //添加过滤条件
        if (saleable != null) {
            sale = "and saleable = " + saleable;
        }

        String limit = "";
        if (row != -1) {     //row==-1即为全部
            limit = "limit " + (page - 1) * row + "," + row;
        }

        //执行查询，获取spu集合
        List<Spu> spus = this.spuMapper.selectLists("%" + key + "%", sale, limit);

        //spu集合转换为spubo集合
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);//实现属性拷贝
            //品牌名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            List<String> names = this.categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "-"));
            spuBo.setBname(brand.getName());
            return spuBo;
        }).collect(Collectors.toList());


        //返回pageResult<SpuBo>
        return new PageResult<SpuBo>(spuMapper.selectCounts("%" + key + "%", sale), spuBos);
    }

    @Transactional
    public void saveGoods(SpuBo spuBo) {
        //先新增spu
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);
        //再去新增spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);
        //新增sku
        saveSkuAndStock(spuBo);

        //sendMeg("insert", spuBo.getId());

    }

    //生产消息（type作为标识,消费方根据key进行接收）
    private void sendMeg(String type, Long id) {
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            //新增sku
            sku.setId(null);
            sku.setPrice(sku.getPrice());
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            //新增stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        });
        return skus;
    }

    @Transactional
    public void updateGoods(SpuBo spuBo) {
        //查询spuId查询要删除的sku
        Sku record = new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            //删除stock
            this.stockMapper.deleteByPrimaryKey(sku.getId());
        });

        //删除sku
        this.skuMapper.delete(record);
        //新增sku,新增stock
        this.saveSkuAndStock(spuBo);
        //更新spu和spuDetail
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
        //sendMeg("update", spuBo.getId());
    }


    public Spu querySpuById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    public Sku querySkusBySkuId(Long skuId) {
        return skuMapper.selectByPrimaryKey(skuId);
    }

    public void offGoods(Long spuId) {
        spuMapper.offGoods(spuId);
    }

    public void upGoods(Long spuId) {
        spuMapper.upGoods(spuId);
    }

    public void deleteGoods(Long spuId) {
        spuMapper.deleteGoods(spuId);
    }


    public List<Spu> querySpuByTitle(String title,Long brandId) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //根据name模糊查询，或者根据首字母查询
        criteria.andLike("title","%"+title+"%");
        criteria.andEqualTo("saleable",true);
        criteria.andEqualTo("valid",1);
        if (brandId!=null){
            criteria.andEqualTo("brandId",brandId);
            System.out.printf("进来");
        }
        List<Spu> spus = spuMapper.selectByExample(example);
        return spus;
    }
}
