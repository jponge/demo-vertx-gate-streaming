plugins {
  id("io.vertx.vertx-plugin") version "0.3.1"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("io.vertx:vertx-kafka-client")
  implementation("io.vertx:vertx-rx-java2")
  implementation("io.vertx:vertx-web")
}

vertx {
  vertxVersion = "3.6.3"
  mainVerticle = "demo.MainVerticle"
}

tasks.wrapper {
  gradleVersion = "5.2.1"
}
