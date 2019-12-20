package com.kafka.flink;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class KafkaToFlink {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //Ĭ������£����㱻���á�Ҫ���ü��㣬����StreamExecutionEnvironment�ϵ���enableCheckpointing(n)������
        // ����n���Ժ���Ϊ��λ�ļ�������ÿ��5000 ms��������һ������,����һ�����㽫����һ��������ɺ�5����������
        env.enableCheckpointing(5000);
        /**
         * ������Ҫ����KafkaConsumerConfig��Ҫ�����ԣ��磺
         * --bootstrap.servers localhost:9092 --topic test --group.id test-consumer-group
         */
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        DataStream<String> dataStream = env.addSource(new FlinkKafkaConsumer<String>(parameterTool.getRequired("topic"), new SimpleStringSchema(), parameterTool.getProperties()));//��kafka�����߷��������ݽ��д����������ҽ��κδ���
        DataStream<WordWithCount> windowCounts = dataStream.rebalance().flatMap(new FlatMapFunction<String, WordWithCount>() {
            public void flatMap(String value, Collector<WordWithCount> out) {
                System.out.println("���յ�kafka���ݣ�" + value);
                for (String word : value.split("\\s")) {
                    out.collect(new WordWithCount(word, 1L));
                }
            }
        }).keyBy("word")
                .timeWindow(Time.seconds(2))
                .reduce(new ReduceFunction<WordWithCount>() {
                    public WordWithCount reduce(WordWithCount a, WordWithCount b) {
                        return new WordWithCount(a.word, a.count + b.count);
                    }
                });
        windowCounts.print().setParallelism(1);
        //�����д��kafka
        dataStream.addSink(new FlinkKafkaProducer<>(
                "localhost:9092",
                "student-write",
                new SimpleStringSchema()
        )).name("flink-connectors-kafka");
        env.execute("KafkaToFlink");
    }
    public static class WordWithCount {
        public String word;
        public long count;
        public WordWithCount() {}
        public WordWithCount(String word, long count) {
           this.word = word;
           this.count = count;
        }
        @Override
        public String toString() {
           return word + " : " + count;
        }
     }
   
}