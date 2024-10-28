package org.example;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;

import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Helper {
    public static double safeDivision(double a, double b) {
        if (b == 0) {
            return 0;
        }
        return a / b;
    }

    public static boolean doesTopicExist(Properties props, String targetTopicName) {
        try (AdminClient client = AdminClient.create(props)) {
            ListTopicsOptions options = new ListTopicsOptions();
            ListTopicsResult topics = client.listTopics(options);
            Set<String> names = topics.names().get();
            for (String name : names) {
                if (Objects.equals(name, targetTopicName)) {
                    return true;
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
