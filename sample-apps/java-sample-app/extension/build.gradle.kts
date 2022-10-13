plugins {

    `java-library`
}
val otelVersion = "1.18.0"
repositories {
    mavenCentral()
}
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    compileOnly("com.google.auto.service:auto-service:1.0.1")
    annotationProcessor("com.google.auto.service:auto-service:1.0.1")

    compileOnly("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure-spi:${otelVersion}")
    compileOnly("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:${otelVersion}")
    compileOnly("io.opentelemetry.javaagent:opentelemetry-javaagent-extension-api:${otelVersion}-alpha")
    compileOnly("io.opentelemetry:opentelemetry-semconv:1.18.0-alpha")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
