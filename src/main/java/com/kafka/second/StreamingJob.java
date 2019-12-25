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
		env.enableCheckpointing(5000); // Ҫ������������
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "127.0.0.1:9092");//kafka�Ľڵ��IP����hostName�����ʹ�ö��ŷָ�
		props.setProperty("zookeeper.connect", "127.0.0.1:2181");//zookeeper�Ľڵ��IP����hostName�����ʹ�ö��Ž��зָ�
		props.setProperty("group.id", "test-consumer-group");//flink consumer flink�������ߵ�group.id

		//����Դ���ã���һ��kafka��Ϣ��������
		FlinkKafkaConsumer<String> consumer =
				new FlinkKafkaConsumer<String>("topic001", new SimpleStringSchema(), props);;//topic001��kafka�п�����topic

		//����ʱ��ˮλ������
		consumer.assignTimestampsAndWatermarks(new CustomWatermarkEmitter());
		DataStream<String> keyedStream = env.addSource(consumer);//��kafka�����߷��������ݽ��д����������ҽ��κδ���
		 keyedStream.print();//ֱ�ӽ��������߽��յ��������ڿ���̨�Ͻ��д�ӡ

		env.execute("Flink-Kafka demo");
	}
}
