/**
 * Copyright 2013 the original author or authors.
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
package org.springframework.batch.core.jsr.configuration.xml;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import Metric.MetricType.PROCESS_SKIP_COUNT;
import Metric.MetricType.READ_SKIP_COUNT;
import Metric.MetricType.WRITE_SKIP_COUNT;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.batch.api.chunk.listener.SkipProcessListener;
import javax.batch.api.chunk.listener.SkipReadListener;
import javax.batch.api.chunk.listener.SkipWriteListener;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.StepExecution;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.jsr.AbstractJsrTestCase;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;


public class ItemSkipParsingTests extends AbstractJsrTestCase {
    @Test
    public void test() throws Exception {
        JobExecution execution = AbstractJsrTestCase.runJob("ItemSkipParsingTests-context", new Properties(), 10000L);
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Assert.assertEquals(FAILED, execution.getBatchStatus());
        List<StepExecution> stepExecutions = jobOperator.getStepExecutions(execution.getExecutionId());
        Assert.assertEquals(1, AbstractJsrTestCase.getMetric(stepExecutions.get(0), READ_SKIP_COUNT).getValue());
        Assert.assertEquals(1, ItemSkipParsingTests.TestSkipListener.readSkips);
        Assert.assertEquals(0, ItemSkipParsingTests.TestSkipListener.processSkips);
        Assert.assertEquals(0, ItemSkipParsingTests.TestSkipListener.writeSkips);
        // Process skip and fail
        execution = AbstractJsrTestCase.restartJob(execution.getExecutionId(), new Properties(), 10000L);
        Assert.assertEquals(FAILED, execution.getBatchStatus());
        stepExecutions = jobOperator.getStepExecutions(execution.getExecutionId());
        Assert.assertEquals(1, AbstractJsrTestCase.getMetric(stepExecutions.get(0), PROCESS_SKIP_COUNT).getValue());
        Assert.assertEquals(0, ItemSkipParsingTests.TestSkipListener.readSkips);
        Assert.assertEquals(1, ItemSkipParsingTests.TestSkipListener.processSkips);
        Assert.assertEquals(0, ItemSkipParsingTests.TestSkipListener.writeSkips);
        // Write skip and fail
        execution = AbstractJsrTestCase.restartJob(execution.getExecutionId(), new Properties(), 10000L);
        Assert.assertEquals(FAILED, execution.getBatchStatus());
        stepExecutions = jobOperator.getStepExecutions(execution.getExecutionId());
        Assert.assertEquals(1, AbstractJsrTestCase.getMetric(stepExecutions.get(0), WRITE_SKIP_COUNT).getValue());
        Assert.assertEquals(0, ItemSkipParsingTests.TestSkipListener.readSkips);
        Assert.assertEquals(0, ItemSkipParsingTests.TestSkipListener.processSkips);
        Assert.assertEquals(1, ItemSkipParsingTests.TestSkipListener.writeSkips);
        // Complete
        execution = AbstractJsrTestCase.restartJob(execution.getExecutionId(), new Properties(), 10000L);
        Assert.assertEquals(COMPLETED, execution.getBatchStatus());
        stepExecutions = jobOperator.getStepExecutions(execution.getExecutionId());
        Assert.assertEquals(0, AbstractJsrTestCase.getMetric(stepExecutions.get(0), WRITE_SKIP_COUNT).getValue());
        Assert.assertEquals(0, ItemSkipParsingTests.TestSkipListener.readSkips);
        Assert.assertEquals(0, ItemSkipParsingTests.TestSkipListener.processSkips);
        Assert.assertEquals(0, ItemSkipParsingTests.TestSkipListener.writeSkips);
    }

    public static class SkipErrorGeneratingReader implements ItemReader<String> {
        private static int count = 0;

        @Override
        public String read() throws Exception {
            (ItemSkipParsingTests.SkipErrorGeneratingReader.count)++;
            if ((ItemSkipParsingTests.SkipErrorGeneratingReader.count) == 1) {
                throw new Exception("read skip me");
            } else
                if ((ItemSkipParsingTests.SkipErrorGeneratingReader.count) == 2) {
                    return "item" + (ItemSkipParsingTests.SkipErrorGeneratingReader.count);
                } else
                    if ((ItemSkipParsingTests.SkipErrorGeneratingReader.count) == 3) {
                        throw new RuntimeException("read fail because of me");
                    } else
                        if ((ItemSkipParsingTests.SkipErrorGeneratingReader.count) < 15) {
                            return "item" + (ItemSkipParsingTests.SkipErrorGeneratingReader.count);
                        } else {
                            return null;
                        }



        }
    }

    public static class SkipErrorGeneratingProcessor implements ItemProcessor<String, String> {
        private static int count = 0;

        @Override
        public String process(String item) throws Exception {
            (ItemSkipParsingTests.SkipErrorGeneratingProcessor.count)++;
            if ((ItemSkipParsingTests.SkipErrorGeneratingProcessor.count) == 4) {
                throw new Exception("process skip me");
            } else
                if ((ItemSkipParsingTests.SkipErrorGeneratingProcessor.count) == 5) {
                    return item;
                } else
                    if ((ItemSkipParsingTests.SkipErrorGeneratingProcessor.count) == 6) {
                        throw new RuntimeException("process fail because of me");
                    } else {
                        return item;
                    }


        }
    }

    public static class SkipErrorGeneratingWriter implements ItemWriter<String> {
        private static int count = 0;

        protected List<String> writtenItems = new ArrayList<>();

        private List<String> skippedItems = new ArrayList<>();

        @Override
        public void write(List<? extends String> items) throws Exception {
            if (((items.size()) > 0) && (!(skippedItems.contains(items.get(0))))) {
                (ItemSkipParsingTests.SkipErrorGeneratingWriter.count)++;
            }
            if ((ItemSkipParsingTests.SkipErrorGeneratingWriter.count) == 7) {
                skippedItems.addAll(items);
                throw new Exception("write skip me");
            } else
                if ((ItemSkipParsingTests.SkipErrorGeneratingWriter.count) == 9) {
                    skippedItems = new ArrayList<>();
                    throw new RuntimeException("write fail because of me");
                } else {
                    writtenItems.addAll(items);
                }

        }
    }

    public static class TestSkipListener implements SkipProcessListener , SkipReadListener , SkipWriteListener {
        protected static int readSkips = 0;

        protected static int processSkips = 0;

        protected static int writeSkips = 0;

        public TestSkipListener() {
            ItemSkipParsingTests.TestSkipListener.readSkips = 0;
            ItemSkipParsingTests.TestSkipListener.processSkips = 0;
            ItemSkipParsingTests.TestSkipListener.writeSkips = 0;
        }

        @Override
        public void onSkipProcessItem(Object item, Exception ex) throws Exception {
            (ItemSkipParsingTests.TestSkipListener.processSkips)++;
        }

        @Override
        public void onSkipReadItem(Exception ex) throws Exception {
            (ItemSkipParsingTests.TestSkipListener.readSkips)++;
        }

        @Override
        public void onSkipWriteItem(List<Object> items, Exception ex) throws Exception {
            (ItemSkipParsingTests.TestSkipListener.writeSkips)++;
        }
    }
}

