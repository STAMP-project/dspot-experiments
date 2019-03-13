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


import GroupProperty.SLOW_OPERATION_DETECTOR_ENABLED;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(SlowTest.class)
public class SlowOperationDetectorBasicTest extends SlowOperationDetectorAbstractTest {
    private HazelcastInstance instance;

    @Test
    public void testDisabled() {
        Config config = new Config();
        config.setProperty(SLOW_OPERATION_DETECTOR_ENABLED.getName(), "false");
        config.setProperty(GroupProperty.SLOW_OPERATION_DETECTOR_THRESHOLD_MILLIS.getName(), "1000");
        instance = createHazelcastInstance(config);
        SlowOperationDetectorBasicTest.SlowRunnable runnable = new SlowOperationDetectorBasicTest.SlowRunnable(5, Operation.GENERIC_PARTITION_ID);
        HazelcastTestSupport.getOperationService(instance).execute(runnable);
        runnable.await();
        SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 0);
    }

    @Test
    public void testSlowRunnableOnGenericOperationThread() {
        instance = getSingleNodeCluster(1000);
        SlowOperationDetectorBasicTest.SlowRunnable runnable = new SlowOperationDetectorBasicTest.SlowRunnable(5, Operation.GENERIC_PARTITION_ID);
        HazelcastTestSupport.getOperationService(instance).execute(runnable);
        runnable.await();
        Collection<SlowOperationLog> logs = SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 1);
        SlowOperationLog firstLog = logs.iterator().next();
        SlowOperationDetectorAbstractTest.assertTotalInvocations(firstLog, 1);
        SlowOperationDetectorAbstractTest.assertOperationContainsClassName(firstLog, "SlowRunnable");
        SlowOperationDetectorAbstractTest.assertStackTraceContainsClassName(firstLog, "SlowRunnable");
    }

    @Test
    public void testSlowRunnableOnPartitionOperationThread() {
        instance = getSingleNodeCluster(1000);
        SlowOperationDetectorBasicTest.SlowRunnable runnable = new SlowOperationDetectorBasicTest.SlowRunnable(5, 1);
        HazelcastTestSupport.getOperationService(instance).execute(runnable);
        runnable.await();
        Collection<SlowOperationLog> logs = SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 1);
        SlowOperationLog firstLog = logs.iterator().next();
        SlowOperationDetectorAbstractTest.assertTotalInvocations(firstLog, 1);
        SlowOperationDetectorAbstractTest.assertOperationContainsClassName(firstLog, "SlowRunnable");
        SlowOperationDetectorAbstractTest.assertStackTraceContainsClassName(firstLog, "SlowRunnable");
    }

    @Test
    public void testSlowOperationOnGenericOperationThread() {
        instance = getSingleNodeCluster(1000);
        SlowOperationDetectorBasicTest.SlowOperation operation = new SlowOperationDetectorBasicTest.SlowOperation(5);
        SlowOperationDetectorAbstractTest.executeOperation(instance, operation);
        operation.join();
        Collection<SlowOperationLog> logs = SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 1);
        SlowOperationLog firstLog = logs.iterator().next();
        SlowOperationDetectorAbstractTest.assertTotalInvocations(firstLog, 1);
        SlowOperationDetectorAbstractTest.assertOperationContainsClassName(firstLog, "SlowOperation");
        SlowOperationDetectorAbstractTest.assertStackTraceContainsClassName(firstLog, "SlowOperation");
    }

    @Test
    public void testSlowOperationOnPartitionOperationThread() {
        instance = getSingleNodeCluster(1000);
        SlowOperationDetectorBasicTest.SlowOperation operation = new SlowOperationDetectorBasicTest.SlowOperation(5, 2);
        SlowOperationDetectorAbstractTest.executeOperation(instance, operation);
        operation.join();
        Collection<SlowOperationLog> logs = SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 1);
        SlowOperationLog firstLog = logs.iterator().next();
        SlowOperationDetectorAbstractTest.assertTotalInvocations(firstLog, 1);
        SlowOperationDetectorAbstractTest.assertOperationContainsClassName(firstLog, "SlowOperation");
        SlowOperationDetectorAbstractTest.assertStackTraceContainsClassName(firstLog, "SlowOperation");
    }

    @Test
    public void testNestedSlowOperationOnSamePartition() {
        instance = getSingleNodeCluster(1000);
        IMap<String, String> map = SlowOperationDetectorAbstractTest.getMapWithSingleElement(instance);
        int partitionId = SlowOperationDetectorAbstractTest.getDefaultPartitionId(instance);
        SlowOperationDetectorBasicTest.NestedSlowOperationOnSamePartition operation = new SlowOperationDetectorBasicTest.NestedSlowOperationOnSamePartition(map, partitionId, 5);
        SlowOperationDetectorAbstractTest.executeOperation(instance, operation);
        operation.await();
        Collection<SlowOperationLog> logs = SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 1);
        SlowOperationLog firstLog = logs.iterator().next();
        SlowOperationDetectorAbstractTest.assertTotalInvocations(firstLog, 1);
        SlowOperationDetectorAbstractTest.assertOperationContainsClassName(firstLog, "NestedSlowOperationOnSamePartition");
        SlowOperationDetectorAbstractTest.assertStackTraceContainsClassName(firstLog, "NestedSlowOperationOnSamePartition");
        SlowOperationDetectorAbstractTest.assertStackTraceContainsClassName(firstLog, "SlowEntryProcessor");
    }

    @Test
    public void testNestedSlowOperationOnPartitionAndGenericOperationThreads() {
        instance = getSingleNodeCluster(1000);
        SlowOperationDetectorBasicTest.NestedSlowOperationOnPartitionAndGenericOperationThreads operation = new SlowOperationDetectorBasicTest.NestedSlowOperationOnPartitionAndGenericOperationThreads(instance, 5);
        SlowOperationDetectorAbstractTest.executeOperation(instance, operation);
        operation.await();
        Collection<SlowOperationLog> logs = SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 1);
        SlowOperationLog firstLog = logs.iterator().next();
        SlowOperationDetectorAbstractTest.assertTotalInvocations(firstLog, 1);
        SlowOperationDetectorAbstractTest.assertStackTraceNotContainsClassName(firstLog, "NestedSlowOperation");
        SlowOperationDetectorAbstractTest.assertStackTraceContainsClassName(firstLog, "SlowOperation");
    }

    @Test
    public void testSlowRecursiveOperation() {
        int partitionThreads = 32;
        int numberOfOperations = 40;
        int recursionDepth = 15;
        Config config = new Config().setProperty(GroupProperty.SLOW_OPERATION_DETECTOR_THRESHOLD_MILLIS.getName(), "1000").setProperty(GroupProperty.SLOW_OPERATION_DETECTOR_LOG_RETENTION_SECONDS.getName(), String.valueOf(Integer.MAX_VALUE)).setProperty(GroupProperty.PARTITION_OPERATION_THREAD_COUNT.getName(), String.valueOf(partitionThreads));
        instance = createHazelcastInstance(config);
        List<SlowOperationDetectorBasicTest.SlowRecursiveOperation> operations = new ArrayList<SlowOperationDetectorBasicTest.SlowRecursiveOperation>(numberOfOperations);
        int partitionCount = HazelcastTestSupport.getPartitionService(instance).getPartitionCount();
        int partitionIndex = 1;
        for (int i = 0; i < numberOfOperations; i++) {
            int partitionId = partitionIndex % partitionCount;
            SlowOperationDetectorBasicTest.SlowRecursiveOperation operation = new SlowOperationDetectorBasicTest.SlowRecursiveOperation(partitionId, recursionDepth, 20);
            operations.add(operation);
            SlowOperationDetectorAbstractTest.executeOperation(instance, operation);
            partitionIndex += (partitionCount / partitionThreads) + 1;
        }
        for (SlowOperationDetectorBasicTest.SlowRecursiveOperation operation : operations) {
            operation.join();
        }
        Collection<SlowOperationLog> logs = SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 1);
        SlowOperationLog firstLog = logs.iterator().next();
        SlowOperationDetectorAbstractTest.assertTotalInvocations(firstLog, numberOfOperations);
        SlowOperationDetectorAbstractTest.assertStackTraceContainsClassName(firstLog, "SlowRecursiveOperation");
    }

    private static class SlowRunnable extends SlowOperationDetectorAbstractTest.CountDownLatchHolder implements PartitionSpecificRunnable {
        private final int sleepSeconds;

        private final int partitionId;

        private SlowRunnable(int sleepSeconds, int partitionId) {
            this.sleepSeconds = sleepSeconds;
            this.partitionId = partitionId;
        }

        @Override
        public void run() {
            HazelcastTestSupport.sleepSeconds(sleepSeconds);
            done();
        }

        @Override
        public int getPartitionId() {
            return partitionId;
        }
    }

    private static class SlowOperation extends SlowOperationDetectorAbstractTest.JoinableOperation {
        private final int sleepSeconds;

        private SlowOperation(int sleepSeconds) {
            this.sleepSeconds = sleepSeconds;
        }

        private SlowOperation(int sleepSeconds, int partitionId) {
            this.sleepSeconds = sleepSeconds;
            setPartitionId(partitionId);
        }

        @Override
        public void run() throws Exception {
            HazelcastTestSupport.sleepSeconds(sleepSeconds);
            done();
        }
    }

    private static class NestedSlowOperationOnSamePartition extends Operation {
        private final IMap<String, String> map;

        private final SlowOperationDetectorAbstractTest.SlowEntryProcessor entryProcessor;

        private NestedSlowOperationOnSamePartition(IMap<String, String> map, int partitionId, int sleepSeconds) {
            this.map = map;
            this.entryProcessor = new SlowOperationDetectorAbstractTest.SlowEntryProcessor(sleepSeconds);
            setPartitionId(partitionId);
        }

        @Override
        public void run() throws Exception {
            SlowOperationDetectorAbstractTest.executeEntryProcessor(map, entryProcessor);
        }

        public void await() {
            entryProcessor.await();
        }
    }

    private static class NestedSlowOperationOnPartitionAndGenericOperationThreads extends Operation {
        private final HazelcastInstance instance;

        private final SlowOperationDetectorBasicTest.SlowOperation operation;

        private NestedSlowOperationOnPartitionAndGenericOperationThreads(HazelcastInstance instance, int sleepSeconds) {
            this.instance = instance;
            this.operation = new SlowOperationDetectorBasicTest.SlowOperation(sleepSeconds, Operation.GENERIC_PARTITION_ID);
            int partitionId = SlowOperationDetectorAbstractTest.getDefaultPartitionId(instance);
            setPartitionId(partitionId);
        }

        @Override
        public void run() throws Exception {
            HazelcastTestSupport.getOperationService(instance).execute(operation);
        }

        public void await() {
            operation.join();
        }
    }

    private static class SlowRecursiveOperation extends SlowOperationDetectorAbstractTest.JoinableOperation {
        private final int recursionDepth;

        private final int sleepSeconds;

        public SlowRecursiveOperation(int partitionId, int recursionDepth, int sleepSeconds) {
            this.recursionDepth = recursionDepth;
            this.sleepSeconds = sleepSeconds;
            setPartitionId(partitionId);
        }

        @Override
        public void run() throws Exception {
            recursiveCall(recursionDepth);
        }

        void recursiveCall(int depth) {
            if (depth == 0) {
                HazelcastTestSupport.sleepSeconds(sleepSeconds);
                done();
                return;
            }
            recursiveCall((depth - 1));
        }
    }
}

