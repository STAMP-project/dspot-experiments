/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.io.network;


import junit.framework.TestCase;
import org.apache.flink.runtime.event.TaskEvent;
import org.apache.flink.runtime.io.network.partition.ResultPartitionID;
import org.apache.flink.runtime.iterative.event.AllWorkersDoneEvent;
import org.apache.flink.runtime.iterative.event.TerminationEvent;
import org.apache.flink.runtime.util.event.EventListener;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Basic tests for {@link TaskEventDispatcher}.
 */
public class TaskEventDispatcherTest extends TestLogger {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void registerPartitionTwice() throws Exception {
        ResultPartitionID partitionId = new ResultPartitionID();
        TaskEventDispatcher ted = new TaskEventDispatcher();
        ted.registerPartition(partitionId);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("already registered at task event dispatcher");
        ted.registerPartition(partitionId);
    }

    @Test
    public void subscribeToEventNotRegistered() throws Exception {
        TaskEventDispatcher ted = new TaskEventDispatcher();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("not registered at task event dispatcher");
        ted.subscribeToEvent(new ResultPartitionID(), new TaskEventDispatcherTest.ZeroShotEventListener(), TaskEvent.class);
    }

    /**
     * Tests {@link TaskEventDispatcher#publish(ResultPartitionID, TaskEvent)} and {@link TaskEventDispatcher#subscribeToEvent(ResultPartitionID, EventListener, Class)} methods.
     */
    @Test
    public void publishSubscribe() throws Exception {
        ResultPartitionID partitionId1 = new ResultPartitionID();
        ResultPartitionID partitionId2 = new ResultPartitionID();
        TaskEventDispatcher ted = new TaskEventDispatcher();
        AllWorkersDoneEvent event1 = new AllWorkersDoneEvent();
        TerminationEvent event2 = new TerminationEvent();
        Assert.assertFalse(ted.publish(partitionId1, event1));
        ted.registerPartition(partitionId1);
        ted.registerPartition(partitionId2);
        // no event listener subscribed yet, but the event is forwarded to a TaskEventHandler
        TestCase.assertTrue(ted.publish(partitionId1, event1));
        TaskEventDispatcherTest.OneShotEventListener eventListener1a = new TaskEventDispatcherTest.OneShotEventListener(event1);
        TaskEventDispatcherTest.ZeroShotEventListener eventListener1b = new TaskEventDispatcherTest.ZeroShotEventListener();
        TaskEventDispatcherTest.ZeroShotEventListener eventListener2 = new TaskEventDispatcherTest.ZeroShotEventListener();
        TaskEventDispatcherTest.OneShotEventListener eventListener3 = new TaskEventDispatcherTest.OneShotEventListener(event2);
        ted.subscribeToEvent(partitionId1, eventListener1a, AllWorkersDoneEvent.class);
        ted.subscribeToEvent(partitionId2, eventListener1b, AllWorkersDoneEvent.class);
        ted.subscribeToEvent(partitionId1, eventListener2, TaskEvent.class);
        ted.subscribeToEvent(partitionId1, eventListener3, TerminationEvent.class);
        TestCase.assertTrue(ted.publish(partitionId1, event1));
        TestCase.assertTrue("listener should have fired for AllWorkersDoneEvent", eventListener1a.fired);
        Assert.assertFalse("listener should not have fired for AllWorkersDoneEvent", eventListener3.fired);
        // publish another event, verify that only the right subscriber is called
        TestCase.assertTrue(ted.publish(partitionId1, event2));
        TestCase.assertTrue("listener should have fired for TerminationEvent", eventListener3.fired);
    }

    @Test
    public void unregisterPartition() throws Exception {
        ResultPartitionID partitionId1 = new ResultPartitionID();
        ResultPartitionID partitionId2 = new ResultPartitionID();
        TaskEventDispatcher ted = new TaskEventDispatcher();
        AllWorkersDoneEvent event = new AllWorkersDoneEvent();
        Assert.assertFalse(ted.publish(partitionId1, event));
        ted.registerPartition(partitionId1);
        ted.registerPartition(partitionId2);
        TaskEventDispatcherTest.OneShotEventListener eventListener1a = new TaskEventDispatcherTest.OneShotEventListener(event);
        TaskEventDispatcherTest.ZeroShotEventListener eventListener1b = new TaskEventDispatcherTest.ZeroShotEventListener();
        TaskEventDispatcherTest.OneShotEventListener eventListener2 = new TaskEventDispatcherTest.OneShotEventListener(event);
        ted.subscribeToEvent(partitionId1, eventListener1a, AllWorkersDoneEvent.class);
        ted.subscribeToEvent(partitionId2, eventListener1b, AllWorkersDoneEvent.class);
        ted.subscribeToEvent(partitionId1, eventListener2, AllWorkersDoneEvent.class);
        ted.unregisterPartition(partitionId2);
        // publish something for partitionId1 triggering all according listeners
        TestCase.assertTrue(ted.publish(partitionId1, event));
        TestCase.assertTrue("listener should have fired for AllWorkersDoneEvent", eventListener1a.fired);
        TestCase.assertTrue("listener should have fired for AllWorkersDoneEvent", eventListener2.fired);
        // now publish something for partitionId2 which should not trigger any listeners
        Assert.assertFalse(ted.publish(partitionId2, event));
    }

    @Test
    public void clearAll() throws Exception {
        ResultPartitionID partitionId = new ResultPartitionID();
        TaskEventDispatcher ted = new TaskEventDispatcher();
        ted.registerPartition(partitionId);
        // noinspection unchecked
        TaskEventDispatcherTest.ZeroShotEventListener eventListener1 = new TaskEventDispatcherTest.ZeroShotEventListener();
        ted.subscribeToEvent(partitionId, eventListener1, AllWorkersDoneEvent.class);
        ted.clearAll();
        Assert.assertFalse(ted.publish(partitionId, new AllWorkersDoneEvent()));
    }

    /**
     * Event listener that expects a given {@link TaskEvent} once in its {@link #onEvent(TaskEvent)}
     * call and will fail for any subsequent call.
     *
     * <p>Be sure to check that {@link #fired} is <tt>true</tt> to ensure that this handle has been
     * called once.
     */
    private static class OneShotEventListener implements EventListener<TaskEvent> {
        private final TaskEvent expected;

        boolean fired = false;

        OneShotEventListener(TaskEvent expected) {
            this.expected = expected;
        }

        public void onEvent(TaskEvent actual) {
            checkState((!(fired)), "Should only fire once");
            fired = true;
            checkArgument((actual == (expected)), "Fired on unexpected event: %s (expected: %s)", actual, expected);
        }
    }

    /**
     * Event listener which ensures that it's {@link #onEvent(TaskEvent)} method is never called.
     */
    private static class ZeroShotEventListener implements EventListener<TaskEvent> {
        public void onEvent(TaskEvent actual) {
            throw new IllegalStateException("Should never fire");
        }
    }
}

