/**
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.core.configuration.annotation;


import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.util.AopTestUtils;
import org.springframework.transaction.PlatformTransactionManager;


/**
 *
 *
 * @author Mahmoud Ben Hassine
 */
public class TransactionManagerConfigurationWithBatchConfigurerTests extends TransactionManagerConfigurationTests {
    @Test
    public void testConfigurationWithNoDataSourceAndNoTransactionManager() throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(TransactionManagerConfigurationWithBatchConfigurerTests.BatchConfigurationWithNoDataSourceAndNoTransactionManager.class);
        BatchConfigurer batchConfigurer = applicationContext.getBean(BatchConfigurer.class);
        PlatformTransactionManager platformTransactionManager = batchConfigurer.getTransactionManager();
        Assert.assertTrue((platformTransactionManager instanceof ResourcelessTransactionManager));
        Assert.assertSame(getTransactionManagerSetOnJobRepository(applicationContext.getBean(JobRepository.class)), platformTransactionManager);
    }

    @Test
    public void testConfigurationWithNoDataSourceAndTransactionManager() throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(TransactionManagerConfigurationWithBatchConfigurerTests.BatchConfigurationWithNoDataSourceAndTransactionManager.class);
        BatchConfigurer batchConfigurer = applicationContext.getBean(BatchConfigurer.class);
        PlatformTransactionManager platformTransactionManager = batchConfigurer.getTransactionManager();
        Assert.assertSame(TransactionManagerConfigurationTests.transactionManager, platformTransactionManager);
        Assert.assertSame(getTransactionManagerSetOnJobRepository(applicationContext.getBean(JobRepository.class)), TransactionManagerConfigurationTests.transactionManager);
    }

    @Test
    public void testConfigurationWithDataSourceAndNoTransactionManager() throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(TransactionManagerConfigurationWithBatchConfigurerTests.BatchConfigurationWithDataSourceAndNoTransactionManager.class);
        BatchConfigurer batchConfigurer = applicationContext.getBean(BatchConfigurer.class);
        PlatformTransactionManager platformTransactionManager = batchConfigurer.getTransactionManager();
        Assert.assertTrue((platformTransactionManager instanceof DataSourceTransactionManager));
        DataSourceTransactionManager dataSourceTransactionManager = AopTestUtils.getTargetObject(platformTransactionManager);
        Assert.assertEquals(applicationContext.getBean(DataSource.class), dataSourceTransactionManager.getDataSource());
        Assert.assertSame(getTransactionManagerSetOnJobRepository(applicationContext.getBean(JobRepository.class)), platformTransactionManager);
    }

    @Test
    public void testConfigurationWithDataSourceAndTransactionManager() throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(TransactionManagerConfigurationWithBatchConfigurerTests.BatchConfigurationWithDataSourceAndTransactionManager.class);
        BatchConfigurer batchConfigurer = applicationContext.getBean(BatchConfigurer.class);
        PlatformTransactionManager platformTransactionManager = batchConfigurer.getTransactionManager();
        Assert.assertSame(TransactionManagerConfigurationTests.transactionManager, platformTransactionManager);
        Assert.assertSame(getTransactionManagerSetOnJobRepository(applicationContext.getBean(JobRepository.class)), TransactionManagerConfigurationTests.transactionManager);
    }

    @EnableBatchProcessing
    public static class BatchConfigurationWithNoDataSourceAndNoTransactionManager {
        @Bean
        public BatchConfigurer batchConfigurer() {
            return new DefaultBatchConfigurer();
        }
    }

    @EnableBatchProcessing
    public static class BatchConfigurationWithNoDataSourceAndTransactionManager {
        @Bean
        public PlatformTransactionManager transactionManager() {
            return TransactionManagerConfigurationTests.transactionManager;
        }

        @Bean
        public BatchConfigurer batchConfigurer() {
            return new DefaultBatchConfigurer() {
                @Override
                public PlatformTransactionManager getTransactionManager() {
                    return transactionManager();
                }
            };
        }
    }

    @EnableBatchProcessing
    public static class BatchConfigurationWithDataSourceAndNoTransactionManager {
        @Bean
        public DataSource dataSource() {
            return TransactionManagerConfigurationTests.createDataSource();
        }

        @Bean
        public BatchConfigurer batchConfigurer(DataSource dataSource) {
            return new DefaultBatchConfigurer(dataSource);
        }
    }

    @EnableBatchProcessing
    public static class BatchConfigurationWithDataSourceAndTransactionManager {
        @Bean
        public DataSource dataSource() {
            return TransactionManagerConfigurationTests.createDataSource();
        }

        @Bean
        public PlatformTransactionManager transactionManager() {
            return TransactionManagerConfigurationTests.transactionManager;
        }

        @Bean
        public BatchConfigurer batchConfigurer(DataSource dataSource) {
            return new DefaultBatchConfigurer(dataSource) {
                @Override
                public PlatformTransactionManager getTransactionManager() {
                    return transactionManager();
                }
            };
        }
    }
}

