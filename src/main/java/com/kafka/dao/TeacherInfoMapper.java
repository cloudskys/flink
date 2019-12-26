package com.kafka.dao;

import java.util.List;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.kafka.domain.TeacherInfo;
@Mapper
public interface TeacherInfoMapper {
	List<TeacherInfo> selectByPage();

}
