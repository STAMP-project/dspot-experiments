/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.lifecycle;


import LifecycleState.IDLE;
import LifecycleState.START;
import LifecycleState.STOP;
import junit.framework.Assert;
import org.junit.Test;

import static LifecycleState.IDLE;
import static LifecycleState.START;
import static LifecycleState.STOP;


public class TestLifecycleController {
    @Test
    public void testWaitForState() throws InterruptedException, LifecycleException {
        LifecycleAware delegate = new TestLifecycleController.SleeperLifecycleDelegate();
        Assert.assertTrue(delegate.getLifecycleState().equals(IDLE));
        delegate.start();
        boolean reached = LifecycleController.waitForState(delegate, START, 2000);
        Assert.assertEquals(true, reached);
        Assert.assertEquals(START, delegate.getLifecycleState());
        delegate.stop();
        reached = LifecycleController.waitForState(delegate, STOP, 2000);
        Assert.assertEquals(true, reached);
        Assert.assertEquals(STOP, delegate.getLifecycleState());
        delegate.start();
        reached = LifecycleController.waitForState(delegate, IDLE, 500);
        Assert.assertEquals(false, reached);
        Assert.assertEquals(START, delegate.getLifecycleState());
    }

    @Test
    public void testWaitForOneOf() throws InterruptedException, LifecycleException {
        LifecycleAware delegate = new TestLifecycleController.SleeperLifecycleDelegate();
        Assert.assertEquals(IDLE, delegate.getLifecycleState());
        delegate.start();
        boolean reached = LifecycleController.waitForOneOf(delegate, new LifecycleState[]{ STOP, START }, 2000);
        Assert.assertTrue("Matched a state change", reached);
        Assert.assertEquals(START, delegate.getLifecycleState());
    }

    public static class SleeperLifecycleDelegate implements LifecycleAware {
        private long sleepTime;

        private LifecycleState state;

        public SleeperLifecycleDelegate() {
            sleepTime = 0;
            state = IDLE;
        }

        @Override
        public void start() {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // Do nothing.
            }
            state = LifecycleState.START;
        }

        @Override
        public void stop() {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // Do nothing
            }
            state = LifecycleState.STOP;
        }

        @Override
        public LifecycleState getLifecycleState() {
            return state;
        }

        public long getSleepTime() {
            return sleepTime;
        }

        public void setSleepTime(long sleepTime) {
            this.sleepTime = sleepTime;
        }
    }
}

