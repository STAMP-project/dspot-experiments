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
package de.codecentric.boot.admin.server.domain.entities;


import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import de.codecentric.boot.admin.server.eventstore.InMemoryEventStore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class SnapshottingInstanceRepositoryTest extends AbstractInstanceRepositoryTest {
    private final Instance instance = Instance.create(InstanceId.of("app-1")).register(Registration.create("app", "http://health").build());

    private InMemoryEventStore eventStore = Mockito.spy(new InMemoryEventStore());

    private SnapshottingInstanceRepository repository;

    @Test
    public void should_return_instance_from_cache() {
        // given
        StepVerifier.create(this.repository.save(this.instance)).expectNext(this.instance).verifyComplete();
        // when
        Mockito.reset(this.eventStore);
        StepVerifier.create(this.repository.find(this.instance.getId())).expectNext(this.instance).verifyComplete();
        // then
        Mockito.verify(this.eventStore, Mockito.never()).find(ArgumentMatchers.any());
    }

    @Test
    public void should_return_all_instances_from_cache() {
        // given
        StepVerifier.create(this.repository.save(this.instance)).expectNext(this.instance).verifyComplete();
        // when
        Mockito.reset(this.eventStore);
        StepVerifier.create(this.repository.findAll()).expectNext(this.instance).verifyComplete();
        // then
        Mockito.verify(this.eventStore, Mockito.never()).findAll();
    }

    @Test
    public void should_update_cache_after_error() {
        // given
        this.repository.stop();
        Mockito.when(this.eventStore.findAll()).thenReturn(Flux.just(new de.codecentric.boot.admin.server.domain.events.InstanceRegisteredEvent(InstanceId.of("broken"), 0L, this.instance.getRegistration()), new de.codecentric.boot.admin.server.domain.events.InstanceRegisteredEvent(InstanceId.of("broken"), 0L, this.instance.getRegistration()), new de.codecentric.boot.admin.server.domain.events.InstanceRegisteredEvent(this.instance.getId(), 0L, this.instance.getRegistration()), new de.codecentric.boot.admin.server.domain.events.InstanceRegisteredEvent(InstanceId.of("broken"), 1L, this.instance.getRegistration())));
        // when
        this.repository.start();
        // then
        Mockito.reset(this.eventStore);
        StepVerifier.create(this.repository.find(this.instance.getId())).expectNext(this.instance).verifyComplete();
        StepVerifier.create(this.repository.find(InstanceId.of("broken"))).assertNext(( i) -> assertThat(i.getVersion()).isEqualTo(1L)).verifyComplete();
    }

    @Test
    public void should_refresh_snapshots_eagerly_on_optimistick_lock_exception() {
        // given
        StepVerifier.create(this.repository.save(this.instance)).expectNextCount(1L).verifyComplete();
        this.repository.stop();
        StepVerifier.create(this.repository.save(this.instance.clearUnsavedEvents().withStatusInfo(StatusInfo.ofDown()))).expectNextCount(1L).verifyComplete();
        // when
        StepVerifier.create(this.repository.computeIfPresent(this.instance.getId(), ( id, i) -> Mono.just(i.withStatusInfo(StatusInfo.ofUp())))).expectNextCount(1L).verifyComplete();
    }
}

