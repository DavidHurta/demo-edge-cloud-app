//
//  Author: David Hurta (xhurta04)
//  Project: DIP
//

package org.example;

import java.time.Duration;

public class Configuration {
    private int sleep;

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ConfigurationMode getMode() {
        return mode;
    }

    public void setMode(ConfigurationMode mode) {
        this.mode = mode;
    }

    public Duration getAggregationWindow() {
        return aggregationWindow;
    }

    public void setAggregationWindow(Duration aggregationWindow) {
        this.aggregationWindow = aggregationWindow;
    }

    private String bootstrapServers;
    private int count;
    private ConfigurationMode mode;
    private Duration aggregationWindow;

    public Configuration() {
        this.bootstrapServers = "127.0.0.1:9094";
        this.count = 10;
        this.sleep = 1000;
        this.mode = ConfigurationMode.EdgeDevice;
        this.aggregationWindow = Duration.ofSeconds(30);
    }
}
