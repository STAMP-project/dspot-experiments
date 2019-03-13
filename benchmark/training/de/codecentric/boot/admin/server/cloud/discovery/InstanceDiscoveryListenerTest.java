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
package de.codecentric.boot.admin.server.cloud.discovery;


import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.services.InstanceRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.ParentHeartbeatEvent;
import reactor.test.StepVerifier;


public class InstanceDiscoveryListenerTest {
    private InstanceDiscoveryListener listener;

    private DiscoveryClient discovery;

    private InstanceRegistry registry;

    @Test
    public void should_discover_instances_when_application_is_ready() {
        Mockito.when(discovery.getServices()).thenReturn(Collections.singletonList("service"));
        Mockito.when(discovery.getInstances("service")).thenReturn(Collections.singletonList(new DefaultServiceInstance("test-1", "service", "localhost", 80, false)));
        listener.onApplicationReady(null);
        StepVerifier.create(registry.getInstances()).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("service")).verifyComplete();
    }

    @Test
    public void should_not_register_instance_when_serviceId_is_ignored() {
        Mockito.when(discovery.getServices()).thenReturn(Collections.singletonList("service"));
        Mockito.when(discovery.getInstances("service")).thenReturn(Collections.singletonList(new DefaultServiceInstance("test-1", "service", "localhost", 80, false)));
        listener.setIgnoredServices(Collections.singleton("service"));
        listener.onInstanceRegistered(new org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent(new Object(), null));
        StepVerifier.create(registry.getInstances()).verifyComplete();
    }

    @Test
    public void should_register_instance_when_serviceId_is_not_ignored() {
        Mockito.when(discovery.getServices()).thenReturn(Collections.singletonList("service"));
        Mockito.when(discovery.getInstances("service")).thenReturn(Collections.singletonList(new DefaultServiceInstance("test-1", "service", "localhost", 80, false)));
        listener.setServices(Collections.singleton("notService"));
        listener.onInstanceRegistered(new org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent(new Object(), null));
        StepVerifier.create(registry.getInstances()).verifyComplete();
    }

    @Test
    public void should_not_register_instance_when_serviceId_matches_ignored_pattern() {
        Mockito.when(discovery.getServices()).thenReturn(Arrays.asList("service", "rabbit-1", "rabbit-2"));
        Mockito.when(discovery.getInstances("service")).thenReturn(Collections.singletonList(new DefaultServiceInstance("test-1", "service", "localhost", 80, false)));
        listener.setIgnoredServices(Collections.singleton("rabbit-*"));
        listener.onInstanceRegistered(new org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent(new Object(), null));
        StepVerifier.create(registry.getInstances()).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("service")).verifyComplete();
    }

    @Test
    public void should_register_instances_when_serviceId_matches_wanted_pattern() {
        Mockito.when(discovery.getServices()).thenReturn(Arrays.asList("service", "rabbit-1", "rabbit-2"));
        Mockito.when(discovery.getInstances("service")).thenReturn(Collections.singletonList(new DefaultServiceInstance("test-1", "service", "localhost", 80, false)));
        listener.setServices(Collections.singleton("ser*"));
        listener.onInstanceRegistered(new org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent(new Object(), null));
        StepVerifier.create(registry.getInstances()).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("service")).verifyComplete();
    }

    @Test
    public void should_register_instances_when_serviceId_matches_wanted_pattern_and_igonred_pattern() {
        Mockito.when(discovery.getServices()).thenReturn(Arrays.asList("service-1", "service", "rabbit-1", "rabbit-2"));
        Mockito.when(discovery.getInstances("service")).thenReturn(Collections.singletonList(new DefaultServiceInstance("test-1", "service", "localhost", 80, false)));
        Mockito.when(discovery.getInstances("service-1")).thenReturn(Collections.singletonList(new DefaultServiceInstance("test-1", "service-1", "localhost", 80, false)));
        listener.setServices(Collections.singleton("ser*"));
        listener.setIgnoredServices(Collections.singleton("service-*"));
        listener.onInstanceRegistered(new org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent(new Object(), null));
        StepVerifier.create(registry.getInstances()).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("service")).verifyComplete();
    }

    @Test
    public void should_register_instance_when_new_service_instance_is_discovered() {
        Mockito.when(discovery.getServices()).thenReturn(Collections.singletonList("service"));
        Mockito.when(discovery.getInstances("service")).thenReturn(Collections.singletonList(new DefaultServiceInstance("test-1", "service", "localhost", 80, false)));
        listener.onInstanceRegistered(new org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent(new Object(), null));
        StepVerifier.create(registry.getInstances()).assertNext(( application) -> {
            Registration registration = application.getRegistration();
            assertThat(registration.getHealthUrl()).isEqualTo("http://localhost:80/actuator/health");
            assertThat(registration.getManagementUrl()).isEqualTo("http://localhost:80/actuator");
            assertThat(registration.getServiceUrl()).isEqualTo("http://localhost:80/");
            assertThat(registration.getName()).isEqualTo("service");
        }).verifyComplete();
    }

    @Test
    public void should_only_discover_new_instances_when_new_heartbeat_is_emitted() {
        Object heartbeat = new Object();
        listener.onParentHeartbeat(new ParentHeartbeatEvent(new Object(), heartbeat));
        Mockito.when(discovery.getServices()).thenReturn(Collections.singletonList("service"));
        Mockito.when(discovery.getInstances("service")).thenReturn(Collections.singletonList(new DefaultServiceInstance("test-1", "service", "localhost", 80, false)));
        listener.onApplicationEvent(new HeartbeatEvent(new Object(), heartbeat));
        StepVerifier.create(registry.getInstances()).verifyComplete();
        listener.onApplicationEvent(new HeartbeatEvent(new Object(), new Object()));
        StepVerifier.create(registry.getInstances()).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("service")).verifyComplete();
    }

    @Test
    public void should_remove_instances_when_they_are_no_longer_available_in_discovery() {
        StepVerifier.create(registry.register(Registration.create("ignored", "http://health").build())).consumeNextWith(( id) -> {
        }).verifyComplete();
        StepVerifier.create(registry.register(Registration.create("different-source", "http://health2").source("http-api").build())).consumeNextWith(( id) -> {
        }).verifyComplete();
        listener.setIgnoredServices(Collections.singleton("ignored"));
        List<ServiceInstance> instances = new ArrayList<>();
        instances.add(new DefaultServiceInstance("test-1", "service", "localhost", 80, false));
        instances.add(new DefaultServiceInstance("test-1", "service", "example.net", 80, false));
        Mockito.when(discovery.getServices()).thenReturn(Collections.singletonList("service"));
        Mockito.when(discovery.getInstances("service")).thenReturn(instances);
        listener.onApplicationEvent(new HeartbeatEvent(new Object(), new Object()));
        StepVerifier.create(registry.getInstances("service")).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("service")).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("service")).verifyComplete();
        StepVerifier.create(registry.getInstances("ignored")).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("ignored")).verifyComplete();
        StepVerifier.create(registry.getInstances("different-source")).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("different-source")).verifyComplete();
        instances.remove(0);
        listener.onApplicationEvent(new HeartbeatEvent(new Object(), new Object()));
        StepVerifier.create(registry.getInstances("service")).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("service")).verifyComplete();
        StepVerifier.create(registry.getInstances("ignored")).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("ignored")).verifyComplete();
        StepVerifier.create(registry.getInstances("different-source")).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("different-source")).verifyComplete();
        // shouldn't deregister a second time
        listener.onApplicationEvent(new HeartbeatEvent(new Object(), new Object()));
        Mockito.verify(registry, Mockito.times(1)).deregister(ArgumentMatchers.any(InstanceId.class));
    }

    @Test
    public void should_not_throw_error_when_conversion_fails_and_proceed_with_next_instance() {
        Mockito.when(discovery.getServices()).thenReturn(Collections.singletonList("service"));
        Mockito.when(discovery.getInstances("service")).thenReturn(Arrays.asList(new DefaultServiceInstance("test-1", "service", "localhost", 80, false), new DefaultServiceInstance("error-1", "error", "localhost", 80, false)));
        listener.setConverter(( instance) -> {
            if (instance.getServiceId().equals("error")) {
                throw new IllegalStateException("Test-Error");
            } else {
                return new DefaultServiceInstanceConverter().convert(instance);
            }
        });
        listener.onInstanceRegistered(new org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent(new Object(), null));
        StepVerifier.create(registry.getInstances()).assertNext(( a) -> assertThat(a.getRegistration().getName()).isEqualTo("service")).verifyComplete();
    }
}

