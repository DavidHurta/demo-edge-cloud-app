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

import java.time.Duration;
import java.util.Properties;

public class CloudServer {
    public static void run(Configuration config) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-cloud-server");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());

        final ObjectMapper objectMapper = new ObjectMapper();
        final Serde<String> stringSerde = Serdes.String();
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, JsonNode> sourceStream = builder.stream("edge-server-01", Consumed.with(stringSerde, jsonSerde));
        sourceStream
                .map((key, value) -> KeyValue.pair("edge-server-01", value))
                .groupByKey(Grouped.with(Serdes.String(), jsonSerde))
                .windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
                .aggregate(
                        () -> objectMapper.valueToTree(new AggregateAverage()),
                        (key, value, aggregate) -> {
                            try {
                                AggregateAverage aggregateAverage = objectMapper.treeToValue(aggregate, AggregateAverage.class);
                                ObjectNode node = (ObjectNode) value;
                                aggregateAverage.add("processCpuTime", node.get("processCpuTime").doubleValue());
                                aggregateAverage.add("processCpuLoad", node.get("processCpuLoad").doubleValue());
                                aggregateAverage.add("cpuLoad", node.get("cpuLoad").doubleValue());
                                aggregateAverage.add("freeSwapSpaceSizePercentage",  node.get("freeSwapSpaceSize").doubleValue() / node.get("totalSwapSpaceSize").doubleValue());
                                aggregateAverage.add("freeMemorySizePercentage", node.get("freeMemorySize").doubleValue() / node.get("totalMemorySize").doubleValue());
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
                        node.put("processCpuTime", Math.ceil(aggregateAverage.getAverage("processCpuTime") * 1e3) / 1e3);
                        node.put("processCpuLoad", Math.ceil(aggregateAverage.getAverage("processCpuLoad") * 1e3) / 1e3);
                        node.put("cpuLoad", Math.ceil(aggregateAverage.getAverage("cpuLoad") * 1e3) / 1e3);
                        node.put("freeSwapSpaceSizePercentage", Math.ceil(aggregateAverage.getAverage("freeSwapSpaceSizePercentage") * 1e3) / 1e3);
                        node.put("freeMemorySizePercentage", Math.ceil(aggregateAverage.getAverage("freeMemorySizePercentage") * 1e3) / 1e3);

                        String status = "All is well";
                        if (
                                node.get("cpuLoad").doubleValue() > 0.8 ||
                                node.get("processCpuLoad").doubleValue() > 0.8 ||
                                node.get("freeSwapSpaceSizePercentage").doubleValue() < 0.1 ||
                                node.get("freeSwapSpaceSizePercentage").doubleValue() < 0.1
                        ){
                            status = "Machines Are Being Heavily Utilized, ";
                        }
                        node.put("status", status);
                        return KeyValue.pair(key.key(), (JsonNode) node);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .to("cloud-server-01", Produced.with(stringSerde, jsonSerde));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
