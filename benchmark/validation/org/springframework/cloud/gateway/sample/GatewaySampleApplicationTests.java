/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.gateway.sample;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.gateway.test.HttpBinCompatibleController;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 *
 *
 * @author Spencer Gibb
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GatewaySampleApplicationTests.TestConfig.class }, webEnvironment = RANDOM_PORT, properties = "management.server.port=${test.port}")
public class GatewaySampleApplicationTests {
    protected static int managementPort;

    @LocalServerPort
    protected int port = 0;

    protected WebTestClient webClient;

    protected String baseUri;

    @Test
    public void contextLoads() {
        webClient.get().uri("/get").exchange().expectStatus().isOk();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readBodyPredicateStringWorks() {
        webClient.post().uri("/post").header("Host", "www.readbody.org").syncBody("hi").exchange().expectStatus().isOk().expectHeader().valueEquals("X-TestHeader", "read_body_pred").expectBody(Map.class).consumeWith(( result) -> assertThat(result.getResponseBody()).containsEntry("data", "hi"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void rewriteRequestBodyStringWorks() {
        webClient.post().uri("/post").header("Host", "www.rewriterequestupper.org").syncBody("hello").exchange().expectStatus().isOk().expectHeader().valueEquals("X-TestHeader", "rewrite_request_upper").expectBody(Map.class).consumeWith(( result) -> assertThat(result.getResponseBody()).containsEntry("data", "HELLOHELLO"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void rewriteRequestBodyObjectWorks() {
        webClient.post().uri("/post").header("Host", "www.rewriterequestobj.org").syncBody("hello").exchange().expectStatus().isOk().expectHeader().valueEquals("X-TestHeader", "rewrite_request").expectBody(Map.class).consumeWith(( result) -> assertThat(result.getResponseBody()).containsEntry("data", "{\"message\":\"HELLO\"}"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void rewriteResponseBodyStringWorks() {
        webClient.post().uri("/post").header("Host", "www.rewriteresponseupper.org").syncBody("hello").exchange().expectStatus().isOk().expectHeader().valueEquals("X-TestHeader", "rewrite_response_upper").expectBody(Map.class).consumeWith(( result) -> assertThat(result.getResponseBody()).containsEntry("DATA", "HELLO"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void rewriteResponeBodyObjectWorks() {
        webClient.post().uri("/post").header("Host", "www.rewriteresponseobj.org").syncBody("hello").exchange().expectStatus().isOk().expectHeader().valueEquals("X-TestHeader", "rewrite_response_obj").expectBody(String.class).consumeWith(( result) -> assertThat(result.getResponseBody()).isEqualTo("hello"));
    }

    @Test
    public void complexPredicate() {
        webClient.get().uri("/anything/png").header("Host", "www.abc.org").exchange().expectHeader().valueEquals("X-TestHeader", "foobar").expectStatus().isOk();
    }

    @Test
    public void actuatorManagementPort() {
        webClient.get().uri((("http://localhost:" + (GatewaySampleApplicationTests.managementPort)) + "/actuator/gateway/routes")).exchange().expectStatus().isOk();
    }

    @Test
    public void actuatorMetrics() {
        contextLoads();
        webClient.get().uri((("http://localhost:" + (GatewaySampleApplicationTests.managementPort)) + "/actuator/metrics/gateway.requests")).exchange().expectStatus().isOk().expectBody().consumeWith(( i) -> {
            String body = new String(i.getResponseBodyContent());
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode actualObj = mapper.readTree(body);
                JsonNode findValue = actualObj.findValue("name");
                assertThat(findValue.asText()).as("Expected to find metric with name gateway.requests").isEqualTo("gateway.requests");
            } catch ( e) {
                throw new <e>IllegalStateException();
            }
        });
    }

    @Configuration
    @EnableAutoConfiguration
    @RibbonClient(name = "httpbin", configuration = GatewaySampleApplicationTests.RibbonConfig.class)
    @Import(GatewaySampleApplication.class)
    protected static class TestConfig {
        @Bean
        public HttpBinCompatibleController httpBinCompatibleController() {
            return new HttpBinCompatibleController();
        }
    }

    protected static class RibbonConfig {
        @LocalServerPort
        int port;

        @Bean
        @Primary
        public ServerList<Server> ribbonServerList() {
            return new org.springframework.cloud.netflix.ribbon.StaticServerList(new Server("localhost", port));
        }
    }
}

