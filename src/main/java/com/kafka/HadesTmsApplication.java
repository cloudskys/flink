package com.kafka;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.kafka.config.SimpleSink;


@SpringBootApplication
@MapperScan(basePackages ={"com.kafka.dao"})
public class HadesTmsApplication implements CommandLineRunner {

	public static void main(String[] args) {

		 SpringApplication.run(HadesTmsApplication.class, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(String... args) {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<String>("test11", new SimpleStringSchema(),getProperties());
		DataStream<String> dataStream = env.addSource(kafkaConsumer);
		// 此处省略处理逻辑
		dataStream.addSink(new SimpleSink());

	}

	private Properties getProperties() {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
		properties.setProperty("zookeeper.connect", "127.0.0.1:2181");
		properties.setProperty("group.id", "test-consumer-group");
		properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return properties;
	}
}
