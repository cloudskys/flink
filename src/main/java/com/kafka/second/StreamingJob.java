package com.kafka.second;
import java.util.Properties;

import javax.annotation.Nullable;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

public class StreamingJob {

	public static void main(String[] args) throws Exception {

		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.enableCheckpointing(5000); // 要设置启动检查点
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "127.0.0.1:9092");//kafka的节点的IP或者hostName，多个使用逗号分隔
		props.setProperty("zookeeper.connect", "127.0.0.1:2181");//zookeeper的节点的IP或者hostName，多个使用逗号进行分隔
		props.setProperty("group.id", "test-consumer-group");//flink consumer flink的消费者的group.id

		//数据源配置，是一个kafka消息的消费者
		FlinkKafkaConsumer<String> consumer =
				new FlinkKafkaConsumer<String>("topic001", new SimpleStringSchema(), props);;//topic001是kafka中开启的topic

		//增加时间水位设置类
		consumer.assignTimestampsAndWatermarks(new CustomWatermarkEmitter());
		DataStream<String> keyedStream = env.addSource(consumer);//将kafka生产者发来的数据进行处理，本例子我进任何处理
		 keyedStream.print();//直接将从生产者接收到的数据在控制台上进行打印

		env.execute("Flink-Kafka demo");
	}
}
