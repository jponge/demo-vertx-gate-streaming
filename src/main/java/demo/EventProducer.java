package demo;


import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.kafka.client.producer.KafkaProducer;
import io.vertx.reactivex.kafka.client.producer.KafkaProducerRecord;

import java.util.Random;

public class EventProducer extends AbstractVerticle {

  private final Random random = new Random();

  private KafkaProducer<String, JsonObject> producer;

  @Override
  public void start() {
    producer = KafkaProducer.create(vertx, Config.producer());
    for (int i = 0; i < Config.GATES_NUMBER; i++) {
      scheduleEvents(i);
    }
  }

  private void scheduleEvents(int gateId) {
    vertx.setPeriodic(200, id -> {
      if (random.nextInt(3) % 3 == 0) {

        // Payload
        String key = Integer.toString(gateId);
        JsonObject value = new JsonObject()
          .put("gateId", gateId)
          .put("timestamp", System.currentTimeMillis());

        // Record
        KafkaProducerRecord<String, JsonObject> record = KafkaProducerRecord
          .create("gate.activation", key, value);

        // Send it!
        producer.write(record/*, ar -> System.out.println("✨")*/);

      }
    });
  }
}
