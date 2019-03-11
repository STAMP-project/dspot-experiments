/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.springboot;


import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.Configurer;
import org.axonframework.disruptor.commandhandling.DisruptorCommandBus;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.axonframework.messaging.correlation.CorrelationDataProvider;
import org.axonframework.messaging.correlation.SimpleCorrelationDataProvider;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.axonframework.spring.stereotype.Aggregate;
import org.axonframework.spring.stereotype.Saga;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@ContextConfiguration(classes = AxonAutoConfigurationWithDisruptorTest.Context.class)
@EnableAutoConfiguration(exclude = { JmxAutoConfiguration.class, WebClientAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class })
@RunWith(SpringRunner.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class AxonAutoConfigurationWithDisruptorTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Configurer configurer;

    @Autowired
    private AxonConfiguration configuration;

    @Test
    public void testContextInitialization() {
        Assert.assertNotNull(applicationContext);
        Assert.assertNotNull(configurer);
        Assert.assertNotNull(applicationContext.getBean(CommandBus.class));
        Assert.assertEquals(DisruptorCommandBus.class, applicationContext.getBean(CommandBus.class).getClass());
        Assert.assertNotNull(applicationContext.getBean(EventBus.class));
        Assert.assertNotNull(applicationContext.getBean(CommandGateway.class));
        Assert.assertNotNull(applicationContext.getBean(Serializer.class));
        Assert.assertEquals(1, applicationContext.getBeansOfType(EventStorageEngine.class).size());
        Assert.assertEquals(0, applicationContext.getBeansOfType(TokenStore.class).size());
        Assert.assertNotNull(applicationContext.getBean(AxonAutoConfigurationWithDisruptorTest.Context.MySaga.class));
        Assert.assertNotNull(applicationContext.getBean(AxonAutoConfigurationWithDisruptorTest.Context.MyAggregate.class));
        Assert.assertEquals(2, configuration.correlationDataProviders().size());
    }

    @Configuration
    public static class Context {
        @Bean
        public EventStorageEngine storageEngine() {
            return new InMemoryEventStorageEngine();
        }

        @Bean
        public CorrelationDataProvider correlationData1() {
            return new SimpleCorrelationDataProvider("key1");
        }

        @Bean
        public CorrelationDataProvider correlationData2() {
            return new SimpleCorrelationDataProvider("key2");
        }

        @Bean
        public DisruptorCommandBus commandBus() {
            return DisruptorCommandBus.builder().build();
        }

        @Aggregate
        public static class MyAggregate {
            @CommandHandler
            public void handle(String type, AxonAutoConfigurationWithDisruptorTest.Context.SomeComponent test) {
            }

            @EventHandler
            public void on(String type, AxonAutoConfigurationWithDisruptorTest.Context.SomeComponent test) {
            }
        }

        @Saga
        public static class MySaga {
            @SagaEventHandler(associationProperty = "toString")
            public void handle(String type, AxonAutoConfigurationWithDisruptorTest.Context.SomeComponent test) {
            }
        }

        @Component
        public static class SomeComponent {
            @EventHandler
            public void handle(String event, AxonAutoConfigurationWithDisruptorTest.Context.SomeOtherComponent test) {
            }
        }

        @Component
        public static class SomeOtherComponent {}
    }
}

