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


import io.fabric8.kubernetes.api.model.EndpointsBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServiceListBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import java.util.HashMap;
import java.util.List;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;


public class KubernetesDiscoveryClientTest {
    @ClassRule
    public static KubernetesServer mockServer = new KubernetesServer();

    private static KubernetesClient mockClient;

    @Test
    public void getInstancesShouldBeAbleToHandleEndpointsSingleAddress() {
        KubernetesDiscoveryClientTest.mockServer.expect().get().withPath("/api/v1/namespaces/test/endpoints/endpoint").andReturn(200, new EndpointsBuilder().withNewMetadata().withName("endpoint").endMetadata().addNewSubset().addNewAddress().withIp("ip1").withNewTargetRef().withUid("uid").endTargetRef().endAddress().addNewPort("http", 80, "TCP").endSubset().build()).once();
        KubernetesDiscoveryClientTest.mockServer.expect().get().withPath("/api/v1/namespaces/test/services/endpoint").andReturn(200, new ServiceBuilder().withNewMetadata().withName("endpoint").withLabels(new HashMap<String, String>() {
            {
                put("l", "v");
            }
        }).endMetadata().build()).once();
        final KubernetesDiscoveryProperties properties = new KubernetesDiscoveryProperties();
        final DiscoveryClient discoveryClient = new KubernetesDiscoveryClient(KubernetesDiscoveryClientTest.mockClient, properties, KubernetesClient::services, new DefaultIsServicePortSecureResolver(properties));
        final List<ServiceInstance> instances = discoveryClient.getInstances("endpoint");
        assertThat(instances).hasSize(1).filteredOn(( s) -> (s.getHost().equals("ip1")) && (!(s.isSecure()))).hasSize(1).filteredOn(( s) -> s.getInstanceId().equals("uid")).hasSize(1);
    }

    @Test
    public void getInstancesShouldBeAbleToHandleEndpointsSingleAddressAndMultiplePorts() {
        KubernetesDiscoveryClientTest.mockServer.expect().get().withPath("/api/v1/namespaces/test/endpoints/endpoint").andReturn(200, new EndpointsBuilder().withNewMetadata().withName("endpoint").endMetadata().addNewSubset().addNewAddress().withIp("ip1").withNewTargetRef().withUid("uid").endTargetRef().endAddress().addNewPort("mgmt", 9000, "TCP").addNewPort("http", 80, "TCP").endSubset().build()).once();
        KubernetesDiscoveryClientTest.mockServer.expect().get().withPath("/api/v1/namespaces/test/services/endpoint").andReturn(200, new ServiceBuilder().withNewMetadata().withName("endpoint").withLabels(new HashMap<String, String>() {
            {
                put("l", "v");
            }
        }).endMetadata().build()).once();
        final KubernetesDiscoveryProperties properties = new KubernetesDiscoveryProperties();
        properties.setPrimaryPortName("http");
        final DiscoveryClient discoveryClient = new KubernetesDiscoveryClient(KubernetesDiscoveryClientTest.mockClient, properties, KubernetesClient::services, new DefaultIsServicePortSecureResolver(properties));
        final List<ServiceInstance> instances = discoveryClient.getInstances("endpoint");
        assertThat(instances).hasSize(1).filteredOn(( s) -> (s.getHost().equals("ip1")) && (!(s.isSecure()))).hasSize(1).filteredOn(( s) -> s.getInstanceId().equals("uid")).hasSize(1).filteredOn(( s) -> 80 == (s.getPort())).hasSize(1);
    }

    @Test
    public void getInstancesShouldBeAbleToHandleEndpointsMultipleAddresses() {
        KubernetesDiscoveryClientTest.mockServer.expect().get().withPath("/api/v1/namespaces/test/endpoints/endpoint").andReturn(200, new EndpointsBuilder().withNewMetadata().withName("endpoint").endMetadata().addNewSubset().addNewAddress().withIp("ip1").endAddress().addNewAddress().withIp("ip2").endAddress().addNewPort("https", 443, "TCP").endSubset().build()).once();
        KubernetesDiscoveryClientTest.mockServer.expect().get().withPath("/api/v1/namespaces/test/services/endpoint").andReturn(200, new ServiceBuilder().withNewMetadata().withName("endpoint").withLabels(new HashMap<String, String>() {
            {
                put("l", "v");
            }
        }).endMetadata().build()).once();
        final KubernetesDiscoveryProperties properties = new KubernetesDiscoveryProperties();
        final DiscoveryClient discoveryClient = new KubernetesDiscoveryClient(KubernetesDiscoveryClientTest.mockClient, properties, KubernetesClient::services, new DefaultIsServicePortSecureResolver(properties));
        final List<ServiceInstance> instances = discoveryClient.getInstances("endpoint");
        assertThat(instances).hasSize(2).filteredOn(ServiceInstance::isSecure).extracting(ServiceInstance::getHost).containsOnly("ip1", "ip2");
    }

    @Test
    public void getServicesShouldReturnAllServicesWhenNoLabelsAreAppliedToTheClient() {
        KubernetesDiscoveryClientTest.mockServer.expect().get().withPath("/api/v1/namespaces/test/services").andReturn(200, new ServiceListBuilder().addNewItem().withNewMetadata().withName("s1").withLabels(new HashMap<String, String>() {
            {
                put("label", "value");
            }
        }).endMetadata().endItem().addNewItem().withNewMetadata().withName("s2").withLabels(new HashMap<String, String>() {
            {
                put("label", "value");
                put("label2", "value2");
            }
        }).endMetadata().endItem().addNewItem().withNewMetadata().withName("s3").endMetadata().endItem().build()).once();
        final KubernetesDiscoveryProperties properties = new KubernetesDiscoveryProperties();
        final DiscoveryClient discoveryClient = new KubernetesDiscoveryClient(KubernetesDiscoveryClientTest.mockClient, properties, KubernetesClient::services, new DefaultIsServicePortSecureResolver(properties));
        final List<String> services = discoveryClient.getServices();
        assertThat(services).containsOnly("s1", "s2", "s3");
    }

    @Test
    public void getServicesShouldReturnOnlyMatchingServicesWhenLabelsAreAppliedToTheClient() {
        KubernetesDiscoveryClientTest.mockServer.expect().get().withPath("/api/v1/namespaces/test/services?labelSelector=label%3Dvalue").andReturn(200, new ServiceListBuilder().addNewItem().withNewMetadata().withName("s1").withLabels(new HashMap<String, String>() {
            {
                put("label", "value");
            }
        }).endMetadata().endItem().addNewItem().withNewMetadata().withName("s2").withLabels(new HashMap<String, String>() {
            {
                put("label", "value");
                put("label2", "value2");
            }
        }).endMetadata().endItem().build()).once();
        final KubernetesDiscoveryProperties properties = new KubernetesDiscoveryProperties();
        final DiscoveryClient discoveryClient = new KubernetesDiscoveryClient(KubernetesDiscoveryClientTest.mockClient, properties, ( client) -> client.services().withLabels(new HashMap<String, String>() {
            {
                put("label", "value");
            }
        }), new DefaultIsServicePortSecureResolver(properties));
        final List<String> services = discoveryClient.getServices();
        assertThat(services).containsOnly("s1", "s2");
    }
}

