/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.client.context;


import FlexibleExecutorContext.DEFAULT_MAX_THREADS;
import FlexibleExecutorContext.DEFAULT_MAX_THREAD_IDLE_MILLISECONDS;
import FlexibleExecutorContext.DEFAULT_MIN_THREADS;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Assert;
import org.junit.Test;


public class FlexibleExecutorContextTest {
    private FlexibleExecutorContext executorContext;

    private static final int CUSTOM_MAX_LIFE_CYCLE_THREADS_IDLE_MILLISECONDS = 40;

    private static final int CUSTOM_MAX_API_THREADS_IDLE_MILLISECONDS = 50;

    private static final int CUSTOM_MAX_CALLBACK_THREADS_IDLE_MILLISECONDS = 60;

    private static final int CUSTOM_MAX_LIFE_CYCLE_THREADS = 4;

    private static final int CUSTOM_MAX_API_THREADS = 5;

    private static final int CUSTOM_MAX_CALLBACK_THREADS = 6;

    private static final int CUSTOM_MIN_SCHEDULED_THREADS = 7;

    @Test
    public void testConstructorWithoutParameters() throws Exception {
        executorContext = new FlexibleExecutorContext();
        executorContext.init();
        ExecutorService executorService;
        executorService = executorContext.getLifeCycleExecutor();
        checkThreadPoolExecutorServices(executorService, DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS, DEFAULT_MAX_THREAD_IDLE_MILLISECONDS, "life cycle");
        executorService = executorContext.getApiExecutor();
        checkThreadPoolExecutorServices(executorService, DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS, DEFAULT_MAX_THREAD_IDLE_MILLISECONDS, "api");
        executorService = executorContext.getCallbackExecutor();
        checkThreadPoolExecutorServices(executorService, DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS, DEFAULT_MAX_THREAD_IDLE_MILLISECONDS, "callback");
        executorService = executorContext.getScheduledExecutor();
        checkScheduledExecutorServices(executorService, DEFAULT_MIN_THREADS);
    }

    @Test
    public void testConstructorWithAllParameters() throws Exception {
        executorContext = new FlexibleExecutorContext(FlexibleExecutorContextTest.CUSTOM_MAX_LIFE_CYCLE_THREADS, FlexibleExecutorContextTest.CUSTOM_MAX_API_THREADS, FlexibleExecutorContextTest.CUSTOM_MAX_CALLBACK_THREADS, FlexibleExecutorContextTest.CUSTOM_MIN_SCHEDULED_THREADS);
        executorContext.init();
        ExecutorService executorService;
        executorService = executorContext.getLifeCycleExecutor();
        checkThreadPoolExecutorServices(executorService, DEFAULT_MIN_THREADS, FlexibleExecutorContextTest.CUSTOM_MAX_LIFE_CYCLE_THREADS, DEFAULT_MAX_THREAD_IDLE_MILLISECONDS, "life cycle");
        executorService = executorContext.getApiExecutor();
        checkThreadPoolExecutorServices(executorService, DEFAULT_MIN_THREADS, FlexibleExecutorContextTest.CUSTOM_MAX_API_THREADS, DEFAULT_MAX_THREAD_IDLE_MILLISECONDS, "api");
        executorService = executorContext.getCallbackExecutor();
        checkThreadPoolExecutorServices(executorService, DEFAULT_MIN_THREADS, FlexibleExecutorContextTest.CUSTOM_MAX_CALLBACK_THREADS, DEFAULT_MAX_THREAD_IDLE_MILLISECONDS, "callback");
        executorService = executorContext.getScheduledExecutor();
        checkScheduledExecutorServices(executorService, FlexibleExecutorContextTest.CUSTOM_MIN_SCHEDULED_THREADS);
    }

    @Test
    public void testConstructorWithBuilder() throws Exception {
        executorContext = new FlexibleExecutorContext.FlexibleExecutorContextBuilder().setMaxLifeCycleThreads(FlexibleExecutorContextTest.CUSTOM_MAX_LIFE_CYCLE_THREADS).setMaxLifeCycleThreadsIdleMilliseconds(FlexibleExecutorContextTest.CUSTOM_MAX_LIFE_CYCLE_THREADS_IDLE_MILLISECONDS).setMaxApiThreads(FlexibleExecutorContextTest.CUSTOM_MAX_API_THREADS).setMaxApiThreadsIdleMilliseconds(FlexibleExecutorContextTest.CUSTOM_MAX_API_THREADS_IDLE_MILLISECONDS).setMaxCallbackThreads(FlexibleExecutorContextTest.CUSTOM_MAX_CALLBACK_THREADS).setMaxCallbackThreadsIdleMilliseconds(FlexibleExecutorContextTest.CUSTOM_MAX_CALLBACK_THREADS_IDLE_MILLISECONDS).setMinScheduledThreads(FlexibleExecutorContextTest.CUSTOM_MIN_SCHEDULED_THREADS).build();
        executorContext.init();
        ExecutorService executorService;
        executorService = executorContext.getLifeCycleExecutor();
        checkThreadPoolExecutorServices(executorService, DEFAULT_MIN_THREADS, FlexibleExecutorContextTest.CUSTOM_MAX_LIFE_CYCLE_THREADS, FlexibleExecutorContextTest.CUSTOM_MAX_LIFE_CYCLE_THREADS_IDLE_MILLISECONDS, "life cycle");
        executorService = executorContext.getApiExecutor();
        checkThreadPoolExecutorServices(executorService, DEFAULT_MIN_THREADS, FlexibleExecutorContextTest.CUSTOM_MAX_API_THREADS, FlexibleExecutorContextTest.CUSTOM_MAX_API_THREADS_IDLE_MILLISECONDS, "api");
        executorService = executorContext.getCallbackExecutor();
        checkThreadPoolExecutorServices(executorService, DEFAULT_MIN_THREADS, FlexibleExecutorContextTest.CUSTOM_MAX_CALLBACK_THREADS, FlexibleExecutorContextTest.CUSTOM_MAX_CALLBACK_THREADS_IDLE_MILLISECONDS, "callback");
        executorService = executorContext.getScheduledExecutor();
        checkScheduledExecutorServices(executorService, FlexibleExecutorContextTest.CUSTOM_MIN_SCHEDULED_THREADS);
    }

    @Test
    public void testShutdownExecutors() throws Exception {
        executorContext = new FlexibleExecutorContext();
        executorContext.init();
        ExecutorService lifeCycleExecutor = executorContext.getLifeCycleExecutor();
        ExecutorService apiExecutor = executorContext.getApiExecutor();
        ExecutorService callbackExecutor = executorContext.getCallbackExecutor();
        ScheduledExecutorService scheduledExecutor = executorContext.getScheduledExecutor();
        executorContext.stop();
        executorContext.getTimeunit().sleep(executorContext.getTimeout());
        Assert.assertTrue(lifeCycleExecutor.isShutdown());
        Assert.assertTrue(apiExecutor.isShutdown());
        Assert.assertTrue(callbackExecutor.isShutdown());
        Assert.assertTrue(scheduledExecutor.isShutdown());
    }
}

