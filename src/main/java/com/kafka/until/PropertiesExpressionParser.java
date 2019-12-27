package com.kafka.until;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kafka.domain.TeacherInfo;

public class PropertiesExpressionParser {

	protected static final Logger logger = LoggerFactory.getLogger(PropertiesExpressionParser.class);

	private static PropertiesExpressionParser _self;
	
	/**
	 * 配置文件的密钥路径
	 */
	private String proKeyPath;
	
	/**
	 * 设置密钥路径
	 * @param proKeyPath
	 */
	public void setProKeyPath(String proKeyPath) {
		this.proKeyPath = proKeyPath;
	}

	private PropertiesExpressionParser(){}
	
	/**
	 * 单例获取对象
	 * 
	 * @return
	 */
	public static PropertiesExpressionParser getInstance() {
		if(_self == null) {
			_self = new PropertiesExpressionParser();
		}
		return _self;
	}
	


	/**
	 * 解密
	 * 
	 * @param key
	 *            需要获取的KEY
	 * @param result
	 *            前一个方法返回值
	 */
	

	/**
	 * 解密
	 * 
	 * @param key
	 *            需要获取的KEY，properties中配置的键名称
	 * @param result
	 *            前一个方法返回值， null
	 * @param ciphertext
	 *            密文
	 */
	

	/**
	 * 加载文件，得到Properties对象
	 * 
	 * @param key
	 *            需要获取的KEY
	 * @param result
	 *            前一个方法返回值
	 * @param filePath
	 * @return
	 */
	public static Properties path(String key, Object result, String filePath) {
		InputStream in = null;
		try {
			in = new FileInputStream(filePath);
			Properties pro = new Properties();
			pro.load(in);
			return pro;
		} catch (Exception e) {
			throw new IllegalArgumentException("load file failed.invalid file path:" + filePath);
		}
	}

	/**
	 * 从properties中获取键keyName的值
	 * 
	 * @param key
	 *            需要获取的KEY
	 * @param result
	 *            前一个方法返回值
	 * @return
	 */
	public Object get(String key, Object result) {
		Properties pro = (Properties) result;
		if (pro == null) {
			return null;
		}
		return pro.get(key);
	}



	/**
	 * Test
	 * 
	 * @param args
	 */
	public static void main1(String[] args) {
		
		// 文件中直接解密
		// jdbc.password=$decrypt(mrLyVojEDEA=);
		// 引用其他公共文件直接获取值
		// jdbc.username=$path(d:/jdbc.properties).$get();
		// 引用其他公共文件，获取值后解密
		// jdbc.username=$path(d:/jdbc.properties).$get().$decrypt();
		
		// 文件中直接解密
		String str1 = "$decrypt(mrLyVojEDEA=)";
		// 引用其他公共文件直接获取值
		String str2 = "$path(d:/jdbc.properties).$get()";
		// 引用其他公共文件，获取值后解密
		String str3 = "$path(d:/jdbc.properties).$get().$decrypt()";
		PropertiesExpressionParser expParser = PropertiesExpressionParser.getInstance();
		String proKeyPath = "C:/home/hefa/sec/deskey.lic";
		expParser.setProKeyPath(proKeyPath);
	
	}
	public static void main(String[] args) {
		List<String> languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");
		languages.forEach(n -> System.out.println(n));
		filter(languages, (str)->((String) str).startsWith("J"));
		
		Map m = new HashMap();
		m.put("1", 12);
		
		m.forEach((k,v) -> System.out.println(k+","+v));
		
		List<TeacherInfo> lan = new ArrayList<TeacherInfo>();
		lan.add(new TeacherInfo(20));
		lan.add(new TeacherInfo(30));
		lan.add(new TeacherInfo(40));
		lan.add(new TeacherInfo(40));
		lan.stream().map(user -> user.getUserId() >= 40).collect(Collectors.toList());
		lan.forEach(n -> System.out.println(n.getUserId()));
		
		lan.stream().filter(user -> user.getUserId() >= 40).collect(Collectors.toList());
		lan.forEach(n -> System.out.println(n.getUserId()));
		System.out.println("---------");
		//lan.stream().limit(1).collect(Collectors.toList());
		//lan.forEach(n -> System.out.println(n.getUserId()));
	}

	public static void filter(List<String> names, Predicate condition) {
	    for(String name: names)  {
	        if(condition.test(name)) {
	            System.out.println(name + " ");
	        }
	    }
	}
	
}

