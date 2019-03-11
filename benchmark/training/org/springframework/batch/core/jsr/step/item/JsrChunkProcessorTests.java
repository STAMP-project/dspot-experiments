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
package org.springframework.batch.core.jsr.step.item;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.jsr.step.builder.JsrSimpleStepBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;


public class JsrChunkProcessorTests {
    private JsrChunkProcessorTests.FailingListItemReader reader;

    private JsrChunkProcessorTests.FailingCountingItemProcessor processor;

    private JsrChunkProcessorTests.StoringItemWriter writer;

    private JsrChunkProcessorTests.CountingListener readListener;

    private JsrSimpleStepBuilder<String, String> builder;

    private JobRepository repository;

    private StepExecution stepExecution;

    @Test
    public void testNoInputNoListeners() throws Exception {
        reader = new JsrChunkProcessorTests.FailingListItemReader(new ArrayList<>());
        Step step = builder.chunk(25).reader(reader).processor(processor).writer(writer).listener(((ItemReadListener<String>) (readListener))).build();
        runStep(step);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(0, processor.count);
        Assert.assertEquals(0, writer.results.size());
        Assert.assertEquals(0, stepExecution.getProcessSkipCount());
        Assert.assertEquals(0, stepExecution.getReadCount());
        Assert.assertEquals(0, stepExecution.getReadSkipCount());
        Assert.assertEquals(0, stepExecution.getSkipCount());
        Assert.assertEquals(0, stepExecution.getWriteCount());
        Assert.assertEquals(0, stepExecution.getFilterCount());
        Assert.assertEquals(0, stepExecution.getWriteSkipCount());
    }

    @Test
    public void testSimpleScenarioNoListeners() throws Exception {
        Step step = builder.chunk(25).reader(reader).processor(processor).writer(writer).build();
        runStep(step);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(0, stepExecution.getProcessSkipCount());
        Assert.assertEquals(25, stepExecution.getReadCount());
        Assert.assertEquals(0, stepExecution.getReadSkipCount());
        Assert.assertEquals(0, stepExecution.getSkipCount());
        Assert.assertEquals(25, stepExecution.getWriteCount());
        Assert.assertEquals(0, stepExecution.getFilterCount());
        Assert.assertEquals(0, stepExecution.getWriteSkipCount());
        Assert.assertEquals(25, writer.results.size());
        Assert.assertEquals(25, processor.count);
        int count = 0;
        for (String curItem : writer.results) {
            Assert.assertEquals(("item " + count), curItem);
            count++;
        }
    }

    @Test
    public void testSimpleScenarioNoProcessor() throws Exception {
        Step step = builder.chunk(25).reader(reader).writer(writer).listener(((ItemReadListener<String>) (readListener))).build();
        runStep(step);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(0, stepExecution.getProcessSkipCount());
        Assert.assertEquals(25, stepExecution.getReadCount());
        Assert.assertEquals(0, stepExecution.getReadSkipCount());
        Assert.assertEquals(0, stepExecution.getSkipCount());
        Assert.assertEquals(25, stepExecution.getWriteCount());
        Assert.assertEquals(0, stepExecution.getFilterCount());
        Assert.assertEquals(0, stepExecution.getWriteSkipCount());
        Assert.assertEquals(0, readListener.afterProcess);
        Assert.assertEquals(25, readListener.afterRead);
        Assert.assertEquals(1, readListener.afterWrite);
        Assert.assertEquals(0, readListener.beforeProcess);
        Assert.assertEquals(26, readListener.beforeRead);
        Assert.assertEquals(1, readListener.beforeWriteCount);
        Assert.assertEquals(0, readListener.onProcessError);
        Assert.assertEquals(0, readListener.onReadError);
        Assert.assertEquals(0, readListener.onWriteError);
        Assert.assertEquals(0, processor.count);
        int count = 0;
        for (String curItem : writer.results) {
            Assert.assertEquals(("item " + count), curItem);
            count++;
        }
    }

    @Test
    public void testProcessorFilteringNoListeners() throws Exception {
        processor.filter = true;
        Step step = builder.chunk(25).reader(reader).processor(processor).writer(writer).listener(((ItemReadListener<String>) (readListener))).build();
        runStep(step);
        int count = 0;
        for (String curItem : writer.results) {
            Assert.assertEquals(("item " + count), curItem);
            count += 2;
        }
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(0, stepExecution.getProcessSkipCount());
        Assert.assertEquals(25, stepExecution.getReadCount());
        Assert.assertEquals(0, stepExecution.getReadSkipCount());
        Assert.assertEquals(0, stepExecution.getSkipCount());
        Assert.assertEquals(13, stepExecution.getWriteCount());
        Assert.assertEquals(12, stepExecution.getFilterCount());
        Assert.assertEquals(0, stepExecution.getWriteSkipCount());
        Assert.assertEquals(25, processor.count);
    }

    @Test
    public void testReadError() throws Exception {
        reader.failCount = 10;
        Step step = builder.chunk(25).reader(reader).processor(processor).writer(writer).listener(((ItemReadListener<String>) (readListener))).build();
        runStep(step);
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals(9, processor.count);
        Assert.assertEquals(0, writer.results.size());
        Assert.assertEquals(0, stepExecution.getProcessSkipCount());
        Assert.assertEquals(9, stepExecution.getReadCount());
        Assert.assertEquals(0, stepExecution.getReadSkipCount());
        Assert.assertEquals(0, stepExecution.getSkipCount());
        Assert.assertEquals(0, stepExecution.getWriteCount());
        Assert.assertEquals(0, stepExecution.getFilterCount());
        Assert.assertEquals(0, stepExecution.getWriteSkipCount());
        Assert.assertEquals(1, stepExecution.getFailureExceptions().size());
        Assert.assertEquals("expected at read index 10", stepExecution.getFailureExceptions().get(0).getMessage());
        Assert.assertEquals(9, readListener.afterProcess);
        Assert.assertEquals(9, readListener.afterRead);
        Assert.assertEquals(0, readListener.afterWrite);
        Assert.assertEquals(9, readListener.beforeProcess);
        Assert.assertEquals(10, readListener.beforeRead);
        Assert.assertEquals(0, readListener.beforeWriteCount);
        Assert.assertEquals(0, readListener.onProcessError);
        Assert.assertEquals(1, readListener.onReadError);
        Assert.assertEquals(0, readListener.onWriteError);
    }

    @Test
    public void testProcessError() throws Exception {
        processor.failCount = 10;
        Step step = builder.chunk(25).reader(reader).processor(processor).writer(writer).listener(((ItemReadListener<String>) (readListener))).build();
        runStep(step);
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals(10, processor.count);
        Assert.assertEquals(0, writer.results.size());
        Assert.assertEquals(0, stepExecution.getProcessSkipCount());
        Assert.assertEquals(10, stepExecution.getReadCount());
        Assert.assertEquals(0, stepExecution.getReadSkipCount());
        Assert.assertEquals(0, stepExecution.getSkipCount());
        Assert.assertEquals(0, stepExecution.getWriteCount());
        Assert.assertEquals(0, stepExecution.getFilterCount());
        Assert.assertEquals(0, stepExecution.getWriteSkipCount());
        Assert.assertEquals("expected at process index 10", stepExecution.getFailureExceptions().get(0).getMessage());
        Assert.assertEquals(9, readListener.afterProcess);
        Assert.assertEquals(10, readListener.afterRead);
        Assert.assertEquals(0, readListener.afterWrite);
        Assert.assertEquals(10, readListener.beforeProcess);
        Assert.assertEquals(10, readListener.beforeRead);
        Assert.assertEquals(0, readListener.beforeWriteCount);
        Assert.assertEquals(1, readListener.onProcessError);
        Assert.assertEquals(0, readListener.onReadError);
        Assert.assertEquals(0, readListener.onWriteError);
    }

    @Test
    public void testWriteError() throws Exception {
        writer.fail = true;
        Step step = builder.chunk(25).reader(reader).processor(processor).writer(writer).listener(((ItemReadListener<String>) (readListener))).build();
        runStep(step);
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals(25, processor.count);
        Assert.assertEquals(0, writer.results.size());
        Assert.assertEquals(0, stepExecution.getProcessSkipCount());
        Assert.assertEquals(25, stepExecution.getReadCount());
        Assert.assertEquals(0, stepExecution.getReadSkipCount());
        Assert.assertEquals(0, stepExecution.getSkipCount());
        Assert.assertEquals(0, stepExecution.getWriteCount());
        Assert.assertEquals(0, stepExecution.getFilterCount());
        Assert.assertEquals(0, stepExecution.getWriteSkipCount());
        Assert.assertEquals("expected in write", stepExecution.getFailureExceptions().get(0).getMessage());
        Assert.assertEquals(25, readListener.afterProcess);
        Assert.assertEquals(25, readListener.afterRead);
        Assert.assertEquals(0, readListener.afterWrite);
        Assert.assertEquals(25, readListener.beforeProcess);
        Assert.assertEquals(25, readListener.beforeRead);
        Assert.assertEquals(1, readListener.beforeWriteCount);
        Assert.assertEquals(0, readListener.onProcessError);
        Assert.assertEquals(0, readListener.onReadError);
        Assert.assertEquals(1, readListener.onWriteError);
    }

    @Test
    public void testMultipleChunks() throws Exception {
        Step step = builder.chunk(10).reader(reader).processor(processor).writer(writer).listener(((ItemReadListener<String>) (readListener))).build();
        runStep(step);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(25, processor.count);
        Assert.assertEquals(25, writer.results.size());
        Assert.assertEquals(0, stepExecution.getProcessSkipCount());
        Assert.assertEquals(25, stepExecution.getReadCount());
        Assert.assertEquals(0, stepExecution.getReadSkipCount());
        Assert.assertEquals(0, stepExecution.getSkipCount());
        Assert.assertEquals(25, stepExecution.getWriteCount());
        Assert.assertEquals(0, stepExecution.getFilterCount());
        Assert.assertEquals(0, stepExecution.getWriteSkipCount());
        Assert.assertEquals(25, readListener.afterProcess);
        Assert.assertEquals(25, readListener.afterRead);
        Assert.assertEquals(3, readListener.afterWrite);
        Assert.assertEquals(25, readListener.beforeProcess);
        Assert.assertEquals(26, readListener.beforeRead);
        Assert.assertEquals(3, readListener.beforeWriteCount);
        Assert.assertEquals(0, readListener.onProcessError);
        Assert.assertEquals(0, readListener.onReadError);
        Assert.assertEquals(0, readListener.onWriteError);
    }

    public static class FailingListItemReader extends ListItemReader<String> {
        protected int failCount = -1;

        protected int count = 0;

        public FailingListItemReader(List<String> list) {
            super(list);
        }

        @Override
        public String read() {
            (count)++;
            if ((failCount) == (count)) {
                throw new RuntimeException(("expected at read index " + (failCount)));
            } else {
                return super.read();
            }
        }
    }

    public static class FailingCountingItemProcessor implements ItemProcessor<String, String> {
        protected int count = 0;

        protected int failCount = -1;

        protected boolean filter = false;

        @Override
        public String process(String item) throws Exception {
            (count)++;
            if ((filter) && (((count) % 2) == 0)) {
                return null;
            } else
                if ((count) == (failCount)) {
                    throw new RuntimeException(("expected at process index " + (failCount)));
                } else {
                    return item;
                }

        }
    }

    public static class StoringItemWriter implements ItemWriter<String> {
        protected List<String> results = new ArrayList<>();

        protected boolean fail = false;

        @Override
        public void write(List<? extends String> items) throws Exception {
            if (fail) {
                throw new RuntimeException("expected in write");
            }
            results.addAll(items);
        }
    }

    public static class CountingListener implements ItemProcessListener<String, String> , ItemReadListener<String> , ItemWriteListener<String> {
        protected int beforeWriteCount = 0;

        protected int afterWrite = 0;

        protected int onWriteError = 0;

        protected int beforeProcess = 0;

        protected int afterProcess = 0;

        protected int onProcessError = 0;

        protected int beforeRead = 0;

        protected int afterRead = 0;

        protected int onReadError = 0;

        @Override
        public void beforeWrite(List<? extends String> items) {
            (beforeWriteCount)++;
        }

        @Override
        public void afterWrite(List<? extends String> items) {
            (afterWrite)++;
        }

        @Override
        public void onWriteError(Exception exception, List<? extends String> items) {
            (onWriteError)++;
        }

        @Override
        public void beforeProcess(String item) {
            (beforeProcess)++;
        }

        @Override
        public void afterProcess(String item, String result) {
            (afterProcess)++;
        }

        @Override
        public void onProcessError(String item, Exception e) {
            (onProcessError)++;
        }

        @Override
        public void beforeRead() {
            (beforeRead)++;
        }

        @Override
        public void afterRead(String item) {
            (afterRead)++;
        }

        @Override
        public void onReadError(Exception ex) {
            (onReadError)++;
        }
    }
}

