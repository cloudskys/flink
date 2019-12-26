package com.kafka.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kafka.domain.TeacherInfo;
public interface TeacherInfoMapper {
	List<TeacherInfo> selectByPage();

}
