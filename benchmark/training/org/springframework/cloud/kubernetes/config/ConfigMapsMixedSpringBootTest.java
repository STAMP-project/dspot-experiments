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
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.kubernetes.config.example.App;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = App.class, properties = { "spring.application.name=" + (ConfigMapsMixedSpringBootTest.APPLICATION_NAME), "spring.cloud.kubernetes.config.enableApi=true", "spring.cloud.kubernetes.config.paths=" + (ConfigMapsMixedSpringBootTest.FILE_NAME_FULL_PATH) })
public class ConfigMapsMixedSpringBootTest {
    protected static final String FILES_ROOT_PATH = "/tmp/scktests";

    protected static final String FILE_NAME = "application-path.yaml";

    protected static final String FILE_NAME_FULL_PATH = ((ConfigMapsMixedSpringBootTest.FILES_ROOT_PATH) + "/") + (ConfigMapsMixedSpringBootTest.FILE_NAME);

    protected static final String APPLICATION_NAME = "configmap-mixed-example";

    @ClassRule
    public static KubernetesServer server = new KubernetesServer();

    private static KubernetesClient mockClient;

    @Autowired
    private WebTestClient webClient;

    @Test
    public void greetingInputShouldReturnPropertyFromFile() {
        this.webClient.get().uri("/api/greeting").exchange().expectStatus().isOk().expectBody().jsonPath("content").isEqualTo("Hello ConfigMap, World from path");
    }

    @Test
    public void farewellInputShouldReturnPropertyFromFile() {
        this.webClient.get().uri("/api/farewell").exchange().expectStatus().isOk().expectBody().jsonPath("content").isEqualTo("Bye ConfigMap, World from path");
    }

    @Test
    public void morningInputShouldReturnPropertyFromApi() {
        this.webClient.get().uri("/api/morning").exchange().expectStatus().isOk().expectBody().jsonPath("content").isEqualTo("Buenos Dias ConfigMap, World");
    }
}

