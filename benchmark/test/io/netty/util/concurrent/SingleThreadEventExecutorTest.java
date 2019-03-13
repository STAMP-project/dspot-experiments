/**
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.concurrent;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class SingleThreadEventExecutorTest {
    @Test
    public void testWrappedExecutorIsShutdown() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        SingleThreadEventExecutor executor = new SingleThreadEventExecutor(null, executorService, false) {
            @Override
            protected void run() {
                while (!(confirmShutdown())) {
                    Runnable task = takeTask();
                    if (task != null) {
                        task.run();
                    }
                } 
            }
        };
        executorService.shutdownNow();
        SingleThreadEventExecutorTest.executeShouldFail(executor);
        SingleThreadEventExecutorTest.executeShouldFail(executor);
        try {
            executor.shutdownGracefully().syncUninterruptibly();
            Assert.fail();
        } catch (RejectedExecutionException expected) {
            // expected
        }
        Assert.assertTrue(executor.isShutdown());
    }

    @Test
    public void testThreadProperties() {
        final AtomicReference<Thread> threadRef = new AtomicReference<Thread>();
        SingleThreadEventExecutor executor = new SingleThreadEventExecutor(null, new DefaultThreadFactory("test"), false) {
            @Override
            protected void run() {
                threadRef.set(Thread.currentThread());
                while (!(confirmShutdown())) {
                    Runnable task = takeTask();
                    if (task != null) {
                        task.run();
                    }
                } 
            }
        };
        ThreadProperties threadProperties = executor.threadProperties();
        Thread thread = threadRef.get();
        Assert.assertEquals(thread.getId(), threadProperties.id());
        Assert.assertEquals(thread.getName(), threadProperties.name());
        Assert.assertEquals(thread.getPriority(), threadProperties.priority());
        Assert.assertEquals(thread.isAlive(), threadProperties.isAlive());
        Assert.assertEquals(thread.isDaemon(), threadProperties.isDaemon());
        Assert.assertTrue(((threadProperties.stackTrace().length) > 0));
        executor.shutdownGracefully();
    }

    @Test(expected = RejectedExecutionException.class, timeout = 3000)
    public void testInvokeAnyInEventLoop() {
        SingleThreadEventExecutorTest.testInvokeInEventLoop(true, false);
    }

    @Test(expected = RejectedExecutionException.class, timeout = 3000)
    public void testInvokeAnyInEventLoopWithTimeout() {
        SingleThreadEventExecutorTest.testInvokeInEventLoop(true, true);
    }

    @Test(expected = RejectedExecutionException.class, timeout = 3000)
    public void testInvokeAllInEventLoop() {
        SingleThreadEventExecutorTest.testInvokeInEventLoop(false, false);
    }

    @Test(expected = RejectedExecutionException.class, timeout = 3000)
    public void testInvokeAllInEventLoopWithTimeout() {
        SingleThreadEventExecutorTest.testInvokeInEventLoop(false, true);
    }
}

