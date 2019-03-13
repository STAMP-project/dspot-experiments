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
package org.springframework.cloud.kubernetes.config;


import SpringBootTest.WebEnvironment;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.kubernetes.config.example.App;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 *
 *
 * @author Charles Moulliard
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = App.class, properties = { "spring.application.name=configmap-with-profile-example", "spring.cloud.kubernetes.reload.enabled=false" })
@ActiveProfiles("development")
@AutoConfigureWebTestClient
public class ConfigMapsWithProfilesSpringBootTest {
    private static final String APPLICATION_NAME = "configmap-with-profile-example";

    @ClassRule
    public static KubernetesServer server = new KubernetesServer();

    private static KubernetesClient mockClient;

    @Autowired(required = false)
    Config config;

    @Autowired
    private WebTestClient webClient;

    @Test
    public void testGreetingEndpoint() {
        this.webClient.get().uri("/api/greeting").exchange().expectStatus().isOk().expectBody().jsonPath("content").isEqualTo("Hello ConfigMap dev, World!");
    }

    @Test
    public void testFarewellEndpoint() {
        this.webClient.get().uri("/api/farewell").exchange().expectStatus().isOk().expectBody().jsonPath("content").isEqualTo("Goodbye ConfigMap default, World!");
    }
}

