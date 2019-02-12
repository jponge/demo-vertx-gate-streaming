package demo;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord;

public class GatesGlobalStats extends AbstractVerticle {

  private EventBus eventBus;
  private KafkaConsumer<String, JsonObject> consumer;

  private long[] total = new long[Config.GATES_NUMBER];

  @Override
  public void start() {
    eventBus = vertx.eventBus();
    consumer = KafkaConsumer.create(vertx, Config.consumer("gates-total"));
    consumer.rxSubscribe("gate.activation")
      .andThen(consumer.toObservable())
      .subscribe(this::processRecord);
  }

  private void processRecord(KafkaConsumerRecord<String, JsonObject> record) {
    // Increment
    Integer id = record.value().getInteger("gateId");
    total[id] = total[id] + 1;

    // Ack
    consumer.commit();

    // Notify whoever wants to be
    eventBus.publish("client.updates.counter", new JsonObject()
      .put("gateId", id)
      .put("total", total[id]));
  }
}
