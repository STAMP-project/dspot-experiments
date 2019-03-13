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


import PagerdutyNotifier.DEFAULT_URI;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class PagerdutyNotifierTest {
    private PagerdutyNotifier notifier;

    private RestTemplate restTemplate;

    private InstanceRepository repository;

    private static final String appName = "App";

    private static final Instance INSTANCE = Instance.create(InstanceId.of("-id-")).register(Registration.create(PagerdutyNotifierTest.appName, "http://health").build());

    @Test
    public void test_onApplicationEvent_resolve() {
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(PagerdutyNotifierTest.INSTANCE.getId(), ((PagerdutyNotifierTest.INSTANCE.getVersion()) + 1), StatusInfo.ofDown()))).verifyComplete();
        Mockito.reset(restTemplate);
        StatusInfo up = StatusInfo.ofUp();
        Mockito.when(repository.find(PagerdutyNotifierTest.INSTANCE.getId())).thenReturn(Mono.just(PagerdutyNotifierTest.INSTANCE.withStatusInfo(up)));
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(PagerdutyNotifierTest.INSTANCE.getId(), ((PagerdutyNotifierTest.INSTANCE.getVersion()) + 2), up))).verifyComplete();
        Map<String, Object> expected = new HashMap<>();
        expected.put("service_key", "--service--");
        expected.put("incident_key", "App/-id-");
        expected.put("event_type", "resolve");
        expected.put("description", "App/-id- is UP");
        Map<String, Object> details = new HashMap<>();
        details.put("from", "DOWN");
        details.put("to", up);
        expected.put("details", details);
        Mockito.verify(restTemplate).postForEntity(ArgumentMatchers.eq(DEFAULT_URI), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(Void.class));
    }

    @Test
    public void test_onApplicationEvent_trigger() {
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(PagerdutyNotifierTest.INSTANCE.getId(), ((PagerdutyNotifierTest.INSTANCE.getVersion()) + 1), StatusInfo.ofUp()))).verifyComplete();
        Mockito.reset(restTemplate);
        StatusInfo down = StatusInfo.ofDown();
        Mockito.when(repository.find(PagerdutyNotifierTest.INSTANCE.getId())).thenReturn(Mono.just(PagerdutyNotifierTest.INSTANCE.withStatusInfo(down)));
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(PagerdutyNotifierTest.INSTANCE.getId(), ((PagerdutyNotifierTest.INSTANCE.getVersion()) + 2), down))).verifyComplete();
        Map<String, Object> expected = new HashMap<>();
        expected.put("service_key", "--service--");
        expected.put("incident_key", "App/-id-");
        expected.put("event_type", "trigger");
        expected.put("description", "App/-id- is DOWN");
        expected.put("client", "TestClient");
        expected.put("client_url", URI.create("http://localhost"));
        Map<String, Object> details = new HashMap<>();
        details.put("from", "UP");
        details.put("to", down);
        expected.put("details", details);
        Map<String, Object> context = new HashMap<>();
        context.put("type", "link");
        context.put("href", "http://health");
        context.put("text", "Application health-endpoint");
        expected.put("contexts", Arrays.asList(context));
        Mockito.verify(restTemplate).postForEntity(ArgumentMatchers.eq(DEFAULT_URI), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(Void.class));
    }
}

