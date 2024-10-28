//
//  Author: David Hurta (xhurta04)
//  Project: DIP
//

package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class EdgeDevice {
    private static final Logger log = LoggerFactory.getLogger(EdgeDevice.class);

    public static void run(Configuration config) {
        // Create a Kafka producer
        log.info("I am a Kafka Producer");
        log.info("Bootstrap Servers: %s".formatted(config.getBootstrapServers()));
        log.info("count: %s".formatted(config.getCount()));
        log.info("sleep: %s".formatted(config.getSleep()));

        // Kafka properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

        // Kafka producer
        KafkaProducer<String, JsonNode> producer = new KafkaProducer<>(properties);
        SystemInfo info = new SystemInfo();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode infoSerialized;

        String[] keys = {"admin-machine", "user-01-machine"};
        int i = 0;
        while (true) {
            if (config.getCount() != -1) {
                if (i >= config.getCount()) {
                    break;
                }
            }

            for (String key : keys) {
                info.Update();
                try {
                    infoSerialized = mapper.valueToTree(info);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                ProducerRecord<String, JsonNode> producerRecord = new ProducerRecord<>("edge-device-01", key, infoSerialized);

                producer.send(producerRecord);

                try {
                    TimeUnit.MILLISECONDS.sleep(config.getSleep());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            i++;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            producer.flush();
            producer.close();
        }));
    }
}