package demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {
    vertx.deployVerticle("demo.EventProducer");
    vertx.deployVerticle("demo.GatesGlobalStats");
    vertx.deployVerticle("demo.ThroughputStats");
    vertx.deployVerticle("demo.WebServer");
  }

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new MainVerticle());
  }
}
