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
package org.apache.nifi.processors.stateful.analysis;


import AttributeRollingWindow.REL_FAILED_SET_STATE;
import AttributeRollingWindow.REL_FAILURE;
import AttributeRollingWindow.REL_SUCCESS;
import AttributeRollingWindow.SUB_WINDOW_LENGTH;
import AttributeRollingWindow.TIME_WINDOW;
import AttributeRollingWindow.VALUE_TO_TRACK;
import Scope.LOCAL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.processor.ProcessSessionFactory;
import org.apache.nifi.state.MockStateManager;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assume;
import org.junit.Test;


public class TestAttributeRollingWindow {
    @Test
    public void testFailureDueToBadAttribute() throws InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(AttributeRollingWindow.class);
        runner.setProperty(VALUE_TO_TRACK, "${value}");
        runner.setProperty(TIME_WINDOW, "3 sec");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("value", "bad");
        runner.enqueue("1".getBytes(), attributes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_FAILURE);
    }

    @Test
    public void testStateFailures() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(AttributeRollingWindow.class);
        MockStateManager mockStateManager = runner.getStateManager();
        final AttributeRollingWindow processor = ((AttributeRollingWindow) (runner.getProcessor()));
        final ProcessSessionFactory processSessionFactory = runner.getProcessSessionFactory();
        runner.setProperty(VALUE_TO_TRACK, "${value}");
        runner.setProperty(TIME_WINDOW, "3 sec");
        processor.onScheduled(runner.getProcessContext());
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("value", "1");
        mockStateManager.setFailOnStateGet(LOCAL, true);
        runner.enqueue(new byte[0], attributes);
        processor.onTrigger(runner.getProcessContext(), processSessionFactory.createSession());
        runner.assertQueueNotEmpty();
        mockStateManager.setFailOnStateGet(LOCAL, false);
        mockStateManager.setFailOnStateSet(LOCAL, true);
        processor.onTrigger(runner.getProcessContext(), processSessionFactory.createSession());
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FAILED_SET_STATE, 1);
        MockFlowFile mockFlowFile = runner.getFlowFilesForRelationship(AttributeRollingWindow.REL_FAILED_SET_STATE).get(0);
        mockFlowFile.assertAttributeNotExists(AttributeRollingWindow.ROLLING_WINDOW_VALUE_KEY);
        mockFlowFile.assertAttributeNotExists(AttributeRollingWindow.ROLLING_WINDOW_COUNT_KEY);
        mockFlowFile.assertAttributeNotExists(AttributeRollingWindow.ROLLING_WINDOW_MEAN_KEY);
    }

    @Test
    public void testBasic() throws InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(AttributeRollingWindow.class);
        runner.setProperty(VALUE_TO_TRACK, "${value}");
        runner.setProperty(TIME_WINDOW, "300 ms");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("value", "1");
        runner.enqueue("1".getBytes(), attributes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        runner.clearTransferState();
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_VALUE_KEY, "1.0");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_COUNT_KEY, "1");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_MEAN_KEY, "1.0");
        runner.enqueue("2".getBytes(), attributes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        runner.clearTransferState();
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_VALUE_KEY, "2.0");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_COUNT_KEY, "2");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_MEAN_KEY, "1.0");
        Thread.sleep(500L);
        runner.enqueue("2".getBytes(), attributes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        runner.clearTransferState();
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_VALUE_KEY, "1.0");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_COUNT_KEY, "1");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_MEAN_KEY, "1.0");
    }

    @Test
    public void testVerifyCount() throws InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final TestRunner runner = TestRunners.newTestRunner(AttributeRollingWindow.class);
        runner.setProperty(VALUE_TO_TRACK, "${value}");
        runner.setProperty(TIME_WINDOW, "10 sec");
        MockFlowFile flowFile;
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("value", "1");
        for (int i = 1; i < 61; i++) {
            runner.enqueue(String.valueOf(i).getBytes(), attributes);
            runner.run();
            flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
            runner.clearTransferState();
            Double value = ((double) (i));
            Double mean = value / i;
            flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_VALUE_KEY, String.valueOf(value));
            flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_COUNT_KEY, String.valueOf(i));
            flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_MEAN_KEY, String.valueOf(mean));
            Thread.sleep(10L);
        }
        runner.setProperty(VALUE_TO_TRACK, "${value}");
        runner.setProperty(SUB_WINDOW_LENGTH, "500 ms");
        runner.setProperty(TIME_WINDOW, "10 sec");
        for (int i = 1; i < 10; i++) {
            runner.enqueue(String.valueOf(i).getBytes(), attributes);
            runner.run();
            flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
            runner.clearTransferState();
            Double value = ((double) (i));
            Double mean = value / i;
            flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_VALUE_KEY, String.valueOf(Double.valueOf(i)));
            flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_COUNT_KEY, String.valueOf(i));
            flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_MEAN_KEY, String.valueOf(mean));
            Thread.sleep(10L);
        }
    }

    @Test
    public void testMicroBatching() throws InterruptedException {
        Assume.assumeFalse(isWindowsEnvironment());
        final TestRunner runner = TestRunners.newTestRunner(AttributeRollingWindow.class);
        runner.setProperty(VALUE_TO_TRACK, "${value}");
        runner.setProperty(SUB_WINDOW_LENGTH, "500 ms");
        runner.setProperty(TIME_WINDOW, "1 sec");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("value", "2");
        runner.enqueue("1".getBytes(), attributes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        runner.clearTransferState();
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_VALUE_KEY, "2.0");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_COUNT_KEY, "1");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_MEAN_KEY, "2.0");
        Thread.sleep(200L);
        runner.enqueue("2".getBytes(), attributes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        runner.clearTransferState();
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_VALUE_KEY, "4.0");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_COUNT_KEY, "2");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_MEAN_KEY, "2.0");
        Thread.sleep(300L);
        runner.enqueue("2".getBytes(), attributes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        runner.clearTransferState();
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_VALUE_KEY, "6.0");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_COUNT_KEY, "3");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_MEAN_KEY, "2.0");
        Thread.sleep(200L);
        runner.enqueue("2".getBytes(), attributes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        runner.clearTransferState();
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_VALUE_KEY, "8.0");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_COUNT_KEY, "4");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_MEAN_KEY, "2.0");
        Thread.sleep(300L);
        runner.enqueue("2".getBytes(), attributes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        runner.clearTransferState();
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_VALUE_KEY, "6.0");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_COUNT_KEY, "3");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_MEAN_KEY, "2.0");
        runner.enqueue("2".getBytes(), attributes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        runner.clearTransferState();
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_VALUE_KEY, "8.0");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_COUNT_KEY, "4");
        flowFile.assertAttributeEquals(AttributeRollingWindow.ROLLING_WINDOW_MEAN_KEY, "2.0");
    }
}

