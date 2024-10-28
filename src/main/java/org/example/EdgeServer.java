//
//  Author: David Hurta (xhurta04)
//  Project: DIP
//

package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.streams.kstream.*;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class EdgeServer {
    public static void run(Configuration config) throws InterruptedException {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-stream-edge-server");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());

        String sourceTopicName = "edge-device-01";
        while (!Helper.doesTopicExist(props, sourceTopicName)) {
            System.out.printf(
                    "Waiting for a topic `%s` to be created, will try again in %d milliseconds.\n",
                    sourceTopicName,
                    config.getSleep());
            TimeUnit.MILLISECONDS.sleep(config.getSleep());
        }

        final ObjectMapper objectMapper = new ObjectMapper();
        final Serde<String> stringSerde = Serdes.String();
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, JsonNode> sourceStream = builder.stream(sourceTopicName, Consumed.with(stringSerde, jsonSerde));
        sourceStream
                .groupByKey()
                .windowedBy(TimeWindows.of(config.getAggregationWindow()))
                .aggregate(
                       () -> objectMapper.valueToTree(new AggregateAverage()),
                       (key, value, aggregate) -> {
                           try {
                               AggregateAverage aggregateAverage = objectMapper.treeToValue(aggregate, AggregateAverage.class);
                               ObjectNode node = (ObjectNode) value;
                               aggregateAverage.add("processCpuTime", node.get("processCpuTime").doubleValue());
                               aggregateAverage.add("processCpuLoad", node.get("processCpuLoad").doubleValue());
                               aggregateAverage.add("cpuLoad", node.get("cpuLoad").doubleValue());
                               aggregateAverage.add("committedVirtualMemorySize", node.get("committedVirtualMemorySize").doubleValue());
                               aggregateAverage.add("freeSwapSpaceSize", node.get("freeSwapSpaceSize").doubleValue());
                               aggregateAverage.add("totalSwapSpaceSize", node.get("totalSwapSpaceSize").doubleValue());
                               aggregateAverage.add("freeMemorySize", node.get("freeMemorySize").doubleValue());
                               aggregateAverage.add("totalMemorySize", node.get("totalMemorySize").doubleValue());
                               aggregate = objectMapper.valueToTree(aggregateAverage);
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                           return aggregate;
                       },
                       Materialized.with(stringSerde, jsonSerde)
                )
                .toStream()
                .map((key, value) -> {
                       ObjectNode node = JsonNodeFactory.instance.objectNode();
                       try {
                           AggregateAverage aggregateAverage = objectMapper.treeToValue(value, AggregateAverage.class);
                            node.put("processCpuTime", Math.ceil(aggregateAverage.getAverage("processCpuTime") / 1e9 * 1e3) / 1e3);
                            node.put("processCpuLoad", Math.ceil(aggregateAverage.getAverage("processCpuLoad") * 1e3) / 1e3);
                            node.put("cpuLoad", Math.ceil(aggregateAverage.getAverage("cpuLoad") * 1e3) / 1e3);
                            node.put("committedVirtualMemorySize", (long) (aggregateAverage.getAverage("committedVirtualMemorySize") / 1e6));
                            node.put("freeSwapSpaceSize", (long) (aggregateAverage.getAverage("freeSwapSpaceSize") / 1e6));
                            node.put("totalSwapSpaceSize", (long) (aggregateAverage.getAverage("totalSwapSpaceSize") / 1e6));
                            node.put("freeMemorySize", (long) (aggregateAverage.getAverage("freeMemorySize") / 1e6));
                            node.put("totalMemorySize", (long) (aggregateAverage.getAverage("totalMemorySize") / 1e6));
                           return KeyValue.pair(key.key(), (JsonNode) node);
                       } catch (JsonProcessingException e) {
                           throw new RuntimeException(e);
                       }
                })
                .to("edge-server-01", Produced.with(stringSerde, jsonSerde));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
