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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.springboot;


import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.jdbc.PersistenceExceptionResolver;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jpa.SQLErrorCodesResolver;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.Upcaster;
import org.axonframework.serialization.upcasting.event.EventUpcaster;
import org.axonframework.springboot.autoconfig.AxonServerAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@ContextConfiguration
@EnableAutoConfiguration(exclude = { JmxAutoConfiguration.class, WebClientAutoConfiguration.class, AxonServerAutoConfiguration.class })
@RunWith(SpringRunner.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class AxonAutoConfigurationWithHibernateTest {
    @Autowired
    private ApplicationContext applicationContext;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Upcaster upcaster;

    @Test
    public void testContextInitialization() {
        Assert.assertNotNull(applicationContext);
        Assert.assertNotNull(applicationContext.getBean(CommandBus.class));
        Assert.assertNotNull(applicationContext.getBean(EventBus.class));
        Assert.assertNotNull(applicationContext.getBean(CommandGateway.class));
        Assert.assertNotNull(applicationContext.getBean(Serializer.class));
        Assert.assertNotNull(applicationContext.getBean(TokenStore.class));
        Assert.assertNotNull(applicationContext.getBean(JpaEventStorageEngine.class));
        Assert.assertEquals(SQLErrorCodesResolver.class, applicationContext.getBean(PersistenceExceptionResolver.class).getClass());
        Assert.assertNotNull(applicationContext.getBean(EntityManagerProvider.class));
        Assert.assertNotNull(applicationContext.getBean(ConnectionProvider.class));
        Assert.assertEquals(5, entityManager.getEntityManagerFactory().getMetamodel().getEntities().size());
    }

    @Transactional
    @Test
    public void testEventStorageEngingeUsesSerializerBean() {
        final Serializer serializer = applicationContext.getBean(Serializer.class);
        final JpaEventStorageEngine engine = applicationContext.getBean(JpaEventStorageEngine.class);
        Assert.assertEquals(serializer, engine.getSnapshotSerializer());
        engine.appendEvents(asEventMessage("hello"));
        List<? extends TrackedEventMessage<?>> events = engine.readEvents(null, false).collect(Collectors.toList());
        Assert.assertEquals(1, events.size());
        Mockito.verify(upcaster).upcast(ArgumentMatchers.any());
    }

    @Configuration
    public static class Config {
        @Bean
        public EventUpcaster upcaster() {
            EventUpcaster mock = Mockito.mock(EventUpcaster.class);
            Mockito.when(mock.upcast(ArgumentMatchers.any())).thenAnswer(( i) -> i.getArgument(0));
            return mock;
        }
    }
}

