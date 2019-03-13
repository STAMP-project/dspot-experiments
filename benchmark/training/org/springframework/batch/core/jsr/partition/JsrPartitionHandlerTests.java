/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.batch.core.jsr.partition;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import java.io.Serializable;
import java.util.Collection;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.batch.api.BatchProperty;
import javax.batch.api.partition.PartitionAnalyzer;
import javax.batch.api.partition.PartitionCollector;
import javax.batch.api.partition.PartitionMapper;
import javax.batch.api.partition.PartitionPlan;
import javax.batch.api.partition.PartitionPlanImpl;
import javax.batch.api.partition.PartitionReducer;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.jsr.AbstractJsrTestCase;
import org.springframework.batch.core.jsr.configuration.support.BatchPropertyContext;
import org.springframework.batch.core.jsr.step.batchlet.BatchletSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.JobRepositorySupport;
import org.springframework.util.StopWatch;


public class JsrPartitionHandlerTests extends AbstractJsrTestCase {
    private JsrPartitionHandler handler;

    private JobRepository repository = new JobRepositorySupport();

    private StepExecution stepExecution;

    private AtomicInteger count;

    private BatchPropertyContext propertyContext;

    private JsrStepExecutionSplitter stepSplitter;

    @Test
    public void testAfterPropertiesSet() throws Exception {
        handler = new JsrPartitionHandler();
        try {
            handler.afterPropertiesSet();
            Assert.fail("PropertyContext was not checked for");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("A BatchPropertyContext is required", iae.getMessage());
        }
        handler.setPropertyContext(new BatchPropertyContext());
        try {
            handler.afterPropertiesSet();
            Assert.fail("Threads or mapper was not checked for");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("Either a mapper implementation or the number of partitions/threads is required", iae.getMessage());
        }
        handler.setThreads(3);
        try {
            handler.afterPropertiesSet();
            Assert.fail("JobRepository was not checked for");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("A JobRepository is required", iae.getMessage());
        }
        handler.setJobRepository(repository);
        handler.afterPropertiesSet();
        handler.setPollingInterval((-1));
        try {
            handler.afterPropertiesSet();
            Assert.fail("Polling interval was not checked for");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("The polling interval must be positive", iae.getMessage());
        }
    }

    @Test
    public void testHardcodedNumberOfPartitions() throws Exception {
        handler.setThreads(3);
        handler.setPartitions(3);
        handler.afterPropertiesSet();
        Collection<StepExecution> executions = handler.handle(stepSplitter, stepExecution);
        Assert.assertEquals(3, executions.size());
        Assert.assertEquals(3, count.get());
    }

    @Test
    public void testPollingPartitionsCompletion() throws Exception {
        handler.setThreads(3);
        handler.setPartitions(3);
        handler.setPollingInterval(1000);
        handler.afterPropertiesSet();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Collection<StepExecution> executions = handler.handle(stepSplitter, stepExecution);
        stopWatch.stop();
        Assert.assertEquals(3, executions.size());
        Assert.assertEquals(3, count.get());
        Assert.assertTrue(((stopWatch.getLastTaskTimeMillis()) >= 1000));
    }

    @Test
    public void testMapperProvidesPartitions() throws Exception {
        handler.setPartitionMapper(new PartitionMapper() {
            @Override
            public PartitionPlan mapPartitions() throws Exception {
                PartitionPlan plan = new PartitionPlanImpl();
                plan.setPartitions(3);
                plan.setThreads(0);
                return plan;
            }
        });
        handler.afterPropertiesSet();
        Collection<StepExecution> executions = handler.handle(new JsrStepExecutionSplitter(repository, false, "step1", true), stepExecution);
        Assert.assertEquals(3, executions.size());
        Assert.assertEquals(3, count.get());
    }

    @Test
    public void testMapperProvidesPartitionsAndThreads() throws Exception {
        handler.setPartitionMapper(new PartitionMapper() {
            @Override
            public PartitionPlan mapPartitions() throws Exception {
                PartitionPlan plan = new PartitionPlanImpl();
                plan.setPartitions(3);
                plan.setThreads(1);
                return plan;
            }
        });
        handler.afterPropertiesSet();
        Collection<StepExecution> executions = handler.handle(new JsrStepExecutionSplitter(repository, false, "step1", true), stepExecution);
        Assert.assertEquals(3, executions.size());
        Assert.assertEquals(3, count.get());
    }

    @Test
    public void testMapperWithProperties() throws Exception {
        handler.setPartitionMapper(new PartitionMapper() {
            @Override
            public PartitionPlan mapPartitions() throws Exception {
                PartitionPlan plan = new PartitionPlanImpl();
                Properties[] props = new Properties[2];
                props[0] = new Properties();
                props[0].put("key1", "value1");
                props[1] = new Properties();
                props[1].put("key1", "value2");
                plan.setPartitionProperties(props);
                plan.setPartitions(3);
                plan.setThreads(1);
                return plan;
            }
        });
        handler.afterPropertiesSet();
        Collection<StepExecution> executions = handler.handle(new JsrStepExecutionSplitter(repository, false, "step1", true), stepExecution);
        Assert.assertEquals(3, executions.size());
        Assert.assertEquals(3, count.get());
        Assert.assertEquals("value1", propertyContext.getStepProperties("step1:partition0").get("key1"));
        Assert.assertEquals("value2", propertyContext.getStepProperties("step1:partition1").get("key1"));
    }

    @Test
    public void testAnalyzer() throws Exception {
        Queue<Serializable> queue = new ConcurrentLinkedQueue<>();
        queue.add("foo");
        queue.add("bar");
        handler.setPartitionDataQueue(queue);
        handler.setThreads(2);
        handler.setPartitions(2);
        handler.setPartitionAnalyzer(new JsrPartitionHandlerTests.Analyzer());
        handler.afterPropertiesSet();
        Collection<StepExecution> executions = handler.handle(new JsrStepExecutionSplitter(repository, false, "step1", true), stepExecution);
        Assert.assertEquals(2, executions.size());
        Assert.assertEquals(2, count.get());
        Assert.assertEquals("foobar", JsrPartitionHandlerTests.Analyzer.collectorData);
        Assert.assertEquals("COMPLETEDdone", JsrPartitionHandlerTests.Analyzer.status);
    }

    @Test
    public void testRestartNoOverride() throws Exception {
        JobExecution execution1 = AbstractJsrTestCase.runJob("jsrPartitionHandlerRestartWithOverrideJob", null, 1000000L);
        Assert.assertEquals(FAILED, execution1.getBatchStatus());
        Assert.assertEquals(1, JsrPartitionHandlerTests.MyPartitionReducer.beginCount);
        Assert.assertEquals(0, JsrPartitionHandlerTests.MyPartitionReducer.beforeCount);
        Assert.assertEquals(1, JsrPartitionHandlerTests.MyPartitionReducer.rollbackCount);
        Assert.assertEquals(1, JsrPartitionHandlerTests.MyPartitionReducer.afterCount);
        Assert.assertEquals(3, JsrPartitionHandlerTests.CountingPartitionCollector.collected);
        JsrPartitionHandlerTests.MyPartitionReducer.reset();
        JsrPartitionHandlerTests.CountingPartitionCollector.reset();
        JobExecution execution2 = AbstractJsrTestCase.restartJob(execution1.getExecutionId(), null, 1000000L);
        Assert.assertEquals(COMPLETED, execution2.getBatchStatus());
        Assert.assertEquals(1, JsrPartitionHandlerTests.MyPartitionReducer.beginCount);
        Assert.assertEquals(1, JsrPartitionHandlerTests.MyPartitionReducer.beforeCount);
        Assert.assertEquals(0, JsrPartitionHandlerTests.MyPartitionReducer.rollbackCount);
        Assert.assertEquals(1, JsrPartitionHandlerTests.MyPartitionReducer.afterCount);
        Assert.assertEquals(1, JsrPartitionHandlerTests.CountingPartitionCollector.collected);
    }

    @Test
    public void testRestartOverride() throws Exception {
        Properties jobParameters = new Properties();
        jobParameters.put("mapper.override", "true");
        JobExecution execution1 = AbstractJsrTestCase.runJob("jsrPartitionHandlerRestartWithOverrideJob", jobParameters, 1000000L);
        Assert.assertEquals(FAILED, execution1.getBatchStatus());
        Assert.assertEquals(1, JsrPartitionHandlerTests.MyPartitionReducer.beginCount);
        Assert.assertEquals(0, JsrPartitionHandlerTests.MyPartitionReducer.beforeCount);
        Assert.assertEquals(1, JsrPartitionHandlerTests.MyPartitionReducer.rollbackCount);
        Assert.assertEquals(1, JsrPartitionHandlerTests.MyPartitionReducer.afterCount);
        Assert.assertEquals(3, JsrPartitionHandlerTests.CountingPartitionCollector.collected);
        JsrPartitionHandlerTests.MyPartitionReducer.reset();
        JsrPartitionHandlerTests.CountingPartitionCollector.reset();
        JobExecution execution2 = AbstractJsrTestCase.restartJob(execution1.getExecutionId(), jobParameters, 1000000L);
        Assert.assertEquals(COMPLETED, execution2.getBatchStatus());
        Assert.assertEquals(1, JsrPartitionHandlerTests.MyPartitionReducer.beginCount);
        Assert.assertEquals(1, JsrPartitionHandlerTests.MyPartitionReducer.beforeCount);
        Assert.assertEquals(0, JsrPartitionHandlerTests.MyPartitionReducer.rollbackCount);
        Assert.assertEquals(1, JsrPartitionHandlerTests.MyPartitionReducer.afterCount);
        Assert.assertEquals(5, JsrPartitionHandlerTests.CountingPartitionCollector.collected);
    }

    public static class CountingPartitionCollector implements PartitionCollector {
        public static int collected = 0;

        public static void reset() {
            JsrPartitionHandlerTests.CountingPartitionCollector.collected = 0;
        }

        @Override
        public Serializable collectPartitionData() throws Exception {
            (JsrPartitionHandlerTests.CountingPartitionCollector.collected)++;
            return null;
        }
    }

    public static class MyPartitionReducer implements PartitionReducer {
        public static int beginCount = 0;

        public static int beforeCount = 0;

        public static int rollbackCount = 0;

        public static int afterCount = 0;

        public static void reset() {
            JsrPartitionHandlerTests.MyPartitionReducer.beginCount = 0;
            JsrPartitionHandlerTests.MyPartitionReducer.beforeCount = 0;
            JsrPartitionHandlerTests.MyPartitionReducer.rollbackCount = 0;
            JsrPartitionHandlerTests.MyPartitionReducer.afterCount = 0;
        }

        @Override
        public void beginPartitionedStep() throws Exception {
            (JsrPartitionHandlerTests.MyPartitionReducer.beginCount)++;
        }

        @Override
        public void beforePartitionedStepCompletion() throws Exception {
            (JsrPartitionHandlerTests.MyPartitionReducer.beforeCount)++;
        }

        @Override
        public void rollbackPartitionedStep() throws Exception {
            (JsrPartitionHandlerTests.MyPartitionReducer.rollbackCount)++;
        }

        @Override
        public void afterPartitionedStepCompletion(PartitionStatus status) throws Exception {
            (JsrPartitionHandlerTests.MyPartitionReducer.afterCount)++;
        }
    }

    public static class MyPartitionMapper implements PartitionMapper {
        private static int count = 0;

        @Inject
        @BatchProperty
        String overrideString = "false";

        @Override
        public PartitionPlan mapPartitions() throws Exception {
            (JsrPartitionHandlerTests.MyPartitionMapper.count)++;
            PartitionPlan plan = new PartitionPlanImpl();
            if (((JsrPartitionHandlerTests.MyPartitionMapper.count) % 2) == 1) {
                plan.setPartitions(3);
                plan.setThreads(3);
            } else {
                plan.setPartitions(5);
                plan.setThreads(5);
            }
            plan.setPartitionsOverride(Boolean.valueOf(overrideString));
            Properties[] props = new Properties[3];
            props[0] = new Properties();
            props[1] = new Properties();
            props[2] = new Properties();
            if (((JsrPartitionHandlerTests.MyPartitionMapper.count) % 2) == 1) {
                props[1].put("fail", "true");
            }
            plan.setPartitionProperties(props);
            return plan;
        }
    }

    public static class MyBatchlet extends BatchletSupport {
        @Inject
        @BatchProperty
        String fail;

        @Override
        public String process() {
            if ("true".equalsIgnoreCase(fail)) {
                throw new RuntimeException("Expected");
            }
            return null;
        }
    }

    public static class Analyzer implements PartitionAnalyzer {
        public static String collectorData;

        public static String status;

        @Override
        public void analyzeCollectorData(Serializable data) throws Exception {
            JsrPartitionHandlerTests.Analyzer.collectorData = (JsrPartitionHandlerTests.Analyzer.collectorData) + data;
        }

        @Override
        public void analyzeStatus(BatchStatus batchStatus, String exitStatus) throws Exception {
            JsrPartitionHandlerTests.Analyzer.status = batchStatus + exitStatus;
        }
    }
}

