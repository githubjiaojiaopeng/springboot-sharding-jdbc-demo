package com.spartajet.shardingboot.sharding;

import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSourceFactory;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.spartajet.shardingboot.core.Database;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @description
 * @create 2017-02-07 下午8:57
 * @email gxz04220427@163.com
 */
@Configuration
public class DataSourceConfig {

    /**
     *
     * 此处通过加载资源文件，获取数据库的相关配置信息
     *
     */
    @Value("classpath:database.json")
    private Resource databaseFile;

    @Bean
    @Lazy
    public List<Database> databases() throws IOException {
        String databasesString = IOUtils.toString(databaseFile.getInputStream(), Charset.forName("UTF-8"));
        //json  转化为 数据源先关信息实体
        List<Database> databases = new Gson().fromJson(databasesString, new TypeToken<List<Database>>() {}.getType());
        return databases;
    }

    /**
     * TODO 此处可改造为使用Durid数据库连接池作为数据源
     *
     * @param databases
     * @return
            */
//    @Bean
//    public HashMap<String, DataSource> dataSourceMap(List<Database> databases) {
//        HashMap<String, DataSource> dataSourceMap = new HashMap<>();
//        for (Database database : databases) {
//            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//            dataSourceBuilder.url(database.getUrl());
//            dataSourceBuilder.driverClassName(database.getDriveClassName());
//            dataSourceBuilder.username(database.getUsername());
//            dataSourceBuilder.password(database.getPassword());
//            DataSource dataSource = dataSourceBuilder.build();
//            dataSourceMap.put(database.getName(), dataSource);
//        }
//        return dataSourceMap;
//    }

    /**
     * druid 数据源
     *
     * @return
     */
    @Bean(name = "dataSource_sh")
    @Qualifier(value = "dataSource_sh")
    @ConfigurationProperties(prefix = "spring.datasource_sh")
    public DataSource datasource_sh() {
        return DataSourceBuilder.create().type(com.alibaba.druid.pool.DruidDataSource.class).build();
    }

    /**
     * druid 数据源
     *
     * @return
     */
    @Bean(name = "dataSource_sz")
    @Qualifier(value = "dataSource_sz")
    @ConfigurationProperties(prefix = "spring.datasource_sz")
    public DataSource datasource_sz() {
        return DataSourceBuilder.create().type(com.alibaba.druid.pool.DruidDataSource.class).build();
    }

    /**
     * 必须指定不同数据源对应的Bean的Qualifier 名称
     *
     * @param dataSource_sh
     * @param dataSource_sz
     * @return
     */
    @Bean
    @Autowired
    public HashMap<String, DataSource> dataSourceMap(@Qualifier(value = "dataSource_sh") DataSource dataSource_sh ,@Qualifier(value = "dataSource_sz")DataSource dataSource_sz) {
        HashMap<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("db_sh",dataSource_sh);
        dataSourceMap.put("db_sz",dataSource_sz);
        return dataSourceMap;
    }


    @Bean
    @Primary
    public DataSource shardingDataSource(HashMap<String, DataSource> dataSourceMap, DatabaseShardingStrategy databaseShardingStrategy, TableShardingStrategy tableShardingStrategy) {
        DataSourceRule dataSourceRule = new DataSourceRule(dataSourceMap);
        TableRule tableRule = TableRule.builder("tick").actualTables(Arrays.asList("db_sh.tick_a_2017_04", "db_sh.tick_a_2017_05", "db_sh.tick_b_2017_04", "db_sh.tick_b_2017_05", "db_sz.tick_a_2017_04", "db_sz.tick_a_2017_05", "db_sz.tick_b_2017_04", "db_sz.tick_a_2017_05")).dataSourceRule(dataSourceRule).build();
        ShardingRule shardingRule = ShardingRule.builder().dataSourceRule(dataSourceRule).tableRules(Arrays.asList(tableRule)).databaseShardingStrategy(databaseShardingStrategy).tableShardingStrategy(tableShardingStrategy).build();
        DataSource shardingDataSource = ShardingDataSourceFactory.createDataSource(shardingRule);
        return shardingDataSource;
    }
}
