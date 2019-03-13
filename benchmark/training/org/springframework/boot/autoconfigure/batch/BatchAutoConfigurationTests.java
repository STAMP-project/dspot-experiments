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
package org.springframework.boot.autoconfigure.batch;


import BatchStatus.COMPLETED;
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.test.City;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Tests for {@link BatchAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @author Vedran Pavic
 * @author Kazuki Shimizu
 */
public class BatchAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(BatchAutoConfiguration.class, TransactionAutoConfiguration.class));

    @Test
    public void testDefaultContext() {
        this.contextRunner.withUserConfiguration(BatchAutoConfigurationTests.TestConfiguration.class, EmbeddedDataSourceConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).getInitializeSchema()).isEqualTo(DataSourceInitializationMode.EMBEDDED);
            assertThat(new JdbcTemplate(context.getBean(.class)).queryForList("select * from BATCH_JOB_EXECUTION")).isEmpty();
        });
    }

    @Test
    public void testNoDatabase() {
        this.contextRunner.withUserConfiguration(BatchAutoConfigurationTests.TestCustomConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JobExplorer explorer = context.getBean(.class);
            assertThat(explorer.getJobInstances("job", 0, 100)).isEmpty();
        });
    }

    @Test
    public void testNoBatchConfiguration() {
        this.contextRunner.withUserConfiguration(BatchAutoConfigurationTests.EmptyConfiguration.class, EmbeddedDataSourceConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void testDefinesAndLaunchesJob() {
        this.contextRunner.withUserConfiguration(BatchAutoConfigurationTests.JobConfiguration.class, EmbeddedDataSourceConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            context.getBean(.class).run();
            assertThat(context.getBean(.class).getLastJobExecution("job", new JobParameters())).isNotNull();
        });
    }

    @Test
    public void testDefinesAndLaunchesNamedJob() {
        this.contextRunner.withUserConfiguration(BatchAutoConfigurationTests.NamedJobConfigurationWithRegisteredJob.class, EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.batch.job.names:discreteRegisteredJob").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            context.getBean(.class).run();
            assertThat(context.getBean(.class).getLastJobExecution("discreteRegisteredJob", new JobParameters())).isNotNull();
        });
    }

    @Test
    public void testDefinesAndLaunchesLocalJob() {
        this.contextRunner.withUserConfiguration(BatchAutoConfigurationTests.NamedJobConfigurationWithLocalJob.class, EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.batch.job.names:discreteLocalJob").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            context.getBean(.class).run();
            assertThat(context.getBean(.class).getLastJobExecution("discreteLocalJob", new JobParameters())).isNotNull();
        });
    }

    @Test
    public void testDisableLaunchesJob() {
        this.contextRunner.withUserConfiguration(BatchAutoConfigurationTests.JobConfiguration.class, EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.batch.job.enabled:false").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void testDisableSchemaLoader() {
        this.contextRunner.withUserConfiguration(BatchAutoConfigurationTests.TestConfiguration.class, EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.datasource.generate-unique-name=true", "spring.batch.initialize-schema:never").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).getInitializeSchema()).isEqualTo(DataSourceInitializationMode.NEVER);
            assertThatExceptionOfType(.class).isThrownBy(() -> new JdbcTemplate(context.getBean(.class)).queryForList("select * from BATCH_JOB_EXECUTION"));
        });
    }

    @Test
    public void testUsingJpa() {
        this.contextRunner.withUserConfiguration(BatchAutoConfigurationTests.TestConfiguration.class, EmbeddedDataSourceConfiguration.class, HibernateJpaAutoConfiguration.class).run(( context) -> {
            PlatformTransactionManager transactionManager = context.getBean(.class);
            // It's a lazy proxy, but it does render its target if you ask for
            // toString():
            assertThat(transactionManager.toString().contains("JpaTransactionManager")).isTrue();
            assertThat(context).hasSingleBean(.class);
            // Ensure the JobRepository can be used (no problem with isolation
            // level)
            assertThat(context.getBean(.class).getLastJobExecution("job", new JobParameters())).isNull();
        });
    }

    @Test
    public void testRenamePrefix() {
        this.contextRunner.withUserConfiguration(BatchAutoConfigurationTests.TestConfiguration.class, EmbeddedDataSourceConfiguration.class, HibernateJpaAutoConfiguration.class).withPropertyValues("spring.datasource.generate-unique-name=true", "spring.batch.schema:classpath:batch/custom-schema-hsql.sql", "spring.batch.tablePrefix:PREFIX_").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).getInitializeSchema()).isEqualTo(DataSourceInitializationMode.EMBEDDED);
            assertThat(new JdbcTemplate(context.getBean(.class)).queryForList("select * from PREFIX_JOB_EXECUTION")).isEmpty();
            JobExplorer jobExplorer = context.getBean(.class);
            assertThat(jobExplorer.findRunningJobExecutions("test")).isEmpty();
            JobRepository jobRepository = context.getBean(.class);
            assertThat(jobRepository.getLastJobExecution("test", new JobParameters())).isNull();
        });
    }

    @Test
    public void testCustomizeJpaTransactionManagerUsingProperties() {
        this.contextRunner.withUserConfiguration(BatchAutoConfigurationTests.TestConfiguration.class, EmbeddedDataSourceConfiguration.class, HibernateJpaAutoConfiguration.class).withPropertyValues("spring.transaction.default-timeout:30", "spring.transaction.rollback-on-commit-failure:true").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            JpaTransactionManager transactionManager = .class.cast(context.getBean(.class).getTransactionManager());
            assertThat(transactionManager.getDefaultTimeout()).isEqualTo(30);
            assertThat(transactionManager.isRollbackOnCommitFailure()).isTrue();
        });
    }

    @Test
    public void testCustomizeDataSourceTransactionManagerUsingProperties() {
        this.contextRunner.withUserConfiguration(BatchAutoConfigurationTests.TestConfiguration.class, EmbeddedDataSourceConfiguration.class).withPropertyValues("spring.transaction.default-timeout:30", "spring.transaction.rollback-on-commit-failure:true").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            DataSourceTransactionManager transactionManager = .class.cast(context.getBean(.class).getTransactionManager());
            assertThat(transactionManager.getDefaultTimeout()).isEqualTo(30);
            assertThat(transactionManager.isRollbackOnCommitFailure()).isTrue();
        });
    }

    @Configuration
    protected static class EmptyConfiguration {}

    @EnableBatchProcessing
    @TestAutoConfigurationPackage(City.class)
    protected static class TestConfiguration {}

    @EnableBatchProcessing
    @TestAutoConfigurationPackage(City.class)
    protected static class TestCustomConfiguration implements BatchConfigurer {
        private JobRepository jobRepository;

        private MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();

        @Override
        public JobRepository getJobRepository() throws Exception {
            if ((this.jobRepository) == null) {
                this.factory.afterPropertiesSet();
                this.jobRepository = this.factory.getObject();
            }
            return this.jobRepository;
        }

        @Override
        public PlatformTransactionManager getTransactionManager() {
            return new ResourcelessTransactionManager();
        }

        @Override
        public JobLauncher getJobLauncher() {
            SimpleJobLauncher launcher = new SimpleJobLauncher();
            launcher.setJobRepository(this.jobRepository);
            return launcher;
        }

        @Override
        public JobExplorer getJobExplorer() throws Exception {
            MapJobExplorerFactoryBean explorer = new MapJobExplorerFactoryBean(this.factory);
            explorer.afterPropertiesSet();
            return explorer.getObject();
        }
    }

    @Configuration
    @EnableBatchProcessing
    protected static class NamedJobConfigurationWithRegisteredJob {
        @Autowired
        private JobRegistry jobRegistry;

        @Autowired
        private JobRepository jobRepository;

        @Bean
        public JobRegistryBeanPostProcessor registryProcessor() {
            JobRegistryBeanPostProcessor processor = new JobRegistryBeanPostProcessor();
            processor.setJobRegistry(this.jobRegistry);
            return processor;
        }

        @Bean
        public Job discreteJob() {
            AbstractJob job = new AbstractJob("discreteRegisteredJob") {
                @Override
                public Collection<String> getStepNames() {
                    return Collections.emptySet();
                }

                @Override
                public Step getStep(String stepName) {
                    return null;
                }

                @Override
                protected void doExecute(JobExecution execution) {
                    execution.setStatus(COMPLETED);
                }
            };
            job.setJobRepository(this.jobRepository);
            return job;
        }
    }

    @Configuration
    @EnableBatchProcessing
    protected static class NamedJobConfigurationWithLocalJob {
        @Autowired
        private JobRepository jobRepository;

        @Bean
        public Job discreteJob() {
            AbstractJob job = new AbstractJob("discreteLocalJob") {
                @Override
                public Collection<String> getStepNames() {
                    return Collections.emptySet();
                }

                @Override
                public Step getStep(String stepName) {
                    return null;
                }

                @Override
                protected void doExecute(JobExecution execution) {
                    execution.setStatus(COMPLETED);
                }
            };
            job.setJobRepository(this.jobRepository);
            return job;
        }
    }

    @Configuration
    @EnableBatchProcessing
    protected static class JobConfiguration {
        @Autowired
        private JobRepository jobRepository;

        @Bean
        public Job job() {
            AbstractJob job = new AbstractJob() {
                @Override
                public Collection<String> getStepNames() {
                    return Collections.emptySet();
                }

                @Override
                public Step getStep(String stepName) {
                    return null;
                }

                @Override
                protected void doExecute(JobExecution execution) {
                    execution.setStatus(COMPLETED);
                }
            };
            job.setJobRepository(this.jobRepository);
            return job;
        }
    }
}

