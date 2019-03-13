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


import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import java.net.URI;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import reactor.test.StepVerifier;


public class DiscordNotifierTest {
    private static final String avatarUrl = "http://avatarUrl";

    private static final String username = "user";

    private static final String appName = "App";

    private static final URI webhookUri = URI.create("http://localhost/");

    private static final Instance INSTANCE = Instance.create(InstanceId.of("-id-")).register(Registration.create(DiscordNotifierTest.appName, "http://health").build());

    private DiscordNotifier notifier;

    private RestTemplate restTemplate;

    private InstanceRepository repository;

    @Test
    public void test_onApplicationEvent_resolve() {
        notifier.setUsername(DiscordNotifierTest.username);
        notifier.setAvatarUrl(DiscordNotifierTest.avatarUrl);
        notifier.setTts(true);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(DiscordNotifierTest.INSTANCE.getId(), DiscordNotifierTest.INSTANCE.getVersion(), StatusInfo.ofDown()))).verifyComplete();
        Mockito.clearInvocations(restTemplate);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(DiscordNotifierTest.INSTANCE.getId(), DiscordNotifierTest.INSTANCE.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        Object expected = expectedMessage(DiscordNotifierTest.username, true, DiscordNotifierTest.avatarUrl, standardMessage("UP"));
        Mockito.verify(restTemplate).postForEntity(ArgumentMatchers.eq(DiscordNotifierTest.webhookUri), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(Void.class));
    }

    @Test
    public void test_onApplicationEvent_resolve_minimum_configuration() {
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(DiscordNotifierTest.INSTANCE.getId(), DiscordNotifierTest.INSTANCE.getVersion(), StatusInfo.ofDown()))).verifyComplete();
        Mockito.clearInvocations(restTemplate);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(DiscordNotifierTest.INSTANCE.getId(), DiscordNotifierTest.INSTANCE.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        Object expected = expectedMessage(null, false, null, standardMessage("UP"));
        Mockito.verify(restTemplate).postForEntity(ArgumentMatchers.eq(DiscordNotifierTest.webhookUri), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(Void.class));
    }
}

