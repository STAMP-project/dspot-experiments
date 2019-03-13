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
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.kubernetes.config.example2.ExampleApp;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 *
 *
 * @author Charles Moulliard
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = ExampleApp.class, properties = { "spring.cloud.bootstrap.name=multiplecms" })
@AutoConfigureWebTestClient
public class MultipleConfigMapsSpringBootTest {
    @ClassRule
    public static KubernetesServer server = new KubernetesServer();

    private static KubernetesClient mockClient;

    @Autowired
    private WebTestClient webClient;

    // the last confimap defined in 'multiplecms.yml' has the highest priority, so
    // the common property defined in all configmaps is taken from the last one defined
    @Test
    public void testCommonMessage() {
        assertResponse("/common", "c3");
    }

    @Test
    public void testMessage1() {
        assertResponse("/m1", "m1");
    }

    @Test
    public void testMessage2() {
        assertResponse("/m2", "m2");
    }

    @Test
    public void testMessage3() {
        assertResponse("/m3", "m3");
    }
}

