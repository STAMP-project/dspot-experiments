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
package org.springframework.batch.core.configuration.annotation;


import javax.sql.DataSource;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.PooledEmbeddedDataSource;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.stereotype.Component;


public class MapJobRepositoryConfigurationTests {
    JobLauncher jobLauncher;

    JobRepository jobRepository;

    Job job;

    JobExplorer jobExplorer;

    @Test
    public void testRoseyScenario() throws Exception {
        testConfigurationClass(MapJobRepositoryConfigurationTests.MapRepositoryBatchConfiguration.class);
    }

    @Test
    public void testOneDataSource() throws Exception {
        testConfigurationClass(MapJobRepositoryConfigurationTests.HsqlBatchConfiguration.class);
    }

    @Test(expected = UnsatisfiedDependencyException.class)
    public void testMultipleDataSources_whenNoneOfThemIsPrimary() throws Exception {
        testConfigurationClass(MapJobRepositoryConfigurationTests.InvalidBatchConfiguration.class);
    }

    @Test
    public void testMultipleDataSources_whenNoneOfThemIsPrimaryButOneOfThemIsNamed_dataSource_() throws Exception {
        testConfigurationClass(MapJobRepositoryConfigurationTests.ValidBatchConfigurationWithoutPrimaryDataSource.class);
    }

    @Test
    public void testMultipleDataSources_whenOneOfThemIsPrimary() throws Exception {
        testConfigurationClass(MapJobRepositoryConfigurationTests.ValidBatchConfigurationWithPrimaryDataSource.class);
    }

    public static class InvalidBatchConfiguration extends MapJobRepositoryConfigurationTests.HsqlBatchConfiguration {
        @Bean
        DataSource dataSource2() {
            return new PooledEmbeddedDataSource(new EmbeddedDatabaseBuilder().setName("badDatabase").build());
        }
    }

    public static class ValidBatchConfigurationWithPrimaryDataSource extends MapJobRepositoryConfigurationTests.HsqlBatchConfiguration {
        @Primary
        @Bean
        DataSource dataSource2() {
            return new PooledEmbeddedDataSource(new EmbeddedDatabaseBuilder().setName("dataSource2").addScript("classpath:org/springframework/batch/core/schema-drop-hsqldb.sql").addScript("classpath:org/springframework/batch/core/schema-hsqldb.sql").build());
        }
    }

    public static class ValidBatchConfigurationWithoutPrimaryDataSource extends MapJobRepositoryConfigurationTests.HsqlBatchConfiguration {
        @Bean
        DataSource dataSource() {
            // will be autowired by name
            return new PooledEmbeddedDataSource(new EmbeddedDatabaseBuilder().setName("dataSource").addScript("classpath:org/springframework/batch/core/schema-drop-hsqldb.sql").addScript("classpath:org/springframework/batch/core/schema-hsqldb.sql").build());
        }
    }

    public static class HsqlBatchConfiguration extends MapJobRepositoryConfigurationTests.MapRepositoryBatchConfiguration {
        @Bean
        DataSource dataSource1() {
            return new PooledEmbeddedDataSource(new EmbeddedDatabaseBuilder().setName("dataSource1").addScript("classpath:org/springframework/batch/core/schema-drop-hsqldb.sql").addScript("classpath:org/springframework/batch/core/schema-hsqldb.sql").build());
        }
    }

    @Component
    @EnableBatchProcessing
    public static class MapRepositoryBatchConfiguration {
        @Autowired
        JobBuilderFactory jobFactory;

        @Autowired
        StepBuilderFactory stepFactory;

        @Bean
        Step step1() {
            return stepFactory.get("step1").tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    return RepeatStatus.FINISHED;
                }
            }).build();
        }

        @Bean
        Job job() {
            return jobFactory.get("job").start(step1()).build();
        }
    }
}

