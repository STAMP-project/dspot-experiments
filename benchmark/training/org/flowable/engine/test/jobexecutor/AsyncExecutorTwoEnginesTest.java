/**
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
package org.flowable.engine.test.jobexecutor;


import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.flowable.engine.ProcessEngine;
import org.flowable.job.api.JobInfo;
import org.flowable.job.service.impl.asyncexecutor.DefaultAsyncJobExecutor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests specifically for the {@link AsyncExecutor}.
 *
 * @author Joram Barrez
 */
public class AsyncExecutorTwoEnginesTest {
    @Test
    public void testAsyncScriptExecutionOnTwoEngines() {
        ProcessEngine firstProcessEngine = null;
        ProcessEngine secondProcessEngine = null;
        try {
            // Deploy
            firstProcessEngine = createProcessEngine(false);
            Date now = setClockToCurrentTime(firstProcessEngine);
            deploy(firstProcessEngine, "AsyncExecutorTest.testAsyncScriptExecution.bpmn20.xml");
            // Start process instance. Nothing should happen
            firstProcessEngine.getRuntimeService().startProcessInstanceByKey("asyncScript");
            Assert.assertEquals(0, firstProcessEngine.getTaskService().createTaskQuery().taskName("Task after script").count());
            Assert.assertEquals(1, firstProcessEngine.getManagementService().createJobQuery().count());
            // Start second engine, with async executor enabled
            secondProcessEngine = createProcessEngine(true, now);// Same timestamp as first engine

            Assert.assertEquals(0, firstProcessEngine.getTaskService().createTaskQuery().taskName("Task after script").count());
            Assert.assertEquals(1, firstProcessEngine.getManagementService().createJobQuery().count());
            // Move the clock 1 second. Should be executed now by second engine
            addSecondsToCurrentTime(secondProcessEngine, 1);
            waitForAllJobsBeingExecuted(secondProcessEngine, 10000L);
            // Verify if all is as expected
            Assert.assertEquals(1, firstProcessEngine.getTaskService().createTaskQuery().taskName("Task after script").count());
            Assert.assertEquals(0, firstProcessEngine.getManagementService().createJobQuery().count());
            Assert.assertEquals(0, getAsyncExecutorJobCount(firstProcessEngine));
            Assert.assertEquals(1, getAsyncExecutorJobCount(secondProcessEngine));
        } finally {
            // Clean up
            cleanup(firstProcessEngine);
            cleanup(secondProcessEngine);
        }
    }

    static class CountingAsyncExecutor extends DefaultAsyncJobExecutor {
        private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExecutorTwoEnginesTest.CountingAsyncExecutor.class);

        private AtomicInteger counter = new AtomicInteger(0);

        @Override
        public boolean executeAsyncJob(JobInfo job) {
            AsyncExecutorTwoEnginesTest.CountingAsyncExecutor.LOGGER.info("About to execute job {}", job.getId());
            counter.incrementAndGet();
            boolean success = super.executeAsyncJob(job);
            AsyncExecutorTwoEnginesTest.CountingAsyncExecutor.LOGGER.info("Handed off job {} to async executor (retries={})", job.getId(), job.getRetries());
            return success;
        }

        public AtomicInteger getCounter() {
            return counter;
        }

        public void setCounter(AtomicInteger counter) {
            this.counter = counter;
        }
    }
}

