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
package org.springframework.boot.autoconfigure.jdbc;


import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AbstractTransactionManagementConfiguration;


/**
 * Tests for {@link DataSourceTransactionManagerAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @author Kazuki Shimizu
 */
public class DataSourceTransactionManagerAutoConfigurationTests {
    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void testDataSourceExists() {
        this.context.register(EmbeddedDataSourceConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, TransactionAutoConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(DataSource.class)).isNotNull();
        assertThat(this.context.getBean(DataSourceTransactionManager.class)).isNotNull();
    }

    @Test
    public void testNoDataSourceExists() {
        this.context.register(DataSourceTransactionManagerAutoConfiguration.class, TransactionAutoConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBeanNamesForType(DataSource.class)).isEmpty();
        assertThat(this.context.getBeanNamesForType(DataSourceTransactionManager.class)).isEmpty();
    }

    @Test
    public void testManualConfiguration() {
        this.context.register(EmbeddedDataSourceConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, TransactionAutoConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(DataSource.class)).isNotNull();
        assertThat(this.context.getBean(DataSourceTransactionManager.class)).isNotNull();
    }

    @Test
    public void testExistingTransactionManager() {
        this.context.register(DataSourceTransactionManagerAutoConfigurationTests.TransactionManagerConfiguration.class, EmbeddedDataSourceConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, TransactionAutoConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBeansOfType(PlatformTransactionManager.class)).hasSize(1);
        assertThat(this.context.getBean(PlatformTransactionManager.class)).isEqualTo(this.context.getBean("myTransactionManager"));
    }

    @Test
    public void testMultiDataSource() {
        this.context.register(MultiDataSourceConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, TransactionAutoConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBeansOfType(PlatformTransactionManager.class)).isEmpty();
    }

    @Test
    public void testMultiDataSourceUsingPrimary() {
        this.context.register(MultiDataSourceUsingPrimaryConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, TransactionAutoConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(DataSourceTransactionManager.class)).isNotNull();
        assertThat(this.context.getBean(AbstractTransactionManagementConfiguration.class)).isNotNull();
    }

    @Test
    public void testCustomizeDataSourceTransactionManagerUsingProperties() {
        TestPropertyValues.of("spring.transaction.default-timeout:30", "spring.transaction.rollback-on-commit-failure:true").applyTo(this.context);
        this.context.register(EmbeddedDataSourceConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, TransactionAutoConfiguration.class);
        this.context.refresh();
        DataSourceTransactionManager transactionManager = this.context.getBean(DataSourceTransactionManager.class);
        assertThat(transactionManager.getDefaultTimeout()).isEqualTo(30);
        assertThat(transactionManager.isRollbackOnCommitFailure()).isTrue();
    }

    @Configuration
    protected static class TransactionManagerConfiguration {
        @Bean
        public PlatformTransactionManager myTransactionManager() {
            return Mockito.mock(PlatformTransactionManager.class);
        }
    }
}

