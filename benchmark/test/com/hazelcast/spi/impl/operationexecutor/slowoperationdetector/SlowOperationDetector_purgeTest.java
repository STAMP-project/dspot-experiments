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
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(SlowTest.class)
public class SlowOperationDetector_purgeTest extends SlowOperationDetectorAbstractTest {
    private HazelcastInstance instance;

    private IMap<String, String> map;

    @Test
    public void testPurging_Invocation() {
        setup("3");
        // all of these entry processors are executed after each other, not in parallel
        for (int i = 0; i < 2; i++) {
            map.executeOnEntries(getSlowEntryProcessor(3));
        }
        map.executeOnEntries(getSlowEntryProcessor(4));
        map.executeOnEntries(getSlowEntryProcessor(3));
        awaitSlowEntryProcessors();
        // shutdown to stop purging, so the last one or two entry processor invocations will survive
        SlowOperationDetectorAbstractTest.shutdownOperationService(instance);
        Collection<SlowOperationLog> logs = SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 1);
        SlowOperationLog firstLog = logs.iterator().next();
        SlowOperationDetectorAbstractTest.assertTotalInvocations(firstLog, 4);
        SlowOperationDetectorAbstractTest.assertEntryProcessorOperation(firstLog);
        SlowOperationDetectorAbstractTest.assertStackTraceContainsClassName(firstLog, "SlowEntryProcessor");
        Collection<SlowOperationLog.Invocation> invocations = SlowOperationDetectorAbstractTest.getInvocations(firstLog);
        int invocationCount = invocations.size();
        Assert.assertTrue(("Expected 1 or 2 invocations, but was " + invocationCount), ((invocationCount >= 1) && (invocationCount <= 2)));
        for (SlowOperationLog.Invocation invocation : invocations) {
            SlowOperationDetectorAbstractTest.assertInvocationDurationBetween(invocation, 1000, 3500);
        }
    }

    @Test
    public void testPurging_SlowOperationLog() {
        setup("2");
        // all of these entry processors are executed after each other, not in parallel
        for (int i = 0; i < 2; i++) {
            map.executeOnEntries(getSlowEntryProcessor(3));
        }
        awaitSlowEntryProcessors();
        // sleep a bit to purge the last entry processor invocation (and the whole slow operation log with it)
        HazelcastTestSupport.sleepSeconds(3);
        SlowOperationDetectorAbstractTest.shutdownOperationService(instance);
        SlowOperationDetectorAbstractTest.getSlowOperationLogsAndAssertNumberOfSlowOperationLogs(instance, 0);
    }
}

