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
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import java.util.HashMap;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.kubernetes.config.example.App;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 *
 *
 * @author Charles Moulliard
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = App.class, properties = { "spring.application.name=configmap-example", "spring.cloud.kubernetes.reload.enabled=false" })
@AutoConfigureWebTestClient
public class ConfigMapsSpringBootTest {
    private static final String APPLICATION_NAME = "configmap-example";

    @ClassRule
    public static KubernetesServer server = new KubernetesServer();

    private static KubernetesClient mockClient;

    @Autowired(required = false)
    private Config config;

    @Autowired
    private WebTestClient webClient;

    @Test
    public void testConfig() {
        assertThat(ConfigMapsSpringBootTest.mockClient.getConfiguration().getMasterUrl()).isEqualTo(this.config.getMasterUrl());
        assertThat(ConfigMapsSpringBootTest.mockClient.getNamespace()).isEqualTo(this.config.getNamespace());
    }

    @Test
    public void testConfigMap() {
        ConfigMap configmap = ConfigMapsSpringBootTest.mockClient.configMaps().inNamespace("test").withName(ConfigMapsSpringBootTest.APPLICATION_NAME).get();
        HashMap<String, String> keys = ((HashMap<String, String>) (configmap.getData()));
        assertThat("Hello ConfigMap, %s!").isEqualTo(keys.get("bean.greeting"));
    }

    @Test
    public void testGreetingEndpoint() {
        this.webClient.get().uri("/api/greeting").exchange().expectStatus().isOk().expectBody().jsonPath("content").isEqualTo("Hello ConfigMap, World!");
    }
}

