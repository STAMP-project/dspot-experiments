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
package de.codecentric.boot.admin.server.notify;


import HttpMethod.POST;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class OpsGenieNotifierTest {
    private OpsGenieNotifier notifier;

    private RestTemplate restTemplate;

    private InstanceRepository repository;

    private static final Instance INSTANCE = Instance.create(InstanceId.of("-id-")).register(Registration.create("App", "http://health").build());

    @Test
    public void test_onApplicationEvent_resolve() {
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(OpsGenieNotifierTest.INSTANCE.getId(), ((OpsGenieNotifierTest.INSTANCE.getVersion()) + 1), StatusInfo.ofDown()))).verifyComplete();
        Mockito.reset(restTemplate);
        Mockito.when(repository.find(OpsGenieNotifierTest.INSTANCE.getId())).thenReturn(Mono.just(OpsGenieNotifierTest.INSTANCE.withStatusInfo(StatusInfo.ofUp())));
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(OpsGenieNotifierTest.INSTANCE.getId(), ((OpsGenieNotifierTest.INSTANCE.getVersion()) + 2), StatusInfo.ofUp()))).verifyComplete();
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.eq("https://api.opsgenie.com/v2/alerts/App_-id-/close"), ArgumentMatchers.eq(POST), ArgumentMatchers.eq(expectedRequest("DOWN", "UP")), ArgumentMatchers.eq(Void.class));
    }

    @Test
    public void test_onApplicationEvent_trigger() {
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(OpsGenieNotifierTest.INSTANCE.getId(), ((OpsGenieNotifierTest.INSTANCE.getVersion()) + 1), StatusInfo.ofUp()))).verifyComplete();
        Mockito.reset(restTemplate);
        Mockito.when(repository.find(OpsGenieNotifierTest.INSTANCE.getId())).thenReturn(Mono.just(OpsGenieNotifierTest.INSTANCE.withStatusInfo(StatusInfo.ofDown())));
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(OpsGenieNotifierTest.INSTANCE.getId(), ((OpsGenieNotifierTest.INSTANCE.getVersion()) + 2), StatusInfo.ofDown()))).verifyComplete();
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.eq("https://api.opsgenie.com/v2/alerts"), ArgumentMatchers.eq(POST), ArgumentMatchers.eq(expectedRequest("UP", "DOWN")), ArgumentMatchers.eq(Void.class));
    }
}

