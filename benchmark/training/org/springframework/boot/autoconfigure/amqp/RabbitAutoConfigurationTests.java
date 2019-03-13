/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.autoconfigure.amqp;


import com.rabbitmq.client.Address;
import com.rabbitmq.client.ConnectionFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.TrustManager;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link RabbitAutoConfiguration}.
 *
 * @author Greg Turnquist
 * @author Stephane Nicoll
 * @author Gary Russell
 */
public class RabbitAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(RabbitAutoConfiguration.class));

    @Test
    public void testDefaultRabbitConfiguration() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).run(( context) -> {
            RabbitTemplate rabbitTemplate = context.getBean(.class);
            RabbitMessagingTemplate messagingTemplate = context.getBean(.class);
            CachingConnectionFactory connectionFactory = context.getBean(.class);
            RabbitAdmin amqpAdmin = context.getBean(.class);
            assertThat(rabbitTemplate.getConnectionFactory()).isEqualTo(connectionFactory);
            assertThat(getMandatory(rabbitTemplate)).isFalse();
            assertThat(messagingTemplate.getRabbitTemplate()).isEqualTo(rabbitTemplate);
            assertThat(amqpAdmin).isNotNull();
            assertThat(connectionFactory.getHost()).isEqualTo("localhost");
            assertThat(connectionFactory.isPublisherConfirms()).isFalse();
            assertThat(connectionFactory.isPublisherReturns()).isFalse();
            assertThat(context.containsBean("rabbitListenerContainerFactory")).as("Listener container factory should be created by default").isTrue();
        });
    }

    @Test
    public void testDefaultRabbitTemplateConfiguration() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).run(( context) -> {
            RabbitTemplate rabbitTemplate = context.getBean(.class);
            RabbitTemplate defaultRabbitTemplate = new RabbitTemplate();
            assertThat(rabbitTemplate.getRoutingKey()).isEqualTo(defaultRabbitTemplate.getRoutingKey());
            assertThat(rabbitTemplate.getExchange()).isEqualTo(defaultRabbitTemplate.getExchange());
        });
    }

    @Test
    public void testDefaultConnectionFactoryConfiguration() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).run(( context) -> {
            RabbitProperties properties = new RabbitProperties();
            ConnectionFactory rabbitConnectionFactory = getTargetConnectionFactory(context);
            assertThat(rabbitConnectionFactory.getUsername()).isEqualTo(properties.getUsername());
            assertThat(rabbitConnectionFactory.getPassword()).isEqualTo(properties.getPassword());
        });
    }

    @Test
    public void testConnectionFactoryWithOverrides() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.host:remote-server", "spring.rabbitmq.port:9000", "spring.rabbitmq.username:alice", "spring.rabbitmq.password:secret", "spring.rabbitmq.virtual_host:/vhost", "spring.rabbitmq.connection-timeout:123").run(( context) -> {
            CachingConnectionFactory connectionFactory = context.getBean(.class);
            assertThat(connectionFactory.getHost()).isEqualTo("remote-server");
            assertThat(connectionFactory.getPort()).isEqualTo(9000);
            assertThat(connectionFactory.getVirtualHost()).isEqualTo("/vhost");
            ConnectionFactory rcf = connectionFactory.getRabbitConnectionFactory();
            assertThat(rcf.getConnectionTimeout()).isEqualTo(123);
            assertThat(((Address[]) (ReflectionTestUtils.getField(connectionFactory, "addresses")))).hasSize(1);
        });
    }

    @Test
    public void testConnectionFactoryWithCustomConnectionNameStrategy() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.ConnectionNameStrategyConfiguration.class).run(( context) -> {
            CachingConnectionFactory connectionFactory = context.getBean(.class);
            Address[] addresses = ((Address[]) (ReflectionTestUtils.getField(connectionFactory, "addresses")));
            assertThat(addresses).hasSize(1);
            ConnectionFactory rcf = mock(.class);
            given(rcf.newConnection(isNull(), eq(addresses), anyString())).willReturn(mock(.class));
            ReflectionTestUtils.setField(connectionFactory, "rabbitConnectionFactory", rcf);
            connectionFactory.createConnection();
            verify(rcf).newConnection(isNull(), eq(addresses), eq("test#0"));
            connectionFactory.resetConnection();
            connectionFactory.createConnection();
            verify(rcf).newConnection(isNull(), eq(addresses), eq("test#1"));
        });
    }

    @Test
    public void testConnectionFactoryEmptyVirtualHost() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.virtual_host:").run(( context) -> {
            CachingConnectionFactory connectionFactory = context.getBean(.class);
            assertThat(connectionFactory.getVirtualHost()).isEqualTo("/");
        });
    }

    @Test
    public void testConnectionFactoryVirtualHostNoLeadingSlash() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.virtual_host:foo").run(( context) -> {
            CachingConnectionFactory connectionFactory = context.getBean(.class);
            assertThat(connectionFactory.getVirtualHost()).isEqualTo("foo");
        });
    }

    @Test
    public void testConnectionFactoryVirtualHostMultiLeadingSlashes() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.virtual_host:///foo").run(( context) -> {
            CachingConnectionFactory connectionFactory = context.getBean(.class);
            assertThat(connectionFactory.getVirtualHost()).isEqualTo("///foo");
        });
    }

    @Test
    public void testConnectionFactoryDefaultVirtualHost() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.virtual_host:/").run(( context) -> {
            CachingConnectionFactory connectionFactory = context.getBean(.class);
            assertThat(connectionFactory.getVirtualHost()).isEqualTo("/");
        });
    }

    @Test
    public void testConnectionFactoryPublisherSettings() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.publisher-confirms=true", "spring.rabbitmq.publisher-returns=true").run(( context) -> {
            CachingConnectionFactory connectionFactory = context.getBean(.class);
            RabbitTemplate rabbitTemplate = context.getBean(.class);
            assertThat(connectionFactory.isPublisherConfirms()).isTrue();
            assertThat(connectionFactory.isPublisherReturns()).isTrue();
            assertThat(getMandatory(rabbitTemplate)).isTrue();
        });
    }

    @Test
    public void testRabbitTemplateMessageConverters() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.MessageConvertersConfiguration.class).run(( context) -> {
            RabbitTemplate rabbitTemplate = context.getBean(.class);
            assertThat(rabbitTemplate.getMessageConverter()).isSameAs(context.getBean("myMessageConverter"));
            assertThat(rabbitTemplate).hasFieldOrPropertyWithValue("retryTemplate", null);
        });
    }

    @Test
    public void testRabbitTemplateRetry() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.template.retry.enabled:true", "spring.rabbitmq.template.retry.maxAttempts:4", "spring.rabbitmq.template.retry.initialInterval:2000", "spring.rabbitmq.template.retry.multiplier:1.5", "spring.rabbitmq.template.retry.maxInterval:5000", "spring.rabbitmq.template.receiveTimeout:123", "spring.rabbitmq.template.replyTimeout:456").run(( context) -> {
            RabbitTemplate rabbitTemplate = context.getBean(.class);
            assertThat(rabbitTemplate).hasFieldOrPropertyWithValue("receiveTimeout", 123L);
            assertThat(rabbitTemplate).hasFieldOrPropertyWithValue("replyTimeout", 456L);
            RetryTemplate retryTemplate = ((RetryTemplate) (ReflectionTestUtils.getField(rabbitTemplate, "retryTemplate")));
            assertThat(retryTemplate).isNotNull();
            SimpleRetryPolicy retryPolicy = ((SimpleRetryPolicy) (ReflectionTestUtils.getField(retryTemplate, "retryPolicy")));
            ExponentialBackOffPolicy backOffPolicy = ((ExponentialBackOffPolicy) (ReflectionTestUtils.getField(retryTemplate, "backOffPolicy")));
            assertThat(retryPolicy.getMaxAttempts()).isEqualTo(4);
            assertThat(backOffPolicy.getInitialInterval()).isEqualTo(2000);
            assertThat(backOffPolicy.getMultiplier()).isEqualTo(1.5);
            assertThat(backOffPolicy.getMaxInterval()).isEqualTo(5000);
        });
    }

    @Test
    public void testRabbitTemplateRetryWithCustomizer() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.RabbitRetryTemplateCustomizerConfiguration.class).withPropertyValues("spring.rabbitmq.template.retry.enabled:true", "spring.rabbitmq.template.retry.initialInterval:2000").run(( context) -> {
            RabbitTemplate rabbitTemplate = context.getBean(.class);
            RetryTemplate retryTemplate = ((RetryTemplate) (ReflectionTestUtils.getField(rabbitTemplate, "retryTemplate")));
            assertThat(retryTemplate).isNotNull();
            ExponentialBackOffPolicy backOffPolicy = ((ExponentialBackOffPolicy) (ReflectionTestUtils.getField(retryTemplate, "backOffPolicy")));
            assertThat(backOffPolicy).isSameAs(context.getBean(.class).backOffPolicy);
            assertThat(backOffPolicy.getInitialInterval()).isEqualTo(100);
        });
    }

    @Test
    public void testRabbitTemplateExchangeAndRoutingKey() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.template.exchange:my-exchange", "spring.rabbitmq.template.routing-key:my-routing-key").run(( context) -> {
            RabbitTemplate rabbitTemplate = context.getBean(.class);
            assertThat(rabbitTemplate.getExchange()).isEqualTo("my-exchange");
            assertThat(rabbitTemplate.getRoutingKey()).isEqualTo("my-routing-key");
        });
    }

    @Test
    public void testRabbitTemplateDefaultReceiveQueue() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.template.default-receive-queue:default-queue").run(( context) -> {
            RabbitTemplate rabbitTemplate = context.getBean(.class);
            assertThat(rabbitTemplate).hasFieldOrPropertyWithValue("defaultReceiveQueue", "default-queue");
        });
    }

    @Test
    public void testRabbitTemplateMandatory() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.template.mandatory:true").run(( context) -> {
            RabbitTemplate rabbitTemplate = context.getBean(.class);
            assertThat(getMandatory(rabbitTemplate)).isTrue();
        });
    }

    @Test
    public void testRabbitTemplateMandatoryDisabledEvenIfPublisherReturnsIsSet() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.template.mandatory:false", "spring.rabbitmq.publisher-returns=true").run(( context) -> {
            RabbitTemplate rabbitTemplate = context.getBean(.class);
            assertThat(getMandatory(rabbitTemplate)).isFalse();
        });
    }

    @Test
    public void testConnectionFactoryBackOff() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration2.class).run(( context) -> {
            RabbitTemplate rabbitTemplate = context.getBean(.class);
            CachingConnectionFactory connectionFactory = context.getBean(.class);
            assertThat(connectionFactory).isEqualTo(rabbitTemplate.getConnectionFactory());
            assertThat(connectionFactory.getHost()).isEqualTo("otherserver");
            assertThat(connectionFactory.getPort()).isEqualTo(8001);
        });
    }

    @Test
    public void testConnectionFactoryCacheSettings() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.cache.channel.size=23", "spring.rabbitmq.cache.channel.checkoutTimeout=1000", "spring.rabbitmq.cache.connection.mode=CONNECTION", "spring.rabbitmq.cache.connection.size=2").run(( context) -> {
            CachingConnectionFactory connectionFactory = context.getBean(.class);
            assertThat(connectionFactory.getChannelCacheSize()).isEqualTo(23);
            assertThat(connectionFactory.getCacheMode()).isEqualTo(CacheMode.CONNECTION);
            assertThat(connectionFactory.getConnectionCacheSize()).isEqualTo(2);
            assertThat(connectionFactory).hasFieldOrPropertyWithValue("channelCheckoutTimeout", 1000L);
        });
    }

    @Test
    public void testRabbitTemplateBackOff() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration3.class).run(( context) -> {
            RabbitTemplate rabbitTemplate = context.getBean(.class);
            assertThat(rabbitTemplate.getMessageConverter()).isEqualTo(context.getBean("testMessageConverter"));
        });
    }

    @Test
    public void testRabbitMessagingTemplateBackOff() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration4.class).run(( context) -> {
            RabbitMessagingTemplate messagingTemplate = context.getBean(.class);
            assertThat(messagingTemplate.getDefaultDestination()).isEqualTo("fooBar");
        });
    }

    @Test
    public void testStaticQueues() {
        // There should NOT be an AmqpAdmin bean when dynamic is switch to false
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.dynamic:false").run(( context) -> assertThatExceptionOfType(.class).isThrownBy(() -> context.getBean(.class)).withMessageContaining((("No qualifying bean of type '" + (.class.getName())) + "'")));
    }

    @Test
    public void testEnableRabbitCreateDefaultContainerFactory() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.EnableRabbitConfiguration.class).run(( context) -> {
            RabbitListenerContainerFactory<?> rabbitListenerContainerFactory = context.getBean("rabbitListenerContainerFactory", .class);
            assertThat(rabbitListenerContainerFactory.getClass()).isEqualTo(.class);
        });
    }

    @Test
    public void testRabbitListenerContainerFactoryBackOff() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration5.class).run(( context) -> {
            SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory = context.getBean("rabbitListenerContainerFactory", .class);
            rabbitListenerContainerFactory.setTxSize(10);
            verify(rabbitListenerContainerFactory).setTxSize(10);
            assertThat(rabbitListenerContainerFactory.getAdviceChain()).isNull();
        });
    }

    @Test
    public void testSimpleRabbitListenerContainerFactoryWithCustomSettings() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.MessageConvertersConfiguration.class, RabbitAutoConfigurationTests.MessageRecoverersConfiguration.class).withPropertyValues("spring.rabbitmq.listener.simple.retry.enabled:true", "spring.rabbitmq.listener.simple.retry.maxAttempts:4", "spring.rabbitmq.listener.simple.retry.initialInterval:2000", "spring.rabbitmq.listener.simple.retry.multiplier:1.5", "spring.rabbitmq.listener.simple.retry.maxInterval:5000", "spring.rabbitmq.listener.simple.autoStartup:false", "spring.rabbitmq.listener.simple.acknowledgeMode:manual", "spring.rabbitmq.listener.simple.concurrency:5", "spring.rabbitmq.listener.simple.maxConcurrency:10", "spring.rabbitmq.listener.simple.prefetch:40", "spring.rabbitmq.listener.simple.defaultRequeueRejected:false", "spring.rabbitmq.listener.simple.idleEventInterval:5", "spring.rabbitmq.listener.simple.transactionSize:20", "spring.rabbitmq.listener.simple.missingQueuesFatal:false").run(( context) -> {
            SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory = context.getBean("rabbitListenerContainerFactory", .class);
            assertThat(rabbitListenerContainerFactory).hasFieldOrPropertyWithValue("concurrentConsumers", 5);
            assertThat(rabbitListenerContainerFactory).hasFieldOrPropertyWithValue("maxConcurrentConsumers", 10);
            assertThat(rabbitListenerContainerFactory).hasFieldOrPropertyWithValue("txSize", 20);
            assertThat(rabbitListenerContainerFactory).hasFieldOrPropertyWithValue("missingQueuesFatal", false);
            checkCommonProps(context, rabbitListenerContainerFactory);
        });
    }

    @Test
    public void testDirectRabbitListenerContainerFactoryWithCustomSettings() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.MessageConvertersConfiguration.class, RabbitAutoConfigurationTests.MessageRecoverersConfiguration.class).withPropertyValues("spring.rabbitmq.listener.type:direct", "spring.rabbitmq.listener.direct.retry.enabled:true", "spring.rabbitmq.listener.direct.retry.maxAttempts:4", "spring.rabbitmq.listener.direct.retry.initialInterval:2000", "spring.rabbitmq.listener.direct.retry.multiplier:1.5", "spring.rabbitmq.listener.direct.retry.maxInterval:5000", "spring.rabbitmq.listener.direct.autoStartup:false", "spring.rabbitmq.listener.direct.acknowledgeMode:manual", "spring.rabbitmq.listener.direct.consumers-per-queue:5", "spring.rabbitmq.listener.direct.prefetch:40", "spring.rabbitmq.listener.direct.defaultRequeueRejected:false", "spring.rabbitmq.listener.direct.idleEventInterval:5", "spring.rabbitmq.listener.direct.missingQueuesFatal:true").run(( context) -> {
            DirectRabbitListenerContainerFactory rabbitListenerContainerFactory = context.getBean("rabbitListenerContainerFactory", .class);
            assertThat(rabbitListenerContainerFactory).hasFieldOrPropertyWithValue("consumersPerQueue", 5);
            assertThat(rabbitListenerContainerFactory).hasFieldOrPropertyWithValue("missingQueuesFatal", true);
            checkCommonProps(context, rabbitListenerContainerFactory);
        });
    }

    @Test
    public void testSimpleRabbitListenerContainerFactoryRetryWithCustomizer() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.RabbitRetryTemplateCustomizerConfiguration.class).withPropertyValues("spring.rabbitmq.listener.simple.retry.enabled:true", "spring.rabbitmq.listener.simple.retry.maxAttempts:4").run(( context) -> {
            SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory = context.getBean("rabbitListenerContainerFactory", .class);
            assertListenerRetryTemplate(rabbitListenerContainerFactory, context.getBean(.class).retryPolicy);
        });
    }

    @Test
    public void testDirectRabbitListenerContainerFactoryRetryWithCustomizer() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.RabbitRetryTemplateCustomizerConfiguration.class).withPropertyValues("spring.rabbitmq.listener.type:direct", "spring.rabbitmq.listener.direct.retry.enabled:true", "spring.rabbitmq.listener.direct.retry.maxAttempts:4").run(( context) -> {
            DirectRabbitListenerContainerFactory rabbitListenerContainerFactory = context.getBean("rabbitListenerContainerFactory", .class);
            assertListenerRetryTemplate(rabbitListenerContainerFactory, context.getBean(.class).retryPolicy);
        });
    }

    @Test
    public void testRabbitListenerContainerFactoryConfigurersAreAvailable() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.listener.simple.concurrency:5", "spring.rabbitmq.listener.simple.maxConcurrency:10", "spring.rabbitmq.listener.simple.prefetch:40", "spring.rabbitmq.listener.direct.consumers-per-queue:5", "spring.rabbitmq.listener.direct.prefetch:40").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void testSimpleRabbitListenerContainerFactoryConfigurerUsesConfig() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.listener.type:direct", "spring.rabbitmq.listener.simple.concurrency:5", "spring.rabbitmq.listener.simple.maxConcurrency:10", "spring.rabbitmq.listener.simple.prefetch:40").run(( context) -> {
            SimpleRabbitListenerContainerFactoryConfigurer configurer = context.getBean(.class);
            SimpleRabbitListenerContainerFactory factory = mock(.class);
            configurer.configure(factory, mock(.class));
            verify(factory).setConcurrentConsumers(5);
            verify(factory).setMaxConcurrentConsumers(10);
            verify(factory).setPrefetchCount(40);
        });
    }

    @Test
    public void testDirectRabbitListenerContainerFactoryConfigurerUsesConfig() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.listener.type:simple", "spring.rabbitmq.listener.direct.consumers-per-queue:5", "spring.rabbitmq.listener.direct.prefetch:40").run(( context) -> {
            DirectRabbitListenerContainerFactoryConfigurer configurer = context.getBean(.class);
            DirectRabbitListenerContainerFactory factory = mock(.class);
            configurer.configure(factory, mock(.class));
            verify(factory).setConsumersPerQueue(5);
            verify(factory).setPrefetchCount(40);
        });
    }

    @Test
    public void enableRabbitAutomatically() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.NoEnableRabbitConfiguration.class).run(( context) -> {
            assertThat(context).hasBean(RabbitListenerConfigUtils.RABBIT_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME);
            assertThat(context).hasBean(RabbitListenerConfigUtils.RABBIT_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME);
        });
    }

    @Test
    public void customizeRequestedHeartBeat() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.requestedHeartbeat:20").run(( context) -> {
            ConnectionFactory rabbitConnectionFactory = getTargetConnectionFactory(context);
            assertThat(rabbitConnectionFactory.getRequestedHeartbeat()).isEqualTo(20);
        });
    }

    @Test
    public void noSslByDefault() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).run(( context) -> {
            ConnectionFactory rabbitConnectionFactory = getTargetConnectionFactory(context);
            assertThat(rabbitConnectionFactory.getSocketFactory()).isNull();
            assertThat(rabbitConnectionFactory.isSSL()).isFalse();
        });
    }

    @Test
    public void enableSsl() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.ssl.enabled:true").run(( context) -> {
            ConnectionFactory rabbitConnectionFactory = getTargetConnectionFactory(context);
            assertThat(rabbitConnectionFactory.isSSL()).isTrue();
            assertThat(rabbitConnectionFactory.getSocketFactory()).as("SocketFactory must use SSL").isInstanceOf(.class);
        });
    }

    // Make sure that we at least attempt to load the store
    @Test
    public void enableSslWithNonExistingKeystoreShouldFail() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.ssl.enabled:true", "spring.rabbitmq.ssl.keyStore=foo", "spring.rabbitmq.ssl.keyStorePassword=secret").run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context).getFailure().hasMessageContaining("foo");
            assertThat(context).getFailure().hasMessageContaining("does not exist");
        });
    }

    // Make sure that we at least attempt to load the store
    @Test
    public void enableSslWithNonExistingTrustStoreShouldFail() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.ssl.enabled:true", "spring.rabbitmq.ssl.trustStore=bar", "spring.rabbitmq.ssl.trustStorePassword=secret").run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context).getFailure().hasMessageContaining("bar");
            assertThat(context).getFailure().hasMessageContaining("does not exist");
        });
    }

    @Test
    public void enableSslWithInvalidKeystoreTypeShouldFail() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.ssl.enabled:true", "spring.rabbitmq.ssl.keyStore=foo", "spring.rabbitmq.ssl.keyStoreType=fooType").run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context).getFailure().hasMessageContaining("fooType");
            assertThat(context).getFailure().hasRootCauseInstanceOf(.class);
        });
    }

    @Test
    public void enableSslWithInvalidTrustStoreTypeShouldFail() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.ssl.enabled:true", "spring.rabbitmq.ssl.trustStore=bar", "spring.rabbitmq.ssl.trustStoreType=barType").run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context).getFailure().hasMessageContaining("barType");
            assertThat(context).getFailure().hasRootCauseInstanceOf(.class);
        });
    }

    @Test
    public void enableSslWithKeystoreTypeAndTrustStoreTypeShouldWork() {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.ssl.enabled:true", "spring.rabbitmq.ssl.keyStore=/org/springframework/boot/autoconfigure/amqp/test.jks", "spring.rabbitmq.ssl.keyStoreType=jks", "spring.rabbitmq.ssl.keyStorePassword=secret", "spring.rabbitmq.ssl.trustStore=/org/springframework/boot/autoconfigure/amqp/test.jks", "spring.rabbitmq.ssl.trustStoreType=jks", "spring.rabbitmq.ssl.trustStorePassword=secret").run(( context) -> assertThat(context).hasNotFailed());
    }

    @Test
    public void enableSslWithValidateServerCertificateFalse() throws Exception {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.ssl.enabled:true", "spring.rabbitmq.ssl.validateServerCertificate=false").run(( context) -> {
            ConnectionFactory rabbitConnectionFactory = getTargetConnectionFactory(context);
            TrustManager trustManager = getTrustManager(rabbitConnectionFactory);
            assertThat(trustManager).isInstanceOf(.class);
        });
    }

    @Test
    public void enableSslWithValidateServerCertificateDefault() throws Exception {
        this.contextRunner.withUserConfiguration(RabbitAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.rabbitmq.ssl.enabled:true").run(( context) -> {
            ConnectionFactory rabbitConnectionFactory = getTargetConnectionFactory(context);
            TrustManager trustManager = getTrustManager(rabbitConnectionFactory);
            assertThat(trustManager).isNotInstanceOf(.class);
        });
    }

    @Configuration
    protected static class TestConfiguration {}

    @Configuration
    protected static class TestConfiguration2 {
        @Bean
        org.springframework.amqp.rabbit.connection.ConnectionFactory aDifferentConnectionFactory() {
            return new CachingConnectionFactory("otherserver", 8001);
        }
    }

    @Configuration
    protected static class TestConfiguration3 {
        @Bean
        RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setMessageConverter(testMessageConverter());
            return rabbitTemplate;
        }

        @Bean
        public MessageConverter testMessageConverter() {
            return Mockito.mock(MessageConverter.class);
        }
    }

    @Configuration
    protected static class TestConfiguration4 {
        @Bean
        RabbitMessagingTemplate messagingTemplate(RabbitTemplate rabbitTemplate) {
            RabbitMessagingTemplate messagingTemplate = new RabbitMessagingTemplate(rabbitTemplate);
            messagingTemplate.setDefaultDestination("fooBar");
            return messagingTemplate;
        }
    }

    @Configuration
    protected static class TestConfiguration5 {
        @Bean
        org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory<?> rabbitListenerContainerFactory() {
            return Mockito.mock(SimpleRabbitListenerContainerFactory.class);
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
    protected static class MessageRecoverersConfiguration {
        @Bean
        @Primary
        public MessageRecoverer myMessageRecoverer() {
            return Mockito.mock(MessageRecoverer.class);
        }

        @Bean
        public MessageRecoverer anotherMessageRecoverer() {
            return Mockito.mock(MessageRecoverer.class);
        }
    }

    @Configuration
    protected static class ConnectionNameStrategyConfiguration {
        private final AtomicInteger counter = new AtomicInteger();

        @Bean
        public ConnectionNameStrategy myConnectionNameStrategy() {
            return ( connectionFactory) -> "test#" + (this.counter.getAndIncrement());
        }
    }

    @Configuration
    protected static class RabbitRetryTemplateCustomizerConfiguration {
        private final BackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();

        private final RetryPolicy retryPolicy = new NeverRetryPolicy();

        @Bean
        public RabbitRetryTemplateCustomizer rabbitTemplateRetryTemplateCustomizer() {
            return ( target, template) -> {
                if (target.equals(RabbitRetryTemplateCustomizer.Target.SENDER)) {
                    template.setBackOffPolicy(this.backOffPolicy);
                }
            };
        }

        @Bean
        public RabbitRetryTemplateCustomizer rabbitListenerRetryTemplateCustomizer() {
            return ( target, template) -> {
                if (target.equals(RabbitRetryTemplateCustomizer.Target.LISTENER)) {
                    template.setRetryPolicy(this.retryPolicy);
                }
            };
        }
    }

    @Configuration
    @EnableRabbit
    protected static class EnableRabbitConfiguration {}

    @Configuration
    protected static class NoEnableRabbitConfiguration {}
}

