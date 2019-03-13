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
package org.apache.camel.spring.cloud.consul;


import QueryParams.DEFAULT;
import WebApplicationType.NONE;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.catalog.model.CatalogService;
import java.util.List;
import java.util.UUID;
import org.apache.camel.cloud.ServiceRegistry;
import org.apache.camel.impl.cloud.DefaultServiceDefinition;
import org.apache.camel.test.testcontainers.Wait;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.SocketUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;


public class CamelCloudConsulServiceRegistryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelCloudConsulServiceRegistryTest.class);

    private static final String SERVICE_ID = UUID.randomUUID().toString();

    private static final String SERVICE_NAME = "my-service";

    private static final String SERVICE_HOST = "localhost";

    private static final int SERVICE_PORT = SocketUtils.findAvailableTcpPort();

    @Rule
    public GenericContainer container = new GenericContainer("consul:1.4.0").withExposedPorts(8500).waitingFor(Wait.forLogMessageContaining("Synced node info", 1)).withLogConsumer(new Slf4jLogConsumer(CamelCloudConsulServiceRegistryTest.LOGGER).withPrefix("consul")).withCommand("agent", "-dev", "-server", "-bootstrap", "-client", "0.0.0.0", "-log-level", "trace");

    @Test
    public void testServiceRegistry() throws Exception {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(CamelCloudConsulServiceRegistryTest.TestConfiguration.class).web(NONE).run("--debug=false", "--spring.main.banner-mode=OFF", ("--spring.application.name=" + (UUID.randomUUID().toString())), "--ribbon.enabled=false", "--ribbon.eureka.enabled=false", "--management.endpoint.enabled=false", "--spring.cloud.consul.enabled=true", ("--spring.cloud.consul.host=" + (container.getContainerIpAddress())), ("--spring.cloud.consul.port=" + (container.getMappedPort(8500))), "--spring.cloud.consul.config.enabled=false", "--spring.cloud.consul.discovery.enabled=true", "--spring.cloud.service-registry.auto-registration.enabled=false", "--camel.cloud.service-registry.service-host=localhost", "--spring.main.allow-bean-definition-overriding=true");
        // TODO: Remove --spring.main.allow-bean-definition-overriding=true when new version of spring-cloud
        // is released that supports Spring Boot 2.1 more properly
        try {
            final ConsulClient client = context.getBean(ConsulClient.class);
            final ServiceRegistry registry = context.getBean(ServiceRegistry.class);
            registry.register(DefaultServiceDefinition.builder().withHost(CamelCloudConsulServiceRegistryTest.SERVICE_HOST).withPort(CamelCloudConsulServiceRegistryTest.SERVICE_PORT).withName(CamelCloudConsulServiceRegistryTest.SERVICE_NAME).withId(CamelCloudConsulServiceRegistryTest.SERVICE_ID).build());
            List<CatalogService> services = client.getCatalogService(CamelCloudConsulServiceRegistryTest.SERVICE_NAME, DEFAULT).getValue();
            assertThat(services).hasSize(1);
            assertThat(services).first().hasFieldOrPropertyWithValue("serviceId", CamelCloudConsulServiceRegistryTest.SERVICE_ID);
            assertThat(services).first().hasFieldOrPropertyWithValue("serviceName", CamelCloudConsulServiceRegistryTest.SERVICE_NAME);
            assertThat(services).first().hasFieldOrPropertyWithValue("serviceAddress", CamelCloudConsulServiceRegistryTest.SERVICE_HOST);
            assertThat(services).first().hasFieldOrPropertyWithValue("servicePort", CamelCloudConsulServiceRegistryTest.SERVICE_PORT);
        } finally {
            context.close();
        }
    }

    // *************************************
    // Config
    // *************************************
    @EnableAutoConfiguration
    @Configuration
    public static class TestConfiguration {}
}

