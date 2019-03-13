/**
 * Copyright 2013 The Netty Project
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


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static GlobalEventExecutor.INSTANCE;


public class GlobalEventExecutorTest {
    private static final GlobalEventExecutor e = INSTANCE;

    @Test
    public void testAutomaticStartStop() throws Exception {
        final GlobalEventExecutorTest.TestRunnable task = new GlobalEventExecutorTest.TestRunnable(500);
        GlobalEventExecutorTest.e.execute(task);
        // Ensure the new thread has started.
        Thread thread = GlobalEventExecutorTest.e.thread;
        Assert.assertThat(thread, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        Assert.assertThat(thread.isAlive(), CoreMatchers.is(true));
        Thread.sleep(1500);
        // Ensure the thread stopped itself after running the task.
        Assert.assertThat(thread.isAlive(), CoreMatchers.is(false));
        Assert.assertThat(task.ran.get(), CoreMatchers.is(true));
        // Ensure another new thread starts again.
        task.ran.set(false);
        GlobalEventExecutorTest.e.execute(task);
        Assert.assertThat(GlobalEventExecutorTest.e.thread, CoreMatchers.not(CoreMatchers.sameInstance(thread)));
        thread = GlobalEventExecutorTest.e.thread;
        Thread.sleep(1500);
        // Ensure the thread stopped itself after running the task.
        Assert.assertThat(thread.isAlive(), CoreMatchers.is(false));
        Assert.assertThat(task.ran.get(), CoreMatchers.is(true));
    }

    @Test
    public void testScheduledTasks() throws Exception {
        GlobalEventExecutorTest.TestRunnable task = new GlobalEventExecutorTest.TestRunnable(0);
        ScheduledFuture<?> f = GlobalEventExecutorTest.e.schedule(task, 1500, TimeUnit.MILLISECONDS);
        f.sync();
        Assert.assertThat(task.ran.get(), CoreMatchers.is(true));
        // Ensure the thread is still running.
        Thread thread = GlobalEventExecutorTest.e.thread;
        Assert.assertThat(thread, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        Assert.assertThat(thread.isAlive(), CoreMatchers.is(true));
        Thread.sleep(1500);
        // Now it should be stopped.
        Assert.assertThat(thread.isAlive(), CoreMatchers.is(false));
    }

    // ensure that when a task submission causes a new thread to be created, the thread inherits the thread group of the
    // submitting thread
    @Test(timeout = 2000)
    public void testThreadGroup() throws InterruptedException {
        final ThreadGroup group = new ThreadGroup("group");
        final AtomicReference<ThreadGroup> capturedGroup = new AtomicReference<ThreadGroup>();
        final Thread thread = new Thread(group, new Runnable() {
            @Override
            public void run() {
                final Thread t = GlobalEventExecutorTest.e.threadFactory.newThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                capturedGroup.set(t.getThreadGroup());
            }
        });
        thread.start();
        thread.join();
        Assert.assertEquals(group, capturedGroup.get());
    }

    private static final class TestRunnable implements Runnable {
        final AtomicBoolean ran = new AtomicBoolean();

        final long delay;

        TestRunnable(long delay) {
            this.delay = delay;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(delay);
                ran.set(true);
            } catch (InterruptedException ignored) {
                // Ignore
            }
        }
    }
}

