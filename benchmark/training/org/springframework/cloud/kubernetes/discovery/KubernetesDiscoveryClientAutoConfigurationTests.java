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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class KubernetesDiscoveryClientAutoConfigurationTests {
    @Autowired(required = false)
    private DiscoveryClient discoveryClient;

    @Test
    public void kubernetesDiscoveryClientCreated() {
        assertThat(this.discoveryClient).isNotNull().isInstanceOf(CompositeDiscoveryClient.class);
        CompositeDiscoveryClient composite = ((CompositeDiscoveryClient) (this.discoveryClient));
        assertThat(composite.getDiscoveryClients().stream().anyMatch(( dc) -> dc instanceof KubernetesDiscoveryClient)).isTrue();
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    protected static class TestConfig {}
}

