/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.kubernetes.discovery;


import io.fabric8.kubernetes.api.model.DoneableEndpoints;
import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.api.model.EndpointsList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationEventPublisher;


/**
 *
 *
 * @author Oleg Vyukov
 */
@RunWith(MockitoJUnitRunner.class)
public class KubernetesCatalogWatchTest {
    @Mock
    private KubernetesClient kubernetesClient;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private MixedOperation<Endpoints, EndpointsList, DoneableEndpoints, Resource<Endpoints, DoneableEndpoints>> endpointsOperation;

    @Captor
    private ArgumentCaptor<HeartbeatEvent> heartbeatEventArgumentCaptor;

    @InjectMocks
    private KubernetesCatalogWatch underTest;

    @Test
    public void testRandomOrderChangePods() throws Exception {
        Mockito.when(this.endpointsOperation.list()).thenReturn(createSingleEndpointEndpointListByPodName("api-pod", "other-pod")).thenReturn(createSingleEndpointEndpointListByPodName("other-pod", "api-pod"));
        Mockito.when(this.kubernetesClient.endpoints()).thenReturn(this.endpointsOperation);
        this.underTest.catalogServicesWatch();
        // second execution on shuffleServices
        this.underTest.catalogServicesWatch();
        Mockito.verify(this.applicationEventPublisher).publishEvent(ArgumentMatchers.any(HeartbeatEvent.class));
    }

    @Test
    public void testRandomOrderChangeServices() throws Exception {
        Mockito.when(this.endpointsOperation.list()).thenReturn(createEndpointsListByServiceName("api-service", "other-service")).thenReturn(createEndpointsListByServiceName("other-service", "api-service"));
        Mockito.when(this.kubernetesClient.endpoints()).thenReturn(this.endpointsOperation);
        this.underTest.catalogServicesWatch();
        // second execution on shuffleServices
        this.underTest.catalogServicesWatch();
        Mockito.verify(this.applicationEventPublisher).publishEvent(ArgumentMatchers.any(HeartbeatEvent.class));
    }

    @Test
    public void testEventBody() throws Exception {
        Mockito.when(this.endpointsOperation.list()).thenReturn(createSingleEndpointEndpointListByPodName("api-pod", "other-pod"));
        Mockito.when(this.kubernetesClient.endpoints()).thenReturn(this.endpointsOperation);
        this.underTest.catalogServicesWatch();
        Mockito.verify(this.applicationEventPublisher).publishEvent(this.heartbeatEventArgumentCaptor.capture());
        HeartbeatEvent event = this.heartbeatEventArgumentCaptor.getValue();
        assertThat(event.getValue()).isInstanceOf(List.class);
        List<String> expectedPodsList = Arrays.asList("api-pod", "other-pod");
        assertThat(event.getValue()).isEqualTo(expectedPodsList);
    }

    @Test
    public void testEndpointsWithoutSubsets() {
        EndpointsList endpoints = createSingleEndpointEndpointListWithoutSubsets();
        Mockito.when(this.endpointsOperation.list()).thenReturn(endpoints);
        Mockito.when(this.kubernetesClient.endpoints()).thenReturn(this.endpointsOperation);
        this.underTest.catalogServicesWatch();
        // second execution on shuffleServices
        this.underTest.catalogServicesWatch();
        Mockito.verify(this.applicationEventPublisher).publishEvent(ArgumentMatchers.any(HeartbeatEvent.class));
    }

    @Test
    public void testEndpointsWithoutAddresses() {
        EndpointsList endpoints = createSingleEndpointEndpointListByPodName("api-pod");
        endpoints.getItems().get(0).getSubsets().get(0).setAddresses(null);
        Mockito.when(this.endpointsOperation.list()).thenReturn(endpoints);
        Mockito.when(this.kubernetesClient.endpoints()).thenReturn(this.endpointsOperation);
        this.underTest.catalogServicesWatch();
        // second execution on shuffleServices
        this.underTest.catalogServicesWatch();
        Mockito.verify(this.applicationEventPublisher).publishEvent(ArgumentMatchers.any(HeartbeatEvent.class));
    }

    @Test
    public void testEndpointsWithoutTargetRefs() {
        EndpointsList endpoints = createSingleEndpointEndpointListByPodName("api-pod");
        endpoints.getItems().get(0).getSubsets().get(0).getAddresses().get(0).setTargetRef(null);
        Mockito.when(this.endpointsOperation.list()).thenReturn(endpoints);
        Mockito.when(this.kubernetesClient.endpoints()).thenReturn(this.endpointsOperation);
        this.underTest.catalogServicesWatch();
        // second execution on shuffleServices
        this.underTest.catalogServicesWatch();
        Mockito.verify(this.applicationEventPublisher).publishEvent(ArgumentMatchers.any(HeartbeatEvent.class));
    }
}

