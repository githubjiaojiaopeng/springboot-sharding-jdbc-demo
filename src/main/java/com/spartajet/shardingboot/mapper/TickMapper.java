package com.spartajet.shardingboot.mapper;

import com.spartajet.shardingboot.bean.Tick;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @description
 * @create 2017-02-07 下午9:58
 * @email gxz04220427@163.com
 */
@Mapper
public interface TickMapper {

    @Insert("insert into tick (id,name,exchange,ask,bid,time) values (#{id},#{name},#{exchange},#{ask},#{bid},#{time})")
    void insertTick(Tick tick);


    @SelectProvider(type = TickProvider.class , method = "listTickByCondition")
    List<Tick> listTickByCondition(@Param(value = "name")String name , @Param(value = "exchange") String exchange , @Param(value = "startDate") LocalDate startDate,@Param(value = "endDate")LocalDate endDate);

    @Select("select t.id , t.`name` , t.exchange , t.ask , t.bid , t.time from tick as t")
    List<Tick> listTickAll();

    @SelectProvider(type = TickProvider.class , method = "listTickForPage")
    List<Tick> listTickForPage(@Param(value = "name")String name , @Param(value = "exchange") String exchange , @Param(value = "startDate") LocalDate startDate,@Param(value = "endDate")LocalDate endDate,@Param(value = "startPage") Integer startPage, @Param(value = "pageSize") Integer pageSize);




}
