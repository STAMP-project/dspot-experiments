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
package org.apache.camel.spring.boot;


import Ordered.HIGHEST_PRECEDENCE;
import Ordered.LOWEST_PRECEDENCE;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Route;
import org.apache.camel.TypeConverter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.BeanRepository;
import org.apache.camel.spi.Registry;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


@DirtiesContext
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = { CamelAutoConfigurationTest.TestConfig.class, CamelAutoConfigurationTest.class, RouteConfigWithCamelContextInjected.class }, properties = { "camel.springboot.consumerTemplateCacheSize=100", "camel.springboot.jmxEnabled=true", "camel.springboot.name=customName", "camel.springboot.typeConversion=true", "camel.springboot.threadNamePattern=customThreadName #counter#" })
public class CamelAutoConfigurationTest extends Assert {
    // Collaborators fixtures
    @Autowired
    CamelContext camelContext;

    @Autowired
    CamelContextConfiguration camelContextConfiguration;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    ConsumerTemplate consumerTemplate;

    @Autowired
    TypeConverter typeConverter;

    // Spring context fixtures
    @EndpointInject(uri = "mock:xmlAutoLoading")
    MockEndpoint xmlAutoLoadingMock;

    // Tests
    @Test
    public void shouldCreateCamelContext() {
        Assert.assertNotNull(camelContext);
    }

    @Test
    public void shouldDetectRoutes() {
        // When
        Route route = camelContext.getRoute(CamelAutoConfigurationTest.TestConfig.ROUTE_ID);
        // Then
        Assert.assertNotNull(route);
    }

    @Test
    public void shouldLoadProducerTemplate() {
        Assert.assertNotNull(producerTemplate);
    }

    @Test
    public void shouldLoadConsumerTemplate() {
        Assert.assertNotNull(consumerTemplate);
    }

    @Test
    public void shouldLoadConsumerTemplateWithSizeFromProperties() {
        Assert.assertEquals(100, consumerTemplate.getMaximumCacheSize());
    }

    @Test
    public void shouldSendAndReceiveMessageWithTemplates() {
        // Given
        String message = "message";
        String seda = "seda:test";
        // When
        producerTemplate.sendBody(seda, message);
        String receivedBody = consumerTemplate.receiveBody(seda, String.class);
        // Then
        Assert.assertEquals(message, receivedBody);
    }

    @Test
    public void shouldLoadTypeConverters() {
        // Given
        Long hundred = 100L;
        // When
        Long convertedLong = typeConverter.convertTo(Long.class, hundred.toString());
        // Then
        Assert.assertEquals(hundred, convertedLong);
    }

    @Test
    public void shouldCallCamelContextConfiguration() {
        Mockito.verify(camelContextConfiguration).beforeApplicationStart(camelContext);
        Mockito.verify(camelContextConfiguration).afterApplicationStart(camelContext);
    }

    @Test
    public void shouldChangeContextNameViaConfigurationCallback() {
        Assert.assertEquals("customName", camelContext.getName());
        Assert.assertEquals(camelContext.getName(), camelContext.getManagementName());
    }

    @Test
    public void shouldStartRoute() {
        // Given
        String message = "msg";
        // When
        producerTemplate.sendBody("seda:test", message);
        String receivedMessage = consumerTemplate.receiveBody("seda:test", String.class);
        // Then
        Assert.assertEquals(message, receivedMessage);
    }

    @Test
    public void shouldLoadXmlRoutes() throws InterruptedException {
        // Given
        String message = "msg";
        xmlAutoLoadingMock.expectedBodiesReceived(message);
        // When
        producerTemplate.sendBody("direct:xmlAutoLoading", message);
        // Then
        xmlAutoLoadingMock.assertIsSatisfied();
    }

    @Test
    public void shouldChangeThreadNamePattern() {
        Assert.assertEquals(camelContext.getExecutorServiceManager().getThreadNamePattern(), "customThreadName #counter#");
    }

    @Test
    public void shouldComposeRegistries() {
        final Registry registry = camelContext.getRegistry();
        Assertions.assertThat(registry.lookupByName("bean")).isEqualTo(HIGHEST_PRECEDENCE);
    }

    @Configuration
    public static class TestConfig {
        // Constants
        static final String ROUTE_ID = "testRoute";

        // Test bean
        @Bean
        RouteBuilder routeBuilder() {
            return new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:test").routeId(CamelAutoConfigurationTest.TestConfig.ROUTE_ID).to("mock:test");
                }
            };
        }

        @Bean
        CamelContextConfiguration camelContextConfiguration() {
            return Mockito.mock(CamelContextConfiguration.class);
        }

        @Bean
        BeanRepository customRegistry1() {
            return mockBeanRepositoryWithBeanValueAndOrder(LOWEST_PRECEDENCE);
        }

        @Bean
        BeanRepository customRegistry2() {
            return mockBeanRepositoryWithBeanValueAndOrder(HIGHEST_PRECEDENCE);
        }

        private BeanRepository mockBeanRepositoryWithBeanValueAndOrder(int value) {
            final BeanRepository repo = Mockito.mock(BeanRepository.class, Mockito.withSettings().extraInterfaces(Ordered.class));
            Mockito.when(repo.lookupByName("bean")).thenReturn(value);
            Mockito.when(getOrder()).thenReturn(value);
            return repo;
        }
    }
}

