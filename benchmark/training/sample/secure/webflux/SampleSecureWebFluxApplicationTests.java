/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.secure.webflux;


import HttpStatus.UNAUTHORIZED;
import MediaType.APPLICATION_JSON;
import SpringBootTest.WebEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * Integration tests for a secure reactive application.
 *
 * @author Madhura Bhave
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "management.endpoint.health.show-details=never")
public class SampleSecureWebFluxApplicationTests {
    @Autowired
    private WebTestClient webClient;

    @Test
    public void userDefinedMappingsSecureByDefault() {
        this.webClient.get().uri("/").accept(APPLICATION_JSON).exchange().expectStatus().isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void healthInsecureByDefault() {
        this.webClient.get().uri("/actuator/health").accept(APPLICATION_JSON).exchange().expectStatus().isOk();
    }

    @Test
    public void infoInsecureByDefault() {
        this.webClient.get().uri("/actuator/info").accept(APPLICATION_JSON).exchange().expectStatus().isOk();
    }

    @Test
    public void otherActuatorsSecureByDefault() {
        this.webClient.get().uri("/actuator/env").accept(APPLICATION_JSON).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void userDefinedMappingsAccessibleOnLogin() {
        this.webClient.get().uri("/").accept(APPLICATION_JSON).header("Authorization", ("basic " + (getBasicAuth()))).exchange().expectBody(String.class).isEqualTo("Hello user");
    }

    @Test
    public void actuatorsAccessibleOnLogin() {
        this.webClient.get().uri("/actuator/health").accept(APPLICATION_JSON).header("Authorization", ("basic " + (getBasicAuth()))).exchange().expectBody(String.class).isEqualTo("{\"status\":\"UP\"}");
    }
}

