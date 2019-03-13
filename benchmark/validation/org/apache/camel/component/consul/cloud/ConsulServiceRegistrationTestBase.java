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


import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.catalog.CatalogService;
import com.orbitz.consul.model.health.ServiceHealth;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.apache.camel.cloud.ServiceDefinition;
import org.apache.camel.component.consul.ConsulTestSupport;
import org.junit.Test;
import org.springframework.util.SocketUtils;


public abstract class ConsulServiceRegistrationTestBase extends ConsulTestSupport {
    protected static final String SERVICE_ID = UUID.randomUUID().toString();

    protected static final String SERVICE_NAME = "my-service";

    protected static final String SERVICE_HOST = "localhost";

    protected static final int SERVICE_PORT = SocketUtils.findAvailableTcpPort();

    @Test
    public void testRegistrationFromRoute() throws Exception {
        final CatalogClient catalog = getConsul().catalogClient();
        final HealthClient health = getConsul().healthClient();
        // the service should not be registered as the route is not running
        assertTrue(catalog.getService(ConsulServiceRegistrationTestBase.SERVICE_NAME).getResponse().isEmpty());
        // let start the route
        context().getRouteController().startRoute(ConsulServiceRegistrationTestBase.SERVICE_ID);
        // check that service has been registered
        List<CatalogService> services = catalog.getService(ConsulServiceRegistrationTestBase.SERVICE_NAME).getResponse();
        assertEquals(1, services.size());
        assertEquals(ConsulServiceRegistrationTestBase.SERVICE_PORT, services.get(0).getServicePort());
        assertEquals("localhost", services.get(0).getServiceAddress());
        assertTrue(services.get(0).getServiceTags().contains(((ServiceDefinition.SERVICE_META_PROTOCOL) + "=http")));
        assertTrue(services.get(0).getServiceTags().contains(((ServiceDefinition.SERVICE_META_PATH) + "=/service/endpoint")));
        getMetadata().forEach(( k, v) -> {
            assertTrue(services.get(0).getServiceTags().contains(((k + "=") + v)));
        });
        List<ServiceHealth> checks = health.getHealthyServiceInstances(ConsulServiceRegistrationTestBase.SERVICE_NAME).getResponse();
        assertEquals(1, checks.size());
        assertEquals(ConsulServiceRegistrationTestBase.SERVICE_PORT, checks.get(0).getService().getPort());
        assertEquals("localhost", checks.get(0).getService().getAddress());
        // let stop the route
        context().getRouteController().stopRoute(ConsulServiceRegistrationTestBase.SERVICE_ID);
        // the service should be removed once the route is stopped
        assertTrue(catalog.getService(ConsulServiceRegistrationTestBase.SERVICE_NAME).getResponse().isEmpty());
    }
}

