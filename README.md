# Kafka Streams PoC

Proof of concept of Kafka Streams with a simple topology.


## What this PoC does

This PoC defines a topology with 2 sources and 2 sinkers. No intermediate nodes.
This app gets a string from input topic and place on output the count of words and the same input string but capitalized.


**Input topic:** word-topic

Each message that comes from word-topic topic is sinked onto two output topics

**Output topic:** word-count-topic <- Counts how many times a word appears among the stream messages. Interacts with local store. Generates 
an output message for each counted word.

**Output topic:** capitalize-topic <- Just capitalize the whole input string and forward to output.


## How to execute

1. Run kafka:

```
$ docker-compose up
```

2. Create topics:

```
$ chmod +x create-topics.sh
$ ./create-topics.sh
```

**Dont skip this step!** If topics are not created previously, the app will crash on startup.

3. Run the producer:

```
$ docker exec -it broker kafka-console-producer --topic word-topic --broker-list localhost:9092
```

4. Paralelly, run the consumers:

```
$ docker exec -it broker kafka-console-consumer --topic word-count-topic --bootstrap-server localhost:9092 --from-beginning
$ docker exec -it broker kafka-console-consumer --topic capitalize-topic --bootstrap-server localhost:9092 --from-beginning
```

5. Start the application running on docker container:

```
$ docker pull openjdk
$ mvn install 
$ docker run -it --rm --name test-kafka-stream --network kafka-streams_default -v "$PWD/stream-java/target":/mnt openjdk:8 java -jar /mnt/stream-java-1.0-SNAPSHOT-jar-with-dependencies.jar
```
