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
package de.codecentric.boot.admin.server.services;


import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.test.publisher.TestPublisher;


public class EndpointDetectionTriggerTest {
    private final Instance instance = Instance.create(InstanceId.of("id-1")).register(Registration.create("foo", "http://health-1").build());

    private TestPublisher<InstanceEvent> events = TestPublisher.create();

    private EndpointDetector detector = Mockito.mock(EndpointDetector.class);

    private EndpointDetectionTrigger trigger;

    @Test
    public void should_detect_on_status_changed() {
        // when status-change event is emitted
        events.next(new de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent(instance.getId(), instance.getVersion(), StatusInfo.ofDown()));
        // then should update
        Mockito.verify(detector, Mockito.times(1)).detectEndpoints(instance.getId());
    }

    @Test
    public void should_detect_on_registration_updated() {
        // when status-change event is emitted
        events.next(new de.codecentric.boot.admin.server.domain.events.InstanceRegistrationUpdatedEvent(instance.getId(), instance.getVersion(), instance.getRegistration()));
        // then should update
        Mockito.verify(detector, Mockito.times(1)).detectEndpoints(instance.getId());
    }

    @Test
    public void should_not_detect_on_non_relevant_event() {
        // when some non-status-change event is emitted
        events.next(new de.codecentric.boot.admin.server.domain.events.InstanceRegisteredEvent(instance.getId(), instance.getVersion(), instance.getRegistration()));
        // then should not update
        Mockito.verify(detector, Mockito.never()).detectEndpoints(instance.getId());
    }

    @Test
    public void should_not_detect_on_trigger_stopped() {
        // when registered event is emitted but the trigger has been stopped
        trigger.stop();
        Mockito.clearInvocations(detector);
        events.next(new de.codecentric.boot.admin.server.domain.events.InstanceRegisteredEvent(instance.getId(), instance.getVersion(), instance.getRegistration()));
        // then should not update
        Mockito.verify(detector, Mockito.never()).detectEndpoints(instance.getId());
    }
}

