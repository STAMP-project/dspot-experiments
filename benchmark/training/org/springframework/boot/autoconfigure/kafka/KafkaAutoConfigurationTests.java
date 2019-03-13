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
package org.springframework.boot.autoconfigure.kafka;


import StreamsConfig.APPLICATION_ID_CONFIG;
import StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.AfterRollbackProcessor;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.security.jaas.KafkaJaasLoginModuleInitializer;
import org.springframework.kafka.support.converter.BatchMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Tests for {@link KafkaAutoConfiguration}.
 *
 * @author Gary Russell
 * @author Stephane Nicoll
 * @author Edd? Mel?ndez
 * @author Nakul Mishra
 */
public class KafkaAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(KafkaAutoConfiguration.class));

    @Test
    public void consumerProperties() {
        // test override common
        this.contextRunner.withPropertyValues("spring.kafka.bootstrap-servers=foo:1234", "spring.kafka.properties.foo=bar", "spring.kafka.properties.baz=qux", "spring.kafka.properties.foo.bar.baz=qux.fiz.buz", "spring.kafka.ssl.key-password=p1", "spring.kafka.ssl.key-store-location=classpath:ksLoc", "spring.kafka.ssl.key-store-password=p2", "spring.kafka.ssl.key-store-type=PKCS12", "spring.kafka.ssl.trust-store-location=classpath:tsLoc", "spring.kafka.ssl.trust-store-password=p3", "spring.kafka.ssl.trust-store-type=PKCS12", "spring.kafka.ssl.protocol=TLSv1.2", "spring.kafka.consumer.auto-commit-interval=123", "spring.kafka.consumer.max-poll-records=42", "spring.kafka.consumer.auto-offset-reset=earliest", "spring.kafka.consumer.client-id=ccid", "spring.kafka.consumer.enable-auto-commit=false", "spring.kafka.consumer.fetch-max-wait=456", "spring.kafka.consumer.properties.fiz.buz=fix.fox", "spring.kafka.consumer.fetch-min-size=1KB", "spring.kafka.consumer.group-id=bar", "spring.kafka.consumer.heartbeat-interval=234", "spring.kafka.consumer.key-deserializer = org.apache.kafka.common.serialization.LongDeserializer", "spring.kafka.consumer.value-deserializer = org.apache.kafka.common.serialization.IntegerDeserializer").run(( context) -> {
            DefaultKafkaConsumerFactory<?, ?> consumerFactory = context.getBean(.class);
            Map<String, Object> configs = consumerFactory.getConfigurationProperties();
            // common
            assertThat(configs.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG)).isEqualTo(Collections.singletonList("foo:1234"));
            assertThat(configs.get(SslConfigs.SSL_KEY_PASSWORD_CONFIG)).isEqualTo("p1");
            assertThat(((String) (configs.get(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG)))).endsWith((File.separator + "ksLoc"));
            assertThat(configs.get(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG)).isEqualTo("p2");
            assertThat(configs.get(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG)).isEqualTo("PKCS12");
            assertThat(((String) (configs.get(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG)))).endsWith((File.separator + "tsLoc"));
            assertThat(configs.get(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG)).isEqualTo("p3");
            assertThat(configs.get(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG)).isEqualTo("PKCS12");
            assertThat(configs.get(SslConfigs.SSL_PROTOCOL_CONFIG)).isEqualTo("TLSv1.2");
            // consumer
            assertThat(configs.get(ConsumerConfig.CLIENT_ID_CONFIG)).isEqualTo("ccid");// override

            assertThat(configs.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG)).isEqualTo(Boolean.FALSE);
            assertThat(configs.get(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG)).isEqualTo(123);
            assertThat(configs.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG)).isEqualTo("earliest");
            assertThat(configs.get(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG)).isEqualTo(456);
            assertThat(configs.get(ConsumerConfig.FETCH_MIN_BYTES_CONFIG)).isEqualTo(1024);
            assertThat(configs.get(ConsumerConfig.GROUP_ID_CONFIG)).isEqualTo("bar");
            assertThat(configs.get(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG)).isEqualTo(234);
            assertThat(configs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG)).isEqualTo(.class);
            assertThat(configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG)).isEqualTo(.class);
            assertThat(configs.get(ConsumerConfig.MAX_POLL_RECORDS_CONFIG)).isEqualTo(42);
            assertThat(configs.get("foo")).isEqualTo("bar");
            assertThat(configs.get("baz")).isEqualTo("qux");
            assertThat(configs.get("foo.bar.baz")).isEqualTo("qux.fiz.buz");
            assertThat(configs.get("fiz.buz")).isEqualTo("fix.fox");
        });
    }

    @Test
    public void producerProperties() {
        // test
        // override
        this.contextRunner.withPropertyValues("spring.kafka.clientId=cid", "spring.kafka.properties.foo.bar.baz=qux.fiz.buz", "spring.kafka.producer.acks=all", "spring.kafka.producer.batch-size=2KB", "spring.kafka.producer.bootstrap-servers=bar:1234", "spring.kafka.producer.buffer-memory=4KB", "spring.kafka.producer.compression-type=gzip", "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.LongSerializer", "spring.kafka.producer.retries=2", "spring.kafka.producer.properties.fiz.buz=fix.fox", "spring.kafka.producer.ssl.key-password=p4", "spring.kafka.producer.ssl.key-store-location=classpath:ksLocP", "spring.kafka.producer.ssl.key-store-password=p5", "spring.kafka.producer.ssl.key-store-type=PKCS12", "spring.kafka.producer.ssl.trust-store-location=classpath:tsLocP", "spring.kafka.producer.ssl.trust-store-password=p6", "spring.kafka.producer.ssl.trust-store-type=PKCS12", "spring.kafka.producer.ssl.protocol=TLSv1.2", "spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.IntegerSerializer").run(( context) -> {
            DefaultKafkaProducerFactory<?, ?> producerFactory = context.getBean(.class);
            Map<String, Object> configs = producerFactory.getConfigurationProperties();
            // common
            assertThat(configs.get(ProducerConfig.CLIENT_ID_CONFIG)).isEqualTo("cid");
            // producer
            assertThat(configs.get(ProducerConfig.ACKS_CONFIG)).isEqualTo("all");
            assertThat(configs.get(ProducerConfig.BATCH_SIZE_CONFIG)).isEqualTo(2048);
            assertThat(configs.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)).isEqualTo(Collections.singletonList("bar:1234"));// override

            assertThat(configs.get(ProducerConfig.BUFFER_MEMORY_CONFIG)).isEqualTo(4096L);
            assertThat(configs.get(ProducerConfig.COMPRESSION_TYPE_CONFIG)).isEqualTo("gzip");
            assertThat(configs.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG)).isEqualTo(.class);
            assertThat(configs.get(SslConfigs.SSL_KEY_PASSWORD_CONFIG)).isEqualTo("p4");
            assertThat(((String) (configs.get(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG)))).endsWith((File.separator + "ksLocP"));
            assertThat(configs.get(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG)).isEqualTo("p5");
            assertThat(configs.get(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG)).isEqualTo("PKCS12");
            assertThat(((String) (configs.get(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG)))).endsWith((File.separator + "tsLocP"));
            assertThat(configs.get(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG)).isEqualTo("p6");
            assertThat(configs.get(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG)).isEqualTo("PKCS12");
            assertThat(configs.get(SslConfigs.SSL_PROTOCOL_CONFIG)).isEqualTo("TLSv1.2");
            assertThat(configs.get(ProducerConfig.RETRIES_CONFIG)).isEqualTo(2);
            assertThat(configs.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG)).isEqualTo(.class);
            assertThat(context.getBeansOfType(.class)).isEmpty();
            assertThat(context.getBeansOfType(.class)).isEmpty();
            assertThat(configs.get("foo.bar.baz")).isEqualTo("qux.fiz.buz");
            assertThat(configs.get("fiz.buz")).isEqualTo("fix.fox");
        });
    }

    @Test
    public void adminProperties() {
        this.contextRunner.withPropertyValues("spring.kafka.clientId=cid", "spring.kafka.properties.foo.bar.baz=qux.fiz.buz", "spring.kafka.admin.fail-fast=true", "spring.kafka.admin.properties.fiz.buz=fix.fox", "spring.kafka.admin.ssl.key-password=p4", "spring.kafka.admin.ssl.key-store-location=classpath:ksLocP", "spring.kafka.admin.ssl.key-store-password=p5", "spring.kafka.admin.ssl.key-store-type=PKCS12", "spring.kafka.admin.ssl.trust-store-location=classpath:tsLocP", "spring.kafka.admin.ssl.trust-store-password=p6", "spring.kafka.admin.ssl.trust-store-type=PKCS12", "spring.kafka.admin.ssl.protocol=TLSv1.2").run(( context) -> {
            KafkaAdmin admin = context.getBean(.class);
            Map<String, Object> configs = admin.getConfig();
            // common
            assertThat(configs.get(AdminClientConfig.CLIENT_ID_CONFIG)).isEqualTo("cid");
            // admin
            assertThat(configs.get(SslConfigs.SSL_KEY_PASSWORD_CONFIG)).isEqualTo("p4");
            assertThat(((String) (configs.get(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG)))).endsWith((File.separator + "ksLocP"));
            assertThat(configs.get(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG)).isEqualTo("p5");
            assertThat(configs.get(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG)).isEqualTo("PKCS12");
            assertThat(((String) (configs.get(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG)))).endsWith((File.separator + "tsLocP"));
            assertThat(configs.get(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG)).isEqualTo("p6");
            assertThat(configs.get(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG)).isEqualTo("PKCS12");
            assertThat(configs.get(SslConfigs.SSL_PROTOCOL_CONFIG)).isEqualTo("TLSv1.2");
            assertThat(context.getBeansOfType(.class)).isEmpty();
            assertThat(configs.get("foo.bar.baz")).isEqualTo("qux.fiz.buz");
            assertThat(configs.get("fiz.buz")).isEqualTo("fix.fox");
            assertThat(KafkaTestUtils.getPropertyValue(admin, "fatalIfBrokerNotAvailable", .class)).isTrue();
        });
    }

    @Test
    public void streamsProperties() {
        this.contextRunner.withUserConfiguration(KafkaAutoConfigurationTests.EnableKafkaStreamsConfiguration.class).withPropertyValues("spring.kafka.client-id=cid", "spring.kafka.bootstrap-servers=localhost:9092,localhost:9093", "spring.application.name=appName", "spring.kafka.properties.foo.bar.baz=qux.fiz.buz", "spring.kafka.streams.auto-startup=false", "spring.kafka.streams.cache-max-size-buffering=1KB", "spring.kafka.streams.client-id=override", "spring.kafka.streams.properties.fiz.buz=fix.fox", "spring.kafka.streams.replication-factor=2", "spring.kafka.streams.state-dir=/tmp/state", "spring.kafka.streams.ssl.key-password=p7", "spring.kafka.streams.ssl.key-store-location=classpath:ksLocP", "spring.kafka.streams.ssl.key-store-password=p8", "spring.kafka.streams.ssl.key-store-type=PKCS12", "spring.kafka.streams.ssl.trust-store-location=classpath:tsLocP", "spring.kafka.streams.ssl.trust-store-password=p9", "spring.kafka.streams.ssl.trust-store-type=PKCS12", "spring.kafka.streams.ssl.protocol=TLSv1.2").run(( context) -> {
            Properties configs = context.getBean(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME, .class).asProperties();
            assertThat(configs.get(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG)).isEqualTo("localhost:9092, localhost:9093");
            assertThat(configs.get(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG)).isEqualTo("1024");
            assertThat(configs.get(StreamsConfig.CLIENT_ID_CONFIG)).isEqualTo("override");
            assertThat(configs.get(StreamsConfig.REPLICATION_FACTOR_CONFIG)).isEqualTo("2");
            assertThat(configs.get(StreamsConfig.STATE_DIR_CONFIG)).isEqualTo("/tmp/state");
            assertThat(configs.get(SslConfigs.SSL_KEY_PASSWORD_CONFIG)).isEqualTo("p7");
            assertThat(((String) (configs.get(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG)))).endsWith((File.separator + "ksLocP"));
            assertThat(configs.get(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG)).isEqualTo("p8");
            assertThat(configs.get(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG)).isEqualTo("PKCS12");
            assertThat(((String) (configs.get(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG)))).endsWith((File.separator + "tsLocP"));
            assertThat(configs.get(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG)).isEqualTo("p9");
            assertThat(configs.get(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG)).isEqualTo("PKCS12");
            assertThat(configs.get(SslConfigs.SSL_PROTOCOL_CONFIG)).isEqualTo("TLSv1.2");
            assertThat(context.getBeansOfType(.class)).isEmpty();
            assertThat(configs.get("foo.bar.baz")).isEqualTo("qux.fiz.buz");
            assertThat(configs.get("fiz.buz")).isEqualTo("fix.fox");
            assertThat(context.getBean(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_BUILDER_BEAN_NAME)).isNotNull();
        });
    }

    @Test
    public void streamsApplicationIdUsesMainApplicationNameByDefault() {
        this.contextRunner.withUserConfiguration(KafkaAutoConfigurationTests.EnableKafkaStreamsConfiguration.class).withPropertyValues("spring.application.name=my-test-app", "spring.kafka.bootstrap-servers=localhost:9092,localhost:9093", "spring.kafka.streams.auto-startup=false").run(( context) -> {
            Properties configs = context.getBean(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME, .class).asProperties();
            assertThat(configs.get(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG)).isEqualTo("localhost:9092, localhost:9093");
            assertThat(configs.get(StreamsConfig.APPLICATION_ID_CONFIG)).isEqualTo("my-test-app");
        });
    }

    @Test
    public void streamsWithCustomKafkaConfiguration() {
        this.contextRunner.withUserConfiguration(KafkaAutoConfigurationTests.EnableKafkaStreamsConfiguration.class, KafkaAutoConfigurationTests.TestKafkaStreamsConfiguration.class).withPropertyValues("spring.application.name=my-test-app", "spring.kafka.bootstrap-servers=localhost:9092,localhost:9093", "spring.kafka.streams.auto-startup=false").run(( context) -> {
            Properties configs = context.getBean(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME, .class).asProperties();
            assertThat(configs.get(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG)).isEqualTo("localhost:9094, localhost:9095");
            assertThat(configs.get(StreamsConfig.APPLICATION_ID_CONFIG)).isEqualTo("test-id");
        });
    }

    @Test
    public void streamsApplicationIdIsMandatory() {
        this.contextRunner.withUserConfiguration(KafkaAutoConfigurationTests.EnableKafkaStreamsConfiguration.class).run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context).getFailure().hasMessageContaining("spring.kafka.streams.application-id").hasMessageContaining("This property is mandatory and fallback 'spring.application.name' is not set either.");
        });
    }

    @Test
    public void streamsApplicationIdIsNotMandatoryIfEnableKafkaStreamsIsNotSet() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void listenerProperties() {
        this.contextRunner.withPropertyValues("spring.kafka.template.default-topic=testTopic", "spring.kafka.listener.ack-mode=MANUAL", "spring.kafka.listener.client-id=client", "spring.kafka.listener.ack-count=123", "spring.kafka.listener.ack-time=456", "spring.kafka.listener.concurrency=3", "spring.kafka.listener.poll-timeout=2000", "spring.kafka.listener.no-poll-threshold=2.5", "spring.kafka.listener.type=batch", "spring.kafka.listener.idle-event-interval=1s", "spring.kafka.listener.monitor-interval=45", "spring.kafka.listener.log-container-config=true", "spring.kafka.jaas.enabled=true", "spring.kafka.producer.transaction-id-prefix=foo", "spring.kafka.jaas.login-module=foo", "spring.kafka.jaas.control-flag=REQUISITE", "spring.kafka.jaas.options.useKeyTab=true").run(( context) -> {
            DefaultKafkaProducerFactory<?, ?> producerFactory = context.getBean(.class);
            DefaultKafkaConsumerFactory<?, ?> consumerFactory = context.getBean(.class);
            KafkaTemplate<?, ?> kafkaTemplate = context.getBean(.class);
            AbstractKafkaListenerContainerFactory<?, ?, ?> kafkaListenerContainerFactory = ((AbstractKafkaListenerContainerFactory<?, ?, ?>) (context.getBean(.class)));
            assertThat(kafkaTemplate.getMessageConverter()).isInstanceOf(.class);
            assertThat(kafkaTemplate).hasFieldOrPropertyWithValue("producerFactory", producerFactory);
            assertThat(kafkaTemplate.getDefaultTopic()).isEqualTo("testTopic");
            assertThat(kafkaListenerContainerFactory.getConsumerFactory()).isEqualTo(consumerFactory);
            ContainerProperties containerProperties = kafkaListenerContainerFactory.getContainerProperties();
            assertThat(containerProperties.getAckMode()).isEqualTo(AckMode.MANUAL);
            assertThat(containerProperties.getClientId()).isEqualTo("client");
            assertThat(containerProperties.getAckCount()).isEqualTo(123);
            assertThat(containerProperties.getAckTime()).isEqualTo(456L);
            assertThat(containerProperties.getPollTimeout()).isEqualTo(2000L);
            assertThat(containerProperties.getNoPollThreshold()).isEqualTo(2.5F);
            assertThat(containerProperties.getIdleEventInterval()).isEqualTo(1000L);
            assertThat(containerProperties.getMonitorInterval()).isEqualTo(45);
            assertThat(containerProperties.isLogContainerConfig()).isTrue();
            assertThat(ReflectionTestUtils.getField(kafkaListenerContainerFactory, "concurrency")).isEqualTo(3);
            assertThat(kafkaListenerContainerFactory.isBatchListener()).isTrue();
            assertThat(context.getBeansOfType(.class)).hasSize(1);
            KafkaJaasLoginModuleInitializer jaas = context.getBean(.class);
            assertThat(jaas).hasFieldOrPropertyWithValue("loginModule", "foo");
            assertThat(jaas).hasFieldOrPropertyWithValue("controlFlag", AppConfigurationEntry.LoginModuleControlFlag.REQUISITE);
            assertThat(context.getBeansOfType(.class)).hasSize(1);
            assertThat(((Map<String, String>) (ReflectionTestUtils.getField(jaas, "options")))).containsExactly(entry("useKeyTab", "true"));
        });
    }

    @Test
    public void testKafkaTemplateRecordMessageConverters() {
        this.contextRunner.withUserConfiguration(KafkaAutoConfigurationTests.MessageConverterConfiguration.class).withPropertyValues("spring.kafka.producer.transaction-id-prefix=test").run(( context) -> {
            KafkaTemplate<?, ?> kafkaTemplate = context.getBean(.class);
            assertThat(kafkaTemplate.getMessageConverter()).isSameAs(context.getBean("myMessageConverter"));
        });
    }

    @Test
    public void testConcurrentKafkaListenerContainerFactoryWithCustomMessageConverter() {
        this.contextRunner.withUserConfiguration(KafkaAutoConfigurationTests.MessageConverterConfiguration.class).run(( context) -> {
            ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory = context.getBean(.class);
            assertThat(kafkaListenerContainerFactory).hasFieldOrPropertyWithValue("messageConverter", context.getBean("myMessageConverter"));
        });
    }

    @Test
    public void testConcurrentKafkaListenerContainerFactoryInBatchModeWithCustomMessageConverter() {
        this.contextRunner.withUserConfiguration(KafkaAutoConfigurationTests.BatchMessageConverterConfiguration.class, KafkaAutoConfigurationTests.MessageConverterConfiguration.class).withPropertyValues("spring.kafka.listener.type=batch").run(( context) -> {
            ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory = context.getBean(.class);
            assertThat(kafkaListenerContainerFactory).hasFieldOrPropertyWithValue("messageConverter", context.getBean("myBatchMessageConverter"));
        });
    }

    @Test
    public void testConcurrentKafkaListenerContainerFactoryInBatchModeWrapsCustomMessageConverter() {
        this.contextRunner.withUserConfiguration(KafkaAutoConfigurationTests.MessageConverterConfiguration.class).withPropertyValues("spring.kafka.listener.type=batch").run(( context) -> {
            ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory = context.getBean(.class);
            Object messageConverter = ReflectionTestUtils.getField(kafkaListenerContainerFactory, "messageConverter");
            assertThat(messageConverter).isInstanceOf(.class);
            assertThat(((BatchMessageConverter) (messageConverter)).getRecordMessageConverter()).isSameAs(context.getBean("myMessageConverter"));
        });
    }

    @Test
    public void testConcurrentKafkaListenerContainerFactoryInBatchModeWithNoMessageConverter() {
        this.contextRunner.withPropertyValues("spring.kafka.listener.type=batch").run(( context) -> {
            ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory = context.getBean(.class);
            Object messageConverter = ReflectionTestUtils.getField(kafkaListenerContainerFactory, "messageConverter");
            assertThat(messageConverter).isInstanceOf(.class);
            assertThat(((BatchMessageConverter) (messageConverter)).getRecordMessageConverter()).isNull();
        });
    }

    @Test
    public void testConcurrentKafkaListenerContainerFactoryWithCustomErrorHandler() {
        this.contextRunner.withUserConfiguration(KafkaAutoConfigurationTests.ErrorHandlerConfiguration.class).run(( context) -> {
            ConcurrentKafkaListenerContainerFactory<?, ?> factory = context.getBean(.class);
            assertThat(KafkaTestUtils.getPropertyValue(factory, "errorHandler")).isSameAs(context.getBean("errorHandler"));
        });
    }

    @Test
    public void testConcurrentKafkaListenerContainerFactoryWithDefaultTransactionManager() {
        this.contextRunner.withPropertyValues("spring.kafka.producer.transaction-id-prefix=test").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            ConcurrentKafkaListenerContainerFactory<?, ?> factory = context.getBean(.class);
            assertThat(factory.getContainerProperties().getTransactionManager()).isSameAs(context.getBean(.class));
        });
    }

    @Test
    public void testConcurrentKafkaListenerContainerFactoryWithCustomTransactionManager() {
        this.contextRunner.withUserConfiguration(KafkaAutoConfigurationTests.TransactionManagerConfiguration.class).withPropertyValues("spring.kafka.producer.transaction-id-prefix=test").run(( context) -> {
            ConcurrentKafkaListenerContainerFactory<?, ?> factory = context.getBean(.class);
            assertThat(factory.getContainerProperties().getTransactionManager()).isSameAs(context.getBean("chainedTransactionManager"));
        });
    }

    @Test
    public void testConcurrentKafkaListenerContainerFactoryWithCustomAfterRollbackProcessor() {
        this.contextRunner.withUserConfiguration(KafkaAutoConfigurationTests.AfterRollbackProcessorConfiguration.class).run(( context) -> {
            ConcurrentKafkaListenerContainerFactory<?, ?> factory = context.getBean(.class);
            assertThat(factory).hasFieldOrPropertyWithValue("afterRollbackProcessor", context.getBean("afterRollbackProcessor"));
        });
    }

    @Test
    public void testConcurrentKafkaListenerContainerFactoryWithKafkaTemplate() {
        this.contextRunner.run(( context) -> {
            ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory = context.getBean(.class);
            assertThat(kafkaListenerContainerFactory).hasFieldOrPropertyWithValue("replyTemplate", context.getBean(.class));
        });
    }

    @Configuration
    protected static class MessageConverterConfiguration {
        @Bean
        public RecordMessageConverter myMessageConverter() {
            return Mockito.mock(RecordMessageConverter.class);
        }
    }

    @Configuration
    protected static class BatchMessageConverterConfiguration {
        @Bean
        public BatchMessageConverter myBatchMessageConverter() {
            return Mockito.mock(BatchMessageConverter.class);
        }
    }

    @Configuration
    protected static class ErrorHandlerConfiguration {
        @Bean
        public SeekToCurrentErrorHandler errorHandler() {
            return new SeekToCurrentErrorHandler();
        }
    }

    @Configuration
    protected static class TransactionManagerConfiguration {
        @Bean
        @Primary
        public PlatformTransactionManager chainedTransactionManager(KafkaTransactionManager<String, String> kafkaTransactionManager) {
            return new org.springframework.kafka.transaction.ChainedKafkaTransactionManager<String, String>(kafkaTransactionManager);
        }
    }

    @Configuration
    protected static class AfterRollbackProcessorConfiguration {
        @Bean
        public AfterRollbackProcessor<Object, Object> afterRollbackProcessor() {
            return ( records, consumer, ex, recoverable) -> {
                // no-op
            };
        }
    }

    @Configuration
    @EnableKafkaStreams
    protected static class EnableKafkaStreamsConfiguration {}

    @Configuration
    protected static class TestKafkaStreamsConfiguration {
        @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
        public KafkaStreamsConfiguration kafkaStreamsConfiguration() {
            java.util.Map<String, Object> streamsProperties = new HashMap<>();
            streamsProperties.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9094, localhost:9095");
            streamsProperties.put(APPLICATION_ID_CONFIG, "test-id");
            return new KafkaStreamsConfiguration(streamsProperties);
        }
    }
}

