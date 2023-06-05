package com.itemservice.mapper;

import com.iteminterfaces.pojo.Brand;
import com.iteminterfaces.pojo.Spu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuMapper extends Mapper<Spu> {
    @Select("select * from tb_spu where  title like #{title} and valid = '1' ${sale} ${limit}")
    List<Spu> selectLists(@Param("title") String title, @Param("sale") String sale, @Param("limit") String limit);

    @Select("select count(*) from tb_spu where title like #{title} and valid = '1'  ${sale}")
    Long selectCounts(@Param("title") String title, @Param("sale") String sale);

    @Update("update tb_spu set saleable = '0' where id = #{spuId}")
    void offGoods(@Param("spuId") Long spuId);

    @Update("update tb_spu set saleable = '1' where id = #{spuId}")
    void upGoods(@Param("spuId") Long spuId);

    @Update("update tb_spu set valid = '0' where id = #{spuId}")
    void deleteGoods(@Param("spuId") Long spuId);
}
