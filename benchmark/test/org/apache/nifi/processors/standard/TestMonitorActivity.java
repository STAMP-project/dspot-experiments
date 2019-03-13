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


import CoreAttributes.FILENAME;
import CoreAttributes.UUID;
import MonitorActivity.CONTINUALLY_SEND_MESSAGES;
import MonitorActivity.COPY_ATTRIBUTES;
import MonitorActivity.MONITORING_SCOPE;
import MonitorActivity.REL_ACTIVITY_RESTORED;
import MonitorActivity.REL_INACTIVE;
import MonitorActivity.REL_SUCCESS;
import MonitorActivity.REPORTING_NODE;
import MonitorActivity.REPORT_NODE_PRIMARY;
import MonitorActivity.SCOPE_CLUSTER;
import MonitorActivity.SCOPE_NODE;
import MonitorActivity.STATE_KEY_LATEST_SUCCESS_TRANSFER;
import MonitorActivity.THRESHOLD;
import Scope.CLUSTER;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.nifi.components.state.StateMap;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestMonitorActivity {
    @Test
    public void testFirstMessage() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(1000L));
        runner.setProperty(CONTINUALLY_SEND_MESSAGES, "false");
        runner.setProperty(THRESHOLD, "100 millis");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.clearTransferState();
        Thread.sleep(1000L);
        runNext(runner);
        runner.assertAllFlowFilesTransferred(REL_INACTIVE, 1);
        runner.clearTransferState();
        // ensure we don't keep creating the message
        for (int i = 0; i < 10; i++) {
            runNext(runner);
            runner.assertTransferCount(REL_SUCCESS, 0);
            runner.assertTransferCount(REL_INACTIVE, 0);
            runner.assertTransferCount(REL_ACTIVITY_RESTORED, 0);
            Thread.sleep(100L);
        }
        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "value");
        attributes.put("key1", "value1");
        runner.enqueue(new byte[0], attributes);
        runNext(runner);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ACTIVITY_RESTORED, 1);
        MockFlowFile restoredFlowFile = runner.getFlowFilesForRelationship(REL_ACTIVITY_RESTORED).get(0);
        String flowFileContent = new String(restoredFlowFile.toByteArray());
        Assert.assertTrue(Pattern.matches("Activity restored at time: (.*) after being inactive for 0 minutes", flowFileContent));
        restoredFlowFile.assertAttributeNotExists("key");
        restoredFlowFile.assertAttributeNotExists("key1");
        runner.clearTransferState();
        runner.setProperty(CONTINUALLY_SEND_MESSAGES, "true");
        Thread.sleep(200L);
        for (int i = 0; i < 10; i++) {
            runNext(runner);
            Thread.sleep(200L);
        }
        runner.assertTransferCount(REL_INACTIVE, 10);
        runner.assertTransferCount(REL_ACTIVITY_RESTORED, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.clearTransferState();
        runner.enqueue(new byte[0], attributes);
        runNext(runner);
        runner.assertTransferCount(REL_INACTIVE, 0);
        runner.assertTransferCount(REL_ACTIVITY_RESTORED, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        restoredFlowFile = runner.getFlowFilesForRelationship(REL_ACTIVITY_RESTORED).get(0);
        flowFileContent = new String(restoredFlowFile.toByteArray());
        Assert.assertTrue(Pattern.matches("Activity restored at time: (.*) after being inactive for 0 minutes", flowFileContent));
        restoredFlowFile.assertAttributeNotExists("key");
        restoredFlowFile.assertAttributeNotExists("key1");
    }

    @Test
    public void testFirstMessageWithInherit() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(1000L));
        runner.setProperty(CONTINUALLY_SEND_MESSAGES, "false");
        runner.setProperty(THRESHOLD, "100 millis");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile originalFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        runner.clearTransferState();
        Thread.sleep(1000L);
        runNext(runner);
        runner.assertAllFlowFilesTransferred(REL_INACTIVE, 1);
        runner.clearTransferState();
        // ensure we don't keep creating the message
        for (int i = 0; i < 10; i++) {
            runNext(runner);
            runner.assertTransferCount(REL_SUCCESS, 0);
            runner.assertTransferCount(REL_INACTIVE, 0);
            runner.assertTransferCount(REL_ACTIVITY_RESTORED, 0);
            Thread.sleep(100L);
        }
        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "value");
        attributes.put("key1", "value1");
        runner.enqueue(new byte[0], attributes);
        runNext(runner);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ACTIVITY_RESTORED, 1);
        MockFlowFile restoredFlowFile = runner.getFlowFilesForRelationship(REL_ACTIVITY_RESTORED).get(0);
        String flowFileContent = new String(restoredFlowFile.toByteArray());
        Assert.assertTrue(Pattern.matches("Activity restored at time: (.*) after being inactive for 0 minutes", flowFileContent));
        restoredFlowFile.assertAttributeEquals("key", "value");
        restoredFlowFile.assertAttributeEquals("key1", "value1");
        // verify the UUIDs are not the same
        restoredFlowFile.assertAttributeNotEquals(UUID.key(), originalFlowFile.getAttribute(UUID.key()));
        restoredFlowFile.assertAttributeNotEquals(FILENAME.key(), originalFlowFile.getAttribute(FILENAME.key()));
        Assert.assertTrue(String.format("file sizes match when they shouldn't original=%1$s restored=%2$s", originalFlowFile.getSize(), restoredFlowFile.getSize()), ((restoredFlowFile.getSize()) != (originalFlowFile.getSize())));
        Assert.assertTrue(String.format("lineage start dates match when they shouldn't original=%1$s restored=%2$s", originalFlowFile.getLineageStartDate(), restoredFlowFile.getLineageStartDate()), ((restoredFlowFile.getLineageStartDate()) != (originalFlowFile.getLineageStartDate())));
        runner.clearTransferState();
        runner.setProperty(CONTINUALLY_SEND_MESSAGES, "true");
        Thread.sleep(200L);
        for (int i = 0; i < 10; i++) {
            runNext(runner);
            Thread.sleep(200L);
        }
        runner.assertTransferCount(REL_INACTIVE, 10);
        runner.assertTransferCount(REL_ACTIVITY_RESTORED, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.clearTransferState();
        runner.enqueue(new byte[0], attributes);
        runNext(runner);
        runner.assertTransferCount(REL_INACTIVE, 0);
        runner.assertTransferCount(REL_ACTIVITY_RESTORED, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        restoredFlowFile = runner.getFlowFilesForRelationship(REL_ACTIVITY_RESTORED).get(0);
        flowFileContent = new String(restoredFlowFile.toByteArray());
        Assert.assertTrue(Pattern.matches("Activity restored at time: (.*) after being inactive for 0 minutes", flowFileContent));
        restoredFlowFile.assertAttributeEquals("key", "value");
        restoredFlowFile.assertAttributeEquals("key1", "value1");
        restoredFlowFile.assertAttributeNotEquals(UUID.key(), originalFlowFile.getAttribute(UUID.key()));
        restoredFlowFile.assertAttributeNotEquals(FILENAME.key(), originalFlowFile.getAttribute(FILENAME.key()));
        Assert.assertTrue(String.format("file sizes match when they shouldn't original=%1$s restored=%2$s", originalFlowFile.getSize(), restoredFlowFile.getSize()), ((restoredFlowFile.getSize()) != (originalFlowFile.getSize())));
        Assert.assertTrue(String.format("lineage start dates match when they shouldn't original=%1$s restored=%2$s", originalFlowFile.getLineageStartDate(), restoredFlowFile.getLineageStartDate()), ((restoredFlowFile.getLineageStartDate()) != (originalFlowFile.getLineageStartDate())));
    }

    @Test(timeout = 5000)
    public void testFirstRunNoMessages() throws IOException, InterruptedException {
        // don't use the TestableProcessor, we want the real timestamp from @OnScheduled
        final TestRunner runner = TestRunners.newTestRunner(new MonitorActivity());
        runner.setProperty(CONTINUALLY_SEND_MESSAGES, "false");
        int threshold = 100;
        boolean rerun = false;
        do {
            rerun = false;
            runner.setProperty(THRESHOLD, (threshold + " millis"));
            Thread.sleep(1000L);
            // shouldn't generate inactivity b/c run() will reset the lastSuccessfulTransfer if @OnSchedule & onTrigger
            // does not  get called more than MonitorActivity.THRESHOLD apart
            runner.run();
            runner.assertTransferCount(REL_SUCCESS, 0);
            List<MockFlowFile> inactiveFlowFiles = runner.getFlowFilesForRelationship(REL_INACTIVE);
            if ((inactiveFlowFiles.size()) == 1) {
                // Seems Threshold was not sufficient, which has caused One inactive message.
                // Step-up and rerun the test until successful or jUnit times out
                threshold += threshold;
                rerun = true;
            } else {
                runner.assertTransferCount(REL_INACTIVE, 0);
            }
            runner.assertTransferCount(REL_ACTIVITY_RESTORED, 0);
            runner.clearTransferState();
        } while (rerun );
    }

    /**
     * Since each call to run() will call @OnScheduled methods which will set the lastSuccessfulTransfer to the
     * current time, we need a way to create an artificial time difference between calls to run.
     */
    private class TestableProcessor extends MonitorActivity {
        private final long timestampDifference;

        public TestableProcessor(final long timestampDifference) {
            this.timestampDifference = timestampDifference;
        }

        @Override
        public void resetLastSuccessfulTransfer() {
            setLastSuccessfulTransfer(((System.currentTimeMillis()) - (timestampDifference)));
        }
    }

    @Test
    public void testClusterMonitorInvalidReportingNode() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(100));
        runner.setClustered(true);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_NODE);
        runner.setProperty(REPORTING_NODE, REPORT_NODE_PRIMARY);
        runner.assertNotValid();
    }

    @Test
    public void testClusterMonitorActive() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(100));
        runner.setClustered(true);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        // This has to be very small threshold, otherwise, MonitorActivity skip persisting state.
        runner.setProperty(THRESHOLD, "1 ms");
        runner.enqueue("Incoming data");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final StateMap updatedState = runner.getStateManager().getState(CLUSTER);
        Assert.assertNotNull("Latest timestamp should be persisted", updatedState.get(STATE_KEY_LATEST_SUCCESS_TRANSFER));
        // Should be null because COPY_ATTRIBUTES is null.
        Assert.assertNull(updatedState.get("key1"));
        Assert.assertNull(updatedState.get("key2"));
    }

    @Test
    public void testClusterMonitorActiveFallbackToNodeScope() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(100));
        runner.setClustered(false);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        // This has to be very small threshold, otherwise, MonitorActivity skip persisting state.
        runner.setProperty(THRESHOLD, "1 ms");
        runner.enqueue("Incoming data");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final StateMap updatedState = runner.getStateManager().getState(CLUSTER);
        Assert.assertNull("Latest timestamp should NOT be persisted, because it's running as 'node' scope", updatedState.get(STATE_KEY_LATEST_SUCCESS_TRANSFER));
    }

    @Test
    public void testClusterMonitorActiveWithLatestTimestamp() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(100));
        runner.setClustered(true);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        // This has to be very small threshold, otherwise, MonitorActivity skip persisting state.
        runner.setProperty(THRESHOLD, "1 ms");
        runner.enqueue("Incoming data");
        // Set future timestamp in state
        final HashMap<String, String> existingState = new HashMap<>();
        final long existingTimestamp = (System.currentTimeMillis()) - 1000;
        existingState.put(STATE_KEY_LATEST_SUCCESS_TRANSFER, String.valueOf(existingTimestamp));
        existingState.put("key1", "value1");
        existingState.put("key2", "value2");
        runner.getStateManager().setState(existingState, CLUSTER);
        runner.getStateManager().replace(runner.getStateManager().getState(CLUSTER), existingState, CLUSTER);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final StateMap postProcessedState = runner.getStateManager().getState(CLUSTER);
        Assert.assertTrue("Existing timestamp should be updated", (existingTimestamp < (Long.parseLong(postProcessedState.get(STATE_KEY_LATEST_SUCCESS_TRANSFER)))));
        // State should be updated. Null in this case.
        Assert.assertNull(postProcessedState.get("key1"));
        Assert.assertNull(postProcessedState.get("key2"));
    }

    @Test
    public void testClusterMonitorActiveMoreRecentTimestampExisted() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(100));
        runner.setClustered(true);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        // This has to be very small threshold, otherwise, MonitorActivity skip persisting state.
        runner.setProperty(THRESHOLD, "1 ms");
        runner.enqueue("Incoming data");
        // Set future timestamp in state
        final HashMap<String, String> existingState = new HashMap<>();
        final long existingTimestamp = (System.currentTimeMillis()) + 10000;
        existingState.put(STATE_KEY_LATEST_SUCCESS_TRANSFER, String.valueOf(existingTimestamp));
        existingState.put("key1", "value1");
        existingState.put("key2", "value2");
        runner.getStateManager().setState(existingState, CLUSTER);
        runner.getStateManager().replace(runner.getStateManager().getState(CLUSTER), existingState, CLUSTER);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final StateMap postProcessedState = runner.getStateManager().getState(CLUSTER);
        Assert.assertEquals("Existing timestamp should NOT be updated", String.valueOf(existingTimestamp), postProcessedState.get(STATE_KEY_LATEST_SUCCESS_TRANSFER));
        // State should stay the same.
        Assert.assertEquals(postProcessedState.get("key1"), existingState.get("key1"));
        Assert.assertEquals(postProcessedState.get("key2"), existingState.get("key2"));
    }

    @Test
    public void testClusterMonitorActiveCopyAttribute() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(100));
        runner.setClustered(true);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        // This has to be very small threshold, otherwise, MonitorActivity skip persisting state.
        runner.setProperty(THRESHOLD, "1 ms");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        final HashMap<String, String> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", "value2");
        runner.enqueue("Incoming data", attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final StateMap updatedState = runner.getStateManager().getState(CLUSTER);
        Assert.assertNotNull("Latest timestamp should be persisted", updatedState.get(STATE_KEY_LATEST_SUCCESS_TRANSFER));
        Assert.assertEquals("value1", updatedState.get("key1"));
        Assert.assertEquals("value2", updatedState.get("key2"));
    }

    @Test
    public void testClusterMonitorInactivity() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(10000));
        runner.setClustered(true);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        runner.setProperty(THRESHOLD, "100 ms");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        // Becomes inactive
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_INACTIVE);
        final List<MockFlowFile> inactiveFiles = runner.getFlowFilesForRelationship(REL_INACTIVE);
        Assert.assertEquals(1, inactiveFiles.size());
        final MockFlowFile inactiveFile = inactiveFiles.get(0);
        Assert.assertNotNull(inactiveFile.getAttribute("inactivityStartMillis"));
        Assert.assertNotNull(inactiveFile.getAttribute("inactivityDurationMillis"));
        runner.clearTransferState();
    }

    @Test
    public void testClusterMonitorInactivityFallbackToNodeScope() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(10000));
        runner.setClustered(false);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        runner.setProperty(THRESHOLD, "100 ms");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        // Becomes inactive
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_INACTIVE);
        final List<MockFlowFile> inactiveFiles = runner.getFlowFilesForRelationship(REL_INACTIVE);
        Assert.assertEquals(1, inactiveFiles.size());
        final MockFlowFile inactiveFile = inactiveFiles.get(0);
        Assert.assertNotNull(inactiveFile.getAttribute("inactivityStartMillis"));
        Assert.assertNotNull(inactiveFile.getAttribute("inactivityDurationMillis"));
        runner.clearTransferState();
    }

    @Test
    public void testClusterMonitorInactivityOnPrimaryNode() throws Exception {
        final TestMonitorActivity.TestableProcessor processor = new TestMonitorActivity.TestableProcessor(10000);
        final TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setClustered(true);
        runner.setPrimaryNode(true);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        runner.setProperty(REPORTING_NODE, REPORT_NODE_PRIMARY);
        runner.setProperty(THRESHOLD, "100 ms");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        // Becomes inactive
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_INACTIVE);
        final List<MockFlowFile> inactiveFiles = runner.getFlowFilesForRelationship(REL_INACTIVE);
        Assert.assertEquals(1, inactiveFiles.size());
        final MockFlowFile inactiveFile = inactiveFiles.get(0);
        Assert.assertNotNull(inactiveFile.getAttribute("inactivityStartMillis"));
        Assert.assertNotNull(inactiveFile.getAttribute("inactivityDurationMillis"));
        runner.clearTransferState();
    }

    @Test
    public void testClusterMonitorInactivityOnNode() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(10000));
        runner.setClustered(true);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        runner.setProperty(REPORTING_NODE, REPORT_NODE_PRIMARY);
        runner.setProperty(THRESHOLD, "100 ms");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        // Becomes inactive, but this not shouldn't send flow file
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_INACTIVE, 0);
        runner.assertTransferCount(REL_ACTIVITY_RESTORED, 0);
        runner.clearTransferState();
    }

    @Test
    public void testClusterMonitorActivityRestoredBySelf() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(10000));
        runner.setClustered(true);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        runner.setProperty(THRESHOLD, "100 ms");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        // Becomes inactive
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_INACTIVE);
        runner.clearTransferState();
        // Activity restored
        final HashMap<String, String> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", "value2");
        runner.enqueue("Incoming data", attributes);
        runNext(runner);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        final List<MockFlowFile> activityRestoredFiles = runner.getFlowFilesForRelationship(REL_ACTIVITY_RESTORED);
        Assert.assertEquals(1, successFiles.size());
        Assert.assertEquals(1, activityRestoredFiles.size());
        Assert.assertEquals("value1", activityRestoredFiles.get(0).getAttribute("key1"));
        Assert.assertEquals("value2", activityRestoredFiles.get(0).getAttribute("key2"));
        // Latest activity should be persisted
        final StateMap updatedState = runner.getStateManager().getState(CLUSTER);
        Assert.assertNotNull("Latest timestamp should be persisted", updatedState.get(STATE_KEY_LATEST_SUCCESS_TRANSFER));
        Assert.assertEquals("value1", updatedState.get("key1"));
        Assert.assertEquals("value2", updatedState.get("key2"));
        runner.clearTransferState();
    }

    @Test
    public void testClusterMonitorActivityRestoredBySelfOnNode() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(10000));
        runner.setClustered(true);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        runner.setProperty(REPORTING_NODE, REPORT_NODE_PRIMARY);
        runner.setProperty(THRESHOLD, "100 ms");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        // Becomes inactive
        runner.run();
        // This node won't send notification files
        runner.assertTransferCount(REL_INACTIVE, 0);
        runner.clearTransferState();
        // Activity restored
        final HashMap<String, String> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", "value2");
        runner.enqueue("Incoming data", attributes);
        runNext(runner);
        // This node should not send restored flow file
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        // Latest activity should be persisted
        final StateMap updatedState = runner.getStateManager().getState(CLUSTER);
        Assert.assertNotNull("Latest timestamp should be persisted", updatedState.get(STATE_KEY_LATEST_SUCCESS_TRANSFER));
        Assert.assertEquals("value1", updatedState.get("key1"));
        Assert.assertEquals("value2", updatedState.get("key2"));
        runner.clearTransferState();
    }

    @Test
    public void testClusterMonitorActivityRestoredBySelfOnPrimaryNode() throws Exception {
        final TestMonitorActivity.TestableProcessor processor = new TestMonitorActivity.TestableProcessor(10000);
        final TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setClustered(true);
        runner.setPrimaryNode(true);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        runner.setProperty(REPORTING_NODE, REPORT_NODE_PRIMARY);
        runner.setProperty(THRESHOLD, "100 ms");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        // Becomes inactive
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_INACTIVE);
        runner.clearTransferState();
        // Activity restored
        final HashMap<String, String> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", "value2");
        runner.enqueue("Incoming data", attributes);
        runNext(runner);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        final List<MockFlowFile> activityRestoredFiles = runner.getFlowFilesForRelationship(REL_ACTIVITY_RESTORED);
        Assert.assertEquals(1, successFiles.size());
        Assert.assertEquals(1, activityRestoredFiles.size());
        Assert.assertEquals("value1", activityRestoredFiles.get(0).getAttribute("key1"));
        Assert.assertEquals("value2", activityRestoredFiles.get(0).getAttribute("key2"));
        // Latest activity should be persisted
        final StateMap updatedState = runner.getStateManager().getState(CLUSTER);
        Assert.assertNotNull("Latest timestamp should be persisted", updatedState.get(STATE_KEY_LATEST_SUCCESS_TRANSFER));
        Assert.assertEquals("value1", updatedState.get("key1"));
        Assert.assertEquals("value2", updatedState.get("key2"));
        runner.clearTransferState();
    }

    @Test
    public void testClusterMonitorActivityRestoredBySelfOnPrimaryNodeFallbackToNodeScope() throws Exception {
        final TestMonitorActivity.TestableProcessor processor = new TestMonitorActivity.TestableProcessor(10000);
        final TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setClustered(false);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        runner.setProperty(REPORTING_NODE, REPORT_NODE_PRIMARY);
        runner.setProperty(THRESHOLD, "100 ms");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        // Becomes inactive
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_INACTIVE);
        runner.clearTransferState();
        // Activity restored
        final HashMap<String, String> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", "value2");
        runner.enqueue("Incoming data", attributes);
        runNext(runner);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        final List<MockFlowFile> activityRestoredFiles = runner.getFlowFilesForRelationship(REL_ACTIVITY_RESTORED);
        Assert.assertEquals(1, successFiles.size());
        Assert.assertEquals(1, activityRestoredFiles.size());
        Assert.assertEquals("value1", activityRestoredFiles.get(0).getAttribute("key1"));
        Assert.assertEquals("value2", activityRestoredFiles.get(0).getAttribute("key2"));
        // Latest activity should NOT be persisted
        final StateMap updatedState = runner.getStateManager().getState(CLUSTER);
        Assert.assertNull("Latest timestamp should NOT be persisted", updatedState.get(STATE_KEY_LATEST_SUCCESS_TRANSFER));
        runner.clearTransferState();
    }

    @Test
    public void testClusterMonitorActivityRestoredByOtherNode() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(10000));
        runner.setClustered(true);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        runner.setProperty(THRESHOLD, "100 ms");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        // Becomes inactive
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_INACTIVE);
        runner.clearTransferState();
        // Activity restored, even if this node doesn't have activity, other node updated the cluster state.
        final HashMap<String, String> clusterState = new HashMap<>();
        clusterState.put(STATE_KEY_LATEST_SUCCESS_TRANSFER, String.valueOf(System.currentTimeMillis()));
        clusterState.put("key1", "value1");
        clusterState.put("key2", "value2");
        runner.getStateManager().setState(clusterState, CLUSTER);
        runner.getStateManager().replace(runner.getStateManager().getState(CLUSTER), clusterState, CLUSTER);
        runNext(runner);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        final List<MockFlowFile> activityRestoredFiles = runner.getFlowFilesForRelationship(REL_ACTIVITY_RESTORED);
        Assert.assertEquals("Should be zero since it doesn't have incoming file.", 0, successFiles.size());
        Assert.assertEquals(1, activityRestoredFiles.size());
        Assert.assertEquals("value1", activityRestoredFiles.get(0).getAttribute("key1"));
        Assert.assertEquals("value2", activityRestoredFiles.get(0).getAttribute("key2"));
        runner.clearTransferState();
    }

    @Test
    public void testClusterMonitorActivityRestoredByOtherNodeOnPrimary() throws Exception {
        final TestMonitorActivity.TestableProcessor processor = new TestMonitorActivity.TestableProcessor(10000);
        final TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setClustered(true);
        runner.setPrimaryNode(true);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        runner.setProperty(REPORTING_NODE, REPORT_NODE_PRIMARY);
        runner.setProperty(THRESHOLD, "100 ms");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        // Becomes inactive
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_INACTIVE);
        runner.clearTransferState();
        // Activity restored, even if this node doesn't have activity, other node updated the cluster state.
        final HashMap<String, String> clusterState = new HashMap<>();
        clusterState.put(STATE_KEY_LATEST_SUCCESS_TRANSFER, String.valueOf(System.currentTimeMillis()));
        clusterState.put("key1", "value1");
        clusterState.put("key2", "value2");
        runner.getStateManager().setState(clusterState, CLUSTER);
        runner.getStateManager().replace(runner.getStateManager().getState(CLUSTER), clusterState, CLUSTER);
        runNext(runner);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        final List<MockFlowFile> activityRestoredFiles = runner.getFlowFilesForRelationship(REL_ACTIVITY_RESTORED);
        Assert.assertEquals("Should be zero since it doesn't have incoming file.", 0, successFiles.size());
        Assert.assertEquals(1, activityRestoredFiles.size());
        Assert.assertEquals("value1", activityRestoredFiles.get(0).getAttribute("key1"));
        Assert.assertEquals("value2", activityRestoredFiles.get(0).getAttribute("key2"));
        runner.clearTransferState();
    }

    @Test
    public void testClusterMonitorActivityRestoredByOtherNodeOnNode() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new TestMonitorActivity.TestableProcessor(10000));
        runner.setClustered(true);
        runner.setPrimaryNode(false);
        runner.setProperty(MONITORING_SCOPE, SCOPE_CLUSTER);
        runner.setProperty(REPORTING_NODE, REPORT_NODE_PRIMARY);
        runner.setProperty(THRESHOLD, "100 ms");
        runner.setProperty(COPY_ATTRIBUTES, "true");
        // Becomes inactive
        runner.run();
        runner.assertTransferCount(REL_INACTIVE, 0);
        runner.clearTransferState();
        // Activity restored, even if this node doesn't have activity, other node updated the cluster state.
        final HashMap<String, String> clusterState = new HashMap<>();
        clusterState.put(STATE_KEY_LATEST_SUCCESS_TRANSFER, String.valueOf(System.currentTimeMillis()));
        clusterState.put("key1", "value1");
        clusterState.put("key2", "value2");
        runner.getStateManager().setState(clusterState, CLUSTER);
        runner.getStateManager().replace(runner.getStateManager().getState(CLUSTER), clusterState, CLUSTER);
        runNext(runner);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_INACTIVE, 0);
        runner.assertTransferCount(REL_ACTIVITY_RESTORED, 0);
        runner.clearTransferState();
    }
}

