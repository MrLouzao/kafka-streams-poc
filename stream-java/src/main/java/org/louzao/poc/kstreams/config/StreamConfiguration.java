package org.louzao.poc.kstreams.config;

/**
 * Configurations here. This class must be populated with values from config file instead of
 * hardcoding the values!
 */
public class StreamConfiguration {

    private StreamConfiguration instance;

    private final String inputTopic = "word-topic";
    private final String outputWordCountTopic = "word-count-topic";
    private final String outputCapitalizeTopic = "capitalize-topic";
    private final String bootstrapServer =  "broker:9092";

    private StreamConfiguration() {
    }

    public String getInputTopic() {
        return inputTopic;
    }

    public String getOutputWordCountTopic() {
        return outputWordCountTopic;
    }

    public String getOutputCapitalizeTopic() {
        return outputCapitalizeTopic;
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }


    /**
     * Only invoke the class as a singleton
     * @return
     */
    public static StreamConfiguration get() {
        return new StreamConfiguration();
    }

}
