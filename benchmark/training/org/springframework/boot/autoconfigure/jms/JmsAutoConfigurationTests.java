/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.jms;


import DefaultMessageListenerContainer.CACHE_CONSUMER;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.transaction.jta.JtaTransactionManager;


/**
 * Tests for {@link JmsAutoConfiguration}.
 *
 * @author Greg Turnquist
 * @author Stephane Nicoll
 * @author Aur?lien Leboulanger
 */
public class JmsAutoConfigurationTests {
    private static final String ACTIVEMQ_EMBEDDED_URL = "vm://localhost?broker.persistent=false";

    private static final String ACTIVEMQ_NETWORK_URL = "tcp://localhost:61616";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(ActiveMQAutoConfiguration.class, JmsAutoConfiguration.class));

    @Test
    public void testDefaultJmsConfiguration() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration.class).run(this::testDefaultJmsConfiguration);
    }

    @Test
    public void testConnectionFactoryBackOff() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration2.class).run(( context) -> assertThat(context.getBean(.class).getBrokerURL()).isEqualTo("foobar"));
    }

    @Test
    public void testJmsTemplateBackOff() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration3.class).run(( context) -> assertThat(context.getBean(.class).getPriority()).isEqualTo(999));
    }

    @Test
    public void testJmsMessagingTemplateBackOff() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration5.class).run(( context) -> assertThat(context.getBean(.class).getDefaultDestinationName()).isEqualTo("fooBar"));
    }

    @Test
    public void testJmsTemplateBackOffEverything() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration2.class, JmsAutoConfigurationTests.TestConfiguration3.class, JmsAutoConfigurationTests.TestConfiguration5.class).run(this::testJmsTemplateBackOffEverything);
    }

    @Test
    public void testEnableJmsCreateDefaultContainerFactory() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.EnableJmsConfiguration.class).run(( context) -> assertThat(context).getBean("jmsListenerContainerFactory", .class).isExactlyInstanceOf(.class));
    }

    @Test
    public void testJmsListenerContainerFactoryBackOff() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration6.class, JmsAutoConfigurationTests.EnableJmsConfiguration.class).run(( context) -> assertThat(context).getBean("jmsListenerContainerFactory", .class).isExactlyInstanceOf(.class));
    }

    @Test
    public void jmsListenerContainerFactoryWhenMultipleConnectionFactoryBeansShouldBackOff() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration10.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void testJmsListenerContainerFactoryWithCustomSettings() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.EnableJmsConfiguration.class).withPropertyValues("spring.jms.listener.autoStartup=false", "spring.jms.listener.acknowledgeMode=client", "spring.jms.listener.concurrency=2", "spring.jms.listener.maxConcurrency=10").run(this::testJmsListenerContainerFactoryWithCustomSettings);
    }

    @Test
    public void testDefaultContainerFactoryWithJtaTransactionManager() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration7.class, JmsAutoConfigurationTests.EnableJmsConfiguration.class).run(( context) -> {
            DefaultMessageListenerContainer container = getContainer(context, "jmsListenerContainerFactory");
            assertThat(container.isSessionTransacted()).isFalse();
            assertThat(container).hasFieldOrPropertyWithValue("transactionManager", context.getBean(.class));
        });
    }

    @Test
    public void testDefaultContainerFactoryNonJtaTransactionManager() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration8.class, JmsAutoConfigurationTests.EnableJmsConfiguration.class).run(( context) -> {
            DefaultMessageListenerContainer container = getContainer(context, "jmsListenerContainerFactory");
            assertThat(container.isSessionTransacted()).isTrue();
            assertThat(container).hasFieldOrPropertyWithValue("transactionManager", null);
        });
    }

    @Test
    public void testDefaultContainerFactoryNoTransactionManager() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.EnableJmsConfiguration.class).run(( context) -> {
            DefaultMessageListenerContainer container = getContainer(context, "jmsListenerContainerFactory");
            assertThat(container.isSessionTransacted()).isTrue();
            assertThat(container).hasFieldOrPropertyWithValue("transactionManager", null);
        });
    }

    @Test
    public void testDefaultContainerFactoryWithMessageConverters() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.MessageConvertersConfiguration.class, JmsAutoConfigurationTests.EnableJmsConfiguration.class).run(( context) -> {
            DefaultMessageListenerContainer container = getContainer(context, "jmsListenerContainerFactory");
            assertThat(container.getMessageConverter()).isSameAs(context.getBean("myMessageConverter"));
        });
    }

    @Test
    public void testCustomContainerFactoryWithConfigurer() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration9.class, JmsAutoConfigurationTests.EnableJmsConfiguration.class).withPropertyValues("spring.jms.listener.autoStartup=false").run(( context) -> {
            DefaultMessageListenerContainer container = getContainer(context, "customListenerContainerFactory");
            assertThat(container.getCacheLevel()).isEqualTo(DefaultMessageListenerContainer.CACHE_CONSUMER);
            assertThat(container.isAutoStartup()).isFalse();
        });
    }

    @Test
    public void testJmsTemplateWithMessageConverter() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.MessageConvertersConfiguration.class).run(( context) -> {
            JmsTemplate jmsTemplate = context.getBean(.class);
            assertThat(jmsTemplate.getMessageConverter()).isSameAs(context.getBean("myMessageConverter"));
        });
    }

    @Test
    public void testJmsTemplateWithDestinationResolver() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.DestinationResolversConfiguration.class).run(( context) -> assertThat(context.getBean(.class).getDestinationResolver()).isSameAs(context.getBean("myDestinationResolver")));
    }

    @Test
    public void testJmsTemplateFullCustomization() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.MessageConvertersConfiguration.class).withPropertyValues("spring.jms.template.default-destination=testQueue", "spring.jms.template.delivery-delay=500", "spring.jms.template.delivery-mode=non-persistent", "spring.jms.template.priority=6", "spring.jms.template.time-to-live=6000", "spring.jms.template.receive-timeout=2000").run(( context) -> {
            JmsTemplate jmsTemplate = context.getBean(.class);
            assertThat(jmsTemplate.getMessageConverter()).isSameAs(context.getBean("myMessageConverter"));
            assertThat(jmsTemplate.isPubSubDomain()).isFalse();
            assertThat(jmsTemplate.getDefaultDestinationName()).isEqualTo("testQueue");
            assertThat(jmsTemplate.getDeliveryDelay()).isEqualTo(500);
            assertThat(jmsTemplate.getDeliveryMode()).isEqualTo(1);
            assertThat(jmsTemplate.getPriority()).isEqualTo(6);
            assertThat(jmsTemplate.getTimeToLive()).isEqualTo(6000);
            assertThat(jmsTemplate.isExplicitQosEnabled()).isTrue();
            assertThat(jmsTemplate.getReceiveTimeout()).isEqualTo(2000);
        });
    }

    @Test
    public void testPubSubDisabledByDefault() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration.class).run(( context) -> assertThat(context.getBean(.class).isPubSubDomain()).isFalse());
    }

    @Test
    public void testJmsTemplatePostProcessedSoThatPubSubIsTrue() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration4.class).run(( context) -> assertThat(context.getBean(.class).isPubSubDomain()).isTrue());
    }

    @Test
    public void testPubSubDomainActive() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.jms.pubSubDomain:true").run(( context) -> {
            JmsTemplate jmsTemplate = context.getBean(.class);
            DefaultMessageListenerContainer defaultMessageListenerContainer = context.getBean(.class).createListenerContainer(mock(.class));
            assertThat(jmsTemplate.isPubSubDomain()).isTrue();
            assertThat(defaultMessageListenerContainer.isPubSubDomain()).isTrue();
        });
    }

    @Test
    public void testPubSubDomainOverride() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.jms.pubSubDomain:false").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            JmsTemplate jmsTemplate = context.getBean(.class);
            ConnectionFactory factory = context.getBean(.class);
            assertThat(jmsTemplate).isNotNull();
            assertThat(jmsTemplate.isPubSubDomain()).isFalse();
            assertThat(factory).isNotNull().isEqualTo(jmsTemplate.getConnectionFactory());
        });
    }

    @Test
    public void testActiveMQOverriddenStandalone() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.activemq.inMemory:false").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            JmsTemplate jmsTemplate = context.getBean(.class);
            ConnectionFactory factory = context.getBean(.class);
            assertThat(factory).isEqualTo(jmsTemplate.getConnectionFactory());
            assertThat(getBrokerUrl(((CachingConnectionFactory) (factory)))).isEqualTo(ACTIVEMQ_NETWORK_URL);
        });
    }

    @Test
    public void testActiveMQOverriddenRemoteHost() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.activemq.brokerUrl:tcp://remote-host:10000").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            JmsTemplate jmsTemplate = context.getBean(.class);
            ConnectionFactory factory = context.getBean(.class);
            assertThat(factory).isEqualTo(jmsTemplate.getConnectionFactory());
            assertThat(getBrokerUrl(((CachingConnectionFactory) (factory)))).isEqualTo("tcp://remote-host:10000");
        });
    }

    @Test
    public void testActiveMQOverriddenPool() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.activemq.pool.enabled:true").run(( context) -> {
            JmsTemplate jmsTemplate = context.getBean(.class);
            JmsPoolConnectionFactory pool = context.getBean(.class);
            assertThat(jmsTemplate).isNotNull();
            assertThat(pool).isNotNull();
            assertThat(pool).isEqualTo(jmsTemplate.getConnectionFactory());
            ActiveMQConnectionFactory factory = ((ActiveMQConnectionFactory) (pool.getConnectionFactory()));
            assertThat(factory.getBrokerURL()).isEqualTo(ACTIVEMQ_EMBEDDED_URL);
        });
    }

    @Test
    public void testActiveMQOverriddenPoolAndStandalone() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.activemq.pool.enabled:true", "spring.activemq.inMemory:false").run(( context) -> {
            JmsTemplate jmsTemplate = context.getBean(.class);
            JmsPoolConnectionFactory pool = context.getBean(.class);
            assertThat(jmsTemplate).isNotNull();
            assertThat(pool).isNotNull();
            assertThat(pool).isEqualTo(jmsTemplate.getConnectionFactory());
            ActiveMQConnectionFactory factory = ((ActiveMQConnectionFactory) (pool.getConnectionFactory()));
            assertThat(factory.getBrokerURL()).isEqualTo(ACTIVEMQ_NETWORK_URL);
        });
    }

    @Test
    public void testActiveMQOverriddenPoolAndRemoteServer() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.activemq.pool.enabled:true", "spring.activemq.brokerUrl:tcp://remote-host:10000").run(( context) -> {
            JmsTemplate jmsTemplate = context.getBean(.class);
            JmsPoolConnectionFactory pool = context.getBean(.class);
            assertThat(jmsTemplate).isNotNull();
            assertThat(pool).isNotNull();
            assertThat(pool).isEqualTo(jmsTemplate.getConnectionFactory());
            ActiveMQConnectionFactory factory = ((ActiveMQConnectionFactory) (pool.getConnectionFactory()));
            assertThat(factory.getBrokerURL()).isEqualTo("tcp://remote-host:10000");
        });
    }

    @Test
    public void enableJmsAutomatically() {
        this.contextRunner.withUserConfiguration(JmsAutoConfigurationTests.NoEnableJmsConfiguration.class).run(( context) -> assertThat(context).hasBean(JmsListenerConfigUtils.JMS_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME).hasBean(JmsListenerConfigUtils.JMS_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME));
    }

    @Configuration
    protected static class TestConfiguration {}

    @Configuration
    protected static class TestConfiguration2 {
        @Bean
        ConnectionFactory connectionFactory() {
            return new ActiveMQConnectionFactory() {
                {
                    setBrokerURL("foobar");
                }
            };
        }
    }

    @Configuration
    protected static class TestConfiguration3 {
        @Bean
        JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
            JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
            jmsTemplate.setPriority(999);
            return jmsTemplate;
        }
    }

    @Configuration
    protected static class TestConfiguration4 implements BeanPostProcessor {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean.getClass().isAssignableFrom(JmsTemplate.class)) {
                JmsTemplate jmsTemplate = ((JmsTemplate) (bean));
                jmsTemplate.setPubSubDomain(true);
            }
            return bean;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }

    @Configuration
    protected static class TestConfiguration5 {
        @Bean
        JmsMessagingTemplate jmsMessagingTemplate(JmsTemplate jmsTemplate) {
            JmsMessagingTemplate messagingTemplate = new JmsMessagingTemplate(jmsTemplate);
            messagingTemplate.setDefaultDestinationName("fooBar");
            return messagingTemplate;
        }
    }

    @Configuration
    protected static class TestConfiguration6 {
        @Bean
        JmsListenerContainerFactory<?> jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
            SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
            factory.setConnectionFactory(connectionFactory);
            return factory;
        }
    }

    @Configuration
    protected static class TestConfiguration7 {
        @Bean
        JtaTransactionManager transactionManager() {
            return Mockito.mock(JtaTransactionManager.class);
        }
    }

    @Configuration
    protected static class TestConfiguration8 {
        @Bean
        DataSourceTransactionManager transactionManager() {
            return Mockito.mock(DataSourceTransactionManager.class);
        }
    }

    @Configuration
    protected static class MessageConvertersConfiguration {
        @Bean
        @Primary
        public MessageConverter myMessageConverter() {
            return Mockito.mock(MessageConverter.class);
        }

        @Bean
        public MessageConverter anotherMessageConverter() {
            return Mockito.mock(MessageConverter.class);
        }
    }

    @Configuration
    protected static class DestinationResolversConfiguration {
        @Bean
        @Primary
        public DestinationResolver myDestinationResolver() {
            return Mockito.mock(DestinationResolver.class);
        }

        @Bean
        public DestinationResolver anotherDestinationResolver() {
            return Mockito.mock(DestinationResolver.class);
        }
    }

    @Configuration
    protected static class TestConfiguration9 {
        @Bean
        JmsListenerContainerFactory<?> customListenerContainerFactory(DefaultJmsListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
            DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
            configurer.configure(factory, connectionFactory);
            factory.setCacheLevel(CACHE_CONSUMER);
            return factory;
        }
    }

    @Configuration
    protected static class TestConfiguration10 {
        @Bean
        public ConnectionFactory connectionFactory1() {
            return new ActiveMQConnectionFactory();
        }

        @Bean
        public ConnectionFactory connectionFactory2() {
            return new ActiveMQConnectionFactory();
        }
    }

    @Configuration
    @EnableJms
    protected static class EnableJmsConfiguration {}

    @Configuration
    protected static class NoEnableJmsConfiguration {}
}

