package com.spartajet.shardingboot;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetClass {

    public static void main(String[] args) throws ClassNotFoundException, ParseException {
        /*arraysSort();
        collectionsSort();
        getCompareTo();*/

    }

    /**
     * 时间排序
     * @throws ParseException
     */
    private static void collectionsSort() throws ParseException {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        List<Date> list=new ArrayList<Date>();
        list.add(dateFormat.parse("2018-12-21"));
        list.add(dateFormat.parse("2018-12-19"));
        list.add(dateFormat.parse("2018-12-20"));
        Collections.sort(list);
        for (Date a:list) {
            System.out.println(a);
        }
    }

    /**
     * 排序
     */
    private static void arraysSort() {
        int array[]=new int[]{1,3,5,7,2,4,6,10,8,9};
        Arrays.sort(array);
    }

    private static void getCompareTo() {
        //金额做比较 =-1 =0 =1 的三种情况
        BigDecimal bigDecimal=new BigDecimal(100);
        BigDecimal bigDecimal2=new BigDecimal(200);
        BigDecimal bigDecimal3=new BigDecimal(100);
        System.out.println(bigDecimal.compareTo(bigDecimal2)+" \n\t"+bigDecimal.compareTo(bigDecimal3)+" \n\t"+bigDecimal2.compareTo(bigDecimal3));
    }
}
