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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.dto.SlowOperationDTO;
import com.hazelcast.internal.management.dto.SlowOperationInvocationDTO;
import com.hazelcast.logging.ILogger;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(SlowTest.class)
public class SlowOperationDetector_JsonTest extends SlowOperationDetectorAbstractTest {
    private HazelcastInstance instance;

    private IMap<String, String> map;

    private ILogger logger;

    @Test
    public void testJSON() throws InterruptedException {
        final String operationDetails = "FakeOperation(id=255, partitionId=2)";
        Object operation = new Object() {
            @Override
            public String toString() {
                return operationDetails;
            }
        };
        String stackTrace = "stackTrace";
        int id = 5;
        int durationMs = 4444;
        long nowMillis = System.currentTimeMillis();
        long nowNanos = System.nanoTime();
        long durationNanos = TimeUnit.MILLISECONDS.toNanos(durationMs);
        SlowOperationLog log = new SlowOperationLog(stackTrace, operation);
        log.totalInvocations.incrementAndGet();
        log.getOrCreate(id, operationDetails, durationNanos, nowNanos, nowMillis);
        JsonObject json = log.createDTO().toJson();
        logger.finest(json.toString());
        SlowOperationDTO slowOperationDTO = new SlowOperationDTO();
        slowOperationDTO.fromJson(json);
        Assert.assertTrue(String.format("Expected operation '%s' to contain inner class", slowOperationDTO.operation), slowOperationDTO.operation.contains("SlowOperationDetector_JsonTest$1"));
        HazelcastTestSupport.assertEqualsStringFormat("Expected stack trace '%s', but was '%s'", stackTrace, slowOperationDTO.stackTrace);
        HazelcastTestSupport.assertEqualsStringFormat("Expected totalInvocations '%d', but was '%d'", 1, slowOperationDTO.totalInvocations);
        HazelcastTestSupport.assertEqualsStringFormat("Expected invocations.size() '%d', but was '%d'", 1, slowOperationDTO.invocations.size());
        SlowOperationInvocationDTO invocationDTO = slowOperationDTO.invocations.get(0);
        HazelcastTestSupport.assertEqualsStringFormat("Expected id '%d', but was '%d'", id, invocationDTO.id);
        HazelcastTestSupport.assertEqualsStringFormat("Expected details '%s', but was '%s'", operationDetails, invocationDTO.operationDetails);
        HazelcastTestSupport.assertEqualsStringFormat("Expected startedAt '%d', but was '%d'", (nowMillis - durationMs), invocationDTO.startedAt);
        HazelcastTestSupport.assertEqualsStringFormat("Expected durationMs '%d', but was '%d'", durationMs, invocationDTO.durationMs);
    }

    @Test
    public void testJSON_SlowEntryProcessor() {
        for (int i = 0; i < 2; i++) {
            map.executeOnEntries(getSlowEntryProcessor(3));
        }
        map.executeOnEntries(getSlowEntryProcessor(4));
        map.executeOnEntries(getSlowEntryProcessor(3));
        awaitSlowEntryProcessors();
        logger.finest(SlowOperationDetectorAbstractTest.getOperationStats(instance).toString());
        JsonObject firstLogJsonObject = SlowOperationDetectorAbstractTest.getSlowOperationLogsJsonArray(instance).get(0).asObject();
        SlowOperationDetectorAbstractTest.assertJSONContainsClassName(firstLogJsonObject, "SlowEntryProcessor");
        HazelcastTestSupport.assertEqualsStringFormat("Expected %d invocations, but was %d", 4, firstLogJsonObject.get("invocations").asArray().size());
    }

    @Test
    public void testJSON_multipleEntryProcessorClasses() throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            map.executeOnEntries(getSlowEntryProcessor(3));
        }
        SlowOperationDetectorAbstractTest.SlowEntryProcessorChild entryProcessorChild = new SlowOperationDetectorAbstractTest.SlowEntryProcessorChild(3);
        map.executeOnEntries(entryProcessorChild);
        map.executeOnEntries(getSlowEntryProcessor(4));
        awaitSlowEntryProcessors();
        entryProcessorChild.await();
        logger.finest(SlowOperationDetectorAbstractTest.getOperationStats(instance).toString());
        JsonArray slowOperationLogsJsonArray = SlowOperationDetectorAbstractTest.getSlowOperationLogsJsonArray(instance);
        JsonObject firstLogJsonObject = slowOperationLogsJsonArray.get(0).asObject();
        JsonObject secondLogJsonObject = slowOperationLogsJsonArray.get(1).asObject();
        SlowOperationDetectorAbstractTest.assertJSONContainsClassNameJustOnce(firstLogJsonObject, secondLogJsonObject, "SlowEntryProcessor");
        SlowOperationDetectorAbstractTest.assertJSONContainsClassNameJustOnce(firstLogJsonObject, secondLogJsonObject, "SlowEntryProcessorChild");
        int firstSize = firstLogJsonObject.get("invocations").asArray().size();
        int secondSize = secondLogJsonObject.get("invocations").asArray().size();
        Assert.assertTrue(String.format("Expected to find 1 and 3 invocations in logs, but was %d and %d", firstSize, secondSize), (((firstSize == 1) ^ (secondSize == 1)) && ((firstSize == 3) ^ (secondSize == 3))));
    }
}

