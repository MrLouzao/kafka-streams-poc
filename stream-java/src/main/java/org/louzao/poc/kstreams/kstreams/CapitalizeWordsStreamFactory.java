package org.louzao.poc.kstreams.kstreams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.louzao.poc.kstreams.config.PropertiesFactory;

import java.util.Properties;

public class CapitalizeWordsStreamFactory {

    public static final String CAPITALIZE_APP_ID = "capitalize";

    public static KafkaStreams buildCapitalizeStringStream(String bootstrapServers, String inputTopic, String outputTopic) {
        final Properties streamConfiguration = PropertiesFactory.createStringSerdersRecordProperties(CAPITALIZE_APP_ID, bootstrapServers);
        final StreamsBuilder builder = createStreamBuilder(inputTopic, outputTopic);
        return new KafkaStreams(builder.build(), streamConfiguration);
    }


    private static StreamsBuilder createStreamBuilder(String inputTopic, String outputTopic) {
        final Serde<String> stringSerde = Serdes.String();
        final StreamsBuilder builder = new StreamsBuilder();

        // 1. Source: input of messages read from Topic. Consume as <key,value> = <String, String>
        KStream<String, String> streamSource =
                builder.stream(
                        inputTopic,
                        Consumed.with(stringSerde, stringSerde)
                );

        // 2. Transform the value to capitalized string
        // ** Check the Kstream <-> KTable duality!
        KStream<String, String> capitalizeStream =
                streamSource
                    .mapValues(msgValue -> msgValue.toUpperCase()                    );

        // 3. Send to output topic
        capitalizeStream.to(
                outputTopic,
                Produced.with(stringSerde, stringSerde)
        );

        return builder;
    }

}
