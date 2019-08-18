/*
 * Copyright 2019 JSquad AB
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

package se.jsquad;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.jsquad.api.client.info.ClientApi;
import se.jsquad.api.client.info.TypeApi;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Execution(ExecutionMode.SAME_THREAD)
public class ClientInformationRestIT {
    private static Gson gson = new Gson();

    private final OpenApiValidationFilter validationFilter = new OpenApiValidationFilter("target/openbankAPI.yaml");

    private static Network network = Network.newNetwork();

    private static GenericContainer openbankContainer = new GenericContainer("openbank:latest")
            .withNetwork(network)
            .withExposedPorts(8080)
            .waitingFor(Wait.forListeningPort());

    private static GenericContainer openbankDbContainer = new GenericContainer("mysql:8.0.17")
            .withEnv("MYSQL_DATABASE", "openbankdb")
            .withEnv("MYSQL_USER", System.getenv("OB_USER"))
            .withEnv("MYSQL_PASSWORD", System.getenv("OB_PASSWORD"))
            .withEnv("MYSQL_ROOT_PASSWORD", System.getenv("ROOT_PASSWORD"))
            .withNetwork(network)
            .withNetworkAliases("openbankdb")
            .withExposedPorts(3306)
            .waitingFor(Wait.forListeningPort());

    @BeforeAll
    static void setupDocker() {
        openbankDbContainer.start();
        openbankContainer.start();

        RestAssured.baseURI = "http://" + openbankContainer.getContainerIpAddress();
        RestAssured.port = openbankContainer.getMappedPort(8080);
        RestAssured.basePath = "/restful-webservice/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterAll
    static void destroyDocker() {
        openbankContainer.stop();
        openbankDbContainer.stop();
    }

    @Test
    public void testGetClientInformation() {
        // Given
        String personIdentificationNumber = "191212121212";

        // When
        Response response = RestAssured
                .given()
                .auth()
                .basic("root", "root")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(validationFilter)
                .when()
                .get(URI.create("/client/info/" + personIdentificationNumber)).andReturn();

        ClientApi clientApi = gson.fromJson(response.getBody().print(), ClientApi.class);

        // Then
        assertEquals(200, response.getStatusCode());

        assertEquals(personIdentificationNumber, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(personIdentificationNumber, clientApi.getPerson().getPersonIdentification());
        assertEquals("john.doe@test.se", clientApi.getPerson().getMail());

        assertEquals(1, clientApi.getAccountList().size());
        assertEquals(500.0, clientApi.getAccountList().get(0).getBalance());

        assertEquals(1, clientApi.getAccountList().get(0).getAccountTransactionList().size());
        assertEquals("DEPOSIT", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getTransactionType().name());
        assertEquals("500$ in deposit", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getMessage());
        assertEquals(0, clientApi.getPerson().getAddressList().size());

        assertEquals(TypeApi.REGULAR, clientApi.getClientType().getType());
        assertEquals(500, clientApi.getClientType().getRating());
    }
}
