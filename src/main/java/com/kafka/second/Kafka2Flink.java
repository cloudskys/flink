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
 * @Description: ����ԭʼ��Ϣ�ĸ�����
 * @author: willzhao E-mail: zq2599@gmail.com
 * @date: 2019/1/1 20:13
 */
public class Kafka2Flink {

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.enableCheckpointing(5000);
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "127.0.0.1:9092");// kafka�Ľڵ��IP����hostName�����ʹ�ö��ŷָ�
		properties.setProperty("zookeeper.connect", "127.0.0.1:2181");// zookeeper�Ľڵ��IP����hostName�����ʹ�ö��Ž��зָ�
		properties.setProperty("group.id", "test-consumer-group");// flink
																	// consumer
																	// flink�������ߵ�group.id
		// ����FlinkKafkaConsumer
		FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<String>("topic", new SimpleStringSchema(),
				properties);
		// ����ʱ��ˮλ������
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
				// ��ԭʼ��Ϣת��Tuple2���󣬱����û����ƺͷ��ʴ���(ÿ����Ϣ���ʴ���Ϊ1)
				.flatMap((FlatMapFunction<String, Tuple2<String, Long>>) (s, collector) -> {
					SingleMessage singleMessage = JSONHelper.parse(s);

					if (null != singleMessage) {
						collector.collect(new Tuple2<>(singleMessage.getName(), 1L));
					}
				}).returns(Types.TUPLE(Types.STRING,Types.STRING,Types.LONG))
				// ���û���Ϊkey
				.keyBy(0)
				// ʱ�䴰��Ϊ2��
				.timeWindow(Time.seconds(2))
				// ��ÿ���û����ʴ����ۼ�����
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
				// �����ʽ��STDOUT
				.print();

		env.execute("Flink-Kafka demo");
	}
}
