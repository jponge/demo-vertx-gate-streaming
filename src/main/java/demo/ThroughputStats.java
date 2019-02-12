package demo;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class ThroughputStats extends AbstractVerticle {

  private KafkaConsumer<String, JsonObject> consumer;
  private EventBus eventBus;

  @Override
  public void start() {
    eventBus = vertx.eventBus();

    consumer = KafkaConsumer.create(vertx, Config.consumer("thrpt-stats"));

    consumer.rxSubscribe("gate.activation")
      .andThen(consumer.toFlowable())
      .map(record -> record.value().getInteger("gateId"))
      .buffer(5, TimeUnit.SECONDS, RxHelper.scheduler(vertx))
      .map(this::computeAggregates)
      .subscribe(this::processAggregates);
  }

  private Map<Integer, Long> computeAggregates(List<Integer> activations) {
    return activations.stream().collect(groupingBy(identity(), counting()));
  }

  private void processAggregates(Map<Integer, Long> aggregates) {
    JsonObject payload = new JsonObject();
    aggregates.forEach((gateId, count) -> {
      String thrpt = String.format("%.2f", ((double) count / 5.0d));
      payload.put(gateId.toString(), thrpt);
    });
    eventBus.publish("client.updates.aggregates", payload);
  }
}
