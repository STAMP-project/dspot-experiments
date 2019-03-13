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


public class SlackNotifierTest {
    private static final String channel = "channel";

    private static final String icon = "icon";

    private static final String user = "user";

    private static final String appName = "App";

    private static final Instance INSTANCE = Instance.create(InstanceId.of("-id-")).register(Registration.create(SlackNotifierTest.appName, "http://health").build());

    private static final String message = "test";

    private SlackNotifier notifier;

    private RestTemplate restTemplate;

    private InstanceRepository repository;

    @Test
    public void test_onApplicationEvent_resolve() {
        notifier.setChannel(SlackNotifierTest.channel);
        notifier.setIcon(SlackNotifierTest.icon);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(SlackNotifierTest.INSTANCE.getId(), SlackNotifierTest.INSTANCE.getVersion(), StatusInfo.ofDown()))).verifyComplete();
        Mockito.clearInvocations(restTemplate);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(SlackNotifierTest.INSTANCE.getId(), SlackNotifierTest.INSTANCE.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        Object expected = expectedMessage("good", SlackNotifierTest.user, SlackNotifierTest.icon, SlackNotifierTest.channel, standardMessage("UP"));
        Mockito.verify(restTemplate).postForEntity(ArgumentMatchers.any(URI.class), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(Void.class));
    }

    @Test
    public void test_onApplicationEvent_resolve_without_channel_and_icon() {
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(SlackNotifierTest.INSTANCE.getId(), SlackNotifierTest.INSTANCE.getVersion(), StatusInfo.ofDown()))).verifyComplete();
        Mockito.clearInvocations(restTemplate);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(SlackNotifierTest.INSTANCE.getId(), SlackNotifierTest.INSTANCE.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        Object expected = expectedMessage("good", SlackNotifierTest.user, null, null, standardMessage("UP"));
        Mockito.verify(restTemplate).postForEntity(ArgumentMatchers.any(URI.class), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(Void.class));
    }

    @Test
    public void test_onApplicationEvent_resolve_with_given_user() {
        String anotherUser = "another user";
        notifier.setUsername(anotherUser);
        notifier.setChannel(SlackNotifierTest.channel);
        notifier.setIcon(SlackNotifierTest.icon);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(SlackNotifierTest.INSTANCE.getId(), SlackNotifierTest.INSTANCE.getVersion(), StatusInfo.ofDown()))).verifyComplete();
        Mockito.clearInvocations(restTemplate);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(SlackNotifierTest.INSTANCE.getId(), SlackNotifierTest.INSTANCE.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        Object expected = expectedMessage("good", anotherUser, SlackNotifierTest.icon, SlackNotifierTest.channel, standardMessage("UP"));
        Mockito.verify(restTemplate).postForEntity(ArgumentMatchers.any(URI.class), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(Void.class));
    }

    @Test
    public void test_onApplicationEvent_resolve_with_given_message() {
        notifier.setMessage(SlackNotifierTest.message);
        notifier.setChannel(SlackNotifierTest.channel);
        notifier.setIcon(SlackNotifierTest.icon);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(SlackNotifierTest.INSTANCE.getId(), SlackNotifierTest.INSTANCE.getVersion(), StatusInfo.ofDown()))).verifyComplete();
        Mockito.clearInvocations(restTemplate);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(SlackNotifierTest.INSTANCE.getId(), SlackNotifierTest.INSTANCE.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        Object expected = expectedMessage("good", SlackNotifierTest.user, SlackNotifierTest.icon, SlackNotifierTest.channel, SlackNotifierTest.message);
        Mockito.verify(restTemplate).postForEntity(ArgumentMatchers.any(URI.class), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(Void.class));
    }

    @Test
    public void test_onApplicationEvent_trigger() {
        notifier.setChannel(SlackNotifierTest.channel);
        notifier.setIcon(SlackNotifierTest.icon);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(SlackNotifierTest.INSTANCE.getId(), SlackNotifierTest.INSTANCE.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        Mockito.clearInvocations(restTemplate);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(SlackNotifierTest.INSTANCE.getId(), SlackNotifierTest.INSTANCE.getVersion(), StatusInfo.ofDown()))).verifyComplete();
        Object expected = expectedMessage("danger", SlackNotifierTest.user, SlackNotifierTest.icon, SlackNotifierTest.channel, standardMessage("DOWN"));
        Mockito.verify(restTemplate).postForEntity(ArgumentMatchers.any(URI.class), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(Void.class));
    }
}

