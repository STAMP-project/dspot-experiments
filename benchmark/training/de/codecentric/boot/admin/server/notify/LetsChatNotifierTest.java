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
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import java.net.URI;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import reactor.test.StepVerifier;


public class LetsChatNotifierTest {
    private static final String room = "text_room";

    private static final String token = "text_token";

    private static final String user = "api_user";

    private static final String host = "http://localhost";

    private static final Instance instance = Instance.create(InstanceId.of("-id-")).register(Registration.create("App", "http://health").build());

    private LetsChatNotifier notifier;

    private RestTemplate restTemplate;

    @Test
    public void test_onApplicationEvent_resolve() {
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(LetsChatNotifierTest.instance.getId(), LetsChatNotifierTest.instance.getVersion(), StatusInfo.ofDown()))).verifyComplete();
        Mockito.clearInvocations(restTemplate);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(LetsChatNotifierTest.instance.getId(), LetsChatNotifierTest.instance.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        HttpEntity<?> expected = expectedMessage(standardMessage("UP"));
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.eq(URI.create(String.format("%s/rooms/%s/messages", LetsChatNotifierTest.host, LetsChatNotifierTest.room))), ArgumentMatchers.eq(POST), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(Void.class));
    }

    @Test
    public void test_onApplicationEvent_resolve_with_custom_message() {
        notifier.setMessage("TEST");
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(LetsChatNotifierTest.instance.getId(), LetsChatNotifierTest.instance.getVersion(), StatusInfo.ofDown()))).verifyComplete();
        Mockito.clearInvocations(restTemplate);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(LetsChatNotifierTest.instance.getId(), LetsChatNotifierTest.instance.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        HttpEntity<?> expected = expectedMessage("TEST");
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.eq(URI.create(String.format("%s/rooms/%s/messages", LetsChatNotifierTest.host, LetsChatNotifierTest.room))), ArgumentMatchers.eq(POST), ArgumentMatchers.eq(expected), ArgumentMatchers.eq(Void.class));
    }
}

