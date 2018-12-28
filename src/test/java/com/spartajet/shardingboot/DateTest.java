package com.spartajet.shardingboot;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @Author hzj
 * @Date 2017/7/7 15:50
 * @Description :
 */

public class DateTest {


    public static void main (String args[]){

        Date start = new Date(System.currentTimeMillis());

//        java.util.Date  date = new java.util.Date(start.getTime());

        LocalDate startTime = LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()).toLocalDate();
//        LocalDate endTime = LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault()).toLocalDate();


        System.out.println(startTime.toString());


    }


}
