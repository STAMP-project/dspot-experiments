/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl.operationexecutor.slowoperationdetector;


import SlowOperationLog.Invocation;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(SlowTest.class)
public class SlowOperationDetector_EntryProcessorTest extends SlowOperationDetectorAbstractTest {
    @Test
    public void testSlowEntryProcessor() throws InterruptedException {
        HazelcastInstance instance = getSingleNodeCluster(1000);
        IMap<String, String> map = SlowOperationDetectorAbstractTest.getMapWithSingleElement(instance);
        for (int i = 0; i < 3; i++) {
            map.executeOnEntries(getSlowEntryProcessor(3));
        }
        map.executeOnEntries(getSlowEntryProcessor(6));
        awaitSlowEntryProcessors();
        Collection<SlowOperationLog> logs = SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 1);
        SlowOperationLog firstLog = logs.iterator().next();
        SlowOperationDetectorAbstractTest.assertTotalInvocations(firstLog, 4);
        SlowOperationDetectorAbstractTest.assertEntryProcessorOperation(firstLog);
        SlowOperationDetectorAbstractTest.assertStackTraceContainsClassName(firstLog, "SlowEntryProcessor");
        Collection<SlowOperationLog.Invocation> invocations = SlowOperationDetectorAbstractTest.getInvocations(firstLog);
        HazelcastTestSupport.assertEqualsStringFormat("Expected %d invocations, but was %d", 4, invocations.size());
        for (SlowOperationLog.Invocation invocation : invocations) {
            SlowOperationDetectorAbstractTest.assertInvocationDurationBetween(invocation, 1000, 6500);
        }
    }

    @Test
    public void testMultipleSlowEntryProcessorClasses() throws InterruptedException {
        HazelcastInstance instance = getSingleNodeCluster(1000);
        IMap<String, String> map = SlowOperationDetectorAbstractTest.getMapWithSingleElement(instance);
        for (int i = 0; i < 3; i++) {
            map.executeOnEntries(getSlowEntryProcessor(3));
        }
        SlowOperationDetectorAbstractTest.SlowEntryProcessorChild entryProcessorChild = new SlowOperationDetectorAbstractTest.SlowEntryProcessorChild(3);
        map.executeOnEntries(entryProcessorChild);
        map.executeOnEntries(getSlowEntryProcessor(5));
        awaitSlowEntryProcessors();
        entryProcessorChild.await();
        Collection<SlowOperationLog> logs = SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 2);
        Iterator<SlowOperationLog> iterator = logs.iterator();
        SlowOperationLog firstLog = iterator.next();
        SlowOperationLog secondLog = iterator.next();
        Collection<SlowOperationLog.Invocation> firstInvocations = SlowOperationDetectorAbstractTest.getInvocations(firstLog);
        Collection<SlowOperationLog.Invocation> secondInvocations = SlowOperationDetectorAbstractTest.getInvocations(secondLog);
        int firstSize = firstInvocations.size();
        int secondSize = secondInvocations.size();
        Assert.assertTrue(String.format("Expected to find 1 and 4 invocations in logs, but was %d and %d. First log: %s%nSecond log: %s", firstSize, secondSize, firstLog.createDTO().toJson(), secondLog.createDTO().toJson()), (((firstSize == 1) ^ (secondSize == 1)) && ((firstSize == 4) ^ (secondSize == 4))));
        for (SlowOperationLog.Invocation invocation : firstInvocations) {
            SlowOperationDetectorAbstractTest.assertInvocationDurationBetween(invocation, 1000, 5500);
        }
        for (SlowOperationLog.Invocation invocation : secondInvocations) {
            SlowOperationDetectorAbstractTest.assertInvocationDurationBetween(invocation, 1000, 5500);
        }
    }

    @Test
    public void testNestedSlowEntryProcessor() {
        HazelcastInstance instance = getSingleNodeCluster(1000);
        IMap<String, String> map = SlowOperationDetectorAbstractTest.getMapWithSingleElement(instance);
        SlowOperationDetector_EntryProcessorTest.NestedSlowEntryProcessor entryProcessor = new SlowOperationDetector_EntryProcessorTest.NestedSlowEntryProcessor(map, 3);
        map.executeOnEntries(entryProcessor);
        entryProcessor.await();
        Collection<SlowOperationLog> logs = SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 1);
        SlowOperationLog firstLog = logs.iterator().next();
        SlowOperationDetectorAbstractTest.assertTotalInvocations(firstLog, 1);
        SlowOperationDetectorAbstractTest.assertEntryProcessorOperation(firstLog);
        SlowOperationDetectorAbstractTest.assertStackTraceContainsClassName(firstLog, "NestedSlowEntryProcessor");
        SlowOperationDetectorAbstractTest.assertStackTraceContainsClassName(firstLog, "SlowEntryProcessor");
    }

    private static class NestedSlowEntryProcessor implements EntryProcessor<String, String> {
        private final IMap<String, String> map;

        private final SlowOperationDetectorAbstractTest.SlowEntryProcessor entryProcessor;

        private NestedSlowEntryProcessor(IMap<String, String> map, int sleepSeconds) {
            this.map = map;
            this.entryProcessor = new SlowOperationDetectorAbstractTest.SlowEntryProcessor(sleepSeconds);
        }

        @Override
        public Object process(Map.Entry<String, String> entry) {
            SlowOperationDetectorAbstractTest.executeEntryProcessor(map, entryProcessor);
            return null;
        }

        @Override
        public EntryBackupProcessor<String, String> getBackupProcessor() {
            return null;
        }

        private void await() {
            entryProcessor.await();
        }
    }
}

