/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.concurrent;


import org.junit.Assert;
import org.junit.Test;


public class LifecycleLockTest {
    @Test
    public void testOnlyOneCanStart() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            testOnlyOneCanStartRun();
        }
    }

    @Test
    public void testOnlyOneCanStop() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            testOnlyOneCanStopRun();
        }
    }

    @Test
    public void testNoStartAfterStop() {
        LifecycleLock lifecycleLock = new LifecycleLock();
        Assert.assertTrue(lifecycleLock.canStart());
        lifecycleLock.started();
        lifecycleLock.exitStart();
        Assert.assertTrue(lifecycleLock.canStop());
        Assert.assertFalse(lifecycleLock.canStart());
    }

    @Test
    public void testNotStarted() {
        LifecycleLock lifecycleLock = new LifecycleLock();
        Assert.assertTrue(lifecycleLock.canStart());
        lifecycleLock.exitStart();
        Assert.assertFalse(lifecycleLock.awaitStarted());
        Assert.assertFalse(lifecycleLock.canStop());
    }

    @Test
    public void testRestart() {
        LifecycleLock lifecycleLock = new LifecycleLock();
        Assert.assertTrue(lifecycleLock.canStart());
        lifecycleLock.started();
        lifecycleLock.exitStart();
        Assert.assertTrue(lifecycleLock.canStop());
        lifecycleLock.exitStopAndReset();
        Assert.assertTrue(lifecycleLock.canStart());
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void testDoubleStarted() {
        LifecycleLock lifecycleLock = new LifecycleLock();
        lifecycleLock.canStart();
        lifecycleLock.started();
        lifecycleLock.started();
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void testDoubleExitStart() {
        LifecycleLock lifecycleLock = new LifecycleLock();
        lifecycleLock.canStart();
        lifecycleLock.started();
        lifecycleLock.exitStart();
        lifecycleLock.exitStart();
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void testCanStopNotExitedStart() {
        LifecycleLock lifecycleLock = new LifecycleLock();
        lifecycleLock.canStart();
        lifecycleLock.started();
        lifecycleLock.canStop();
    }
}

