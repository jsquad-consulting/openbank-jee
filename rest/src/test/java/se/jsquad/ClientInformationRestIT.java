/*
 *    Copyright 2019 JSquad AB
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package se.jsquad;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import se.jsquad.client.info.ClientApi;
import se.jsquad.client.info.TypeApi;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class ClientInformationRestIT {
    Gson gson = new Gson();

    @Container
    private static GenericContainer container = new GenericContainer("openbank")
            .withExposedPorts(8080)
            .withFileSystemBind("../target/jacoco-agent", "/jacoco-agent", BindMode.READ_WRITE)
            .withFileSystemBind("../target/jacoco-it", "/jacoco-it", BindMode.READ_WRITE)
            .withFileSystemBind("../target", "/jacoco-path", BindMode.READ_WRITE)
            .withCopyFileToContainer(MountableFile.forClasspathResource("configuration/jboss/standalone.conf"),
                    "/usr/wildfly/bin/standalone.conf")
            .withCommand("/usr/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0");

    @BeforeAll
    static void setupDocker() {
        container.start();

        RestAssured.baseURI = "http://" + container.getContainerIpAddress();
        RestAssured.port = container.getMappedPort(8080);
        RestAssured.basePath = "/restful-webservice/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterAll
    static void destroyDocker() {
        container.close();
    }

    @Test
    public void testGetClientInformation() {
        // Given
        String personIdentificationNumber = "191212121212";

        // When
        Response response = RestAssured.given()
                .auth()
                .basic("root", "root")
                .contentType(ContentType.JSON)
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

        assertEquals(TypeApi.REGULAR, clientApi.getClientType().getType());
        assertEquals(500, clientApi.getClientType().getRating());
    }
}
