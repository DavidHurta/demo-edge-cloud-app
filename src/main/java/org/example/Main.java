//
//  Author: David Hurta (xhurta04)
//  Project: DIP
//

package org.example;

import org.apache.commons.cli.*;

import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        // Parse Options
        Options options = new Options();
        Option option = Option.builder("servers").longOpt("servers")
                .argName("servers")
                .hasArg()
                .required(true)
                .desc("Kafka Bootstrap Servers.").build();
        options.addOption(option);
        option = Option.builder("c").longOpt("count")
                .argName("count")
                .hasArg()
                .required(false)
                .desc("The number of messages to be sent. Applicable for sensors.").build();
        options.addOption(option);
        option = Option.builder("s").longOpt("sleep")
                .argName("sleep")
                .hasArg()
                .required(false)
                .desc("The sleep duration between messages. Applicable for sensors.").build();
        options.addOption(option);
        option = Option.builder("m").longOpt("mode")
                .argName("mode")
                .hasArg()
                .required(false)
                .desc("Mode of the application. Valid values: EdgeDevice, EdgeServer, CloudServer.").build();
        options.addOption(option);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Set Configuration
        Configuration config = new Configuration();
        if(cmd.hasOption("servers")) {
            config.setBootstrapServers(cmd.getOptionValue("servers"));
        }
        if(cmd.hasOption("count")) {
            config.setCount(Integer.parseInt(cmd.getOptionValue("count")));
        }
        if(cmd.hasOption("sleep")) {
            config.setSleep(Integer.parseInt(cmd.getOptionValue("sleep")));
        }
        if(cmd.hasOption("mode")) {
            String mode = cmd.getOptionValue("mode");
            if (Objects.equals(mode, "EdgeDevice")) {
                config.setMode(ConfigurationMode.EdgeDevice);
            } else if (Objects.equals(mode, "EdgeServer")) {
                config.setMode(ConfigurationMode.EdgeServer);
            } else {
                config.setMode(ConfigurationMode.CloudServer);
            }
        }

        switch(config.getMode()) {
            case EdgeDevice:
                EdgeDevice.run(config);
                break;
            case EdgeServer:
                EdgeServer.run(config);
                break;
            case CloudServer:
                CloudServer.run(config);
                break;
            default:
                System.err.println("Invalid Mode Value.");
        }
    }
}
