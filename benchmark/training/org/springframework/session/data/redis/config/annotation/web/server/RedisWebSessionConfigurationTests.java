/**
 * Copyright 2014-2018 the original author or authors.
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
package org.springframework.session.data.redis.config.annotation.web.server;


import RedisFlushMode.IMMEDIATE;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisOperations;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link RedisWebSessionConfiguration}.
 *
 * @author Vedran Pavic
 */
public class RedisWebSessionConfigurationTests {
    private static final String REDIS_NAMESPACE = "testNamespace";

    private static final int MAX_INACTIVE_INTERVAL_IN_SECONDS = 600;

    private AnnotationConfigApplicationContext context;

    @Test
    public void defaultConfiguration() {
        registerAndRefresh(RedisWebSessionConfigurationTests.RedisConfig.class, RedisWebSessionConfigurationTests.DefaultConfig.class);
        ReactiveRedisOperationsSessionRepository repository = this.context.getBean(ReactiveRedisOperationsSessionRepository.class);
        assertThat(repository).isNotNull();
    }

    @Test
    public void springSessionRedisOperationsResolvingConfiguration() {
        registerAndRefresh(RedisWebSessionConfigurationTests.RedisConfig.class, RedisWebSessionConfigurationTests.SpringSessionRedisOperationsResolvingConfig.class);
        ReactiveRedisOperationsSessionRepository repository = this.context.getBean(ReactiveRedisOperationsSessionRepository.class);
        assertThat(repository).isNotNull();
        ReactiveRedisOperations<String, Object> springSessionRedisOperations = this.context.getBean(RedisWebSessionConfigurationTests.SpringSessionRedisOperationsResolvingConfig.class).getSpringSessionRedisOperations();
        assertThat(springSessionRedisOperations).isNotNull();
        assertThat(((ReactiveRedisOperations) (ReflectionTestUtils.getField(repository, "sessionRedisOperations")))).isEqualTo(springSessionRedisOperations);
    }

    @Test
    public void customNamespace() {
        registerAndRefresh(RedisWebSessionConfigurationTests.RedisConfig.class, RedisWebSessionConfigurationTests.CustomNamespaceConfig.class);
        ReactiveRedisOperationsSessionRepository repository = this.context.getBean(ReactiveRedisOperationsSessionRepository.class);
        assertThat(repository).isNotNull();
        assertThat(ReflectionTestUtils.getField(repository, "namespace")).isEqualTo(((RedisWebSessionConfigurationTests.REDIS_NAMESPACE) + ":"));
    }

    @Test
    public void customMaxInactiveInterval() {
        registerAndRefresh(RedisWebSessionConfigurationTests.RedisConfig.class, RedisWebSessionConfigurationTests.CustomMaxInactiveIntervalConfig.class);
        ReactiveRedisOperationsSessionRepository repository = this.context.getBean(ReactiveRedisOperationsSessionRepository.class);
        assertThat(repository).isNotNull();
        assertThat(ReflectionTestUtils.getField(repository, "defaultMaxInactiveInterval")).isEqualTo(RedisWebSessionConfigurationTests.MAX_INACTIVE_INTERVAL_IN_SECONDS);
    }

    @Test
    public void customFlushMode() {
        registerAndRefresh(RedisWebSessionConfigurationTests.RedisConfig.class, RedisWebSessionConfigurationTests.CustomFlushModeConfig.class);
        ReactiveRedisOperationsSessionRepository repository = this.context.getBean(ReactiveRedisOperationsSessionRepository.class);
        assertThat(repository).isNotNull();
        assertThat(ReflectionTestUtils.getField(repository, "redisFlushMode")).isEqualTo(IMMEDIATE);
    }

    @Test
    public void qualifiedConnectionFactoryRedisConfig() {
        registerAndRefresh(RedisWebSessionConfigurationTests.RedisConfig.class, RedisWebSessionConfigurationTests.QualifiedConnectionFactoryRedisConfig.class);
        ReactiveRedisOperationsSessionRepository repository = this.context.getBean(ReactiveRedisOperationsSessionRepository.class);
        ReactiveRedisConnectionFactory redisConnectionFactory = this.context.getBean("qualifiedRedisConnectionFactory", ReactiveRedisConnectionFactory.class);
        assertThat(repository).isNotNull();
        assertThat(redisConnectionFactory).isNotNull();
        ReactiveRedisOperations redisOperations = ((ReactiveRedisOperations) (ReflectionTestUtils.getField(repository, "sessionRedisOperations")));
        assertThat(redisOperations).isNotNull();
        assertThat(ReflectionTestUtils.getField(redisOperations, "connectionFactory")).isEqualTo(redisConnectionFactory);
    }

    @Test
    public void primaryConnectionFactoryRedisConfig() {
        registerAndRefresh(RedisWebSessionConfigurationTests.RedisConfig.class, RedisWebSessionConfigurationTests.PrimaryConnectionFactoryRedisConfig.class);
        ReactiveRedisOperationsSessionRepository repository = this.context.getBean(ReactiveRedisOperationsSessionRepository.class);
        ReactiveRedisConnectionFactory redisConnectionFactory = this.context.getBean("primaryRedisConnectionFactory", ReactiveRedisConnectionFactory.class);
        assertThat(repository).isNotNull();
        assertThat(redisConnectionFactory).isNotNull();
        ReactiveRedisOperations redisOperations = ((ReactiveRedisOperations) (ReflectionTestUtils.getField(repository, "sessionRedisOperations")));
        assertThat(redisOperations).isNotNull();
        assertThat(ReflectionTestUtils.getField(redisOperations, "connectionFactory")).isEqualTo(redisConnectionFactory);
    }

    @Test
    public void qualifiedAndPrimaryConnectionFactoryRedisConfig() {
        registerAndRefresh(RedisWebSessionConfigurationTests.RedisConfig.class, RedisWebSessionConfigurationTests.QualifiedAndPrimaryConnectionFactoryRedisConfig.class);
        ReactiveRedisOperationsSessionRepository repository = this.context.getBean(ReactiveRedisOperationsSessionRepository.class);
        ReactiveRedisConnectionFactory redisConnectionFactory = this.context.getBean("qualifiedRedisConnectionFactory", ReactiveRedisConnectionFactory.class);
        assertThat(repository).isNotNull();
        assertThat(redisConnectionFactory).isNotNull();
        ReactiveRedisOperations redisOperations = ((ReactiveRedisOperations) (ReflectionTestUtils.getField(repository, "sessionRedisOperations")));
        assertThat(redisOperations).isNotNull();
        assertThat(ReflectionTestUtils.getField(redisOperations, "connectionFactory")).isEqualTo(redisConnectionFactory);
    }

    @Test
    public void namedConnectionFactoryRedisConfig() {
        registerAndRefresh(RedisWebSessionConfigurationTests.RedisConfig.class, RedisWebSessionConfigurationTests.NamedConnectionFactoryRedisConfig.class);
        ReactiveRedisOperationsSessionRepository repository = this.context.getBean(ReactiveRedisOperationsSessionRepository.class);
        ReactiveRedisConnectionFactory redisConnectionFactory = this.context.getBean("redisConnectionFactory", ReactiveRedisConnectionFactory.class);
        assertThat(repository).isNotNull();
        assertThat(redisConnectionFactory).isNotNull();
        ReactiveRedisOperations redisOperations = ((ReactiveRedisOperations) (ReflectionTestUtils.getField(repository, "sessionRedisOperations")));
        assertThat(redisOperations).isNotNull();
        assertThat(ReflectionTestUtils.getField(redisOperations, "connectionFactory")).isEqualTo(redisConnectionFactory);
    }

    @Test
    public void multipleConnectionFactoryRedisConfig() {
        assertThatExceptionOfType(BeanCreationException.class).isThrownBy(() -> registerAndRefresh(.class, .class)).withMessageContaining("expected single matching bean but found 2");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void customRedisSerializerConfig() {
        registerAndRefresh(RedisWebSessionConfigurationTests.RedisConfig.class, RedisWebSessionConfigurationTests.CustomRedisSerializerConfig.class);
        ReactiveRedisOperationsSessionRepository repository = this.context.getBean(ReactiveRedisOperationsSessionRepository.class);
        RedisSerializer<Object> redisSerializer = this.context.getBean("springSessionDefaultRedisSerializer", RedisSerializer.class);
        assertThat(repository).isNotNull();
        assertThat(redisSerializer).isNotNull();
        ReactiveRedisOperations redisOperations = ((ReactiveRedisOperations) (ReflectionTestUtils.getField(repository, "sessionRedisOperations")));
        assertThat(redisOperations).isNotNull();
        RedisSerializationContext serializationContext = redisOperations.getSerializationContext();
        assertThat(ReflectionTestUtils.getField(serializationContext.getValueSerializationPair().getReader(), "serializer")).isEqualTo(redisSerializer);
        assertThat(ReflectionTestUtils.getField(serializationContext.getValueSerializationPair().getWriter(), "serializer")).isEqualTo(redisSerializer);
        assertThat(ReflectionTestUtils.getField(serializationContext.getHashValueSerializationPair().getReader(), "serializer")).isEqualTo(redisSerializer);
        assertThat(ReflectionTestUtils.getField(serializationContext.getHashValueSerializationPair().getWriter(), "serializer")).isEqualTo(redisSerializer);
    }

    @Configuration
    static class RedisConfig {
        @Bean
        public ReactiveRedisConnectionFactory defaultRedisConnectionFactory() {
            return Mockito.mock(ReactiveRedisConnectionFactory.class);
        }
    }

    @EnableRedisWebSession
    static class DefaultConfig {}

    @EnableRedisWebSession
    static class SpringSessionRedisOperationsResolvingConfig {
        @SpringSessionRedisOperations
        private ReactiveRedisOperations<String, Object> springSessionRedisOperations;

        public ReactiveRedisOperations<String, Object> getSpringSessionRedisOperations() {
            return this.springSessionRedisOperations;
        }
    }

    @EnableRedisWebSession(redisNamespace = RedisWebSessionConfigurationTests.REDIS_NAMESPACE)
    static class CustomNamespaceConfig {}

    @EnableRedisWebSession(maxInactiveIntervalInSeconds = RedisWebSessionConfigurationTests.MAX_INACTIVE_INTERVAL_IN_SECONDS)
    static class CustomMaxInactiveIntervalConfig {}

    @EnableRedisWebSession(redisFlushMode = RedisFlushMode.IMMEDIATE)
    static class CustomFlushModeConfig {}

    @EnableRedisWebSession
    static class QualifiedConnectionFactoryRedisConfig {
        @Bean
        @SpringSessionRedisConnectionFactory
        public ReactiveRedisConnectionFactory qualifiedRedisConnectionFactory() {
            return Mockito.mock(ReactiveRedisConnectionFactory.class);
        }
    }

    @EnableRedisWebSession
    static class PrimaryConnectionFactoryRedisConfig {
        @Bean
        @Primary
        public ReactiveRedisConnectionFactory primaryRedisConnectionFactory() {
            return Mockito.mock(ReactiveRedisConnectionFactory.class);
        }
    }

    @EnableRedisWebSession
    static class QualifiedAndPrimaryConnectionFactoryRedisConfig {
        @Bean
        @SpringSessionRedisConnectionFactory
        public ReactiveRedisConnectionFactory qualifiedRedisConnectionFactory() {
            return Mockito.mock(ReactiveRedisConnectionFactory.class);
        }

        @Bean
        @Primary
        public ReactiveRedisConnectionFactory primaryRedisConnectionFactory() {
            return Mockito.mock(ReactiveRedisConnectionFactory.class);
        }
    }

    @EnableRedisWebSession
    static class NamedConnectionFactoryRedisConfig {
        @Bean
        public ReactiveRedisConnectionFactory redisConnectionFactory() {
            return Mockito.mock(ReactiveRedisConnectionFactory.class);
        }
    }

    @EnableRedisWebSession
    static class MultipleConnectionFactoryRedisConfig {
        @Bean
        public ReactiveRedisConnectionFactory secondaryRedisConnectionFactory() {
            return Mockito.mock(ReactiveRedisConnectionFactory.class);
        }
    }

    @EnableRedisWebSession
    static class CustomRedisSerializerConfig {
        @Bean
        @SuppressWarnings("unchecked")
        public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
            return Mockito.mock(RedisSerializer.class);
        }
    }
}

