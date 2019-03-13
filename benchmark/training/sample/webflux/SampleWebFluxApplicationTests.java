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
package sample.webflux;


import MediaType.APPLICATION_JSON;
import MediaType.TEXT_PLAIN;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


/**
 * Basic integration tests for WebFlux application.
 *
 * @author Brian Clozel
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SampleWebFluxApplicationTests {
    @Autowired
    private WebTestClient webClient;

    @Test
    public void testWelcome() {
        this.webClient.get().uri("/").accept(TEXT_PLAIN).exchange().expectBody(String.class).isEqualTo("Hello World");
    }

    @Test
    public void testEcho() {
        this.webClient.post().uri("/echo").contentType(TEXT_PLAIN).accept(TEXT_PLAIN).body(Mono.just("Hello WebFlux!"), String.class).exchange().expectBody(String.class).isEqualTo("Hello WebFlux!");
    }

    @Test
    public void testActuatorStatus() {
        this.webClient.get().uri("/actuator/health").accept(APPLICATION_JSON).exchange().expectStatus().isOk().expectBody().json("{\"status\":\"UP\"}");
    }
}

