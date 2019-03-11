/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.s3a;


import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import org.apache.hadoop.util.BlockingThreadPoolExecutorService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Basic test for S3A's blocking executor service.
 */
public class ITestBlockingThreadPoolExecutorService {
    private static final Logger LOG = LoggerFactory.getLogger(BlockingThreadPoolExecutorService.class);

    private static final int NUM_ACTIVE_TASKS = 4;

    private static final int NUM_WAITING_TASKS = 2;

    private static final int TASK_SLEEP_MSEC = 100;

    private static final int SHUTDOWN_WAIT_MSEC = 200;

    private static final int SHUTDOWN_WAIT_TRIES = 5;

    private static final int BLOCKING_THRESHOLD_MSEC = 50;

    private static final Integer SOME_VALUE = 1337;

    private static BlockingThreadPoolExecutorService tpe;

    @Rule
    public Timeout testTimeout = new Timeout((60 * 1000));

    /**
     * Basic test of running one trivial task.
     */
    @Test
    public void testSubmitCallable() throws Exception {
        ITestBlockingThreadPoolExecutorService.ensureCreated();
        ListenableFuture<Integer> f = ITestBlockingThreadPoolExecutorService.tpe.submit(callableSleeper);
        Integer v = f.get();
        Assert.assertEquals(ITestBlockingThreadPoolExecutorService.SOME_VALUE, v);
    }

    /**
     * More involved test, including detecting blocking when at capacity.
     */
    @Test
    public void testSubmitRunnable() throws Exception {
        ITestBlockingThreadPoolExecutorService.ensureCreated();
        verifyQueueSize(ITestBlockingThreadPoolExecutorService.tpe, ((ITestBlockingThreadPoolExecutorService.NUM_ACTIVE_TASKS) + (ITestBlockingThreadPoolExecutorService.NUM_WAITING_TASKS)));
    }

    @Test
    public void testShutdown() throws Exception {
        // Cover create / destroy, regardless of when this test case runs
        ITestBlockingThreadPoolExecutorService.ensureCreated();
        ITestBlockingThreadPoolExecutorService.ensureDestroyed();
        // Cover create, execute, destroy, regardless of when test case runs
        ITestBlockingThreadPoolExecutorService.ensureCreated();
        testSubmitRunnable();
        ITestBlockingThreadPoolExecutorService.ensureDestroyed();
    }

    @Test
    public void testChainedQueue() throws Throwable {
        ITestBlockingThreadPoolExecutorService.ensureCreated();
        int size = 2;
        ExecutorService wrapper = new org.apache.hadoop.util.SemaphoredDelegatingExecutor(ITestBlockingThreadPoolExecutorService.tpe, size, true);
        verifyQueueSize(wrapper, size);
    }

    private Runnable sleeper = new Runnable() {
        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            try {
                Thread.sleep(ITestBlockingThreadPoolExecutorService.TASK_SLEEP_MSEC);
            } catch (InterruptedException e) {
                ITestBlockingThreadPoolExecutorService.LOG.info("Thread {} interrupted.", name);
                Thread.currentThread().interrupt();
            }
        }
    };

    private Callable<Integer> callableSleeper = new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
            sleeper.run();
            return ITestBlockingThreadPoolExecutorService.SOME_VALUE;
        }
    };
}

