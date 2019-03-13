/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.threadpool.support.cached;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.threadlocal.InternalThread;
import org.apache.dubbo.common.threadpool.ThreadPool;
import org.apache.dubbo.common.threadpool.support.AbortPolicyWithReport;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class CachedThreadPoolTest {
    @Test
    public void getExecutor1() throws Exception {
        URL url = URL.valueOf((((((((((("dubbo://10.20.130.230:20880/context/path?" + (Constants.THREAD_NAME_KEY)) + "=demo&") + (Constants.CORE_THREADS_KEY)) + "=1&") + (Constants.THREADS_KEY)) + "=2&") + (Constants.ALIVE_KEY)) + "=1000&") + (Constants.QUEUES_KEY)) + "=0"));
        ThreadPool threadPool = new CachedThreadPool();
        ThreadPoolExecutor executor = ((ThreadPoolExecutor) (threadPool.getExecutor(url)));
        MatcherAssert.assertThat(executor.getCorePoolSize(), Matchers.is(1));
        MatcherAssert.assertThat(executor.getMaximumPoolSize(), Matchers.is(2));
        MatcherAssert.assertThat(executor.getQueue(), Matchers.<BlockingQueue<Runnable>>instanceOf(SynchronousQueue.class));
        MatcherAssert.assertThat(executor.getRejectedExecutionHandler(), Matchers.<RejectedExecutionHandler>instanceOf(AbortPolicyWithReport.class));
        final CountDownLatch latch = new CountDownLatch(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Thread thread = Thread.currentThread();
                MatcherAssert.assertThat(thread, Matchers.instanceOf(InternalThread.class));
                MatcherAssert.assertThat(thread.getName(), Matchers.startsWith("demo"));
                latch.countDown();
            }
        });
        latch.await();
        MatcherAssert.assertThat(latch.getCount(), Matchers.is(0L));
    }

    @Test
    public void getExecutor2() throws Exception {
        URL url = URL.valueOf((("dubbo://10.20.130.230:20880/context/path?" + (Constants.QUEUES_KEY)) + "=1"));
        ThreadPool threadPool = new CachedThreadPool();
        ThreadPoolExecutor executor = ((ThreadPoolExecutor) (threadPool.getExecutor(url)));
        MatcherAssert.assertThat(executor.getQueue(), Matchers.<BlockingQueue<Runnable>>instanceOf(LinkedBlockingQueue.class));
    }
}

