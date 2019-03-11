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
package org.apache.flink.streaming.api.operators;


import IntSerializer.INSTANCE;
import InternalTimerServiceSerializationProxy.VERSION;
import InternalTimersSnapshotReaderWriters.NO_VERSION;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.flink.runtime.state.KeyGroupRange;
import org.apache.flink.runtime.state.KeyGroupRangeAssignment;
import org.apache.flink.runtime.state.PriorityQueueSetFactory;
import org.apache.flink.streaming.runtime.tasks.TestProcessingTimeService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests for {@link InternalTimerServiceImpl}.
 */
@RunWith(Parameterized.class)
public class InternalTimerServiceImplTest {
    private final int maxParallelism;

    private final KeyGroupRange testKeyGroupRange;

    public InternalTimerServiceImplTest(int startKeyGroup, int endKeyGroup, int maxParallelism) {
        this.testKeyGroupRange = new KeyGroupRange(startKeyGroup, endKeyGroup);
        this.maxParallelism = maxParallelism;
    }

    @Test
    public void testKeyGroupStartIndexSetting() {
        int startKeyGroupIdx = 7;
        int endKeyGroupIdx = 21;
        KeyGroupRange testKeyGroupList = new KeyGroupRange(startKeyGroupIdx, endKeyGroupIdx);
        InternalTimerServiceImplTest.TestKeyContext keyContext = new InternalTimerServiceImplTest.TestKeyContext();
        TestProcessingTimeService processingTimeService = new TestProcessingTimeService();
        InternalTimerServiceImpl<Integer, String> service = InternalTimerServiceImplTest.createInternalTimerService(testKeyGroupList, keyContext, processingTimeService, INSTANCE, StringSerializer.INSTANCE, createQueueFactory());
        Assert.assertEquals(startKeyGroupIdx, service.getLocalKeyGroupRangeStartIdx());
    }

    @Test
    public void testTimerAssignmentToKeyGroups() {
        int totalNoOfTimers = 100;
        int totalNoOfKeyGroups = 100;
        int startKeyGroupIdx = 0;
        int endKeyGroupIdx = totalNoOfKeyGroups - 1;// we have 0 to 99

        @SuppressWarnings("unchecked")
        Set<TimerHeapInternalTimer<Integer, String>>[] expectedNonEmptyTimerSets = new HashSet[totalNoOfKeyGroups];
        InternalTimerServiceImplTest.TestKeyContext keyContext = new InternalTimerServiceImplTest.TestKeyContext();
        final KeyGroupRange keyGroupRange = new KeyGroupRange(startKeyGroupIdx, endKeyGroupIdx);
        final PriorityQueueSetFactory priorityQueueSetFactory = createQueueFactory(keyGroupRange, totalNoOfKeyGroups);
        InternalTimerServiceImpl<Integer, String> timerService = InternalTimerServiceImplTest.createInternalTimerService(keyGroupRange, keyContext, new TestProcessingTimeService(), INSTANCE, StringSerializer.INSTANCE, priorityQueueSetFactory);
        timerService.startTimerService(INSTANCE, StringSerializer.INSTANCE, Mockito.mock(Triggerable.class));
        for (int i = 0; i < totalNoOfTimers; i++) {
            // create the timer to be registered
            TimerHeapInternalTimer<Integer, String> timer = new TimerHeapInternalTimer((10 + i), i, ("hello_world_" + i));
            int keyGroupIdx = KeyGroupRangeAssignment.assignToKeyGroup(timer.getKey(), totalNoOfKeyGroups);
            // add it in the adequate expected set of timers per keygroup
            Set<TimerHeapInternalTimer<Integer, String>> timerSet = expectedNonEmptyTimerSets[keyGroupIdx];
            if (timerSet == null) {
                timerSet = new HashSet();
                expectedNonEmptyTimerSets[keyGroupIdx] = timerSet;
            }
            timerSet.add(timer);
            // register the timer as both processing and event time one
            keyContext.setCurrentKey(timer.getKey());
            timerService.registerEventTimeTimer(timer.getNamespace(), timer.getTimestamp());
            timerService.registerProcessingTimeTimer(timer.getNamespace(), timer.getTimestamp());
        }
        List<Set<TimerHeapInternalTimer<Integer, String>>> eventTimeTimers = timerService.getEventTimeTimersPerKeyGroup();
        List<Set<TimerHeapInternalTimer<Integer, String>>> processingTimeTimers = timerService.getProcessingTimeTimersPerKeyGroup();
        // finally verify that the actual timers per key group sets are the expected ones.
        for (int i = 0; i < (expectedNonEmptyTimerSets.length); i++) {
            Set<TimerHeapInternalTimer<Integer, String>> expected = expectedNonEmptyTimerSets[i];
            Set<TimerHeapInternalTimer<Integer, String>> actualEvent = eventTimeTimers.get(i);
            Set<TimerHeapInternalTimer<Integer, String>> actualProcessing = processingTimeTimers.get(i);
            if (expected == null) {
                Assert.assertTrue(actualEvent.isEmpty());
                Assert.assertTrue(actualProcessing.isEmpty());
            } else {
                Assert.assertEquals(expected, actualEvent);
                Assert.assertEquals(expected, actualProcessing);
            }
        }
    }

    /**
     * Verify that we only ever have one processing-time task registered at the
     * {@link ProcessingTimeService}.
     */
    @Test
    public void testOnlySetsOnePhysicalProcessingTimeTimer() throws Exception {
        @SuppressWarnings("unchecked")
        Triggerable<Integer, String> mockTriggerable = Mockito.mock(Triggerable.class);
        InternalTimerServiceImplTest.TestKeyContext keyContext = new InternalTimerServiceImplTest.TestKeyContext();
        TestProcessingTimeService processingTimeService = new TestProcessingTimeService();
        PriorityQueueSetFactory priorityQueueSetFactory = new org.apache.flink.runtime.state.heap.HeapPriorityQueueSetFactory(testKeyGroupRange, maxParallelism, 128);
        InternalTimerServiceImpl<Integer, String> timerService = InternalTimerServiceImplTest.createAndStartInternalTimerService(mockTriggerable, keyContext, processingTimeService, testKeyGroupRange, priorityQueueSetFactory);
        int key = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        keyContext.setCurrentKey(key);
        timerService.registerProcessingTimeTimer("ciao", 10);
        timerService.registerProcessingTimeTimer("ciao", 20);
        timerService.registerProcessingTimeTimer("ciao", 30);
        timerService.registerProcessingTimeTimer("hello", 10);
        timerService.registerProcessingTimeTimer("hello", 20);
        Assert.assertEquals(5, timerService.numProcessingTimeTimers());
        Assert.assertEquals(2, timerService.numProcessingTimeTimers("hello"));
        Assert.assertEquals(3, timerService.numProcessingTimeTimers("ciao"));
        Assert.assertEquals(1, processingTimeService.getNumActiveTimers());
        Assert.assertThat(processingTimeService.getActiveTimerTimestamps(), Matchers.containsInAnyOrder(10L));
        processingTimeService.setCurrentTime(10);
        Assert.assertEquals(3, timerService.numProcessingTimeTimers());
        Assert.assertEquals(1, timerService.numProcessingTimeTimers("hello"));
        Assert.assertEquals(2, timerService.numProcessingTimeTimers("ciao"));
        Assert.assertEquals(1, processingTimeService.getNumActiveTimers());
        Assert.assertThat(processingTimeService.getActiveTimerTimestamps(), Matchers.containsInAnyOrder(20L));
        processingTimeService.setCurrentTime(20);
        Assert.assertEquals(1, timerService.numProcessingTimeTimers());
        Assert.assertEquals(0, timerService.numProcessingTimeTimers("hello"));
        Assert.assertEquals(1, timerService.numProcessingTimeTimers("ciao"));
        Assert.assertEquals(1, processingTimeService.getNumActiveTimers());
        Assert.assertThat(processingTimeService.getActiveTimerTimestamps(), Matchers.containsInAnyOrder(30L));
        processingTimeService.setCurrentTime(30);
        Assert.assertEquals(0, timerService.numProcessingTimeTimers());
        Assert.assertEquals(0, processingTimeService.getNumActiveTimers());
        timerService.registerProcessingTimeTimer("ciao", 40);
        Assert.assertEquals(1, processingTimeService.getNumActiveTimers());
    }

    /**
     * Verify that registering a processing-time timer that is earlier than the existing timers
     * removes the one physical timer and creates one for the earlier timestamp
     * {@link ProcessingTimeService}.
     */
    @Test
    public void testRegisterEarlierProcessingTimerMovesPhysicalProcessingTimer() throws Exception {
        @SuppressWarnings("unchecked")
        Triggerable<Integer, String> mockTriggerable = Mockito.mock(Triggerable.class);
        InternalTimerServiceImplTest.TestKeyContext keyContext = new InternalTimerServiceImplTest.TestKeyContext();
        TestProcessingTimeService processingTimeService = new TestProcessingTimeService();
        InternalTimerServiceImpl<Integer, String> timerService = InternalTimerServiceImplTest.createAndStartInternalTimerService(mockTriggerable, keyContext, processingTimeService, testKeyGroupRange, createQueueFactory());
        int key = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        keyContext.setCurrentKey(key);
        timerService.registerProcessingTimeTimer("ciao", 20);
        Assert.assertEquals(1, timerService.numProcessingTimeTimers());
        Assert.assertEquals(1, processingTimeService.getNumActiveTimers());
        Assert.assertThat(processingTimeService.getActiveTimerTimestamps(), Matchers.containsInAnyOrder(20L));
        timerService.registerProcessingTimeTimer("ciao", 10);
        Assert.assertEquals(2, timerService.numProcessingTimeTimers());
        Assert.assertEquals(1, processingTimeService.getNumActiveTimers());
        Assert.assertThat(processingTimeService.getActiveTimerTimestamps(), Matchers.containsInAnyOrder(10L));
    }

    /**
     *
     */
    @Test
    public void testRegisteringProcessingTimeTimerInOnProcessingTimeDoesNotLeakPhysicalTimers() throws Exception {
        @SuppressWarnings("unchecked")
        Triggerable<Integer, String> mockTriggerable = Mockito.mock(Triggerable.class);
        InternalTimerServiceImplTest.TestKeyContext keyContext = new InternalTimerServiceImplTest.TestKeyContext();
        TestProcessingTimeService processingTimeService = new TestProcessingTimeService();
        final InternalTimerServiceImpl<Integer, String> timerService = InternalTimerServiceImplTest.createAndStartInternalTimerService(mockTriggerable, keyContext, processingTimeService, testKeyGroupRange, createQueueFactory());
        int key = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        keyContext.setCurrentKey(key);
        timerService.registerProcessingTimeTimer("ciao", 10);
        Assert.assertEquals(1, timerService.numProcessingTimeTimers());
        Assert.assertEquals(1, processingTimeService.getNumActiveTimers());
        Assert.assertThat(processingTimeService.getActiveTimerTimestamps(), Matchers.containsInAnyOrder(10L));
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Exception {
                timerService.registerProcessingTimeTimer("ciao", 20);
                return null;
            }
        }).when(mockTriggerable).onProcessingTime(InternalTimerServiceImplTest.anyInternalTimer());
        processingTimeService.setCurrentTime(10);
        Assert.assertEquals(1, processingTimeService.getNumActiveTimers());
        Assert.assertThat(processingTimeService.getActiveTimerTimestamps(), Matchers.containsInAnyOrder(20L));
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Exception {
                timerService.registerProcessingTimeTimer("ciao", 30);
                return null;
            }
        }).when(mockTriggerable).onProcessingTime(InternalTimerServiceImplTest.anyInternalTimer());
        processingTimeService.setCurrentTime(20);
        Assert.assertEquals(1, timerService.numProcessingTimeTimers());
        Assert.assertEquals(1, processingTimeService.getNumActiveTimers());
        Assert.assertThat(processingTimeService.getActiveTimerTimestamps(), Matchers.containsInAnyOrder(30L));
    }

    @Test
    public void testCurrentProcessingTime() throws Exception {
        @SuppressWarnings("unchecked")
        Triggerable<Integer, String> mockTriggerable = Mockito.mock(Triggerable.class);
        InternalTimerServiceImplTest.TestKeyContext keyContext = new InternalTimerServiceImplTest.TestKeyContext();
        TestProcessingTimeService processingTimeService = new TestProcessingTimeService();
        InternalTimerServiceImpl<Integer, String> timerService = InternalTimerServiceImplTest.createAndStartInternalTimerService(mockTriggerable, keyContext, processingTimeService, testKeyGroupRange, createQueueFactory());
        processingTimeService.setCurrentTime(17L);
        Assert.assertEquals(17, timerService.currentProcessingTime());
        processingTimeService.setCurrentTime(42);
        Assert.assertEquals(42, timerService.currentProcessingTime());
    }

    @Test
    public void testCurrentEventTime() throws Exception {
        @SuppressWarnings("unchecked")
        Triggerable<Integer, String> mockTriggerable = Mockito.mock(Triggerable.class);
        InternalTimerServiceImplTest.TestKeyContext keyContext = new InternalTimerServiceImplTest.TestKeyContext();
        TestProcessingTimeService processingTimeService = new TestProcessingTimeService();
        InternalTimerServiceImpl<Integer, String> timerService = InternalTimerServiceImplTest.createAndStartInternalTimerService(mockTriggerable, keyContext, processingTimeService, testKeyGroupRange, createQueueFactory());
        timerService.advanceWatermark(17);
        Assert.assertEquals(17, timerService.currentWatermark());
        timerService.advanceWatermark(42);
        Assert.assertEquals(42, timerService.currentWatermark());
    }

    /**
     * This also verifies that we don't have leakage between keys/namespaces.
     */
    @Test
    public void testSetAndFireEventTimeTimers() throws Exception {
        @SuppressWarnings("unchecked")
        Triggerable<Integer, String> mockTriggerable = Mockito.mock(Triggerable.class);
        InternalTimerServiceImplTest.TestKeyContext keyContext = new InternalTimerServiceImplTest.TestKeyContext();
        TestProcessingTimeService processingTimeService = new TestProcessingTimeService();
        InternalTimerServiceImpl<Integer, String> timerService = InternalTimerServiceImplTest.createAndStartInternalTimerService(mockTriggerable, keyContext, processingTimeService, testKeyGroupRange, createQueueFactory());
        // get two different keys
        int key1 = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        int key2 = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        while (key2 == key1) {
            key2 = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        } 
        keyContext.setCurrentKey(key1);
        timerService.registerEventTimeTimer("ciao", 10);
        timerService.registerEventTimeTimer("hello", 10);
        keyContext.setCurrentKey(key2);
        timerService.registerEventTimeTimer("ciao", 10);
        timerService.registerEventTimeTimer("hello", 10);
        Assert.assertEquals(4, timerService.numEventTimeTimers());
        Assert.assertEquals(2, timerService.numEventTimeTimers("hello"));
        Assert.assertEquals(2, timerService.numEventTimeTimers("ciao"));
        timerService.advanceWatermark(10);
        Mockito.verify(mockTriggerable, Mockito.times(4)).onEventTime(InternalTimerServiceImplTest.anyInternalTimer());
        Mockito.verify(mockTriggerable, Mockito.times(1)).onEventTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key1, "ciao")));
        Mockito.verify(mockTriggerable, Mockito.times(1)).onEventTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key1, "hello")));
        Mockito.verify(mockTriggerable, Mockito.times(1)).onEventTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key2, "ciao")));
        Mockito.verify(mockTriggerable, Mockito.times(1)).onEventTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key2, "hello")));
        Assert.assertEquals(0, timerService.numEventTimeTimers());
    }

    /**
     * This also verifies that we don't have leakage between keys/namespaces.
     */
    @Test
    public void testSetAndFireProcessingTimeTimers() throws Exception {
        @SuppressWarnings("unchecked")
        Triggerable<Integer, String> mockTriggerable = Mockito.mock(Triggerable.class);
        InternalTimerServiceImplTest.TestKeyContext keyContext = new InternalTimerServiceImplTest.TestKeyContext();
        TestProcessingTimeService processingTimeService = new TestProcessingTimeService();
        InternalTimerServiceImpl<Integer, String> timerService = InternalTimerServiceImplTest.createAndStartInternalTimerService(mockTriggerable, keyContext, processingTimeService, testKeyGroupRange, createQueueFactory());
        // get two different keys
        int key1 = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        int key2 = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        while (key2 == key1) {
            key2 = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        } 
        keyContext.setCurrentKey(key1);
        timerService.registerProcessingTimeTimer("ciao", 10);
        timerService.registerProcessingTimeTimer("hello", 10);
        keyContext.setCurrentKey(key2);
        timerService.registerProcessingTimeTimer("ciao", 10);
        timerService.registerProcessingTimeTimer("hello", 10);
        Assert.assertEquals(4, timerService.numProcessingTimeTimers());
        Assert.assertEquals(2, timerService.numProcessingTimeTimers("hello"));
        Assert.assertEquals(2, timerService.numProcessingTimeTimers("ciao"));
        processingTimeService.setCurrentTime(10);
        Mockito.verify(mockTriggerable, Mockito.times(4)).onProcessingTime(InternalTimerServiceImplTest.anyInternalTimer());
        Mockito.verify(mockTriggerable, Mockito.times(1)).onProcessingTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key1, "ciao")));
        Mockito.verify(mockTriggerable, Mockito.times(1)).onProcessingTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key1, "hello")));
        Mockito.verify(mockTriggerable, Mockito.times(1)).onProcessingTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key2, "ciao")));
        Mockito.verify(mockTriggerable, Mockito.times(1)).onProcessingTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key2, "hello")));
        Assert.assertEquals(0, timerService.numProcessingTimeTimers());
    }

    /**
     * This also verifies that we don't have leakage between keys/namespaces.
     *
     * <p>This also verifies that deleted timers don't fire.
     */
    @Test
    public void testDeleteEventTimeTimers() throws Exception {
        @SuppressWarnings("unchecked")
        Triggerable<Integer, String> mockTriggerable = Mockito.mock(Triggerable.class);
        InternalTimerServiceImplTest.TestKeyContext keyContext = new InternalTimerServiceImplTest.TestKeyContext();
        TestProcessingTimeService processingTimeService = new TestProcessingTimeService();
        InternalTimerServiceImpl<Integer, String> timerService = InternalTimerServiceImplTest.createAndStartInternalTimerService(mockTriggerable, keyContext, processingTimeService, testKeyGroupRange, createQueueFactory());
        // get two different keys
        int key1 = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        int key2 = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        while (key2 == key1) {
            key2 = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        } 
        keyContext.setCurrentKey(key1);
        timerService.registerEventTimeTimer("ciao", 10);
        timerService.registerEventTimeTimer("hello", 10);
        keyContext.setCurrentKey(key2);
        timerService.registerEventTimeTimer("ciao", 10);
        timerService.registerEventTimeTimer("hello", 10);
        Assert.assertEquals(4, timerService.numEventTimeTimers());
        Assert.assertEquals(2, timerService.numEventTimeTimers("hello"));
        Assert.assertEquals(2, timerService.numEventTimeTimers("ciao"));
        keyContext.setCurrentKey(key1);
        timerService.deleteEventTimeTimer("hello", 10);
        keyContext.setCurrentKey(key2);
        timerService.deleteEventTimeTimer("ciao", 10);
        Assert.assertEquals(2, timerService.numEventTimeTimers());
        Assert.assertEquals(1, timerService.numEventTimeTimers("hello"));
        Assert.assertEquals(1, timerService.numEventTimeTimers("ciao"));
        timerService.advanceWatermark(10);
        Mockito.verify(mockTriggerable, Mockito.times(2)).onEventTime(InternalTimerServiceImplTest.anyInternalTimer());
        Mockito.verify(mockTriggerable, Mockito.times(1)).onEventTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key1, "ciao")));
        Mockito.verify(mockTriggerable, Mockito.times(0)).onEventTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key1, "hello")));
        Mockito.verify(mockTriggerable, Mockito.times(0)).onEventTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key2, "ciao")));
        Mockito.verify(mockTriggerable, Mockito.times(1)).onEventTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key2, "hello")));
        Assert.assertEquals(0, timerService.numEventTimeTimers());
    }

    /**
     * This also verifies that we don't have leakage between keys/namespaces.
     *
     * <p>This also verifies that deleted timers don't fire.
     */
    @Test
    public void testDeleteProcessingTimeTimers() throws Exception {
        @SuppressWarnings("unchecked")
        Triggerable<Integer, String> mockTriggerable = Mockito.mock(Triggerable.class);
        InternalTimerServiceImplTest.TestKeyContext keyContext = new InternalTimerServiceImplTest.TestKeyContext();
        TestProcessingTimeService processingTimeService = new TestProcessingTimeService();
        InternalTimerServiceImpl<Integer, String> timerService = InternalTimerServiceImplTest.createAndStartInternalTimerService(mockTriggerable, keyContext, processingTimeService, testKeyGroupRange, createQueueFactory());
        // get two different keys
        int key1 = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        int key2 = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        while (key2 == key1) {
            key2 = InternalTimerServiceImplTest.getKeyInKeyGroupRange(testKeyGroupRange, maxParallelism);
        } 
        keyContext.setCurrentKey(key1);
        timerService.registerProcessingTimeTimer("ciao", 10);
        timerService.registerProcessingTimeTimer("hello", 10);
        keyContext.setCurrentKey(key2);
        timerService.registerProcessingTimeTimer("ciao", 10);
        timerService.registerProcessingTimeTimer("hello", 10);
        Assert.assertEquals(4, timerService.numProcessingTimeTimers());
        Assert.assertEquals(2, timerService.numProcessingTimeTimers("hello"));
        Assert.assertEquals(2, timerService.numProcessingTimeTimers("ciao"));
        keyContext.setCurrentKey(key1);
        timerService.deleteProcessingTimeTimer("hello", 10);
        keyContext.setCurrentKey(key2);
        timerService.deleteProcessingTimeTimer("ciao", 10);
        Assert.assertEquals(2, timerService.numProcessingTimeTimers());
        Assert.assertEquals(1, timerService.numProcessingTimeTimers("hello"));
        Assert.assertEquals(1, timerService.numProcessingTimeTimers("ciao"));
        processingTimeService.setCurrentTime(10);
        Mockito.verify(mockTriggerable, Mockito.times(2)).onProcessingTime(InternalTimerServiceImplTest.anyInternalTimer());
        Mockito.verify(mockTriggerable, Mockito.times(1)).onProcessingTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key1, "ciao")));
        Mockito.verify(mockTriggerable, Mockito.times(0)).onProcessingTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key1, "hello")));
        Mockito.verify(mockTriggerable, Mockito.times(0)).onProcessingTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key2, "ciao")));
        Mockito.verify(mockTriggerable, Mockito.times(1)).onProcessingTime(ArgumentMatchers.eq(new TimerHeapInternalTimer(10, key2, "hello")));
        Assert.assertEquals(0, timerService.numEventTimeTimers());
    }

    @Test
    public void testSnapshotAndRestore() throws Exception {
        testSnapshotAndRestore(VERSION);
    }

    @Test
    public void testSnapshotAndRestorePreVersioned() throws Exception {
        testSnapshotAndRestore(NO_VERSION);
    }

    /**
     * This test checks whether timers are assigned to correct key groups
     * and whether snapshot/restore respects key groups.
     */
    @Test
    public void testSnapshotAndRebalancingRestore() throws Exception {
        testSnapshotAndRebalancingRestore(VERSION);
    }

    @Test
    public void testSnapshotAndRebalancingRestorePreVersioned() throws Exception {
        testSnapshotAndRebalancingRestore(NO_VERSION);
    }

    private static class TestKeyContext implements KeyContext {
        private Object key;

        @Override
        public void setCurrentKey(Object key) {
            this.key = key;
        }

        @Override
        public Object getCurrentKey() {
            return key;
        }
    }
}

