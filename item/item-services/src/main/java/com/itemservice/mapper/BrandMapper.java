package com.itemservice.mapper;

import com.github.pagehelper.Page;
import com.iteminterfaces.pojo.Brand;
import com.iteminterfaces.pojo.Category;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.ArrayList;
import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    @Insert("insert into tb_category_brand(category_id,brand_id) values (#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long id);

    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    void deleteCategoryAndBrand(@Param("bid") Long id);

    @Select("select * from tb_brand where(( name like #{key} or letter like #{key}) and letter != '1') ${sortBy} ${limit}")
    List<Brand> selectLists(@Param("key") String key, @Param("sortBy") String sortBy, @Param("limit") String limit);

    @Select("select count(*) from tb_brand where (name like #{key} or letter like #{key}) and letter != '1' ")
    int selectCounts(@Param("key") String key);

    @Select("select * from tb_brand where letter != '1' and id in (select brand_id from tb_category_brand where category_id=#{cid})")
    List<Brand> selectBrandsByCid(Long cid);

    @Select("select * from tb_category where id in (select category_id from tb_category_brand where brand_id = #{bid})")
    List<Category> queryCategoryByBid(Long bid);

    @Update("update tb_brand set letter = '1' where id = #{bid} ")
    void deleteBrandByBid(Long bid);

}
