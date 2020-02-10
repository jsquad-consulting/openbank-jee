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

package se.jsquad;

import com.google.gson.Gson;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.jsquad.api.client.info.ClientApi;
import se.jsquad.api.client.info.TypeApi;
import se.jsquad.getclientservice.ClientType;
import se.jsquad.getclientservice.Type;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Execution(ExecutionMode.SAME_THREAD)
public class ClientInformationServletIT {
    private Gson gson = new Gson();
    private CloseableHttpClient httpClient;

    private static String baseUrl;
    private static String port;
    private static String basePath = "/servlet";

    private static DockerComposeContainer dockerComposeContainer = new DockerComposeContainer(
            new File("../docker-compose_local.yaml"))
            .withExposedService("openbank_1", 8080)
            .withTailChildContainers(true)
            .withLocalCompose(true);

    @BeforeAll
    static void setupDocker() {
        dockerComposeContainer.start();

        baseUrl = "http://" + dockerComposeContainer.getServiceHost("openbank_1", 8080);
        port = Integer.toString(dockerComposeContainer.getServicePort("openbank_1", 8080));
    }

    @AfterAll
    static void destroyDocker() {
        dockerComposeContainer.stop();
    }

    @BeforeEach
    void initBeforeEachTest() {
        CredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials("root", "root");

        basicCredentialsProvider.setCredentials(AuthScope.ANY, credentials);
        httpClient =  HttpClients.custom().setDefaultCredentialsProvider(basicCredentialsProvider).build();
    }

    @Test
    public void testGetClientInformation() throws IOException {
        // Given
        HttpGet request = new HttpGet(baseUrl + ":" + port + basePath + "/ClientInformationServlet");
        request.addHeader("personalIdentificationNumber", "191212121212");

        // When
        CloseableHttpResponse response = httpClient.execute(request);

        // Then
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("OK", response.getStatusLine().getReasonPhrase());

        StringBuilder stringBuilder = new StringBuilder();
        String line ;

        try (BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                             StandardCharsets.UTF_8))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        ClientApi clientApi = gson.fromJson(stringBuilder.toString(), ClientApi.class);

        assertEquals("191212121212", clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
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

    @Test
    public void testClientThroughSoapService() throws IOException {
        // Given
        HttpGet request = new HttpGet(baseUrl + ":" + port + basePath + "/ClientToSoapServlet");
        request.addHeader("personalIdentificationNumber", "191212121212");

        // When
        CloseableHttpResponse response = httpClient.execute(request);

        // Then
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("OK", response.getStatusLine().getReasonPhrase());

        StringBuilder stringBuilder = new StringBuilder();
        String line ;

        try (BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                             StandardCharsets.UTF_8))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        ClientType clientType = gson.fromJson(stringBuilder.toString(), ClientType.class);

        assertEquals("191212121212", clientType.getPerson().getPersonIdentification());
        assertEquals("John", clientType.getPerson().getFirstName());
        assertEquals("Doe", clientType.getPerson().getLastName());
        assertEquals("john.doe@test.se", clientType.getPerson().getMail());

        assertEquals(1, clientType.getAccountList().size());
        assertEquals(500.0, clientType.getAccountList().get(0).getBalance());

        assertEquals(1, clientType.getAccountList().get(0).getAccountTransactionList().size());
        assertEquals("DEPOSIT", clientType.getAccountList().get(0).getAccountTransactionList().get(0)
                .getTransactionType().name());
        assertEquals("500$ in deposit", clientType.getAccountList().get(0).getAccountTransactionList().get(0)
                .getMessage());
        assertEquals(0, clientType.getPerson().getAddressList().size());

        assertEquals(Type.REGULAR, clientType.getClientType().getType());
        assertEquals(500, clientType.getClientType().getRating());
    }
}