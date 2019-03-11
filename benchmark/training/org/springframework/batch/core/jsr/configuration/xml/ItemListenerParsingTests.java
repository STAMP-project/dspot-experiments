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
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ItemListenerParsingTests {
    @Autowired
    public Job job;

    @Autowired
    public JobLauncher jobLauncher;

    @Autowired
    public ItemListenerParsingTests.SpringItemListener springListener;

    @Autowired
    public ItemListenerParsingTests.JsrItemListener jsrListener;

    @Test
    public void test() throws Exception {
        JobExecution execution = jobLauncher.run(job, new JobParameters());
        Assert.assertEquals(COMPLETED, execution.getStatus());
        Assert.assertEquals(3, execution.getStepExecutions().size());
        Assert.assertEquals(6, springListener.beforeReadCount);
        Assert.assertEquals(4, springListener.afterReadCount);
        Assert.assertEquals(4, springListener.beforeProcessCount);
        Assert.assertEquals(4, springListener.afterProcessCount);
        Assert.assertEquals(2, springListener.beforeWriteCount);
        Assert.assertEquals(2, springListener.afterWriteCount);
        Assert.assertEquals(6, jsrListener.beforeReadCount);
        Assert.assertEquals(4, jsrListener.afterReadCount);
        Assert.assertEquals(4, jsrListener.beforeProcessCount);
        Assert.assertEquals(4, jsrListener.afterProcessCount);
        Assert.assertEquals(2, jsrListener.beforeWriteCount);
        Assert.assertEquals(2, jsrListener.afterWriteCount);
    }

    public static class SpringItemListener implements ItemProcessListener<Object, Object> , ItemReadListener<Object> , ItemWriteListener<Object> {
        protected int beforeReadCount = 0;

        protected int afterReadCount = 0;

        protected int onReadErrorCount = 0;

        protected int beforeProcessCount = 0;

        protected int afterProcessCount = 0;

        protected int onProcessErrorCount = 0;

        protected int beforeWriteCount = 0;

        protected int afterWriteCount = 0;

        protected int onWriteErrorCount = 0;

        @Override
        public void beforeRead() {
            (beforeReadCount)++;
        }

        @Override
        public void afterRead(Object item) {
            (afterReadCount)++;
        }

        @Override
        public void onReadError(Exception ex) {
            (onReadErrorCount)++;
        }

        @Override
        public void beforeWrite(List<? extends Object> items) {
            (beforeWriteCount)++;
        }

        @Override
        public void afterWrite(List<? extends Object> items) {
            (afterWriteCount)++;
        }

        @Override
        public void onWriteError(Exception exception, List<? extends Object> items) {
            (onWriteErrorCount)++;
        }

        @Override
        public void beforeProcess(Object item) {
            (beforeProcessCount)++;
        }

        @Override
        public void afterProcess(Object item, Object result) {
            (afterProcessCount)++;
        }

        @Override
        public void onProcessError(Object item, Exception e) {
            (onProcessErrorCount)++;
        }
    }

    public static class JsrItemListener implements javax.batch.api.chunk.listener.ItemProcessListener , javax.batch.api.chunk.listener.ItemReadListener , javax.batch.api.chunk.listener.ItemWriteListener {
        protected int beforeReadCount = 0;

        protected int afterReadCount = 0;

        protected int onReadErrorCount = 0;

        protected int beforeProcessCount = 0;

        protected int afterProcessCount = 0;

        protected int onProcessErrorCount = 0;

        protected int beforeWriteCount = 0;

        protected int afterWriteCount = 0;

        protected int onWriteErrorCount = 0;

        @Override
        public void beforeWrite(List<Object> items) throws Exception {
            (beforeWriteCount)++;
        }

        @Override
        public void afterWrite(List<Object> items) throws Exception {
            (afterWriteCount)++;
        }

        @Override
        public void onWriteError(List<Object> items, Exception ex) throws Exception {
            (onWriteErrorCount)++;
        }

        @Override
        public void beforeProcess(Object item) throws Exception {
            (beforeProcessCount)++;
        }

        @Override
        public void afterProcess(Object item, Object result) throws Exception {
            (afterProcessCount)++;
        }

        @Override
        public void onProcessError(Object item, Exception ex) throws Exception {
            (onProcessErrorCount)++;
        }

        @Override
        public void beforeRead() throws Exception {
            (beforeReadCount)++;
        }

        @Override
        public void afterRead(Object item) throws Exception {
            (afterReadCount)++;
        }

        @Override
        public void onReadError(Exception ex) throws Exception {
            (onReadErrorCount)++;
        }
    }
}

