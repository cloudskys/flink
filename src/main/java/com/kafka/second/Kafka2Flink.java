package com.kafka.second;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import javax.annotation.Nullable;
import java.util.Properties;

/**
 * @Description: 解析原始消息的辅助类
 * @author: willzhao E-mail: zq2599@gmail.com
 * @date: 2019/1/1 20:13
 */
public class Kafka2Flink {

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.enableCheckpointing(5000);
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "127.0.0.1:9092");// kafka的节点的IP或者hostName，多个使用逗号分隔
		properties.setProperty("zookeeper.connect", "127.0.0.1:2181");// zookeeper的节点的IP或者hostName，多个使用逗号进行分隔
		properties.setProperty("group.id", "test-consumer-group");// flink
																	// consumer
																	// flink的消费者的group.id
		// 构建FlinkKafkaConsumer
		FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<String>("topic", new SimpleStringSchema(),
				properties);
		// 增加时间水位设置类
		consumer.assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks<String>() {
			@Override
			public long extractTimestamp(String element, long previousElementTimestamp) {
				return JSONHelper.getTimeLongFromRawMessage(element);
			}

			@Nullable
			@Override
			public Watermark checkAndGetNextWatermark(String lastElement, long extractedTimestamp) {
				if (lastElement != null) {
					return new Watermark(JSONHelper.getTimeLongFromRawMessage(lastElement));
				}
				return null;
			}
		});

		env.addSource(consumer)
				// 将原始消息转成Tuple2对象，保留用户名称和访问次数(每个消息访问次数为1)
				.flatMap((FlatMapFunction<String, Tuple2<String, Long>>) (s, collector) -> {
					SingleMessage singleMessage = JSONHelper.parse(s);

					if (null != singleMessage) {
						collector.collect(new Tuple2<>(singleMessage.getName(), 1L));
					}
				}).returns(Types.TUPLE(Types.STRING,Types.STRING,Types.LONG))
				// 以用户名为key
				.keyBy(0)
				// 时间窗口为2秒
				.timeWindow(Time.seconds(2))
				// 将每个用户访问次数累加起来
				.apply((WindowFunction<Tuple2<String, Long>, Tuple2<String, Long>, Tuple, TimeWindow>) (tuple, window,
						input, out) -> {
					long sum = 0L;
					for (Tuple2<String, Long> record : input) {
						sum += record.f1;
					}

					Tuple2<String, Long> result = input.iterator().next();
					result.f1 = sum;
					out.collect(result);
				})
				// 输出方式是STDOUT
				.print();

		env.execute("Flink-Kafka demo");
	}
}
