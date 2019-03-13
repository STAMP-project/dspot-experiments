/**
 * Copyright 2006-2018 the original author or authors.
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
package org.springframework.batch.core.configuration.annotation;


import BatchStatus.COMPLETED;
import javax.sql.DataSource;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.AbstractStep;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Dave Syer
 * @author Mahmoud Ben Hassine
 */
public class JobBuilderConfigurationTests {
    public static boolean fail = false;

    private JobExecution execution;

    @Test
    public void testVanillaBatchConfiguration() throws Exception {
        testJob(COMPLETED, 2, JobBuilderConfigurationTests.TestConfiguration.class);
    }

    @Test
    public void testConfigurerAsConfiguration() throws Exception {
        testJob(COMPLETED, 1, JobBuilderConfigurationTests.TestConfigurer.class);
    }

    @Test
    public void testConfigurerAsBean() throws Exception {
        testJob(COMPLETED, 1, JobBuilderConfigurationTests.BeansConfigurer.class);
    }

    @Test
    public void testTwoConfigurations() throws Exception {
        testJob("testJob", COMPLETED, 2, JobBuilderConfigurationTests.TestConfiguration.class, JobBuilderConfigurationTests.AnotherConfiguration.class);
    }

    @Test
    public void testTwoConfigurationsAndConfigurer() throws Exception {
        testJob("testJob", COMPLETED, 2, JobBuilderConfigurationTests.TestConfiguration.class, JobBuilderConfigurationTests.TestConfigurer.class);
    }

    @Test
    public void testTwoConfigurationsAndBeansConfigurer() throws Exception {
        testJob("testJob", COMPLETED, 2, JobBuilderConfigurationTests.TestConfiguration.class, JobBuilderConfigurationTests.BeansConfigurer.class);
    }

    @Configuration
    @EnableBatchProcessing
    public static class TestConfiguration {
        @Autowired
        private JobBuilderFactory jobs;

        @Autowired
        private StepBuilderFactory steps;

        @Bean
        public Job testJob() throws Exception {
            SimpleJobBuilder builder = jobs.get("test").start(step1()).next(step2());
            return builder.build();
        }

        @Bean
        protected Step step1() throws Exception {
            return steps.get("step1").tasklet(tasklet()).build();
        }

        @Bean
        protected Step step2() throws Exception {
            return steps.get("step2").tasklet(tasklet()).build();
        }

        @Bean
        protected Tasklet tasklet() {
            return new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
                    if (JobBuilderConfigurationTests.fail) {
                        throw new RuntimeException("Planned!");
                    }
                    return RepeatStatus.FINISHED;
                }
            };
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class AnotherConfiguration {
        @Autowired
        private JobBuilderFactory jobs;

        @Autowired
        private StepBuilderFactory steps;

        @Autowired
        private Tasklet tasklet;

        @Bean
        public Job anotherJob() throws Exception {
            SimpleJobBuilder builder = jobs.get("another").start(step3());
            return builder.build();
        }

        @Bean
        protected Step step3() throws Exception {
            return steps.get("step3").tasklet(tasklet).build();
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class TestConfigurer extends DefaultBatchConfigurer {
        @Autowired
        private SimpleBatchConfiguration jobs;

        @Bean
        public Job testConfigurerJob() throws Exception {
            SimpleJobBuilder builder = jobs.jobBuilders().get("configurer").start(step1());
            return builder.build();
        }

        @Bean
        protected Step step1() throws Exception {
            AbstractStep step = new AbstractStep("step1") {
                @Override
                protected void doExecute(StepExecution stepExecution) throws Exception {
                    stepExecution.setExitStatus(ExitStatus.COMPLETED);
                    stepExecution.setStatus(COMPLETED);
                }
            };
            step.setJobRepository(getJobRepository());
            return step;
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class BeansConfigurer {
        @Autowired
        private JobBuilderFactory jobs;

        @Autowired
        private StepBuilderFactory steps;

        @Bean
        public Job beansConfigurerJob() throws Exception {
            SimpleJobBuilder builder = jobs.get("beans").start(step1());
            return builder.build();
        }

        @Bean
        protected Step step1() throws Exception {
            return steps.get("step1").tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    return null;
                }
            }).build();
        }

        @Bean
        @Autowired
        protected BatchConfigurer configurer(DataSource dataSource) {
            return new DefaultBatchConfigurer(dataSource);
        }
    }
}

