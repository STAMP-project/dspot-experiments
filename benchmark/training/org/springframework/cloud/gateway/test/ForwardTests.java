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
package org.springframework.cloud.gateway.test;


import HttpHeaders.HOST;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
@SuppressWarnings("unchecked")
public class ForwardTests {
    @LocalServerPort
    protected int port = 0;

    protected WebTestClient client;

    @Test
    public void forwardWorks() {
        this.client.get().uri("/localcontroller").header(HOST, "www.forward.org").exchange().expectStatus().isOk().expectBody().json("{\"from\":\"localcontroller\"}");
    }

    @Test
    public void forwardWithCorrectPath() {
        this.client.get().uri("/foo").header(HOST, "www.forward.org").exchange().expectStatus().isOk().expectBody().json("{\"from\":\"localcontroller\"}");
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @RestController
    @Import(PermitAllSecurityConfiguration.class)
    public static class TestConfig {
        @RequestMapping("/httpbin/localcontroller")
        public Map<String, String> localController() {
            return Collections.singletonMap("from", "localcontroller");
        }
    }
}

