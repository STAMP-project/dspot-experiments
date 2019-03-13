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
package org.springframework.batch.sample;


import ExitStatus.COMPLETED;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.sample.domain.trade.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;


/**
 *
 *
 * @author Mahmoud Ben Hassine
 */
public class JsonSupportIntegrationTests {
    private static final String INPUT_FILE_DIRECTORY = "src/test/resources/org/springframework/batch/item/json/";

    private static final String OUTPUT_FILE_DIRECTORY = "build/";

    @Configuration
    @EnableBatchProcessing
    public static class JobConfiguration {
        @Autowired
        private JobBuilderFactory jobs;

        @Autowired
        private StepBuilderFactory steps;

        @Bean
        public JsonItemReader<Trade> itemReader() {
            return new org.springframework.batch.item.json.builder.JsonItemReaderBuilder<Trade>().name("tradesJsonItemReader").resource(new FileSystemResource(((JsonSupportIntegrationTests.INPUT_FILE_DIRECTORY) + "trades.json"))).jsonObjectReader(new org.springframework.batch.item.json.GsonJsonObjectReader(Trade.class)).build();
        }

        @Bean
        public JsonFileItemWriter<Trade> itemWriter() {
            return new org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder<Trade>().resource(new FileSystemResource(((JsonSupportIntegrationTests.OUTPUT_FILE_DIRECTORY) + "trades.json"))).jsonObjectMarshaller(new org.springframework.batch.item.json.JacksonJsonObjectMarshaller()).name("tradesJsonFileItemWriter").build();
        }

        @Bean
        public Step step() {
            return steps.get("step").<Trade, Trade>chunk(2).reader(itemReader()).writer(itemWriter()).build();
        }

        @Bean
        public Job job() {
            return jobs.get("job").start(step()).build();
        }
    }

    @Test
    public void testJsonReadingAndWriting() throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(JsonSupportIntegrationTests.JobConfiguration.class);
        JobLauncher jobLauncher = context.getBean(JobLauncher.class);
        Job job = context.getBean(Job.class);
        JobExecution jobExecution = jobLauncher.run(job, new JobParameters());
        Assert.assertEquals(COMPLETED.getExitCode(), jobExecution.getExitStatus().getExitCode());
        assertFileEquals(new File(((JsonSupportIntegrationTests.INPUT_FILE_DIRECTORY) + "trades.json")), new File(((JsonSupportIntegrationTests.OUTPUT_FILE_DIRECTORY) + "trades.json")));
    }
}

