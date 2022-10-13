import com.google.cloud.tools.jib.gradle.ExtraDirectoryParameters

/*
 * Copyright The OpenTelemetry Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    application
    id("com.google.cloud.tools.jib").version("3.3.0")
}

val otelVersion = "1.18.0"

repositories {
    mavenCentral()
}

val javaagentDependency by configurations.creating {
    extendsFrom()
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Base application
    implementation(project(":base"))

    // Necessary to download the jar of the Java Agent
    javaagentDependency("software.amazon.opentelemetry:aws-opentelemetry-agent:${otelVersion}@jar")
    javaagentDependency(project(":extension"))
}


application {
    mainClass.set("software.amazon.adot.sampleapp.MainAuto")
    applicationDefaultJvmArgs = listOf(
        "-javaagent:$buildDir/javaagent/aws-opentelemetry-agent-${otelVersion}.jar", // Use the Java agent when the application is run
        "-Dotel.service.name=java-sample-app",  // sets the name of the application in traces and metrics.
        "-Dotel.javaagent.extensions=${buildDir}/javaagent/extension.jar")
}

val agentJibDir = "/opt/adot-agent"
val hostDir = "${buildDir}/javaagent"

jib {
    to {
        image = "public.ecr.aws/u3f4a6a8/java-sample-app:alpha"
    }
    extraDirectories.paths {
        path {
            setFrom(hostDir)
            into = agentJibDir
        }
    }
    container.jvmFlags = listOf("-javaagent:$agentJibDir/aws-opentelemetry-agent-${otelVersion}.jar", // Use the Java agent when the application is run
        "-Dotel.service.name=java-sample-app",  // sets the name of the application in traces and metrics.
        "-Dotel.javaagent.extensions=${agentJibDir}/extension.jar")
    from {
        image = "gcr.io/distroless/java17-debian11:debug"
    }
}

tasks.register<Copy>("download") {
    from(javaagentDependency)
    into("$buildDir/javaagent")
}

tasks.named("run") {
    dependsOn("download")
}

tasks.named("jib") {
    dependsOn("download")
}

tasks.named("jibDockerBuild") {
    dependsOn("download")
}
