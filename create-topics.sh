docker exec -it broker /usr/bin/kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic input-topic

docker exec -it broker /usr/bin/kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic output-topic

docker exec -it broker /usr/bin/kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic input-forward-topic

docker exec -it broker /usr/bin/kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic output-forward-topic
	
