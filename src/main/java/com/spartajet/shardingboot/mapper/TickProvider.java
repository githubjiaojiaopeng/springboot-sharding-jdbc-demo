package com.spartajet.shardingboot.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * @Author hzj
 * @Date 2017/7/6 17:59
 * @Description :
 */

public class TickProvider {


    private final static String LIST_TICK_SQL ="select t.id , t.`name` , t.exchange , t.ask , t.bid , t.time from tick as t where 1=1 ";

    /**
     *
     *
     * @param name
     * @param exchange
     * @param startDate
     * @param endDate
     * @return
     */
    public String  listTickByCondition(@Param(value = "name")String name , @Param(value = "exchange") String exchange , @Param(value = "startDate") LocalDate startDate, @Param(value = "endDate")LocalDate endDate){
        StringBuilder sb = new StringBuilder(LIST_TICK_SQL);


        if(!StringUtils.isEmpty(name)){
            sb.append(" and t.name = #{name} ");
        }
        if(!StringUtils.isEmpty(exchange)){
            sb.append(" and t.exchange = #{exchange} ");
        }
        if(startDate != null && endDate != null){
            sb.append(" and t.time between #{startDate} and #{endDate} ");
        }

        return sb.toString();

    }


    public String listTickForPage(@Param(value = "name")String name , @Param(value = "exchange") String exchange , @Param(value = "startDate") LocalDate startDate,@Param(value = "endDate")LocalDate endDate,@Param(value = "startPage") Integer startPage, @Param(value = "pageSize") Integer pageSize){

        String sql = listTickByCondition(name,exchange,startDate,endDate);

        StringBuilder sb = new StringBuilder(sql);
        sb.append(" order by id ");
        if(startPage!=null && pageSize !=null){
            sb.append(" limit #{startPage} , #{pageSize} ");
        }
        return sb.toString();
    }

}
