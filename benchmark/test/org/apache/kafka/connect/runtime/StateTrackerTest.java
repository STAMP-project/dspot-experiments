/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.runtime;


import State.DESTROYED;
import State.FAILED;
import State.PAUSED;
import State.RUNNING;
import State.UNASSIGNED;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.connect.runtime.AbstractStatus.State;
import org.junit.Assert;
import org.junit.Test;


public class StateTrackerTest {
    private static final double DELTA = 1.0E-6;

    private StateTracker tracker;

    private MockTime time;

    private State state;

    @Test
    public void currentStateIsNullWhenNotInitialized() {
        Assert.assertNull(tracker.currentState());
    }

    @Test
    public void currentState() {
        for (State state : State.values()) {
            tracker.changeState(state, time.milliseconds());
            Assert.assertEquals(state, tracker.currentState());
        }
    }

    @Test
    public void calculateDurations() {
        tracker.changeState(UNASSIGNED, time.milliseconds());
        time.sleep(1000L);
        Assert.assertEquals(1.0, tracker.durationRatio(UNASSIGNED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.0, tracker.durationRatio(RUNNING, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.0, tracker.durationRatio(PAUSED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.0, tracker.durationRatio(FAILED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.0, tracker.durationRatio(DESTROYED, time.milliseconds()), StateTrackerTest.DELTA);
        tracker.changeState(RUNNING, time.milliseconds());
        time.sleep(3000L);
        Assert.assertEquals(0.25, tracker.durationRatio(UNASSIGNED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.75, tracker.durationRatio(RUNNING, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.0, tracker.durationRatio(PAUSED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.0, tracker.durationRatio(FAILED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.0, tracker.durationRatio(DESTROYED, time.milliseconds()), StateTrackerTest.DELTA);
        tracker.changeState(PAUSED, time.milliseconds());
        time.sleep(4000L);
        Assert.assertEquals(0.125, tracker.durationRatio(UNASSIGNED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.375, tracker.durationRatio(RUNNING, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.5, tracker.durationRatio(PAUSED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.0, tracker.durationRatio(FAILED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.0, tracker.durationRatio(DESTROYED, time.milliseconds()), StateTrackerTest.DELTA);
        tracker.changeState(RUNNING, time.milliseconds());
        time.sleep(8000L);
        Assert.assertEquals(0.0625, tracker.durationRatio(UNASSIGNED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.6875, tracker.durationRatio(RUNNING, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.25, tracker.durationRatio(PAUSED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.0, tracker.durationRatio(FAILED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.0, tracker.durationRatio(DESTROYED, time.milliseconds()), StateTrackerTest.DELTA);
        tracker.changeState(FAILED, time.milliseconds());
        time.sleep(16000L);
        Assert.assertEquals(0.03125, tracker.durationRatio(UNASSIGNED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.34375, tracker.durationRatio(RUNNING, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.125, tracker.durationRatio(PAUSED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.5, tracker.durationRatio(FAILED, time.milliseconds()), StateTrackerTest.DELTA);
        Assert.assertEquals(0.0, tracker.durationRatio(DESTROYED, time.milliseconds()), StateTrackerTest.DELTA);
    }
}

