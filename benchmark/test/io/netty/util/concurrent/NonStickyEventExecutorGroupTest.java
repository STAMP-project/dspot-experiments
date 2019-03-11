/**
 * Copyright 2016 The Netty Project
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


import io.netty.util.NettyRuntime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class NonStickyEventExecutorGroupTest {
    private final int maxTaskExecutePerRun;

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidGroup() {
        EventExecutorGroup group = new DefaultEventExecutorGroup(1);
        try {
            new NonStickyEventExecutorGroup(group);
        } finally {
            group.shutdownGracefully();
        }
    }

    public NonStickyEventExecutorGroupTest(int maxTaskExecutePerRun) {
        this.maxTaskExecutePerRun = maxTaskExecutePerRun;
    }

    @Test(timeout = 10000)
    public void testOrdering() throws Throwable {
        final int threads = (NettyRuntime.availableProcessors()) * 2;
        final EventExecutorGroup group = new UnorderedThreadPoolEventExecutor(threads);
        final NonStickyEventExecutorGroup nonStickyGroup = new NonStickyEventExecutorGroup(group, maxTaskExecutePerRun);
        try {
            final CountDownLatch startLatch = new CountDownLatch(1);
            final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
            List<Thread> threadList = new ArrayList<Thread>(threads);
            for (int i = 0; i < threads; i++) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            NonStickyEventExecutorGroupTest.execute(nonStickyGroup, startLatch);
                        } catch (Throwable cause) {
                            error.compareAndSet(null, cause);
                        }
                    }
                });
                threadList.add(thread);
                thread.start();
            }
            startLatch.countDown();
            for (Thread t : threadList) {
                t.join();
            }
            Throwable cause = error.get();
            if (cause != null) {
                throw cause;
            }
        } finally {
            nonStickyGroup.shutdownGracefully();
        }
    }

    @Test
    public void testRaceCondition() throws InterruptedException {
        EventExecutorGroup group = new UnorderedThreadPoolEventExecutor(1);
        NonStickyEventExecutorGroup nonStickyGroup = new NonStickyEventExecutorGroup(group, maxTaskExecutePerRun);
        try {
            EventExecutor executor = nonStickyGroup.next();
            for (int j = 0; j < 5000; j++) {
                final CountDownLatch firstCompleted = new CountDownLatch(1);
                final CountDownLatch latch = new CountDownLatch(2);
                for (int i = 0; i < 2; i++) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            firstCompleted.countDown();
                            latch.countDown();
                        }
                    });
                    Assert.assertTrue(firstCompleted.await(1, TimeUnit.SECONDS));
                }
                Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
            }
        } finally {
            nonStickyGroup.shutdownGracefully();
        }
    }
}

