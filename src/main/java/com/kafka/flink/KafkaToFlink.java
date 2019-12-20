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
        //默认情况下，检查点被禁用。要启用检查点，请在StreamExecutionEnvironment上调用enableCheckpointing(n)方法，
        // 其中n是以毫秒为单位的检查点间隔。每隔5000 ms进行启动一个检查点,则下一个检查点将在上一个检查点完成后5秒钟内启动
        env.enableCheckpointing(5000);
        /**
         * 这里主要配置KafkaConsumerConfig需要的属性，如：
         * --bootstrap.servers localhost:9092 --topic test --group.id test-consumer-group
         */
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        DataStream<String> dataStream = env.addSource(new FlinkKafkaConsumer<String>(parameterTool.getRequired("topic"), new SimpleStringSchema(), parameterTool.getProperties()));//将kafka生产者发来的数据进行处理，本例子我进任何处理
        DataStream<WordWithCount> windowCounts = dataStream.rebalance().flatMap(new FlatMapFunction<String, WordWithCount>() {
            public void flatMap(String value, Collector<WordWithCount> out) {
                System.out.println("接收到kafka数据：" + value);
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
        //将结果写到kafka
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