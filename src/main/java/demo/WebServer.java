package demo;

import io.reactivex.Completable;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;

public class WebServer extends AbstractVerticle {

  @Override
  public Completable rxStart() {
    Router router = Router.router(vertx);

    router.get("/").handler(rc -> rc.reroute("/static/index.html"));
    router.get("/static/*").handler(StaticHandler.create());

    PermittedOptions permissions = new PermittedOptions()
      .setAddressRegex("client.updates\\..+");
    BridgeOptions options = new BridgeOptions()
      .addOutboundPermitted(permissions);

    SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
    sockJSHandler.bridge(options);

    router.route("/eventbus/*").handler(sockJSHandler);

    return vertx.createHttpServer()
      .requestHandler(router)
      .rxListen(8080)
      .ignoreElement();
  }
}
