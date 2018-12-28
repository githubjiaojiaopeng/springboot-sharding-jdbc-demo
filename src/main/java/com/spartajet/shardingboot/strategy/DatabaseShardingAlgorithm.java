package com.spartajet.shardingboot.strategy;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.SingleKeyDatabaseShardingAlgorithm;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 单分片键算法 ，需要实现接口中的三个方法
 *
 * @create 2017-02-07 下午9:15
 * @email gxz04220427@163.com
 */
@Slf4j
@Service
public class DatabaseShardingAlgorithm implements SingleKeyDatabaseShardingAlgorithm<String> {
    /**
     * 根据分片值和SQL的=运算符计算分片结果名称集合.<br/>
     *  <p>doEqualSharding在WHERE使用=作为条件分片键。算法中使用shardingValue.getValue()获取等=后的值</p>
     *
     * @param availableTargetNames 所有的可用目标名称集合, 一般是数据源或表名称
     * @param shardingValue        分片值
     *
     * @return 分片后指向的目标名称, 一般是数据源或表名称
     */
    @Override
    public String doEqualSharding(Collection<String> availableTargetNames, ShardingValue<String> shardingValue) {

        log.info("[数据库策略] 当前SQL语句为：=条件语句， 分片建的值为：{}", new GsonBuilder().create().toJson(shardingValue));

        String databaseName = "";
        for (String targetName : availableTargetNames) {
            if (targetName.endsWith(shardingValue.getValue())) {
                databaseName = targetName;
                break;
            }
        }
        log.info("[数据库策略] 当前SQL语句为：=条件语句， 目标数据库为：{}",databaseName);

        return databaseName;
    }

    /**
     * 根据分片值和SQL的IN运算符计算分片结果名称集合.<br/>
     *
     * <p>doInSharding在WHERE使用IN作为条件分片键。算法中使用shardingValue.getValues()获取IN后的值</p>
     *
     * @param availableTargetNames 所有的可用目标名称集合, 一般是数据源或表名称
     * @param shardingValue        分片值
     *
     * @return 分片后指向的目标名称集合, 一般是数据源或表名称
     */
    @Override
    public Collection<String> doInSharding(Collection<String> availableTargetNames, ShardingValue<String> shardingValue) {

        log.info("[数据库策略] 当前SQL语句为：in 条件语句，通过IN 条件分片键的值获取目标数据库集合； 分片建的值为：{}", new GsonBuilder().create().toJson(shardingValue));

        Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        for (String value : shardingValue.getValues()) {
            for (String tableName : availableTargetNames) {
                if (tableName.equals(value)) {
                    result.add(tableName);
                }
            }
        }
        log.info("[数据库策略] 当前SQL语句的目标数据库集合为：{}",new GsonBuilder().create().toJson(result));
        return result;
    }

    /**
     * 根据分片值和SQL的BETWEEN运算符计算分片结果名称集合.<br/>
     * <p>doBetweenSharding在WHERE使用BETWEEN作为条件分片键。算法中使用shardingValue.getValueRange()获取BETWEEN后的值</p>
     *
     * @param availableTargetNames 所有的可用目标名称集合, 一般是数据源或表名称
     * @param shardingValue        分片值
     *
     * @return 分片后指向的目标名称集合, 一般是数据源或表名称
     */
    @Override
    public Collection<String> doBetweenSharding(Collection<String> availableTargetNames, ShardingValue<String> shardingValue) {

        log.error("[数据库策略] 该分片键不支持使用between...and 语法进行查询");
        return null;

//        Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
//        Range<String> range = (Range<String>) shardingValue.getValueRange();
//        for (String i = range.lowerEndpoint(); i <= range.upperEndpoint(); i++) {
//            for (String each : tableNames) {
//                if (each.endsWith(i % 2 + "")) {
//                    result.add(each);
//                }
//            }
//        }
    }
}
