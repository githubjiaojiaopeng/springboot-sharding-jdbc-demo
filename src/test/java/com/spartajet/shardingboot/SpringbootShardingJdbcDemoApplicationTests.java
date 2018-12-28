package com.spartajet.shardingboot;

import com.dangdang.ddframe.rdb.sharding.id.generator.self.CommonSelfIdGenerator;
import com.spartajet.shardingboot.bean.Tick;
import com.spartajet.shardingboot.common.GsonUtils;
import com.spartajet.shardingboot.mapper.TickMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class SpringbootShardingJdbcDemoApplicationTests {
    @Autowired
    private TickMapper tickMapper;
    @Autowired
    private CommonSelfIdGenerator commonSelfIdGenerator;


    @Test
    public void insertTest() {
        Tick tick = new Tick(commonSelfIdGenerator.generateId().longValue(), "a", "sz", 300, 100, getDate("2017-05-07"));
        this.tickMapper.insertTick(tick);
    }

    /**
     * select All 全表查询
     *
     */
    @Test
    public void selectAllTest(){

        List<Tick> list = this.tickMapper.listTickAll();

        log.info("result : {}", GsonUtils.objToJson(list));
    }

    /**
     * 分片列 条件查询
     *
     */
    @Test
    public void selectByConditionTest(){

        List<Tick> list = this.tickMapper.listTickByCondition("a","sh" ,getDate("2017-03-01"), getDate("2017-05-05"));

        log.info("result : {}", GsonUtils.objToJson(list));
    }

    /**
     * 分片列 条件分页查询
     *
     */
    @Test
    public void selectForPageTest(){

        List<Tick> list = this.tickMapper.listTickForPage("a","sh" ,getDate("2017-03-01"), getDate("2017-05-05"),1,1);

        log.info("listSize: {} , result : {}", list.size(),  GsonUtils.objToJson(list));
    }


    /**
     *
     * @param dateStr  如： 2011-12-03
     * @return
     */
    private LocalDate getDate(String dateStr){

        return LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
    }




}
