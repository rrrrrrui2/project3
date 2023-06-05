package com.itemservice.service;

import com.common.pojo.PageResult;
import com.github.pagehelper.PageInfo;
import com.iteminterfaces.pojo.Brand;
import com.iteminterfaces.pojo.Category;
import com.itemservice.mapper.BrandMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据条件分页查询并排序品牌信息
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
      /* //初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        //根据name模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }
        // 添加排序条件
        if (StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy+" "+(desc?"desc":"asc"));
        }*/
        //保留旧关键词用于判断
        //判断关键词是否为空


        //判断排序是否排序
        if (desc != null && sortBy != null) {
            if (desc != null) {
                if (desc == true) sortBy = "order by " + sortBy + " desc";
                if (desc == false) sortBy = "order by " + sortBy + " asc";
            }
        } else {
            sortBy = "";
        }
        //判断如何分页
        String limit = "";
        if (rows != -1) {
            limit = "limit " + (page - 1) * rows + "," + rows;
        }


        List<Brand> brandLists = brandMapper.selectLists("%" + key + "%", sortBy, limit);
        int total = brandMapper.selectCounts("%" + key + "%");
        //包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brandLists);
        pageInfo.setTotal(total);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }


    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //先新增brand
        Boolean flag = brandMapper.insertSelective(brand) == 1;
        //再新增中间表
        if (flag) {
            cids.forEach(cid -> {
                brandMapper.insertCategoryAndBrand(cid, brand.getId());
            });
        }
    }

    /**
     * 修改品牌
     * @param brand
     * @param cids
     */
    @Transactional
    public void editBrand(Brand brand, List<Long> cids) {
        brandMapper.updateByPrimaryKeySelective(brand);
        brandMapper.deleteCategoryAndBrand(brand.getId());
        cids.forEach(cid -> {
            brandMapper.insertCategoryAndBrand(cid, brand.getId());
        });
    }


    public List<Brand> queryBrandByCid(Long cid) {
        return this.brandMapper.selectBrandsByCid(cid);
    }

    public Brand queryBrandById(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }

    public List<Category> queryCategoryByBid(Long bid) {
        return this.brandMapper.queryCategoryByBid(bid);
    }

    public void deleteBrandByBid(Long bid) {
        brandMapper.deleteBrandByBid(bid);
    }

}
