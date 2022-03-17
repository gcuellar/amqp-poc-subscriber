# amqp-poc-subscriber

## Run RabbitMQ in localhost with Docker
```
docker run -detach --rm \
--hostname poc-fanout-notification-service-rabbit \
--name poc-fanout-notification-service-rabbit \
--publish 15672:15672 \
--publish 5672:5672 \
rabbitmq:3.8-management
```

## Build 

```bash
./gradlew clean assemble
```

## Run

```bash
java -jar build/libs/amqp.poc-subscriber-0.0.1-SNAPSHOT.jar
```
