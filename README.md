## How to execute

1. Run kafka:

$ docker-compose up

2. Create topics:

$ chmod +x create-topics.sh
$ ./create-topics.sh

3. Run the producer:

$ docker exec -it broker kafka-console-producer --topic input-topic --broker-list localhost:9092


4. Paralelly, run the consumer:

$ docker exec -it broker kafka-console-consumer --topic input-topic --bootstrap-server localhost:9092 --from-beginning


5. Start the application running on docker container:

$ docker pull openjdk
$ mvn install 
$ docker run -it --rm --name test-kafka-stream --network kafka-streams_default -v "$PWD/stream-java/target":/mnt openjdk:8 java -jar /mnt/stream-java-1.0-SNAPSHOT-jar-with-dependencies.jar

