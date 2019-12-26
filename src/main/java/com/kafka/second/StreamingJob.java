package com.kafka.second;
import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

public class StreamingJob {

	public static void main(String[] args) throws Exception {

		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.enableCheckpointing(5000);
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "127.0.0.1:9092");
		props.setProperty("zookeeper.connect", "127.0.0.1:2181");
		props.setProperty("group.id", "test-consumer-group");

		
		FlinkKafkaConsumer<String> consumer =
				new FlinkKafkaConsumer<String>("topic001", new SimpleStringSchema(), props);

		
		consumer.assignTimestampsAndWatermarks(new CustomWatermarkEmitter());
		DataStream<String> keyedStream = env.addSource(consumer);
		 keyedStream.print();

		env.execute("Flink-Kafka demo");
	}
}
