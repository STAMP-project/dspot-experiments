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
package org.springframework.boot.autoconfigure.transaction;


import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Tests for {@link TransactionAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 */
public class TransactionAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void noTransactionManager() {
        load(TransactionAutoConfigurationTests.EmptyConfiguration.class);
        assertThat(this.context.getBeansOfType(TransactionTemplate.class)).isEmpty();
    }

    @Test
    public void singleTransactionManager() {
        load(new Class<?>[]{ DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class }, "spring.datasource.initialization-mode:never");
        PlatformTransactionManager transactionManager = this.context.getBean(PlatformTransactionManager.class);
        TransactionTemplate transactionTemplate = this.context.getBean(TransactionTemplate.class);
        assertThat(transactionTemplate.getTransactionManager()).isSameAs(transactionManager);
    }

    @Test
    public void severalTransactionManagers() {
        load(TransactionAutoConfigurationTests.SeveralTransactionManagersConfiguration.class);
        assertThat(this.context.getBeansOfType(TransactionTemplate.class)).isEmpty();
    }

    @Test
    public void customTransactionManager() {
        load(TransactionAutoConfigurationTests.CustomTransactionManagerConfiguration.class);
        Map<String, TransactionTemplate> beans = this.context.getBeansOfType(TransactionTemplate.class);
        assertThat(beans).hasSize(1);
        assertThat(beans.containsKey("transactionTemplateFoo")).isTrue();
    }

    @Test
    public void platformTransactionManagerCustomizers() {
        load(TransactionAutoConfigurationTests.SeveralTransactionManagersConfiguration.class);
        TransactionManagerCustomizers customizers = this.context.getBean(TransactionManagerCustomizers.class);
        List<?> field = ((List<?>) (ReflectionTestUtils.getField(customizers, "customizers")));
        assertThat(field).hasSize(1).first().isInstanceOf(TransactionProperties.class);
    }

    @Test
    public void transactionNotManagedWithNoTransactionManager() {
        load(TransactionAutoConfigurationTests.BaseConfiguration.class);
        assertThat(this.context.getBean(TransactionAutoConfigurationTests.TransactionalService.class).isTransactionActive()).isFalse();
    }

    @Test
    public void transactionManagerUsesCglibByDefault() {
        load(TransactionAutoConfigurationTests.TransactionManagersConfiguration.class);
        assertThat(this.context.getBean(TransactionAutoConfigurationTests.AnotherServiceImpl.class).isTransactionActive()).isTrue();
        assertThat(this.context.getBeansOfType(TransactionAutoConfigurationTests.TransactionalServiceImpl.class)).hasSize(1);
    }

    @Test
    public void transactionManagerCanBeConfiguredToJdkProxy() {
        load(TransactionAutoConfigurationTests.TransactionManagersConfiguration.class, "spring.aop.proxy-target-class=false");
        assertThat(this.context.getBean(TransactionAutoConfigurationTests.AnotherService.class).isTransactionActive()).isTrue();
        assertThat(this.context.getBeansOfType(TransactionAutoConfigurationTests.AnotherServiceImpl.class)).hasSize(0);
        assertThat(this.context.getBeansOfType(TransactionAutoConfigurationTests.TransactionalServiceImpl.class)).hasSize(0);
    }

    @Test
    public void customEnableTransactionManagementTakesPrecedence() {
        load(new Class<?>[]{ TransactionAutoConfigurationTests.CustomTransactionManagementConfiguration.class, TransactionAutoConfigurationTests.TransactionManagersConfiguration.class }, "spring.aop.proxy-target-class=true");
        assertThat(this.context.getBean(TransactionAutoConfigurationTests.AnotherService.class).isTransactionActive()).isTrue();
        assertThat(this.context.getBeansOfType(TransactionAutoConfigurationTests.AnotherServiceImpl.class)).hasSize(0);
        assertThat(this.context.getBeansOfType(TransactionAutoConfigurationTests.TransactionalServiceImpl.class)).hasSize(0);
    }

    @Configuration
    static class EmptyConfiguration {}

    @Configuration
    static class SeveralTransactionManagersConfiguration {
        @Bean
        public PlatformTransactionManager transactionManagerOne() {
            return Mockito.mock(PlatformTransactionManager.class);
        }

        @Bean
        public PlatformTransactionManager transactionManagerTwo() {
            return Mockito.mock(PlatformTransactionManager.class);
        }
    }

    @Configuration
    static class CustomTransactionManagerConfiguration {
        @Bean
        public TransactionTemplate transactionTemplateFoo() {
            return new TransactionTemplate(transactionManagerFoo());
        }

        @Bean
        public PlatformTransactionManager transactionManagerFoo() {
            return Mockito.mock(PlatformTransactionManager.class);
        }
    }

    @Configuration
    static class BaseConfiguration {
        @Bean
        public TransactionAutoConfigurationTests.TransactionalService transactionalService() {
            return new TransactionAutoConfigurationTests.TransactionalServiceImpl();
        }

        @Bean
        public TransactionAutoConfigurationTests.AnotherServiceImpl anotherService() {
            return new TransactionAutoConfigurationTests.AnotherServiceImpl();
        }
    }

    @Configuration
    @Import(TransactionAutoConfigurationTests.BaseConfiguration.class)
    static class TransactionManagersConfiguration {
        @Bean
        public DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create().driverClassName("org.hsqldb.jdbc.JDBCDriver").url("jdbc:hsqldb:mem:tx").username("sa").build();
        }
    }

    @Configuration
    @EnableTransactionManagement(proxyTargetClass = false)
    static class CustomTransactionManagementConfiguration {}

    interface TransactionalService {
        @Transactional
        boolean isTransactionActive();
    }

    static class TransactionalServiceImpl implements TransactionAutoConfigurationTests.TransactionalService {
        @Override
        public boolean isTransactionActive() {
            return TransactionSynchronizationManager.isActualTransactionActive();
        }
    }

    interface AnotherService {
        boolean isTransactionActive();
    }

    static class AnotherServiceImpl implements TransactionAutoConfigurationTests.AnotherService {
        @Override
        @Transactional
        public boolean isTransactionActive() {
            return TransactionSynchronizationManager.isActualTransactionActive();
        }
    }
}

