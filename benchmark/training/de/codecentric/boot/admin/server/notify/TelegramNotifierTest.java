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
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reactor.test.StepVerifier;


public class TelegramNotifierTest {
    private final Instance instance = Instance.create(InstanceId.of("-id-")).register(Registration.create("Telegram", "http://health").build());

    private InstanceRepository repository;

    private TelegramNotifier notifier;

    private RestTemplate restTemplate;

    @Test
    public void test_onApplicationEvent_resolve() {
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(instance.getId(), instance.getVersion(), StatusInfo.ofDown()))).verifyComplete();
        Mockito.clearInvocations(restTemplate);
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(instance.getId(), instance.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        Mockito.verify(restTemplate).getForObject(ArgumentMatchers.eq(("https://telegram.com/bot--token-/sendmessage?chat_id={chat_id}&text={text}" + "&parse_mode={parse_mode}&disable_notification={disable_notification}")), ArgumentMatchers.eq(Void.class), ArgumentMatchers.eq(getParameters("UP")));
    }

    @Test
    public void test_onApplicationEvent_trigger() {
        StatusInfo infoDown = StatusInfo.ofDown();
        @SuppressWarnings("unchecked")
        ArgumentCaptor<HttpEntity<Map<String, Object>>> httpRequest = ArgumentCaptor.forClass(((Class<HttpEntity<Map<String, Object>>>) ((Class<?>) (HttpEntity.class))));
        Mockito.when(restTemplate.postForEntity(ArgumentMatchers.isA(String.class), httpRequest.capture(), ArgumentMatchers.eq(Void.class))).thenReturn(ResponseEntity.ok().build());
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(instance.getId(), instance.getVersion(), StatusInfo.ofUp()))).verifyComplete();
        StepVerifier.create(notifier.notify(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(instance.getId(), instance.getVersion(), infoDown))).verifyComplete();
        Mockito.verify(restTemplate).getForObject(ArgumentMatchers.eq(("https://telegram.com/bot--token-/sendmessage?chat_id={chat_id}&text={text}" + "&parse_mode={parse_mode}&disable_notification={disable_notification}")), ArgumentMatchers.eq(Void.class), ArgumentMatchers.eq(getParameters("DOWN")));
    }
}

