package org.louzao.poc.kstreams;

import org.apache.kafka.streams.*;
import org.louzao.poc.kstreams.config.StreamConfiguration;
import org.louzao.poc.kstreams.kstreams.CapitalizeWordsStreamFactory;
import org.louzao.poc.kstreams.kstreams.CountWordStreamFactory;


public class MainStreamsProcessor {

    public static void main(String[] args) {
        final StreamConfiguration config = StreamConfiguration.get();
        final String inputTopic = config.getInputTopic();
        final String outputWordCountTopic = config.getOutputWordCountTopic();
        final String outputCapitalizeTopic = config.getOutputCapitalizeTopic();
        final String bootstrapServer =  config.getBootstrapServer();


        final KafkaStreams countWordsStream =
                CountWordStreamFactory
                        .buildCountWordsStream(bootstrapServer, inputTopic, outputWordCountTopic);
        final KafkaStreams capitalizeStream =
                CapitalizeWordsStreamFactory
                        .buildCapitalizeStringStream(bootstrapServer, inputTopic, outputCapitalizeTopic);


        System.out.println("Setting up streams...");
        countWordsStream.cleanUp();
        capitalizeStream.cleanUp();
        countWordsStream.start();
        capitalizeStream.start();
        System.out.println("Started!");


        // Shutdown hook to gracefully stop the stream app
        Runtime.getRuntime().addShutdownHook(new Thread(countWordsStream::close));
        Runtime.getRuntime().addShutdownHook(new Thread(capitalizeStream::close));
    }


}

