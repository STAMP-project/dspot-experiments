/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.util.concurrent;


import java.util.concurrent.ExecutorService;
import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public final class ExecutorServiceObjectTest {
    private ExecutorServiceObject executorServiceObject;

    @Test
    public void assertCreateExecutorService() {
        executorServiceObject = new ExecutorServiceObject("executor-service-test", 1);
        MatcherAssert.assertThat(executorServiceObject.getActiveThreadCount(), Is.is(0));
        MatcherAssert.assertThat(executorServiceObject.getWorkQueueSize(), Is.is(0));
        TestCase.assertFalse(executorServiceObject.isShutdown());
        ExecutorService executorService = executorServiceObject.createExecutorService();
        executorService.submit(new ExecutorServiceObjectTest.FooTask());
        BlockUtils.waitingShortTime();
        MatcherAssert.assertThat(executorServiceObject.getActiveThreadCount(), Is.is(1));
        MatcherAssert.assertThat(executorServiceObject.getWorkQueueSize(), Is.is(0));
        TestCase.assertFalse(executorServiceObject.isShutdown());
        executorService.submit(new ExecutorServiceObjectTest.FooTask());
        BlockUtils.waitingShortTime();
        MatcherAssert.assertThat(executorServiceObject.getActiveThreadCount(), Is.is(1));
        MatcherAssert.assertThat(executorServiceObject.getWorkQueueSize(), Is.is(1));
        TestCase.assertFalse(executorServiceObject.isShutdown());
        executorService.shutdownNow();
        MatcherAssert.assertThat(executorServiceObject.getWorkQueueSize(), Is.is(0));
        TestCase.assertTrue(executorServiceObject.isShutdown());
    }

    class FooTask implements Runnable {
        @Override
        public void run() {
            BlockUtils.sleep(1000L);
        }
    }
}

