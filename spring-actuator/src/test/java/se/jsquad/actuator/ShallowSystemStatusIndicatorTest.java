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

package se.jsquad.actuator;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import se.jsquad.health.check.HealthStatus;
import se.jsquad.health.check.ShallowSystemStatusResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:test/application.properties"})
public class ShallowSystemStatusIndicatorTest {
    private Gson gson = new Gson();

    @Autowired
    private WebApplicationContext context;

    @Test
    public void testShallowSystemStatus() throws Exception {
        // Given
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // When and then
        MvcResult mvcResult =
                mockMvc.perform(get("/actuator/shallowhealth").contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        ShallowSystemStatusResponse shallowSystemStatusResponse =
                gson.fromJson(mvcResult.getResponse().getContentAsString(), ShallowSystemStatusResponse.class);

        assertEquals(HealthStatus.UP, shallowSystemStatusResponse.getStatus());

    }
}