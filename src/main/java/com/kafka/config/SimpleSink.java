package com.kafka.config;

import java.util.List;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;

import com.kafka.dao.TeacherInfoMapper;
import com.kafka.domain.TeacherInfo;

@SuppressWarnings("serial")
@EnableAutoConfiguration
public class SimpleSink extends RichSinkFunction<String> {
 
 
	private TeacherInfoMapper teacherInfoMapper;
 
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        teacherInfoMapper =ApplicationContextUtil.getBean(TeacherInfoMapper.class);
        System.out.println(teacherInfoMapper);
    }
 
    @Override
    public void close() throws Exception {
        super.close();
    }
 
    @Override
    public void invoke(String value, Context context) throws Exception {
    	System.out.println("--------kafkaMessage:"+value);
        List<TeacherInfo> teacherInfoList = teacherInfoMapper.selectByPage();
        teacherInfoList.stream().forEach(teacherInfo -> System.out.println("teacherinfo:" + teacherInfo.getUserId() + "," + teacherInfo.getUserName() + "," + teacherInfo.getMobilePhone()));
    }
}
