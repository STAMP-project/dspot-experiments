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
package de.codecentric.boot.admin.server;


import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;


public abstract class AbstractAdminApplicationTest {
    private WebTestClient webClient;

    private int port;

    @Test
    public void lifecycle() {
        AtomicReference<URI> location = new AtomicReference<>();
        StepVerifier.create(getEventStream().log()).expectSubscription().then(() -> {
            listEmptyInstances();
            location.set(registerInstance());
        }).assertNext(( event) -> assertThat(event.opt("type")).isEqualTo("REGISTERED")).assertNext(( event) -> assertThat(event.opt("type")).isEqualTo("STATUS_CHANGED")).assertNext(( event) -> assertThat(event.opt("type")).isEqualTo("ENDPOINTS_DETECTED")).assertNext(( event) -> assertThat(event.opt("type")).isEqualTo("INFO_CHANGED")).then(() -> {
            getInstance(location.get());
            listInstances();
            deregisterInstance(location.get());
        }).assertNext(( event) -> assertThat(event.opt("type")).isEqualTo("DEREGISTERED")).then(this::listEmptyInstances).thenCancel().verify(Duration.ofSeconds(60));
    }
}

