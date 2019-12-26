package com.kafka.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kafka.dao.TeacherInfoMapper;
import com.kafka.domain.TeacherInfo;

@RestController
public class IndexController  {
	@Autowired
	private TeacherInfoMapper teacherInfoMapper;
	
	@RequestMapping("/selectAdminAll")
	public Object selectAdminAll(){
		
		List<TeacherInfo> adminList  =  teacherInfoMapper.selectByPage();
		System.out.println(adminList.size());
		return adminList;
	}
}
