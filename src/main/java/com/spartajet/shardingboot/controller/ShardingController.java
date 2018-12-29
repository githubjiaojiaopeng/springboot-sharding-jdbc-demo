package com.spartajet.shardingboot.controller;

import com.dangdang.ddframe.rdb.sharding.id.generator.self.CommonSelfIdGenerator;
import com.spartajet.shardingboot.bean.Tick;
import com.spartajet.shardingboot.common.CommonResponse;
import com.spartajet.shardingboot.mapper.TickMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/shardingController")
public class ShardingController {

    @Resource
    private TickMapper tickMapper;
    @Autowired
    private CommonSelfIdGenerator commonSelfIdGenerator;
    /*
     * @Description:插入数据
     * @Author: Peng
     * @Date:  2018-12-29
     */
    @PostMapping("/insertTest")
    public void insertTest() {
        Tick tick = new Tick(commonSelfIdGenerator.generateId().longValue(), "a", "sz", 300, 100, getDate("2017-05-07"));
        this.tickMapper.insertTick(tick);
    }

    @GetMapping("/selectAllTest")
    public CommonResponse<List> selectAllTest(){
        List<Tick> list = this.tickMapper.listTickAll();
        return CommonResponse.success(list);
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
