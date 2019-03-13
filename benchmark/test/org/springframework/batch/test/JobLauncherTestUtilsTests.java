/**
 * Copyright 2014 the original author or authors.
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
package org.springframework.batch.test;


import ExitStatus.COMPLETED;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author mminella
 */
public class JobLauncherTestUtilsTests {
    @Test
    public void testStepExecutionWithJavaConfig() {
        ApplicationContext context = new AnnotationConfigApplicationContext(JobLauncherTestUtilsTests.TestJobConfiguration.class);
        JobLauncherTestUtils testUtils = context.getBean(JobLauncherTestUtils.class);
        JobExecution execution = testUtils.launchStep("step1");
        Assert.assertEquals(COMPLETED, execution.getExitStatus());
    }

    @Configuration
    @EnableBatchProcessing
    public static class TestJobConfiguration {
        @Autowired
        public JobBuilderFactory jobBuilderFactory;

        @Autowired
        public StepBuilderFactory stepBuilderFactory;

        @Bean
        public Step step() {
            return stepBuilderFactory.get("step1").tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    return null;
                }
            }).build();
        }

        @Bean
        public Job job() {
            return jobBuilderFactory.get("job").flow(step()).end().build();
        }

        @Bean
        public JobLauncherTestUtils testUtils() {
            JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
            jobLauncherTestUtils.setJob(job());
            return jobLauncherTestUtils;
        }
    }
}

