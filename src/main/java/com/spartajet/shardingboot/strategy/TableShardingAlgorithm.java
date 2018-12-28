package com.spartajet.shardingboot.strategy;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.MultipleKeysTableShardingAlgorithm;
import com.google.common.collect.Sets;
import com.spartajet.shardingboot.common.GsonUtils;
import com.spartajet.shardingboot.common.ShardingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 多分片键   分表算法
 *
 * @description
 * @create 2017-02-07 下午9:23
 * @email gxz04220427@163.com
 */
@Slf4j
@Service
public class TableShardingAlgorithm implements MultipleKeysTableShardingAlgorithm {
    /**
     * 根据分片值计算分片结果名称集合.<br/>
     *  <h3>多分片键分表算法</h3>
     *  <p>
     *      1、获取每个分片键的value集合;<br/>
     *      2、获取个分片键value集合的笛卡尔积；<br/>
     *      3、通过笛卡尔积组装表名先关信息，匹配目标表；<br/>
     *      4、返回匹配到的目标表集合；<br/>
     *  </p>
     *  <h3>多分片键分表算法注意事项</h3>
     *  <p>
     *      1、需要和单分片键分表策略一样，对 = 、 IN 、 between...and 三种情况分别处理；<br/>
     *      2、主要有些分片键是不能够使用 between...and 条件；<br/>
     *      3、在多分片键分表算法时，如果其中一个分片键没有包含在shardingValues中时，就会导致目标表不能命中（多分片键要么都存在，要么都不存在，如果部分存在，则就会到导致无法命中目标表）；
     *          因此需要处理SQL条件中分片键部分存在的情况<br/>
     *  </p>
     *
     * @param availableTargetNames 所有的可用目标名称集合, 一般是数据源或表名称
     * @param shardingValues       分片值集合
     *
     * @return 分片后指向的目标名称集合, 一般是数据源或表名称
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, Collection<ShardingValue<?>> shardingValues) {
        log.info("[表策略] targetTableNames:{}，分片健对应的值为：{}", GsonUtils.objToJson(availableTargetNames),GsonUtils.objToJson(shardingValues));

        Collection<String> result = new LinkedHashSet<>();

        Set<Object> nameValueSet = getShardingValue(shardingValues,"name");
        Set<Object> timeValueSet = getShardingValue(shardingValues,"time");

        //计算两个集合的笛卡尔积
        Set<List<Object>> valueResult = Sets.cartesianProduct(nameValueSet, timeValueSet);

        log.info("[库策略] 当前SQL语句分片键值的笛卡尔积为：{}",GsonUtils.objToJson(valueResult));

        for (List<Object> value : valueResult) {
            //组装多个分片键的值，确定其对应的目标数据表
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM");
            String timeString = ((LocalDate)value.get(1)).format(formatter);
//          String timeString = new SimpleDateFormat("yyyy_MM").format(value.get(1));
            String suffix = (String)value.get(0) + "_" + timeString;

            for (String tableName : availableTargetNames) {
                if (tableName.endsWith(suffix)) {
                    result.add(tableName);
                }
            }
        }
        log.info("[表策略] 目标数据表集合为：{}",GsonUtils.objToJson(result));
        if(result.isEmpty())
            log.info("[表策略] ------error，当前SQL中的分片键值未命中目标数据库表-----");

        return result;
    }

    /**
     * 获取指定分片键对应的分片值的集合
     *
     * @param shardingValues
     * @param shardingKey
     * @return
     */
    private  Set<Object> getShardingValue(final Collection<ShardingValue<?>> shardingValues, final String shardingKey) {
        Set<Object> valueSet = new HashSet<>();
        ShardingValue<?> shardingValue = null;
        //获取当前分片键的value值
        for (ShardingValue<?> each : shardingValues) {
            if (each.getColumnName().equals(shardingKey)) {
                shardingValue =  each;
                break;
            }
        }
        //如果当前SQL中没有以改分片键作为条件，返回默认的分片键值
        if (null == shardingValue) {
            return  ShardingUtils.defaultShardingValues(shardingKey);
        }
        switch (shardingValue.getType()) {
            case SINGLE:
                log.info("[表策略] 当前SQL语句对应的分片键条件为： equal(=) ");
                valueSet.add(shardingValue.getValue());
                break;
            case LIST:
                log.info("[表策略] 当前SQL语句对应的分片键条件为： IN ");
                valueSet.addAll(shardingValue.getValues());
                break;
            case RANGE:
                log.info("[表策略] 当前SQL语句对应的分片键条件为： between ... and ");
                if("time".equals(shardingKey)){

                    /**
                     *此处需要注意，java.sql.Date 不能通过强转为 java.util.Date 后 调用ofInstant 的方法 转化话为LocalDate方法
                     *
                     */
                    Date start = new Date(((java.sql.Date)shardingValue.getValueRange().lowerEndpoint()).getTime());
                    Date end = new Date(((java.sql.Date)shardingValue.getValueRange().upperEndpoint()).getTime());

                    LocalDate startTime = LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()).toLocalDate();
                    LocalDate endTime = LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault()).toLocalDate();

                    LocalDate tableStart =  LocalDate.of(2017,4,1);
                    LocalDate tableend =  LocalDate.of(2017,5,1);

                    if(!tableStart.isBefore(startTime) && !tableStart.isAfter(endTime)){
                        //添加其实日期到
                        valueSet.add(tableStart);
                    }

                    if(!tableend.isBefore(startTime) && !tableend.isAfter(endTime)){
                        valueSet.add(tableend);
                    }

                }else{
                    log.error("[表策略] sorry, 此键值分片不支持使用 between ... and  语句");
                }


                break;
            default:
                throw new UnsupportedOperationException();

        }
        log.info("[表策略] 分片键--{}--对应的值为：{}",shardingKey,valueSet);

        return valueSet;
    }
}
