package com.zp.myeshopinventory.mapper;

import com.zp.myeshopinventory.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Author zp
 * @create 2020/10/19 9:31
 */
@Mapper
public interface InventoryMapper {
    @Select("select *  from inventory where id = #{id}")
    Inventory selectByPk(int id);


    @Update("update inventory set cnt = #{cnt} where id = #{id}")
    void updateByPk(int id, int cnt);
}
