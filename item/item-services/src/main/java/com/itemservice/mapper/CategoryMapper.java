package com.itemservice.mapper;

import com.iteminterfaces.pojo.Category;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category, Long> {

    @Select("select * from tb_category where parent_id = #{parentId}")
   List<Category> selectByParentId(Long parentId);
}
