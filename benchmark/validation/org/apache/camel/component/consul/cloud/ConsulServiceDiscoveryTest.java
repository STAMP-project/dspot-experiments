/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.consul.cloud;


import ServiceDefinition.SERVICE_META_ID;
import ServiceDefinition.SERVICE_META_NAME;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.model.agent.Registration;
import java.util.List;
import org.apache.camel.cloud.ServiceDefinition;
import org.apache.camel.cloud.ServiceDiscovery;
import org.apache.camel.component.consul.ConsulConfiguration;
import org.apache.camel.component.consul.ConsulTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ConsulServiceDiscoveryTest extends ConsulTestSupport {
    private AgentClient client;

    private List<Registration> registrations;

    // *************************************************************************
    // Test
    // *************************************************************************
    @Test
    public void testServiceDiscovery() throws Exception {
        ConsulConfiguration configuration = new ConsulConfiguration();
        configuration.setUrl(consulUrl());
        ServiceDiscovery discovery = new ConsulServiceDiscovery(configuration);
        List<ServiceDefinition> services = discovery.getServices("my-service");
        assertNotNull(services);
        assertEquals(6, services.size());
        for (ServiceDefinition service : services) {
            Assertions.assertThat(service.getMetadata()).isNotEmpty();
            Assertions.assertThat(service.getMetadata()).containsEntry(SERVICE_META_NAME, "my-service");
            Assertions.assertThat(service.getMetadata()).containsKey(SERVICE_META_ID);
            Assertions.assertThat(service.getMetadata()).containsKey("a-tag");
            Assertions.assertThat(service.getMetadata()).containsEntry("key1", "value1");
            Assertions.assertThat(service.getMetadata()).containsEntry("key2", "value2");
            Assertions.assertThat(service.getMetadata()).containsEntry("meta-key", "meta-val");
            Assertions.assertThat(("" + (service.getHealth().isHealthy()))).isEqualTo(service.getMetadata().get("healthy"));
        }
    }
}

