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
@MapperScan(basePackages = {"com.kafka.dao"})
public class SimpleSink extends RichSinkFunction<String> {
 
 
    TeacherInfoMapper teacherInfoMapper;
 
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        SpringApplication application = new SpringApplication(SimpleSink.class);
        ApplicationContext context = application.run(new String[]{});
        teacherInfoMapper = context.getBean(TeacherInfoMapper.class);
    }
 
    @Override
    public void close() throws Exception {
        super.close();
    }
 
    @Override
    public void invoke(String value, Context context) throws Exception {
    	System.out.println("------------");
        List<TeacherInfo> teacherInfoList = teacherInfoMapper.selectByPage();
        teacherInfoList.stream().forEach(teacherInfo -> System.out.println("teacherinfo:" + teacherInfo.getTeacherId() + "," + teacherInfo.getTimeBit() + "," + teacherInfo.getWeek()));
    }
}
