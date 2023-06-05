package com.itemservice.service;

import com.alibaba.fastjson.JSON;
import com.iteminterfaces.pojo.Category;
import com.itemservice.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import springfox.documentation.spring.web.json.Json;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService{

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父节点查询子节点
     *
     * @param pid
     * @return
     */

    public List<Category> queryCategoriesByPid(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        return categoryMapper.select(record);
    }

    public List<String> queryNamesByIds(List<Long> ids) {
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
        return categories.stream().map(category -> category.getName()).collect(Collectors.toList());
    }

    public void saveNode(Object node) {
        String s = node.toString();
        Category category = JSON.parseObject(s, Category.class);
        Long parentId = category.getParentId();
        Category category1 = categoryMapper.selectByPrimaryKey(parentId);
        category1.setIsParent(true);
        categoryMapper.updateByPrimaryKey(category1);
        categoryMapper.insert(category);
    }

    public void updateNode(Long id, String name) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        category.setName(name);
        categoryMapper.updateByPrimaryKey(category);
    }

    public void deleteNode(Long id) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        //逻辑删除
        Long parentId = category.getParentId();
        category.setParentId(-1L);
        categoryMapper.updateByPrimaryKey(category);

        //判断父节点下的子节点是否为空，是的话设置为子节点
        List<Category> categories = categoryMapper.selectByParentId(parentId);
        if (CollectionUtils.isEmpty(categories)){
            Category category1 = categoryMapper.selectByPrimaryKey(parentId);
            category1.setIsParent(false);
            categoryMapper.updateByPrimaryKey(category1);
        }
    }
}
