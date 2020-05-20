package org.louzao.poc.kstreams.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.StreamsConfig;
import org.louzao.poc.kstreams.kstreams.StreamConsts;

import java.util.Properties;

/**
 * Properties Factory for kafka configuration
 */
public class PropertiesFactory {


    public static Properties createStringSerdersRecordProperties(String appId, String bootstrapServers) {
        return createProperties(
                appId,
                bootstrapServers,
                StreamConsts.SERDES_STRING_CLASS,
                StreamConsts.SERDES_STRING_CLASS
        );
    }


    public static Properties createProperties(String appId, String bootstrapServers, String serdesKey, String serdesValue) {
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, appId + "-client");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, serdesKey);
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, serdesValue);
        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return streamsConfiguration;
    }
}
