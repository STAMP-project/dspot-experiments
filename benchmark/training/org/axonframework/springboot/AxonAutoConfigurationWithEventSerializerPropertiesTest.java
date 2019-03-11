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
 * Copyright (c) 2010-2017. Axon Framework
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


import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.jdbc.PersistenceExceptionResolver;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jpa.SQLErrorCodesResolver;
import org.axonframework.serialization.JavaSerializer;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.axonframework.springboot.autoconfig.AxonServerAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@ContextConfiguration(classes = AxonAutoConfigurationWithEventSerializerPropertiesTest.TestContext.class)
@EnableAutoConfiguration(exclude = { JmxAutoConfiguration.class, WebClientAutoConfiguration.class, AxonServerAutoConfiguration.class })
@RunWith(SpringRunner.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@TestPropertySource("classpath:application.serializertest.properties")
public class AxonAutoConfigurationWithEventSerializerPropertiesTest {
    @Autowired
    private ApplicationContext applicationContext;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testContextInitialization() {
        Assert.assertNotNull(applicationContext);
        Assert.assertNotNull(applicationContext.getBean(CommandBus.class));
        Assert.assertNotNull(applicationContext.getBean(EventBus.class));
        Assert.assertNotNull(applicationContext.getBean(CommandGateway.class));
        Assert.assertNotNull(applicationContext.getBean(Serializer.class));
        Assert.assertNotNull(applicationContext.getBean("messageSerializer", Serializer.class));
        Assert.assertNotNull(applicationContext.getBean("eventSerializer", Serializer.class));
        AxonConfiguration axonConfiguration = applicationContext.getBean(AxonConfiguration.class);
        Assert.assertSame(axonConfiguration.serializer(), axonConfiguration.eventSerializer());
        Assert.assertNotSame(axonConfiguration.serializer(), axonConfiguration.messageSerializer());
        Assert.assertNotSame(axonConfiguration.messageSerializer(), axonConfiguration.eventSerializer());
        Assert.assertNotNull(applicationContext.getBean(TokenStore.class));
        Assert.assertNotNull(applicationContext.getBean(JpaEventStorageEngine.class));
        Assert.assertEquals(SQLErrorCodesResolver.class, applicationContext.getBean(PersistenceExceptionResolver.class).getClass());
        Assert.assertNotNull(applicationContext.getBean(EntityManagerProvider.class));
        Assert.assertNotNull(applicationContext.getBean(ConnectionProvider.class));
        Assert.assertEquals(5, entityManager.getEntityManagerFactory().getMetamodel().getEntities().size());
    }

    @Test
    public void testEventStorageEngineUsesSerializerBean() {
        final Serializer serializer = applicationContext.getBean(Serializer.class);
        final Serializer eventSerializer = applicationContext.getBean("eventSerializer", Serializer.class);
        final Serializer messageSerializer = applicationContext.getBean("messageSerializer", Serializer.class);
        final JpaEventStorageEngine engine = applicationContext.getBean(JpaEventStorageEngine.class);
        Assert.assertTrue((messageSerializer instanceof JavaSerializer));
        Assert.assertEquals(serializer, engine.getSnapshotSerializer());
        Assert.assertEquals(eventSerializer, engine.getEventSerializer());
    }

    @Test
    public void testEventSerializerIsOfTypeJacksonSerializerAndUsesDefinedObjectMapperBean() {
        final Serializer serializer = applicationContext.getBean(Serializer.class);
        final Serializer eventSerializer = applicationContext.getBean("eventSerializer", Serializer.class);
        final ObjectMapper objectMapper = applicationContext.getBean("testObjectMapper", ObjectMapper.class);
        Assert.assertTrue((serializer instanceof JacksonSerializer));
        Assert.assertEquals(objectMapper, getObjectMapper());
        Assert.assertTrue((eventSerializer instanceof JacksonSerializer));
        Assert.assertEquals(objectMapper, getObjectMapper());
    }

    @Configuration
    public static class TestContext {
        @Bean("testObjectMapper")
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}

