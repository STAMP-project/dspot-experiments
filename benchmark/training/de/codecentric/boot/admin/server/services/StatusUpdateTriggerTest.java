/**
 * Copyright 2014-2019 the original author or authors.
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
import de.codecentric.boot.admin.server.domain.values.Info;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import java.time.Duration;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import reactor.test.publisher.TestPublisher;


public class StatusUpdateTriggerTest {
    private final Instance instance = Instance.create(InstanceId.of("id-1")).register(Registration.create("foo", "http://health-1").build());

    private final StatusUpdater updater = Mockito.mock(StatusUpdater.class);

    private final TestPublisher<InstanceEvent> events = TestPublisher.create();

    private StatusUpdateTrigger trigger;

    @Test
    public void should_start_and_stop_monitor() throws Exception {
        // given
        this.trigger.stop();
        this.trigger.setInterval(Duration.ofMillis(10));
        this.trigger.setLifetime(Duration.ofMillis(10));
        this.trigger.start();
        Thread.sleep(50L);// wait for subscription

        this.events.next(new de.codecentric.boot.admin.server.domain.events.InstanceRegisteredEvent(this.instance.getId(), 0L, this.instance.getRegistration()));
        Thread.sleep(50L);
        // then it should start updating one time for registration and at least once for monitor
        Mockito.verify(this.updater, Mockito.atLeast(2)).updateStatus(this.instance.getId());
        // given long lifetime
        this.trigger.setLifetime(Duration.ofSeconds(10));
        Thread.sleep(50L);
        Mockito.clearInvocations(this.updater);
        // when the lifetime is not expired
        Thread.sleep(50L);
        // should never update
        Mockito.verify(this.updater, Mockito.never()).updateStatus(ArgumentMatchers.any(InstanceId.class));
        // when trigger ist destroyed
        this.trigger.setLifetime(Duration.ofMillis(10));
        this.trigger.stop();
        Mockito.clearInvocations(this.updater);
        Thread.sleep(15L);
        // it should stop updating
        Mockito.verify(this.updater, Mockito.never()).updateStatus(ArgumentMatchers.any(InstanceId.class));
    }

    @Test
    public void should_not_update_when_stopped() {
        // when registered event is emitted but the trigger has been stopped
        this.trigger.stop();
        Mockito.clearInvocations(this.updater);
        this.events.next(new de.codecentric.boot.admin.server.domain.events.InstanceRegisteredEvent(this.instance.getId(), this.instance.getVersion(), this.instance.getRegistration()));
        // then should not update
        Mockito.verify(this.updater, Mockito.never()).updateStatus(this.instance.getId());
    }

    @Test
    public void should_update_on_instance_registered_event() {
        // when registered event is emitted
        this.events.next(new de.codecentric.boot.admin.server.domain.events.InstanceRegisteredEvent(this.instance.getId(), this.instance.getVersion(), this.instance.getRegistration()));
        // then should update
        Mockito.verify(this.updater, Mockito.times(1)).updateStatus(this.instance.getId());
    }

    @Test
    public void should_update_on_instance_registration_update_event() {
        // when registered event is emitted
        this.events.next(new de.codecentric.boot.admin.server.domain.events.InstanceRegistrationUpdatedEvent(this.instance.getId(), this.instance.getVersion(), this.instance.getRegistration()));
        // then should update
        Mockito.verify(this.updater, Mockito.times(1)).updateStatus(this.instance.getId());
    }

    @Test
    public void should_not_update_on_non_relevant_event() {
        // when some non-registered event is emitted
        this.events.next(new de.codecentric.boot.admin.server.domain.events.InstanceInfoChangedEvent(this.instance.getId(), this.instance.getVersion(), Info.empty()));
        // then should not update
        Mockito.verify(this.updater, Mockito.never()).updateStatus(this.instance.getId());
    }
}

