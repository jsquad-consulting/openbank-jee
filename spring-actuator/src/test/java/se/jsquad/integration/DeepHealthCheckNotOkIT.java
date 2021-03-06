/*
 * Copyright 2021 JSquad AB
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

import com.google.gson.Gson;
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
import se.jsquad.health.check.DeepSystemStatusResponse;
import se.jsquad.health.check.HealthStatus;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Execution(ExecutionMode.SAME_THREAD)
public class DeepHealthCheckNotOkIT {
    private Gson gson = new Gson();

    private static int servicePort = 8081;

    private static DockerComposeContainer dockerComposeContainer = new DockerComposeContainer(
            new File("../docker-compose-int.yaml"))
            .withExposedService("openbank", servicePort)
            .withPull(false)
            .withTailChildContainers(false)
            .withLocalCompose(true);

    @BeforeAll
    static void setupDocker() {
        dockerComposeContainer.start();

        RestAssured.baseURI = "http://" + dockerComposeContainer.getServiceHost("openbank", servicePort);
        RestAssured.port = dockerComposeContainer.getServicePort("openbank", servicePort);
        RestAssured.basePath = "/management/actuator";
    }

    @AfterAll
    static void destroyDocker() {
        dockerComposeContainer.stop();
    }

    @Test
    public void testDeepHealthCheckNotOk() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        // Given
        Method method = DockerComposeContainer.class.getDeclaredMethod("runWithCompose", String.class);
        method.setAccessible(true);
        method.invoke(dockerComposeContainer, "kill openbankdb");

        // When
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(URI.create("/deephealth")).andReturn();

        // Then
        DeepSystemStatusResponse deepSystemStatusResponse = gson.fromJson(response.getBody().asString(),
                DeepSystemStatusResponse.class);

        assertEquals(200, response.getStatusCode());
        assertEquals(HealthStatus.DOWN, deepSystemStatusResponse.getStatus());
        assertEquals(HealthStatus.UP, deepSystemStatusResponse.getService());
        assertEquals(HealthStatus.DOWN, deepSystemStatusResponse.getDependencies().getOpenbankDb());
    }
}