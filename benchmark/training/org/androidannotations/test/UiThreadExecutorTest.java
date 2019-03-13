/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.androidannotations.api.UiThreadExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;


@RunWith(RobolectricTestRunner.class)
public class UiThreadExecutorTest {
    @Test
    public void oneTaskTest() throws Exception {
        final AtomicBoolean done = new AtomicBoolean(false);
        UiThreadExecutor.runTask("test", new Runnable() {
            @Override
            public void run() {
                done.set(true);
            }
        }, 10);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        Assert.assertTrue("Task is still under execution", done.get());
    }

    @Test
    public void oneTaskCancelTest() throws Exception {
        final AtomicBoolean done = new AtomicBoolean(false);
        UiThreadExecutor.runTask("test", new Runnable() {
            @Override
            public void run() {
                done.set(true);
            }
        }, 10);
        UiThreadExecutor.cancelAll("test");
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        Assert.assertFalse("Task is not cancelled", done.get());
    }

    @Test
    public void oneTaskInThreadTest() throws Exception {
        final CountDownLatch taskStartedLatch = new CountDownLatch(1);
        final CountDownLatch taskFinishedLatch = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                UiThreadExecutor.runTask("test", new Runnable() {
                    @Override
                    public void run() {
                        await(taskStartedLatch);
                        taskFinishedLatch.countDown();
                    }
                }, 10);
                taskStartedLatch.countDown();
                ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
            }
        }.start();
        await(taskFinishedLatch);
    }
}

