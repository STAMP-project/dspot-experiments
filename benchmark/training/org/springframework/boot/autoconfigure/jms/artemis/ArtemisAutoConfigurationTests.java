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
package org.springframework.boot.autoconfigure.jms.artemis;


import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.server.config.JMSConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSQueueConfiguration;
import org.apache.activemq.artemis.jms.server.config.TopicConfiguration;
import org.apache.activemq.artemis.jms.server.config.impl.JMSConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSQueueConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.TopicConfigurationImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;


/**
 * Tests for {@link ArtemisAutoConfiguration}.
 *
 * @author Edd? Mel?ndez
 * @author Stephane Nicoll
 */
public class ArtemisAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(ArtemisAutoConfiguration.class, JmsAutoConfiguration.class));

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void connectionFactoryIsCachedByDefault() {
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            CachingConnectionFactory connectionFactory = context.getBean(.class);
            assertThat(connectionFactory.getTargetConnectionFactory()).isInstanceOf(.class);
            assertThat(connectionFactory.isCacheConsumers()).isFalse();
            assertThat(connectionFactory.isCacheProducers()).isTrue();
            assertThat(connectionFactory.getSessionCacheSize()).isEqualTo(1);
        });
    }

    @Test
    public void connectionFactoryCachingCanBeCustomized() {
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).withPropertyValues("spring.jms.cache.consumers=true", "spring.jms.cache.producers=false", "spring.jms.cache.session-cache-size=10").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            CachingConnectionFactory connectionFactory = context.getBean(.class);
            assertThat(connectionFactory.isCacheConsumers()).isTrue();
            assertThat(connectionFactory.isCacheProducers()).isFalse();
            assertThat(connectionFactory.getSessionCacheSize()).isEqualTo(10);
        });
    }

    @Test
    public void connectionFactoryCachingCanBeDisabled() {
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).withPropertyValues("spring.jms.cache.enabled=false").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context.getBean(.class)).isInstanceOf(.class);
        });
    }

    @Test
    public void nativeConnectionFactory() {
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).withPropertyValues("spring.artemis.mode:native").run(( context) -> {
            JmsTemplate jmsTemplate = context.getBean(.class);
            ConnectionFactory connectionFactory = context.getBean(.class);
            assertThat(connectionFactory).isEqualTo(jmsTemplate.getConnectionFactory());
            ActiveMQConnectionFactory activeMQConnectionFactory = getActiveMQConnectionFactory(connectionFactory);
            assertNettyConnectionFactory(activeMQConnectionFactory, "localhost", 61616);
            assertThat(activeMQConnectionFactory.getUser()).isNull();
            assertThat(activeMQConnectionFactory.getPassword()).isNull();
        });
    }

    @Test
    public void nativeConnectionFactoryCustomHost() {
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).withPropertyValues("spring.artemis.mode:native", "spring.artemis.host:192.168.1.144", "spring.artemis.port:9876").run(( context) -> assertNettyConnectionFactory(getActiveMQConnectionFactory(context.getBean(.class)), "192.168.1.144", 9876));
    }

    @Test
    public void nativeConnectionFactoryCredentials() {
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).withPropertyValues("spring.artemis.mode:native", "spring.artemis.user:user", "spring.artemis.password:secret").run(( context) -> {
            JmsTemplate jmsTemplate = context.getBean(.class);
            ConnectionFactory connectionFactory = context.getBean(.class);
            assertThat(connectionFactory).isEqualTo(jmsTemplate.getConnectionFactory());
            ActiveMQConnectionFactory activeMQConnectionFactory = getActiveMQConnectionFactory(connectionFactory);
            assertNettyConnectionFactory(activeMQConnectionFactory, "localhost", 61616);
            assertThat(activeMQConnectionFactory.getUser()).isEqualTo("user");
            assertThat(activeMQConnectionFactory.getPassword()).isEqualTo("secret");
        });
    }

    @Test
    public void embeddedConnectionFactory() {
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).withPropertyValues("spring.artemis.mode:embedded").run(( context) -> {
            ArtemisProperties properties = context.getBean(.class);
            assertThat(properties.getMode()).isEqualTo(ArtemisMode.EMBEDDED);
            assertThat(context).hasSingleBean(.class);
            Configuration configuration = context.getBean(.class);
            assertThat(configuration.isPersistenceEnabled()).isFalse();
            assertThat(configuration.isSecurityEnabled()).isFalse();
            assertInVmConnectionFactory(getActiveMQConnectionFactory(context.getBean(.class)));
        });
    }

    @Test
    public void embeddedConnectionFactoryByDefault() {
        // No mode is specified
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            Configuration configuration = context.getBean(.class);
            assertThat(configuration.isPersistenceEnabled()).isFalse();
            assertThat(configuration.isSecurityEnabled()).isFalse();
            assertInVmConnectionFactory(getActiveMQConnectionFactory(context.getBean(.class)));
        });
    }

    @Test
    public void nativeConnectionFactoryIfEmbeddedServiceDisabledExplicitly() {
        // No mode is specified
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).withPropertyValues("spring.artemis.embedded.enabled:false").run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertNettyConnectionFactory(getActiveMQConnectionFactory(context.getBean(.class)), "localhost", 61616);
        });
    }

    @Test
    public void embeddedConnectionFactoryEvenIfEmbeddedServiceDisabled() {
        // No mode is specified
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).withPropertyValues("spring.artemis.mode:embedded", "spring.artemis.embedded.enabled:false").run(( context) -> {
            assertThat(context.getBeansOfType(.class)).isEmpty();
            assertInVmConnectionFactory(getActiveMQConnectionFactory(context.getBean(.class)));
        });
    }

    @Test
    public void embeddedServerWithDestinations() {
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).withPropertyValues("spring.artemis.embedded.queues=Queue1,Queue2", "spring.artemis.embedded.topics=Topic1").run(( context) -> {
            org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker checker = new org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker(context);
            checker.checkQueue("Queue1", true);
            checker.checkQueue("Queue2", true);
            checker.checkQueue("QueueWillNotBeAutoCreated", true);
            checker.checkTopic("Topic1", true);
            checker.checkTopic("TopicWillBeAutoCreated", true);
        });
    }

    @Test
    public void embeddedServerWithDestinationConfig() {
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.DestinationConfiguration.class).run(( context) -> {
            org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker checker = new org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker(context);
            checker.checkQueue("sampleQueue", true);
            checker.checkTopic("sampleTopic", true);
        });
    }

    @Test
    public void embeddedServiceWithCustomJmsConfiguration() {
        // Ignored with custom config
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.CustomJmsConfiguration.class).withPropertyValues("spring.artemis.embedded.queues=Queue1,Queue2").run(( context) -> {
            org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker checker = new org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker(context);
            checker.checkQueue("custom", true);// See CustomJmsConfiguration

            checker.checkQueue("Queue1", true);
            checker.checkQueue("Queue2", true);
        });
    }

    @Test
    public void embeddedServiceWithCustomArtemisConfiguration() {
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.CustomArtemisConfiguration.class).run(( context) -> assertThat(context.getBean(.class).getName()).isEqualTo("customFooBar"));
    }

    @Test
    public void embeddedWithPersistentMode() throws IOException {
        File dataFolder = this.temp.newFolder();
        final String messageId = UUID.randomUUID().toString();
        // Start the server and post a message to some queue
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).withPropertyValues("spring.artemis.embedded.queues=TestQueue", "spring.artemis.embedded.persistent:true", ("spring.artemis.embedded.dataDirectory:" + (dataFolder.getAbsolutePath()))).run(( context) -> context.getBean(.class).send("TestQueue", ( session) -> session.createTextMessage(messageId))).run(( context) -> {
            // Start the server again and check if our message is still here
            JmsTemplate jmsTemplate2 = context.getBean(.class);
            jmsTemplate2.setReceiveTimeout(1000L);
            Message message = jmsTemplate2.receive("TestQueue");
            assertThat(message).isNotNull();
            assertThat(((TextMessage) (message)).getText()).isEqualTo(messageId);
        });
    }

    @Test
    public void severalEmbeddedBrokers() {
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).withPropertyValues("spring.artemis.embedded.queues=Queue1").run(( first) -> {
            this.contextRunner.withPropertyValues("spring.artemis.embedded.queues=Queue2").run(( second) -> {
                ArtemisProperties firstProperties = first.getBean(.class);
                ArtemisProperties secondProperties = second.getBean(.class);
                assertThat(firstProperties.getEmbedded().getServerId()).isLessThan(secondProperties.getEmbedded().getServerId());
                org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker firstChecker = new org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker(first);
                firstChecker.checkQueue("Queue1", true);
                firstChecker.checkQueue("Queue2", true);
                org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker secondChecker = new org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker(second);
                secondChecker.checkQueue("Queue2", true);
                secondChecker.checkQueue("Queue1", true);
            });
        });
    }

    @Test
    public void connectToASpecificEmbeddedBroker() {
        this.contextRunner.withUserConfiguration(ArtemisAutoConfigurationTests.EmptyConfiguration.class).withPropertyValues("spring.artemis.embedded.serverId=93", "spring.artemis.embedded.queues=Queue1").run(( first) -> {
            // Connect to the "main" broker
            // Do not start a specific one
            this.contextRunner.withUserConfiguration(.class).withPropertyValues("spring.artemis.mode=embedded", "spring.artemis.embedded.serverId=93", "spring.artemis.embedded.enabled=false").run(( secondContext) -> {
                org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker firstChecker = new org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker(first);
                firstChecker.checkQueue("Queue1", true);
                org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker secondChecker = new org.springframework.boot.autoconfigure.jms.artemis.DestinationChecker(secondContext);
                secondChecker.checkQueue("Queue1", true);
            });
        });
    }

    @Test
    public void defaultPoolConnectionFactoryIsApplied() {
        this.contextRunner.withPropertyValues("spring.artemis.pool.enabled=true").run(( context) -> {
            assertThat(context.getBeansOfType(.class)).hasSize(1);
            JmsPoolConnectionFactory connectionFactory = context.getBean(.class);
            JmsPoolConnectionFactory defaultFactory = new JmsPoolConnectionFactory();
            assertThat(connectionFactory.isBlockIfSessionPoolIsFull()).isEqualTo(defaultFactory.isBlockIfSessionPoolIsFull());
            assertThat(connectionFactory.getBlockIfSessionPoolIsFullTimeout()).isEqualTo(defaultFactory.getBlockIfSessionPoolIsFullTimeout());
            assertThat(connectionFactory.getConnectionIdleTimeout()).isEqualTo(defaultFactory.getConnectionIdleTimeout());
            assertThat(connectionFactory.getMaxConnections()).isEqualTo(defaultFactory.getMaxConnections());
            assertThat(connectionFactory.getMaxSessionsPerConnection()).isEqualTo(defaultFactory.getMaxSessionsPerConnection());
            assertThat(connectionFactory.getConnectionCheckInterval()).isEqualTo(defaultFactory.getConnectionCheckInterval());
            assertThat(connectionFactory.isUseAnonymousProducers()).isEqualTo(defaultFactory.isUseAnonymousProducers());
        });
    }

    @Test
    public void customPoolConnectionFactoryIsApplied() {
        this.contextRunner.withPropertyValues("spring.artemis.pool.enabled=true", "spring.artemis.pool.blockIfFull=false", "spring.artemis.pool.blockIfFullTimeout=64", "spring.artemis.pool.idleTimeout=512", "spring.artemis.pool.maxConnections=256", "spring.artemis.pool.maxSessionsPerConnection=1024", "spring.artemis.pool.timeBetweenExpirationCheck=2048", "spring.artemis.pool.useAnonymousProducers=false").run(( context) -> {
            assertThat(context.getBeansOfType(.class)).hasSize(1);
            JmsPoolConnectionFactory connectionFactory = context.getBean(.class);
            assertThat(connectionFactory.isBlockIfSessionPoolIsFull()).isFalse();
            assertThat(connectionFactory.getBlockIfSessionPoolIsFullTimeout()).isEqualTo(64);
            assertThat(connectionFactory.getConnectionIdleTimeout()).isEqualTo(512);
            assertThat(connectionFactory.getMaxConnections()).isEqualTo(256);
            assertThat(connectionFactory.getMaxSessionsPerConnection()).isEqualTo(1024);
            assertThat(connectionFactory.getConnectionCheckInterval()).isEqualTo(2048);
            assertThat(connectionFactory.isUseAnonymousProducers()).isFalse();
        });
    }

    @Test
    public void poolConnectionFactoryConfiguration() {
        this.contextRunner.withPropertyValues("spring.artemis.pool.enabled:true").run(( context) -> {
            ConnectionFactory factory = context.getBean(.class);
            assertThat(factory).isInstanceOf(.class);
            context.getSourceApplicationContext().close();
            assertThat(factory.createConnection()).isNull();
        });
    }

    private static final class DestinationChecker {
        private final JmsTemplate jmsTemplate;

        private final DestinationResolver destinationResolver;

        private DestinationChecker(ApplicationContext applicationContext) {
            this.jmsTemplate = applicationContext.getBean(JmsTemplate.class);
            this.destinationResolver = new DynamicDestinationResolver();
        }

        public void checkQueue(String name, boolean shouldExist) {
            checkDestination(name, false, shouldExist);
        }

        public void checkTopic(String name, boolean shouldExist) {
            checkDestination(name, true, shouldExist);
        }

        public void checkDestination(String name, final boolean pubSub, final boolean shouldExist) {
            this.jmsTemplate.execute(((SessionCallback<Void>) (( session) -> {
                try {
                    Destination destination = this.destinationResolver.resolveDestinationName(session, name, pubSub);
                    if (!shouldExist) {
                        throw new IllegalStateException(((("Destination '" + name) + "' was not expected but got ") + destination));
                    }
                } catch ( ex) {
                    if (shouldExist) {
                        throw new IllegalStateException(((("Destination '" + name) + "' was expected but got ") + (ex.getMessage())));
                    }
                }
                return null;
            })));
        }
    }

    @org.springframework.context.annotation.Configuration
    protected static class EmptyConfiguration {}

    @org.springframework.context.annotation.Configuration
    protected static class DestinationConfiguration {
        @Bean
        JMSQueueConfiguration sampleQueueConfiguration() {
            JMSQueueConfigurationImpl jmsQueueConfiguration = new JMSQueueConfigurationImpl();
            jmsQueueConfiguration.setName("sampleQueue");
            jmsQueueConfiguration.setSelector("foo=bar");
            jmsQueueConfiguration.setDurable(false);
            jmsQueueConfiguration.setBindings("/queue/1");
            return jmsQueueConfiguration;
        }

        @Bean
        TopicConfiguration sampleTopicConfiguration() {
            TopicConfigurationImpl topicConfiguration = new TopicConfigurationImpl();
            topicConfiguration.setName("sampleTopic");
            topicConfiguration.setBindings("/topic/1");
            return topicConfiguration;
        }
    }

    @org.springframework.context.annotation.Configuration
    protected static class CustomJmsConfiguration {
        @Bean
        public JMSConfiguration myJmsConfiguration() {
            JMSConfiguration config = new JMSConfigurationImpl();
            JMSQueueConfiguration jmsQueueConfiguration = new JMSQueueConfigurationImpl();
            jmsQueueConfiguration.setName("custom");
            jmsQueueConfiguration.setDurable(false);
            config.getQueueConfigurations().add(jmsQueueConfiguration);
            return config;
        }
    }

    @org.springframework.context.annotation.Configuration
    protected static class CustomArtemisConfiguration {
        @Bean
        public ArtemisConfigurationCustomizer myArtemisCustomize() {
            return ( configuration) -> {
                configuration.setClusterPassword("Foobar");
                configuration.setName("customFooBar");
            };
        }
    }
}

