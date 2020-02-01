/*
 * Copyright 2020 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.jsquad.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@Execution(ExecutionMode.SAME_THREAD)
public class MonitorPrometheusUpStatusIT {
    private static int servicePort = 8081;

    private static DockerComposeContainer dockerComposeContainer = new DockerComposeContainer(
            new File("src/test/resources/docker-compose-int.yaml"))
            .withExposedService("openbank_1", servicePort)
            .withPull(false)
            .withTailChildContainers(true)
            .withLocalCompose(true);

    @BeforeAll
    static void setupDocker() {
        dockerComposeContainer.start();

        RestAssured.baseURI = "http://" + dockerComposeContainer.getServiceHost("openbank_1", servicePort);
        RestAssured.port = dockerComposeContainer.getServicePort("openbank_1", servicePort);
        RestAssured.basePath = "/management/actuator";
    }

    @AfterAll
    static void destroyDocker() {
        dockerComposeContainer.stop();
    }

    @Test
    public void testDeepHealthMetricsOk() {
        // When
        Response response = RestAssured
                .given()
                .contentType(ContentType.ANY)
                .accept(ContentType.ANY)
                .when()
                .get(URI.create("/prometheus")).andReturn();

        // Then
        assertEquals(200, response.getStatusCode());

        assertTrue(response.getBody().asString().contains("deep_health{status=\"up\",} 1.0"));
        assertTrue(response.getBody().asString().contains("deep_health_service{status=\"up\",} 1.0"));
        assertTrue(response.getBody().asString().contains("deep_health_openbank_database{status=\"up\",} 1.0"));
    }
}