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
package org.apache.camel.component.zookeeper.cloud;


import ServiceDefinition.SERVICE_META_PATH;
import ServiceDefinition.SERVICE_META_PROTOCOL;
import ZooKeeperServiceRegistry.MetaData;
import java.util.Collection;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.apache.camel.component.zookeeper.ZooKeeperTestSupport;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.junit.Test;


public abstract class ZooKeeperServiceRegistrationTestBase extends CamelTestSupport {
    protected static final String SERVICE_ID = UUID.randomUUID().toString();

    protected static final String SERVICE_NAME = "my-service";

    protected static final String SERVICE_HOST = "localhost";

    protected static final String SERVICE_PATH = "/camel";

    protected static final int SERVICE_PORT = AvailablePortFinder.getNextAvailable();

    protected static final int SERVER_PORT = AvailablePortFinder.getNextAvailable();

    protected ZooKeeperTestSupport.TestZookeeperServer server;

    protected CuratorFramework curator;

    protected ServiceDiscovery<ZooKeeperServiceRegistry.MetaData> discovery;

    @Test
    public void testRegistrationFromRoute() throws Exception {
        // the service should not be registered as the route is not running
        assertTrue(discovery.queryForInstances(ZooKeeperServiceRegistrationTestBase.SERVICE_NAME).isEmpty());
        // let start the route
        context().getRouteController().startRoute(ZooKeeperServiceRegistrationTestBase.SERVICE_ID);
        // check that service has been registered
        Collection<ServiceInstance<ZooKeeperServiceRegistry.MetaData>> services = discovery.queryForInstances(ZooKeeperServiceRegistrationTestBase.SERVICE_NAME);
        assertEquals(1, services.size());
        ServiceInstance<ZooKeeperServiceRegistry.MetaData> instance = services.iterator().next();
        assertEquals(ZooKeeperServiceRegistrationTestBase.SERVICE_PORT, ((int) (instance.getPort())));
        assertEquals("localhost", instance.getAddress());
        assertEquals("http", instance.getPayload().get(SERVICE_META_PROTOCOL));
        assertEquals("/service/endpoint", instance.getPayload().get(SERVICE_META_PATH));
        getMetadata().forEach(( k, v) -> {
            assertEquals(v, instance.getPayload().get(k));
        });
        // let stop the route
        context().getRouteController().stopRoute(ZooKeeperServiceRegistrationTestBase.SERVICE_ID);
        // the service should be removed once the route is stopped
        assertTrue(discovery.queryForInstances(ZooKeeperServiceRegistrationTestBase.SERVICE_NAME).isEmpty());
    }
}

