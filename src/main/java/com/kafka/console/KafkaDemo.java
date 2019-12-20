package com.kafka.console;

import java.util.Properties;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

public class KafkaDemo {
	public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //Ĭ������£����㱻���á�Ҫ���ü��㣬����StreamExecutionEnvironment�ϵ���enableCheckpointing(n)������
        // ����n���Ժ���Ϊ��λ�ļ�������ÿ��5000 ms��������һ������,����һ�����㽫����һ��������ɺ�5����������

        env.enableCheckpointing(500);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");//kafka�Ľڵ��IP����hostName�����ʹ�ö��ŷָ�
        properties.setProperty("zookeeper.connect", "127.0.0.1:2181");//zookeeper�Ľڵ��IP����hostName�����ʹ�ö��Ž��зָ�
        properties.setProperty("group.id", "test-consumer-group");//flink consumer flink�������ߵ�group.id
        FlinkKafkaConsumer<String> myConsumer = new FlinkKafkaConsumer<String>("test", new org.apache.flink.api.common.serialization.SimpleStringSchema(), properties);
        // FlinkKafkaConsumer010<String> myConsumer = new FlinkKafkaConsumer010<String>("test",new SimpleStringSchema(),properties);//test0��kafka�п�����topic
        myConsumer.assignTimestampsAndWatermarks(new CustomWatermarkEmitter());
        DataStream<String> keyedStream = env.addSource(myConsumer);//��kafka�����߷��������ݽ��д����������ҽ��κδ���
        //keyedStream.print();//ֱ�ӽ��������߽��յ��������ڿ���̨�Ͻ��д�ӡ
        keyedStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out)
                throws Exception {
                System.out.println(value);
            }
        });
        env.execute("Flink Streaming Java API Skeleton");

    }
}
