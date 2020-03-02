import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

public class MainStreamsProcessor {

    public static void main(String[] args) {
        final String inputTopic = "input-topic";
        final String outputTopic = "output-topic";
        final String bootstrapServer =  "broker:9092";

        final KafkaStreams streams = buildCountWordsStream(bootstrapServer, inputTopic, outputTopic);
        streams.cleanUp();
        streams.start();
        System.out.println("Started!");

        // Shutdown hook to gracefully stop the stream app
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }



    static KafkaStreams buildForwardingStream(String bootstrapServers, String inputTopic, String outputTopic) {
                // 1. CONFIGURE THE INPUT TOPIC
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "forward-live-test");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "forward-live-test-client");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        //streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5 * 1000); // each 5 seconds commit


        // 2.  FORWARD MESSAGES FROM INPUT TO OUTPUT WITHOUT PROCESSING
        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream(inputTopic);
        stream.foreach((o, o2) -> System.out.println("IMPRIMO EL OBJETO QUE ME VIENE: " + o));
        stream.to(outputTopic);

        // Create the object of streaming working
        return new KafkaStreams(builder.build(), streamsConfiguration);
    }


    static KafkaStreams buildCountWordsStream(String bootstrapServers,  String inputTopic, String outputTopic) {
        Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);
        Serde<String> stringSerde = Serdes.String(); // Serde gives predefined String serializer/deserializer. This is the key
        Serde<Long> longSerde = Serdes.Long(); // This is the value

        // 1. CONFIGURE THE INPUT TOPIC
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-live-test");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "wordcount-live-test-client");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        // 2. CREATE THE KAFKA STREAM TOPOLOGY - counts words that comes in messages from kafka input-topic
        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> stream = builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));
        KTable<String, Long> source = stream
                .flatMapValues(value -> Arrays.asList(pattern.split(((String)value).toLowerCase()))) // Split words using reg ex
                .groupBy((key, word) -> word)
                .count(); // Count ocurrences of each word

        // 3. PUBLISH RESULT OF STREAM TO ANOTHER TOPIC
        // Print results and send to another topic
        source
                .toStream()
                .foreach((w, c) -> System.out.println("Word " + w + " -> " +  c));
        source.toStream().to(outputTopic, Produced.with(stringSerde, longSerde));


        // Create the object of streaming working
        return new KafkaStreams(builder.build(), streamsConfiguration);
    }


}

