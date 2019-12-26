package com.kafka;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.kafka.config.SimpleSink;
import com.kafka.until.SysConst;



@SpringBootApplication
@MapperScan(basePackages ="com.kafka.dao")
public class FlinkApplication implements CommandLineRunner {

	public static void main(String[] args) {

		 SpringApplication.run(FlinkApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<String>(SysConst.ribonTopic, new SimpleStringSchema(),getProperties());
		DataStreamSource<String> dataStream = env.addSource(kafkaConsumer);
		dataStream.addSink(new SimpleSink());
		env.execute();

	}

	private Properties getProperties() {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", SysConst.bootstrapservers);
		properties.setProperty("zookeeper.connect", SysConst.zookeeperconnect);
		properties.setProperty("group.id", "test-consumer-group");
		properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return properties;
	}
}
