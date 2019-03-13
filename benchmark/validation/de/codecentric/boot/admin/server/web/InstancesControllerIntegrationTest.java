/**
 * Copyright 2014-2018 the original author or authors.
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
package de.codecentric.boot.admin.server.web;


import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;


public class InstancesControllerIntegrationTest {
    private int localPort;

    private WebTestClient client;

    private String register_as_test;

    private String register_as_twice;

    private ConfigurableApplicationContext instance;

    private ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, Object>>() {};

    @Test
    public void should_return_not_found_when_get_unknown_instance() {
        this.client.get().uri("/instances/unknown").exchange().expectStatus().isNotFound();
    }

    @Test
    public void should_return_empty_list() {
        this.client.get().uri("/instances?name=unknown").exchange().expectStatus().isOk().expectBody(List.class).isEqualTo(Collections.emptyList());
    }

    @Test
    public void should_return_not_found_when_deleting_unknown_instance() {
        this.client.delete().uri("/instances/unknown").exchange().expectStatus().isNotFound();
    }

    @Test
    public void should_return_registered_instances() {
        AtomicReference<String> id = new AtomicReference<>();
        CountDownLatch cdl = new CountDownLatch(1);
        StepVerifier.create(this.getEventStream().log()).expectSubscription().then(() -> {
            id.set(register());
            cdl.countDown();
        }).assertNext(( body) -> {
            try {
                cdl.await();
            } catch ( e) {
                Thread.interrupted();
            }
            assertThat(body).containsEntry("instance", id.get()).containsEntry("version", 0).containsEntry("type", "REGISTERED");
        }).then(() -> {
            assertInstances(id.get());
            assertInstancesByName(id.get());
            assertInstanceById(id.get());
        }).assertNext(( body) -> assertThat(body).containsEntry("instance", id.get()).containsEntry("version", 1).containsEntry("type", "STATUS_CHANGED")).then(() -> registerSecondTime(id.get())).assertNext(( body) -> assertThat(body).containsEntry("instance", id.get()).containsEntry("version", 2).containsEntry("type", "REGISTRATION_UPDATED")).then(() -> deregister(id.get())).assertNext(( body) -> assertThat(body).containsEntry("instance", id.get()).containsEntry("version", 3).containsEntry("type", "DEREGISTERED")).then(() -> {
            assertInstanceNotFound(id.get());
            assertEvents(id.get());
        }).thenCancel().verify(Duration.ofSeconds(60));
    }
}

