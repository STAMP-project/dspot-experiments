/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import ControlRate.ATTRIBUTE_RATE;
import ControlRate.DATA_RATE;
import ControlRate.FLOWFILE_RATE;
import ControlRate.GROUPING_ATTRIBUTE_NAME;
import ControlRate.MAX_RATE;
import ControlRate.RATE_CONTROL_ATTRIBUTE_NAME;
import ControlRate.RATE_CONTROL_CRITERIA;
import ControlRate.REL_FAILURE;
import ControlRate.REL_SUCCESS;
import ControlRate.TIME_PERIOD;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestControlRate {
    @Test
    public void testLimitExceededThenOtherLimitNotExceeded() {
        // If we have flowfiles queued that have different values for the "Rate Controlled Attribute"
        // and we encounter a FlowFile whose rate should be throttled, we should continue pulling other flowfiles
        // whose rate does not need to be throttled.
        final TestRunner runner = TestRunners.newTestRunner(new ControlRate());
        runner.setProperty(RATE_CONTROL_CRITERIA, FLOWFILE_RATE);
        runner.setProperty(MAX_RATE, "3");
        runner.setProperty(TIME_PERIOD, "1 min");
        runner.setProperty(GROUPING_ATTRIBUTE_NAME, "group");
        final Map<String, String> group1 = Collections.singletonMap("group", "1");
        final Map<String, String> group2 = Collections.singletonMap("group", "2");
        for (int i = 0; i < 5; i++) {
            runner.enqueue("test data", group1);
        }
        runner.enqueue("test data", group2);
        // Run several times, just to allow the processor to terminate the first poll if it wishes to
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 4);
        final List<MockFlowFile> output = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(3L, output.stream().filter(( ff) -> ff.getAttribute("group").equals("1")).count());
        Assert.assertEquals(1L, output.stream().filter(( ff) -> ff.getAttribute("group").equals("2")).count());
    }

    @Test
    public void testFileCountRate() throws InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new ControlRate());
        runner.setProperty(RATE_CONTROL_CRITERIA, FLOWFILE_RATE);
        runner.setProperty(MAX_RATE, "3");
        runner.setProperty(TIME_PERIOD, "1 sec");
        runner.enqueue("test data 1");
        runner.enqueue("test data 2");
        runner.enqueue("test data 3");
        runner.enqueue("test data 4");
        runner.run(4, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        runner.clearTransferState();
        runner.run(50, false);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertQueueNotEmpty();
        // we have sent 3 files and after 1 second, we should be able to send the 4th
        Thread.sleep(1100L);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.assertQueueEmpty();
    }

    @Test
    public void testFileCountWithGrouping() throws InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new ControlRate());
        runner.setProperty(RATE_CONTROL_CRITERIA, FLOWFILE_RATE);
        runner.setProperty(MAX_RATE, "2");
        runner.setProperty(TIME_PERIOD, "1 sec");
        runner.setProperty(GROUPING_ATTRIBUTE_NAME, "group");
        createFlowFileWithGroup(runner, "one");
        createFlowFileWithGroup(runner, "two");
        createFlowFileWithGroup(runner, "one");
        createFlowFileWithGroup(runner, "two");
        createFlowFileWithGroup(runner, "one");
        createFlowFileWithGroup(runner, "two");
        runner.run(6, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 4);
        runner.clearTransferState();
        runner.run(50, false);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertQueueNotEmpty();
        // we have sent 2 files per group and after 1 second, we should be able to send the remaining 1 file per group
        Thread.sleep(1100L);
        runner.run(2);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        runner.assertQueueEmpty();
    }

    @Test
    public void testDataSizeRate() throws InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new ControlRate());
        runner.setProperty(RATE_CONTROL_CRITERIA, DATA_RATE);
        runner.setProperty(MAX_RATE, "20 b");
        runner.setProperty(TIME_PERIOD, "1 sec");
        runner.enqueue("testdata 1");
        runner.enqueue("testdata 2");
        runner.enqueue("testdata 3");
        runner.enqueue("testdata 4");
        runner.run(4, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        runner.clearTransferState();
        runner.run(50, false);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertQueueNotEmpty();
        // we have sent 20 bytes and after 1 second, we should be able to send 20 more
        Thread.sleep(1100L);
        runner.run(2, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        runner.assertQueueEmpty();
    }

    @Test
    public void testViaAttribute() throws InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new ControlRate());
        runner.setProperty(RATE_CONTROL_CRITERIA, ATTRIBUTE_RATE);
        runner.setProperty(RATE_CONTROL_ATTRIBUTE_NAME, "count");
        runner.setProperty(MAX_RATE, "20000");
        runner.setProperty(TIME_PERIOD, "1 sec");
        createFlowFile(runner, 1000);
        createFlowFile(runner, 3000);
        createFlowFile(runner, 5000);
        createFlowFile(runner, 20000);
        createFlowFile(runner, 1000);
        runner.run(5, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 4);
        runner.clearTransferState();
        // at this point, we have sent through 29,000 but our max is 20,000 per second.
        // After 1.45 seconds (29000 / 20000), we should be able to send another 20,000
        runner.run(50, false);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertQueueNotEmpty();
        Thread.sleep(1200L);
        // at this point, more than TIME_PERIOD 1.0 seconds but less than 1.45 seconds have passed
        runner.run(50, false);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertQueueNotEmpty();
        Thread.sleep(600L);
        // at this point, more than 1.45 seconds have passed, so we should be able to send another 20,000
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertQueueEmpty();
    }

    @Test
    public void testBadAttributeRate() {
        final TestRunner runner = TestRunners.newTestRunner(new ControlRate());
        runner.setProperty(RATE_CONTROL_CRITERIA, ATTRIBUTE_RATE);
        runner.setProperty(RATE_CONTROL_ATTRIBUTE_NAME, "count");
        runner.setProperty(MAX_RATE, "20000");
        runner.setProperty(TIME_PERIOD, "1 sec");
        final Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put("count", "bad string");
        runner.enqueue(new byte[0], attributeMap);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertQueueEmpty();
    }

    @Test
    public void testBatchLimit() throws InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new ControlRate());
        runner.setProperty(RATE_CONTROL_CRITERIA, FLOWFILE_RATE);
        runner.setProperty(MAX_RATE, "5555");
        runner.setProperty(TIME_PERIOD, "1 sec");
        final int TEST_FILE_COUNT = 1500;
        for (int i = 0; i < TEST_FILE_COUNT; i++) {
            runner.enqueue(("test data " + i));
        }
        runner.run(1, false);
        // after 1 run should have MAX_FLOW_FILES_PER_BATCH files transferred and remainder of TEST_FILE_COUNT in queue
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, ControlRate.MAX_FLOW_FILES_PER_BATCH);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertQueueNotEmpty();
        Assert.assertEquals((TEST_FILE_COUNT - (ControlRate.MAX_FLOW_FILES_PER_BATCH)), runner.getQueueSize().getObjectCount());
        runner.run(1, false);
        // after 2 runs should have TEST_FILE_COUNT files transferred and 0 in queue
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, TEST_FILE_COUNT);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertQueueEmpty();
    }

    @Test
    public void testNonExistingGroupAttribute() throws InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new ControlRate());
        runner.setProperty(RATE_CONTROL_CRITERIA, FLOWFILE_RATE);
        runner.setProperty(MAX_RATE, "2");
        runner.setProperty(TIME_PERIOD, "1 sec");
        runner.setProperty(GROUPING_ATTRIBUTE_NAME, "group");
        createFlowFileWithGroup(runner, "one");
        createFlowFile(runner, 1);// no group set on this flow file

        createFlowFileWithGroup(runner, "one");
        createFlowFile(runner, 2);// no group set on this flow file

        runner.run(4, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 4);
        runner.assertQueueEmpty();
    }
}

