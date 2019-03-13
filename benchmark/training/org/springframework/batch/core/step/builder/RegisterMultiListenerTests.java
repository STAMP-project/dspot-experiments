/**
 * Copyright 2013-2014 the original author or authors.
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
package org.springframework.batch.core.step.builder;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import EmbeddedDatabaseType.HSQL;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.PooledEmbeddedDataSource;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;


/**
 * Test for registering a listener class that implements different listeners interfaces
 * just once in java based configuration.
 *
 * @author Tobias Flohre
 * @author Michael Minella
 */
public class RegisterMultiListenerTests {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private RegisterMultiListenerTests.CallChecker callChecker;

    @Autowired
    private EmbeddedDatabase dataSource;

    private GenericApplicationContext context;

    /**
     * The times the beforeChunkCalled occurs are:
     *  - Before chunk 1 (item1, item2)
     *  - Before the re-attempt of item1 (scanning)
     *  - Before the re-attempt of item2 (scanning)
     *  - Before the checking that scanning is complete
     *  - Before chunk 2 (item3, item4)
     *  - Before chunk 3 (null)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMultiListenerFaultTolerantStep() throws Exception {
        bootstrap(RegisterMultiListenerTests.MultiListenerFaultTolerantTestConfiguration.class);
        JobExecution execution = jobLauncher.run(job, new JobParameters());
        Assert.assertEquals(COMPLETED, execution.getStatus());
        Assert.assertEquals(1, callChecker.beforeStepCalled);
        Assert.assertEquals(6, callChecker.beforeChunkCalled);
        Assert.assertEquals(2, callChecker.beforeWriteCalled);
        Assert.assertEquals(1, callChecker.skipInWriteCalled);
    }

    @Test
    public void testMultiListenerSimpleStep() throws Exception {
        bootstrap(RegisterMultiListenerTests.MultiListenerTestConfiguration.class);
        JobExecution execution = jobLauncher.run(job, new JobParameters());
        Assert.assertEquals(FAILED, execution.getStatus());
        Assert.assertEquals(1, callChecker.beforeStepCalled);
        Assert.assertEquals(1, callChecker.beforeChunkCalled);
        Assert.assertEquals(1, callChecker.beforeWriteCalled);
        Assert.assertEquals(0, callChecker.skipInWriteCalled);
    }

    public abstract static class MultiListenerTestConfigurationSupport {
        @Autowired
        protected JobBuilderFactory jobBuilders;

        @Autowired
        protected StepBuilderFactory stepBuilders;

        @Bean
        public Job testJob() {
            return jobBuilders.get("testJob").start(step()).build();
        }

        @Bean
        public RegisterMultiListenerTests.CallChecker callChecker() {
            return new RegisterMultiListenerTests.CallChecker();
        }

        @Bean
        public RegisterMultiListenerTests.MultiListener listener() {
            return new RegisterMultiListenerTests.MultiListener(callChecker());
        }

        @Bean
        public ItemReader<String> reader() {
            return new ItemReader<String>() {
                private int count = 0;

                @Override
                public String read() throws Exception, NonTransientResourceException, ParseException, UnexpectedInputException {
                    (count)++;
                    if ((count) < 5) {
                        return "item" + (count);
                    } else {
                        return null;
                    }
                }
            };
        }

        @Bean
        public ItemWriter<String> writer() {
            return new ItemWriter<String>() {
                @Override
                public void write(List<? extends String> items) throws Exception {
                    if (items.contains("item2")) {
                        throw new RegisterMultiListenerTests.MySkippableException();
                    }
                }
            };
        }

        public abstract Step step();
    }

    @Configuration
    @EnableBatchProcessing
    public static class MultiListenerFaultTolerantTestConfiguration extends RegisterMultiListenerTests.MultiListenerTestConfigurationSupport {
        @Bean
        public DataSource dataSource() {
            return new PooledEmbeddedDataSource(new EmbeddedDatabaseBuilder().addScript("classpath:org/springframework/batch/core/schema-drop-hsqldb.sql").addScript("classpath:org/springframework/batch/core/schema-hsqldb.sql").setType(HSQL).build());
        }

        @Override
        @Bean
        public Step step() {
            return // ChunkListener registered twice for checking BATCH-2149
            stepBuilders.get("step").listener(listener()).<String, String>chunk(2).reader(reader()).writer(writer()).faultTolerant().skipLimit(1).skip(RegisterMultiListenerTests.MySkippableException.class).listener(((ChunkListener) (listener()))).build();
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class MultiListenerTestConfiguration extends RegisterMultiListenerTests.MultiListenerTestConfigurationSupport {
        @Bean
        public DataSource dataSource() {
            return new PooledEmbeddedDataSource(new EmbeddedDatabaseBuilder().addScript("classpath:org/springframework/batch/core/schema-drop-hsqldb.sql").addScript("classpath:org/springframework/batch/core/schema-hsqldb.sql").setType(HSQL).build());
        }

        @Override
        @Bean
        public Step step() {
            return stepBuilders.get("step").listener(listener()).<String, String>chunk(2).reader(reader()).writer(writer()).build();
        }
    }

    private static class CallChecker {
        int beforeStepCalled = 0;

        int beforeChunkCalled = 0;

        int beforeWriteCalled = 0;

        int skipInWriteCalled = 0;
    }

    private static class MultiListener implements ChunkListener , ItemWriteListener<String> , SkipListener<String, String> , StepExecutionListener {
        private RegisterMultiListenerTests.CallChecker callChecker;

        private MultiListener(RegisterMultiListenerTests.CallChecker callChecker) {
            super();
            this.callChecker = callChecker;
        }

        @Override
        public void onSkipInRead(Throwable t) {
        }

        @Override
        public void onSkipInWrite(String item, Throwable t) {
            (callChecker.skipInWriteCalled)++;
        }

        @Override
        public void onSkipInProcess(String item, Throwable t) {
        }

        @Override
        public void beforeWrite(List<? extends String> items) {
            (callChecker.beforeWriteCalled)++;
        }

        @Override
        public void afterWrite(List<? extends String> items) {
        }

        @Override
        public void onWriteError(Exception exception, List<? extends String> items) {
        }

        @Override
        public void beforeChunk(ChunkContext context) {
            (callChecker.beforeChunkCalled)++;
        }

        @Override
        public void afterChunk(ChunkContext context) {
        }

        @Override
        public void afterChunkError(ChunkContext context) {
        }

        @Override
        public void beforeStep(StepExecution stepExecution) {
            (callChecker.beforeStepCalled)++;
        }

        @Override
        public ExitStatus afterStep(StepExecution stepExecution) {
            return null;
        }
    }

    private static class MySkippableException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}

