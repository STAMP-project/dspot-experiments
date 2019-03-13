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
package org.apache.camel.component.zookeeper.springboot.cloud;


import ZooKeeperServiceRegistry.MetaData;
import java.io.File;
import java.util.UUID;
import org.apache.camel.CamelContext;
import org.apache.camel.cloud.ServiceRegistry;
import org.apache.camel.component.zookeeper.cloud.ZooKeeperServiceRegistry;
import org.apache.camel.impl.cloud.DefaultServiceDefinition;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.SocketUtils;


public class ZooKeeperServiceRegistryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceRegistryTest.class);

    private static final String SERVICE_PATH = "/camel";

    private static final String SERVICE_ID = UUID.randomUUID().toString();

    private static final String SERVICE_NAME = "my-service";

    private static final String SERVICE_HOST = "localhost";

    private static final int SERVICE_PORT = SocketUtils.findAvailableTcpPort();

    @Rule
    public final TestName testName = new TestName();

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testServiceRegistry() throws Exception {
        final int zkPort = AvailablePortFinder.getNextAvailable();
        final File zkDir = temporaryFolder.newFolder();
        final TestingServer zkServer = new TestingServer(zkPort, zkDir);
        zkServer.start();
        final ZooKeeperServiceRegistryTest.ZooKeeperTestClient zkClient = new ZooKeeperServiceRegistryTest.ZooKeeperTestClient(("localhost:" + zkPort));
        zkClient.start();
        try {
            new ApplicationContextRunner().withUserConfiguration(ZooKeeperServiceRegistryTest.TestConfiguration.class).withPropertyValues("debug=false", "spring.main.banner-mode=OFF", ("spring.application.name=" + (UUID.randomUUID().toString())), "camel.component.zookeeper.service-registry.enabled=true", ("camel.component.zookeeper.service-registry.nodes=localhost:" + zkPort), ("camel.component.zookeeper.service-registry.id=" + (UUID.randomUUID().toString())), ("camel.component.zookeeper.service-registry.base-path=" + (ZooKeeperServiceRegistryTest.SERVICE_PATH)), "camel.component.zookeeper.service-registry.service-host=localhost").run(( context) -> {
                assertThat(context).hasSingleBean(.class);
                assertThat(context).hasSingleBean(.class);
                final CamelContext camelContext = context.getBean(.class);
                final ServiceRegistry serviceRegistry = camelContext.hasService(.class);
                assertThat(serviceRegistry).isNotNull();
                serviceRegistry.register(DefaultServiceDefinition.builder().withHost(SERVICE_HOST).withPort(SERVICE_PORT).withName(SERVICE_NAME).withId(SERVICE_ID).build());
                final Collection<ServiceInstance<ZooKeeperServiceRegistry.MetaData>> services = zkClient.discovery().queryForInstances(SERVICE_NAME);
                assertThat(services).hasSize(1);
                assertThat(services).first().hasFieldOrPropertyWithValue("id", SERVICE_ID);
                assertThat(services).first().hasFieldOrPropertyWithValue("name", SERVICE_NAME);
                assertThat(services).first().hasFieldOrPropertyWithValue("address", SERVICE_HOST);
                assertThat(services).first().hasFieldOrPropertyWithValue("port", SERVICE_PORT);
            });
        } finally {
            zkClient.stop();
            zkServer.stop();
        }
    }

    // *************************************
    // Config
    // *************************************
    @EnableAutoConfiguration
    @Configuration
    public static class TestConfiguration {}

    // *************************************
    // Helpers
    // *************************************
    public static class ZooKeeperTestClient {
        private final CuratorFramework curator;

        private final ServiceDiscovery<ZooKeeperServiceRegistry.MetaData> discovery;

        public ZooKeeperTestClient(String nodes) {
            curator = CuratorFrameworkFactory.builder().connectString(nodes).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
            discovery = ServiceDiscoveryBuilder.builder(MetaData.class).client(curator).basePath(ZooKeeperServiceRegistryTest.SERVICE_PATH).serializer(new org.apache.curator.x.discovery.details.JsonInstanceSerializer(MetaData.class)).build();
        }

        public CuratorFramework curator() {
            return curator;
        }

        public ServiceDiscovery<ZooKeeperServiceRegistry.MetaData> discovery() {
            return discovery;
        }

        public void start() throws Exception {
            curator.start();
            discovery.start();
        }

        public void stop() throws Exception {
            CloseableUtils.closeQuietly(discovery);
            CloseableUtils.closeQuietly(curator);
        }
    }
}

