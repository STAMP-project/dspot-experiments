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


import KubernetesDiscoveryProperties.Metadata;
import io.fabric8.kubernetes.api.model.DoneableEndpoints;
import io.fabric8.kubernetes.api.model.DoneableService;
import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.api.model.EndpointsList;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.ServiceResource;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.client.ServiceInstance;


@RunWith(MockitoJUnitRunner.class)
public class KubernetesDiscoveryClientFilterMetadataTest {
    @Mock
    private KubernetesClient kubernetesClient;

    @Mock
    private KubernetesDiscoveryProperties properties;

    @Mock
    private DefaultIsServicePortSecureResolver isServicePortSecureResolver;

    @Mock
    private Metadata metadata;

    @Mock
    private MixedOperation<Service, ServiceList, DoneableService, ServiceResource<Service, DoneableService>> serviceOperation;

    @Mock
    private MixedOperation<Endpoints, EndpointsList, DoneableEndpoints, Resource<Endpoints, DoneableEndpoints>> endpointsOperation;

    @Mock
    private ServiceResource<Service, DoneableService> serviceResource;

    @Mock
    private Resource<Endpoints, DoneableEndpoints> endpointsResource;

    @InjectMocks
    private KubernetesDiscoveryClient underTest;

    @Test
    public void testAllExtraMetadataDisabled() {
        final String serviceId = "s";
        Mockito.when(this.properties.getMetadata()).thenReturn(this.metadata);
        Mockito.when(this.metadata.isAddLabels()).thenReturn(false);
        Mockito.when(this.metadata.isAddAnnotations()).thenReturn(false);
        Mockito.when(this.metadata.isAddPorts()).thenReturn(false);
        setupServiceWithLabelsAndAnnotationsAndPorts(serviceId, new HashMap<String, String>() {
            {
                put("l1", "lab");
            }
        }, new HashMap<String, String>() {
            {
                put("l1", "lab");
            }
        }, new HashMap<Integer, String>() {
            {
                put(80, "http");
                put(5555, "");
            }
        });
        final List<ServiceInstance> instances = this.underTest.getInstances(serviceId);
        assertThat(instances).hasSize(1);
        assertThat(instances.get(0).getMetadata()).isEmpty();
    }

    @Test
    public void testLabelsEnabled() {
        final String serviceId = "s";
        Mockito.when(this.properties.getMetadata()).thenReturn(this.metadata);
        Mockito.when(this.metadata.isAddLabels()).thenReturn(true);
        Mockito.when(this.metadata.isAddAnnotations()).thenReturn(false);
        Mockito.when(this.metadata.isAddPorts()).thenReturn(false);
        setupServiceWithLabelsAndAnnotationsAndPorts(serviceId, new HashMap<String, String>() {
            {
                put("l1", "v1");
                put("l2", "v2");
            }
        }, new HashMap<String, String>() {
            {
                put("l1", "lab");
            }
        }, new HashMap<Integer, String>() {
            {
                put(80, "http");
                put(5555, "");
            }
        });
        final List<ServiceInstance> instances = this.underTest.getInstances(serviceId);
        assertThat(instances).hasSize(1);
        assertThat(instances.get(0).getMetadata()).containsOnly(entry("l1", "v1"), entry("l2", "v2"));
    }

    @Test
    public void testLabelsEnabledWithPrefix() {
        final String serviceId = "s";
        Mockito.when(this.properties.getMetadata()).thenReturn(this.metadata);
        Mockito.when(this.metadata.isAddLabels()).thenReturn(true);
        Mockito.when(this.metadata.getLabelsPrefix()).thenReturn("l_");
        Mockito.when(this.metadata.isAddAnnotations()).thenReturn(false);
        Mockito.when(this.metadata.isAddPorts()).thenReturn(false);
        setupServiceWithLabelsAndAnnotationsAndPorts(serviceId, new HashMap<String, String>() {
            {
                put("l1", "v1");
                put("l2", "v2");
            }
        }, new HashMap<String, String>() {
            {
                put("l1", "lab");
            }
        }, new HashMap<Integer, String>() {
            {
                put(80, "http");
                put(5555, "");
            }
        });
        final List<ServiceInstance> instances = this.underTest.getInstances(serviceId);
        assertThat(instances).hasSize(1);
        assertThat(instances.get(0).getMetadata()).containsOnly(entry("l_l1", "v1"), entry("l_l2", "v2"));
    }

    @Test
    public void testAnnotationsEnabled() {
        final String serviceId = "s";
        Mockito.when(this.properties.getMetadata()).thenReturn(this.metadata);
        Mockito.when(this.metadata.isAddLabels()).thenReturn(false);
        Mockito.when(this.metadata.isAddAnnotations()).thenReturn(true);
        Mockito.when(this.metadata.isAddPorts()).thenReturn(false);
        setupServiceWithLabelsAndAnnotationsAndPorts(serviceId, new HashMap<String, String>() {
            {
                put("l1", "v1");
            }
        }, new HashMap<String, String>() {
            {
                put("a1", "v1");
                put("a2", "v2");
            }
        }, new HashMap<Integer, String>() {
            {
                put(80, "http");
                put(5555, "");
            }
        });
        final List<ServiceInstance> instances = this.underTest.getInstances(serviceId);
        assertThat(instances).hasSize(1);
        assertThat(instances.get(0).getMetadata()).containsOnly(entry("a1", "v1"), entry("a2", "v2"));
    }

    @Test
    public void testAnnotationsEnabledWithPrefix() {
        final String serviceId = "s";
        Mockito.when(this.properties.getMetadata()).thenReturn(this.metadata);
        Mockito.when(this.metadata.isAddLabels()).thenReturn(false);
        Mockito.when(this.metadata.isAddAnnotations()).thenReturn(true);
        Mockito.when(this.metadata.getAnnotationsPrefix()).thenReturn("a_");
        Mockito.when(this.metadata.isAddPorts()).thenReturn(false);
        setupServiceWithLabelsAndAnnotationsAndPorts(serviceId, new HashMap<String, String>() {
            {
                put("l1", "v1");
            }
        }, new HashMap<String, String>() {
            {
                put("a1", "v1");
                put("a2", "v2");
            }
        }, new HashMap<Integer, String>() {
            {
                put(80, "http");
                put(5555, "");
            }
        });
        final List<ServiceInstance> instances = this.underTest.getInstances(serviceId);
        assertThat(instances).hasSize(1);
        assertThat(instances.get(0).getMetadata()).containsOnly(entry("a_a1", "v1"), entry("a_a2", "v2"));
    }

    @Test
    public void testPortsEnabled() {
        final String serviceId = "s";
        Mockito.when(this.properties.getMetadata()).thenReturn(this.metadata);
        Mockito.when(this.metadata.isAddLabels()).thenReturn(false);
        Mockito.when(this.metadata.isAddAnnotations()).thenReturn(false);
        Mockito.when(this.metadata.isAddPorts()).thenReturn(true);
        setupServiceWithLabelsAndAnnotationsAndPorts(serviceId, new HashMap<String, String>() {
            {
                put("l1", "v1");
            }
        }, new HashMap<String, String>() {
            {
                put("a1", "v1");
                put("a2", "v2");
            }
        }, new HashMap<Integer, String>() {
            {
                put(80, "http");
                put(5555, "");
            }
        });
        final List<ServiceInstance> instances = this.underTest.getInstances(serviceId);
        assertThat(instances).hasSize(1);
        assertThat(instances.get(0).getMetadata()).containsOnly(entry("http", "80"));
    }

    @Test
    public void testPortsEnabledWithPrefix() {
        final String serviceId = "s";
        Mockito.when(this.properties.getMetadata()).thenReturn(this.metadata);
        Mockito.when(this.metadata.isAddLabels()).thenReturn(false);
        Mockito.when(this.metadata.isAddAnnotations()).thenReturn(false);
        Mockito.when(this.metadata.isAddPorts()).thenReturn(true);
        Mockito.when(this.metadata.getPortsPrefix()).thenReturn("p_");
        setupServiceWithLabelsAndAnnotationsAndPorts(serviceId, new HashMap<String, String>() {
            {
                put("l1", "v1");
            }
        }, new HashMap<String, String>() {
            {
                put("a1", "v1");
                put("a2", "v2");
            }
        }, new HashMap<Integer, String>() {
            {
                put(80, "http");
                put(5555, "");
            }
        });
        final List<ServiceInstance> instances = this.underTest.getInstances(serviceId);
        assertThat(instances).hasSize(1);
        assertThat(instances.get(0).getMetadata()).containsOnly(entry("p_http", "80"));
    }

    @Test
    public void testLabelsAndAnnotationsAndPortsEnabledWithPrefix() {
        final String serviceId = "s";
        Mockito.when(this.properties.getMetadata()).thenReturn(this.metadata);
        Mockito.when(this.metadata.isAddLabels()).thenReturn(true);
        Mockito.when(this.metadata.getLabelsPrefix()).thenReturn("l_");
        Mockito.when(this.metadata.isAddAnnotations()).thenReturn(true);
        Mockito.when(this.metadata.getAnnotationsPrefix()).thenReturn("a_");
        Mockito.when(this.metadata.isAddPorts()).thenReturn(true);
        Mockito.when(this.metadata.getPortsPrefix()).thenReturn("p_");
        setupServiceWithLabelsAndAnnotationsAndPorts(serviceId, new HashMap<String, String>() {
            {
                put("l1", "la1");
            }
        }, new HashMap<String, String>() {
            {
                put("a1", "an1");
                put("a2", "an2");
            }
        }, new HashMap<Integer, String>() {
            {
                put(80, "http");
                put(5555, "");
            }
        });
        final List<ServiceInstance> instances = this.underTest.getInstances(serviceId);
        assertThat(instances).hasSize(1);
        assertThat(instances.get(0).getMetadata()).containsOnly(entry("a_a1", "an1"), entry("a_a2", "an2"), entry("l_l1", "la1"), entry("p_http", "80"));
    }
}

