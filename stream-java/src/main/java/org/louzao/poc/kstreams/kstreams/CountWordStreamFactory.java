package org.louzao.poc.kstreams.kstreams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.louzao.poc.kstreams.config.PropertiesFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public class CountWordStreamFactory {

    public static final String WORD_COUNT_APP_ID = "word-count-live";

    /**
     * Create a Stream that count words
     * @param bootstrapServers
     * @param inputTopic
     * @param outputTopic
     * @return
     */
    public static KafkaStreams buildCountWordsStream(String bootstrapServers, String inputTopic, String outputTopic) {
        final Properties streamConfiguration = PropertiesFactory.createStringSerdersRecordProperties(WORD_COUNT_APP_ID, bootstrapServers);
        final StreamsBuilder builder = createStreamBuilder(inputTopic, outputTopic);
        return new KafkaStreams(builder.build(), streamConfiguration);
    }


    private static StreamsBuilder createStreamBuilder(String inputTopic, String outputTopic) {
        // Indicate which serdes are used here
        final Serde<String> stringSerde = Serdes.String(); // Serde gives predefined String serializer/deserializer. This is the key
        final Serde<Long> longSerde = Serdes.Long(); // This is the value


        // Create a builder
        final StreamsBuilder builder = new StreamsBuilder();

        // 1. Source: input of messages read from Topic. Consume as <key,value> = <String, String>
        KStream<String, String> streamSource =
                builder.stream(
                        inputTopic,
                        Consumed.with(stringSerde, stringSerde)
                );

        // 2. Transform stream into KTable to obtain pairs of <String: word, Long: howTimesAppear> table
        // ** Check the Kstream <-> KTable duality!
        KTable<String, Long> countWordsTable = streamSource
                .flatMapValues(msgValue -> splitWordsByBlankSpaceAsList(msgValue)) // modify the value of the message, maintain the key
                .groupBy((key, word) -> word) // change key and create a list of keys depending on grouping criteria as key
                .count(); // Count ocurrences of each word

        // 3. Send words counting to output topic with a message indicating how many times the word is repeated
        // Produced -> <String key, Long value>
        countWordsTable
                .toStream()
                .to(
                        outputTopic,
                        Produced.with(stringSerde, longSerde)
                    );


        return builder;
    }


    private static List<String> splitWordsByBlankSpaceAsList(String words) {
        final Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);
        final String[] splitedWordsInLowerCase = pattern.split(words.toLowerCase());
        return Arrays.asList(splitedWordsInLowerCase);
    }


}
