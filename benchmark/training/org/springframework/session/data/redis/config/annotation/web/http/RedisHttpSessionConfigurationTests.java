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
package org.springframework.session.data.redis.config.annotation.web.http;


import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link RedisHttpSessionConfiguration}.
 *
 * @author Edd? Mel?ndez
 * @author Mark Paluch
 * @author Vedran Pavic
 */
public class RedisHttpSessionConfigurationTests {
    private static final String CLEANUP_CRON_EXPRESSION = "0 0 * * * *";

    private AnnotationConfigApplicationContext context;

    @Test
    public void resolveValue() {
        registerAndRefresh(RedisHttpSessionConfigurationTests.RedisConfig.class, RedisHttpSessionConfigurationTests.CustomRedisHttpSessionConfiguration.class);
        RedisHttpSessionConfiguration configuration = this.context.getBean(RedisHttpSessionConfiguration.class);
        assertThat(ReflectionTestUtils.getField(configuration, "redisNamespace")).isEqualTo("myRedisNamespace");
    }

    @Test
    public void resolveValueByPlaceholder() {
        this.context.setEnvironment(new MockEnvironment().withProperty("session.redis.namespace", "customRedisNamespace"));
        registerAndRefresh(RedisHttpSessionConfigurationTests.RedisConfig.class, RedisHttpSessionConfigurationTests.PropertySourceConfiguration.class, RedisHttpSessionConfigurationTests.CustomRedisHttpSessionConfiguration2.class);
        RedisHttpSessionConfiguration configuration = this.context.getBean(RedisHttpSessionConfiguration.class);
        assertThat(ReflectionTestUtils.getField(configuration, "redisNamespace")).isEqualTo("customRedisNamespace");
    }

    @Test
    public void customCleanupCronAnnotation() {
        registerAndRefresh(RedisHttpSessionConfigurationTests.RedisConfig.class, RedisHttpSessionConfigurationTests.CustomCleanupCronExpressionAnnotationConfiguration.class);
        RedisHttpSessionConfiguration configuration = this.context.getBean(RedisHttpSessionConfiguration.class);
        assertThat(configuration).isNotNull();
        assertThat(ReflectionTestUtils.getField(configuration, "cleanupCron")).isEqualTo(RedisHttpSessionConfigurationTests.CLEANUP_CRON_EXPRESSION);
    }

    @Test
    public void customCleanupCronSetter() {
        registerAndRefresh(RedisHttpSessionConfigurationTests.RedisConfig.class, RedisHttpSessionConfigurationTests.CustomCleanupCronExpressionSetterConfiguration.class);
        RedisHttpSessionConfiguration configuration = this.context.getBean(RedisHttpSessionConfiguration.class);
        assertThat(configuration).isNotNull();
        assertThat(ReflectionTestUtils.getField(configuration, "cleanupCron")).isEqualTo(RedisHttpSessionConfigurationTests.CLEANUP_CRON_EXPRESSION);
    }

    @Test
    public void qualifiedConnectionFactoryRedisConfig() {
        registerAndRefresh(RedisHttpSessionConfigurationTests.RedisConfig.class, RedisHttpSessionConfigurationTests.QualifiedConnectionFactoryRedisConfig.class);
        RedisOperationsSessionRepository repository = this.context.getBean(RedisOperationsSessionRepository.class);
        RedisConnectionFactory redisConnectionFactory = this.context.getBean("qualifiedRedisConnectionFactory", RedisConnectionFactory.class);
        assertThat(repository).isNotNull();
        assertThat(redisConnectionFactory).isNotNull();
        RedisOperations redisOperations = ((RedisOperations) (ReflectionTestUtils.getField(repository, "sessionRedisOperations")));
        assertThat(redisOperations).isNotNull();
        assertThat(ReflectionTestUtils.getField(redisOperations, "connectionFactory")).isEqualTo(redisConnectionFactory);
    }

    @Test
    public void primaryConnectionFactoryRedisConfig() {
        registerAndRefresh(RedisHttpSessionConfigurationTests.RedisConfig.class, RedisHttpSessionConfigurationTests.PrimaryConnectionFactoryRedisConfig.class);
        RedisOperationsSessionRepository repository = this.context.getBean(RedisOperationsSessionRepository.class);
        RedisConnectionFactory redisConnectionFactory = this.context.getBean("primaryRedisConnectionFactory", RedisConnectionFactory.class);
        assertThat(repository).isNotNull();
        assertThat(redisConnectionFactory).isNotNull();
        RedisOperations redisOperations = ((RedisOperations) (ReflectionTestUtils.getField(repository, "sessionRedisOperations")));
        assertThat(redisOperations).isNotNull();
        assertThat(ReflectionTestUtils.getField(redisOperations, "connectionFactory")).isEqualTo(redisConnectionFactory);
    }

    @Test
    public void qualifiedAndPrimaryConnectionFactoryRedisConfig() {
        registerAndRefresh(RedisHttpSessionConfigurationTests.RedisConfig.class, RedisHttpSessionConfigurationTests.QualifiedAndPrimaryConnectionFactoryRedisConfig.class);
        RedisOperationsSessionRepository repository = this.context.getBean(RedisOperationsSessionRepository.class);
        RedisConnectionFactory redisConnectionFactory = this.context.getBean("qualifiedRedisConnectionFactory", RedisConnectionFactory.class);
        assertThat(repository).isNotNull();
        assertThat(redisConnectionFactory).isNotNull();
        RedisOperations redisOperations = ((RedisOperations) (ReflectionTestUtils.getField(repository, "sessionRedisOperations")));
        assertThat(redisOperations).isNotNull();
        assertThat(ReflectionTestUtils.getField(redisOperations, "connectionFactory")).isEqualTo(redisConnectionFactory);
    }

    @Test
    public void namedConnectionFactoryRedisConfig() {
        registerAndRefresh(RedisHttpSessionConfigurationTests.RedisConfig.class, RedisHttpSessionConfigurationTests.NamedConnectionFactoryRedisConfig.class);
        RedisOperationsSessionRepository repository = this.context.getBean(RedisOperationsSessionRepository.class);
        RedisConnectionFactory redisConnectionFactory = this.context.getBean("redisConnectionFactory", RedisConnectionFactory.class);
        assertThat(repository).isNotNull();
        assertThat(redisConnectionFactory).isNotNull();
        RedisOperations redisOperations = ((RedisOperations) (ReflectionTestUtils.getField(repository, "sessionRedisOperations")));
        assertThat(redisOperations).isNotNull();
        assertThat(ReflectionTestUtils.getField(redisOperations, "connectionFactory")).isEqualTo(redisConnectionFactory);
    }

    @Test
    public void multipleConnectionFactoryRedisConfig() {
        assertThatExceptionOfType(BeanCreationException.class).isThrownBy(() -> registerAndRefresh(.class, .class)).withMessageContaining("expected single matching bean but found 2");
    }

    @Configuration
    static class PropertySourceConfiguration {
        @Bean
        public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    @Configuration
    static class RedisConfig {
        @Bean
        public RedisConnectionFactory defaultRedisConnectionFactory() {
            return RedisHttpSessionConfigurationTests.mockRedisConnectionFactory();
        }
    }

    @EnableRedisHttpSession(cleanupCron = RedisHttpSessionConfigurationTests.CLEANUP_CRON_EXPRESSION)
    static class CustomCleanupCronExpressionAnnotationConfiguration {}

    @Configuration
    static class CustomCleanupCronExpressionSetterConfiguration extends RedisHttpSessionConfiguration {
        CustomCleanupCronExpressionSetterConfiguration() {
            setCleanupCron(RedisHttpSessionConfigurationTests.CLEANUP_CRON_EXPRESSION);
        }
    }

    @Configuration
    @EnableRedisHttpSession
    static class QualifiedConnectionFactoryRedisConfig {
        @Bean
        @SpringSessionRedisConnectionFactory
        public RedisConnectionFactory qualifiedRedisConnectionFactory() {
            return RedisHttpSessionConfigurationTests.mockRedisConnectionFactory();
        }
    }

    @Configuration
    @EnableRedisHttpSession
    static class PrimaryConnectionFactoryRedisConfig {
        @Bean
        @Primary
        public RedisConnectionFactory primaryRedisConnectionFactory() {
            return RedisHttpSessionConfigurationTests.mockRedisConnectionFactory();
        }
    }

    @Configuration
    @EnableRedisHttpSession
    static class QualifiedAndPrimaryConnectionFactoryRedisConfig {
        @Bean
        @SpringSessionRedisConnectionFactory
        public RedisConnectionFactory qualifiedRedisConnectionFactory() {
            return RedisHttpSessionConfigurationTests.mockRedisConnectionFactory();
        }

        @Bean
        @Primary
        public RedisConnectionFactory primaryRedisConnectionFactory() {
            return RedisHttpSessionConfigurationTests.mockRedisConnectionFactory();
        }
    }

    @Configuration
    @EnableRedisHttpSession
    static class NamedConnectionFactoryRedisConfig {
        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            return RedisHttpSessionConfigurationTests.mockRedisConnectionFactory();
        }
    }

    @Configuration
    @EnableRedisHttpSession
    static class MultipleConnectionFactoryRedisConfig {
        @Bean
        public RedisConnectionFactory secondaryRedisConnectionFactory() {
            return RedisHttpSessionConfigurationTests.mockRedisConnectionFactory();
        }
    }

    @Configuration
    @EnableRedisHttpSession(redisNamespace = "myRedisNamespace")
    static class CustomRedisHttpSessionConfiguration {}

    @Configuration
    @EnableRedisHttpSession(redisNamespace = "${session.redis.namespace}")
    static class CustomRedisHttpSessionConfiguration2 {}
}

