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


import EnforceOrder.ATTR_DETAIL;
import EnforceOrder.ATTR_EXPECTED_ORDER;
import EnforceOrder.ATTR_RESULT;
import EnforceOrder.ATTR_STARTED_AT;
import EnforceOrder.GROUP_IDENTIFIER;
import EnforceOrder.INACTIVE_TIMEOUT;
import EnforceOrder.INITIAL_ORDER;
import EnforceOrder.MAX_ORDER;
import EnforceOrder.ORDER_ATTRIBUTE;
import EnforceOrder.REL_FAILURE;
import EnforceOrder.REL_OVERTOOK;
import EnforceOrder.REL_SKIPPED;
import EnforceOrder.REL_SUCCESS;
import EnforceOrder.REL_WAIT;
import EnforceOrder.WAIT_TIMEOUT;
import Scope.LOCAL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.prioritizer.FirstInFirstOutPrioritizer;
import org.apache.nifi.state.MockStateManager;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestEnforceOrder {
    @Test
    public void testDefaultPropertyValidation() {
        final TestRunner runner = TestRunners.newTestRunner(EnforceOrder.class);
        // Default values should not be valid.
        runner.assertNotValid();
        // Set required properties.
        runner.setProperty(ORDER_ATTRIBUTE, "index");
        runner.assertValid();
    }

    @Test
    public void testCustomPropertyValidation() {
        final TestRunner runner = TestRunners.newTestRunner(EnforceOrder.class);
        // Set required properties.
        runner.setProperty(ORDER_ATTRIBUTE, "index");
        runner.assertValid();
        // Inactive Timeout should be longer than Wait Timeout
        runner.setProperty(WAIT_TIMEOUT, "30 sec");
        runner.setProperty(INACTIVE_TIMEOUT, "29 sec");
        runner.assertNotValid();
        // Inactive Timeout should be longer than Wait Timeout
        runner.setProperty(INACTIVE_TIMEOUT, "30 sec");
        runner.assertNotValid();
        // Inactive Timeout should be longer than Wait Timeout
        runner.setProperty(INACTIVE_TIMEOUT, "31 sec");
        runner.assertValid();
    }

    private static class Ordered {
        private final Map<String, String> map = new HashMap<>();

        private Ordered(final int index) {
            map.put("index", String.valueOf(index));
        }

        private static TestEnforceOrder.Ordered i(final int index) {
            return new TestEnforceOrder.Ordered(index);
        }

        private static TestEnforceOrder.Ordered i(final String group, final int index) {
            return new TestEnforceOrder.Ordered(index).put("group", group);
        }

        private TestEnforceOrder.Ordered put(final String key, final String value) {
            map.put(key, value);
            return this;
        }

        private Map<String, String> map() {
            return map;
        }

        private static MockFlowFile enqueue(final TestRunner runner, final String group, final int index) {
            return runner.enqueue(((group + ".") + index), TestEnforceOrder.Ordered.i(group, index).map());
        }
    }

    @Test
    public void testSort() {
        final TestRunner runner = TestRunners.newTestRunner(EnforceOrder.class);
        runner.setProperty(GROUP_IDENTIFIER, "${group}");
        runner.setProperty(ORDER_ATTRIBUTE, "index");
        runner.assertValid();
        TestEnforceOrder.Ordered.enqueue(runner, "b", 0);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 1);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 0);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        final List<MockFlowFile> succeeded = runner.getFlowFilesForRelationship(REL_SUCCESS);
        succeeded.sort(new FirstInFirstOutPrioritizer());
        succeeded.get(0).assertContentEquals("a.0");
        succeeded.get(1).assertContentEquals("a.1");
        succeeded.get(2).assertContentEquals("b.0");
    }

    @Test
    public void testDuplicatedOrder() {
        final TestRunner runner = TestRunners.newTestRunner(EnforceOrder.class);
        runner.setProperty(GROUP_IDENTIFIER, "${group}");
        runner.setProperty(ORDER_ATTRIBUTE, "index");
        runner.setProperty(INITIAL_ORDER, "1");
        runner.assertValid();
        TestEnforceOrder.Ordered.enqueue(runner, "b", 1);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 2);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 1);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 2);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 3);
        runner.run();
        final List<MockFlowFile> succeeded = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(4, succeeded.size());
        succeeded.sort(new FirstInFirstOutPrioritizer());
        succeeded.get(0).assertContentEquals("a.1");
        succeeded.get(1).assertContentEquals("a.2");
        succeeded.get(2).assertContentEquals("a.3");
        succeeded.get(3).assertContentEquals("b.1");
        // It's not possible to distinguish skipped and duplicated, since we only tracks target order number.
        final List<MockFlowFile> skipped = runner.getFlowFilesForRelationship(REL_SKIPPED);
        Assert.assertEquals(1, skipped.size());
        skipped.get(0).assertContentEquals("a.2");
        skipped.get(0).assertAttributeEquals(ATTR_EXPECTED_ORDER, "3");
    }

    @Test
    public void testNoGroupIdentifier() {
        final TestRunner runner = TestRunners.newTestRunner(EnforceOrder.class);
        runner.setProperty(GROUP_IDENTIFIER, "${group}");
        runner.setProperty(ORDER_ATTRIBUTE, "index");
        runner.setProperty(INITIAL_ORDER, "1");
        runner.assertValid();
        TestEnforceOrder.Ordered.enqueue(runner, "b", 1);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 2);
        runner.enqueue("no group id", TestEnforceOrder.Ordered.i(1).map());// without group attribute

        TestEnforceOrder.Ordered.enqueue(runner, "a", 1);
        runner.run();
        final List<MockFlowFile> succeeded = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(3, succeeded.size());
        succeeded.sort(new FirstInFirstOutPrioritizer());
        succeeded.get(0).assertContentEquals("a.1");
        succeeded.get(1).assertContentEquals("a.2");
        succeeded.get(2).assertContentEquals("b.1");
        final List<MockFlowFile> failed = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, failed.size());
        failed.get(0).assertAttributeExists(ATTR_DETAIL);
    }

    @Test
    public void testIllegalOrderValue() {
        final TestRunner runner = TestRunners.newTestRunner(EnforceOrder.class);
        runner.setProperty(GROUP_IDENTIFIER, "${group}");
        runner.setProperty(ORDER_ATTRIBUTE, "index");
        runner.setProperty(INITIAL_ORDER, "1");
        runner.assertValid();
        TestEnforceOrder.Ordered.enqueue(runner, "b", 1);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 2);
        runner.enqueue("illegal order", TestEnforceOrder.Ordered.i("a", 1).put("index", "non-integer").map());
        TestEnforceOrder.Ordered.enqueue(runner, "a", 1);
        runner.run();
        final List<MockFlowFile> succeeded = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(3, succeeded.size());
        succeeded.sort(new FirstInFirstOutPrioritizer());
        succeeded.get(0).assertContentEquals("a.1");
        succeeded.get(1).assertContentEquals("a.2");
        succeeded.get(2).assertContentEquals("b.1");
        final List<MockFlowFile> failed = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, failed.size());
        failed.get(0).assertAttributeExists(ATTR_DETAIL);
        failed.get(0).assertContentEquals("illegal order");
    }

    @Test
    public void testInitialOrderValue() {
        final TestRunner runner = TestRunners.newTestRunner(EnforceOrder.class);
        runner.setProperty(GROUP_IDENTIFIER, "${group}");
        runner.setProperty(ORDER_ATTRIBUTE, "index");
        runner.setProperty(INITIAL_ORDER, "${index.start}");
        runner.setProperty(MAX_ORDER, "${index.max}");
        runner.assertValid();
        runner.enqueue("b.0", TestEnforceOrder.Ordered.i("b", 0).put("index.start", "0").put("index.max", "99").map());
        runner.enqueue("a.100", TestEnforceOrder.Ordered.i("a", 100).put("index.start", "100").put("index.max", "103").map());
        runner.enqueue("a.101", TestEnforceOrder.Ordered.i("a", 101).put("index.start", "100").put("index.max", "103").map());
        runner.enqueue("illegal initial order", TestEnforceOrder.Ordered.i("c", 1).put("index.start", "non-integer").map());
        runner.enqueue("without initial order", TestEnforceOrder.Ordered.i("d", 1).map());
        // Even if this flow file doesn't have initial order attribute, this will be routed to success.
        // Because target order for group b is already computed from b.0.
        TestEnforceOrder.Ordered.enqueue(runner, "b", 1);
        runner.run();
        List<MockFlowFile> succeeded = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(4, succeeded.size());
        succeeded.sort(new FirstInFirstOutPrioritizer());
        succeeded.get(0).assertContentEquals("a.100");
        succeeded.get(1).assertContentEquals("a.101");
        succeeded.get(2).assertContentEquals("b.0");
        succeeded.get(3).assertContentEquals("b.1");
        final List<MockFlowFile> failed = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(2, failed.size());
        failed.get(0).assertAttributeExists(ATTR_DETAIL);
        failed.get(0).assertContentEquals("illegal initial order");
        failed.get(1).assertAttributeExists(ATTR_DETAIL);
        failed.get(1).assertContentEquals("without initial order");
        final MockStateManager stateManager = runner.getStateManager();
        stateManager.assertStateEquals("a.target", "102", LOCAL);
        stateManager.assertStateEquals("a.max", "103", LOCAL);
        stateManager.assertStateEquals("b.target", "2", LOCAL);
        stateManager.assertStateEquals("b.max", "99", LOCAL);
        runner.clearTransferState();
    }

    @Test
    public void testMaxOrder() {
        final TestRunner runner = TestRunners.newTestRunner(EnforceOrder.class);
        runner.setProperty(GROUP_IDENTIFIER, "${fragment.identifier}");
        runner.setProperty(ORDER_ATTRIBUTE, "index");
        runner.setProperty(INITIAL_ORDER, "1");
        runner.setProperty(MAX_ORDER, "${fragment.count}");
        runner.assertValid();
        runner.enqueue("b.1", TestEnforceOrder.Ordered.i(1).put("fragment.identifier", "b").put("fragment.count", "3").map());
        runner.enqueue("a.2", TestEnforceOrder.Ordered.i(2).put("fragment.identifier", "a").put("fragment.count", "2").map());
        runner.enqueue("without max order", TestEnforceOrder.Ordered.i(1).put("fragment.identifier", "c").map());
        runner.enqueue("illegal max order", TestEnforceOrder.Ordered.i(1).put("fragment.identifier", "d").put("fragment.count", "X").map());
        runner.enqueue("a.1", TestEnforceOrder.Ordered.i(1).put("fragment.identifier", "a").put("fragment.count", "2").map());
        runner.enqueue("a.3", TestEnforceOrder.Ordered.i(3).put("fragment.identifier", "a").put("fragment.count", "2").map());// Exceed max

        runner.run();
        final List<MockFlowFile> succeeded = runner.getFlowFilesForRelationship(REL_SUCCESS);
        succeeded.sort(new FirstInFirstOutPrioritizer());
        Assert.assertEquals(3, succeeded.size());
        succeeded.get(0).assertContentEquals("a.1");
        succeeded.get(1).assertContentEquals("a.2");
        succeeded.get(2).assertContentEquals("b.1");
        final List<MockFlowFile> failed = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(3, failed.size());
        failed.get(0).assertContentEquals("without max order");
        failed.get(1).assertContentEquals("illegal max order");
        failed.get(2).assertContentEquals("a.3");// exceeds max order

        final MockStateManager stateManager = runner.getStateManager();
        stateManager.assertStateEquals("a.target", "2", LOCAL);
        stateManager.assertStateEquals("a.max", "2", LOCAL);
    }

    @Test
    public void testWaitOvertakeSkip() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(EnforceOrder.class);
        runner.setProperty(GROUP_IDENTIFIER, "${group}");
        runner.setProperty(ORDER_ATTRIBUTE, "index");
        runner.setProperty(INITIAL_ORDER, "1");
        runner.setProperty(MAX_ORDER, "10");
        runner.assertValid();
        TestEnforceOrder.Ordered.enqueue(runner, "b", 1);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 2);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 1);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 5);// waits for a.3 and a.4

        TestEnforceOrder.Ordered.enqueue(runner, "b", 3);// waits for b.2

        TestEnforceOrder.Ordered.enqueue(runner, "c", 9);// waits for c.1 to 8

        TestEnforceOrder.Ordered.enqueue(runner, "d", 10);// waits for d.1 to 9

        runner.run();
        List<MockFlowFile> succeeded = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(3, succeeded.size());
        final FirstInFirstOutPrioritizer fifo = new FirstInFirstOutPrioritizer();
        succeeded.sort(fifo);
        succeeded.get(0).assertContentEquals("a.1");
        succeeded.get(1).assertContentEquals("a.2");
        succeeded.get(2).assertContentEquals("b.1");
        List<MockFlowFile> waiting = runner.getFlowFilesForRelationship(REL_WAIT);
        Assert.assertEquals(4, waiting.size());
        waiting.get(0).assertContentEquals("a.5");
        waiting.get(1).assertContentEquals("b.3");
        waiting.get(2).assertContentEquals("c.9");
        waiting.get(3).assertContentEquals("d.10");
        waiting.get(0).assertAttributeExists("EnforceOrder.startedAt");
        waiting.get(1).assertAttributeExists("EnforceOrder.startedAt");
        waiting.get(2).assertAttributeExists("EnforceOrder.startedAt");
        waiting.get(3).assertAttributeExists("EnforceOrder.startedAt");
        final MockStateManager stateManager = runner.getStateManager();
        stateManager.assertStateEquals("a.target", "3", LOCAL);
        stateManager.assertStateEquals("b.target", "2", LOCAL);
        stateManager.assertStateEquals("c.target", "1", LOCAL);
        stateManager.assertStateEquals("d.target", "1", LOCAL);
        stateManager.assertStateSet("a.updatedAt", LOCAL);
        stateManager.assertStateSet("b.updatedAt", LOCAL);
        stateManager.assertStateSet("c.updatedAt", LOCAL);
        stateManager.assertStateSet("d.updatedAt", LOCAL);
        // Run it again with waiting files.
        runner.clearTransferState();
        waiting.forEach(runner::enqueue);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_WAIT, 4);
        waiting = runner.getFlowFilesForRelationship(REL_WAIT);
        // Run it again with shorter wait timeout to make overtaking happen.
        runner.clearTransferState();
        runner.setProperty(WAIT_TIMEOUT, "10 ms");
        Thread.sleep(20);
        waiting.forEach(runner::enqueue);
        TestEnforceOrder.Ordered.enqueue(runner, "b", 2);// arrived in time

        TestEnforceOrder.Ordered.enqueue(runner, "a", 6);// a.4 and a.5 have not arrived yet

        runner.run();
        succeeded = runner.getFlowFilesForRelationship(REL_SUCCESS);
        succeeded.sort(fifo);
        Assert.assertEquals(3, succeeded.size());
        succeeded.get(0).assertContentEquals("a.6");// This is ok because a.5 was there.

        succeeded.get(1).assertContentEquals("b.2");
        succeeded.get(2).assertContentEquals("b.3");
        List<MockFlowFile> overtook = runner.getFlowFilesForRelationship(REL_OVERTOOK);
        Assert.assertEquals(3, overtook.size());
        overtook.get(0).assertContentEquals("a.5");// overtook a.3.

        overtook.get(0).assertAttributeEquals(ATTR_EXPECTED_ORDER, "3");
        overtook.get(1).assertContentEquals("c.9");// overtook c.1 - 8.

        overtook.get(1).assertAttributeEquals(ATTR_EXPECTED_ORDER, "1");
        overtook.get(2).assertContentEquals("d.10");// overtook d.1 - 9.

        overtook.get(2).assertAttributeEquals(ATTR_EXPECTED_ORDER, "1");
        stateManager.assertStateEquals("a.target", "7", LOCAL);
        stateManager.assertStateEquals("b.target", "4", LOCAL);
        stateManager.assertStateEquals("c.target", "10", LOCAL);// it was c.9, so +1

        stateManager.assertStateEquals("d.target", "10", LOCAL);// it was d.10 (max) so don't +1

        // Simulate a.3 and a.4 arrive but too late..
        runner.clearTransferState();
        TestEnforceOrder.Ordered.enqueue(runner, "a", 3);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 4);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SKIPPED, 2);
        final List<MockFlowFile> skipped = runner.getFlowFilesForRelationship(REL_SKIPPED);
        skipped.get(0).assertContentEquals("a.3");
        skipped.get(0).assertAttributeExists(ATTR_DETAIL);
        skipped.get(1).assertContentEquals("a.4");
        skipped.get(1).assertAttributeExists(ATTR_DETAIL);
    }

    @Test
    public void testCleanInactiveGroups() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(EnforceOrder.class);
        runner.setProperty(GROUP_IDENTIFIER, "${group}");
        runner.setProperty(ORDER_ATTRIBUTE, "index");
        runner.setProperty(INITIAL_ORDER, "1");
        runner.assertValid();
        TestEnforceOrder.Ordered.enqueue(runner, "b", 1);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 2);
        TestEnforceOrder.Ordered.enqueue(runner, "c", 1);
        TestEnforceOrder.Ordered.enqueue(runner, "a", 1);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 4);
        // Run it again with shorter inactive timeout
        runner.clearTransferState();
        runner.setProperty(WAIT_TIMEOUT, "5 ms");
        runner.setProperty(INACTIVE_TIMEOUT, "10 ms");
        Thread.sleep(15);
        // No group b.
        TestEnforceOrder.Ordered.enqueue(runner, "a", 3);
        TestEnforceOrder.Ordered.enqueue(runner, "c", 2);
        runner.run();
        // Group b was determined as inactive, thus its states should be removed.
        final MockStateManager stateManager = runner.getStateManager();
        stateManager.assertStateEquals("a.target", "4", LOCAL);
        stateManager.assertStateNotSet("b.target", LOCAL);
        stateManager.assertStateEquals("c.target", "3", LOCAL);
        stateManager.assertStateSet("a.updatedAt", LOCAL);
        stateManager.assertStateNotSet("b.updatedAt", LOCAL);
        stateManager.assertStateSet("c.updatedAt", LOCAL);
        // If b comes again, it'll be treated as brand new group.
        runner.clearTransferState();
        TestEnforceOrder.Ordered.enqueue(runner, "b", 2);
        runner.run();
        stateManager.assertStateEquals("b.target", "1", LOCAL);
        stateManager.assertStateSet("b.updatedAt", LOCAL);
        // b.2 should be routed to wait, since there's no b.1. It will eventually overtake.
        runner.assertAllFlowFilesTransferred(REL_WAIT, 1);
        final List<MockFlowFile> waiting = runner.getFlowFilesForRelationship(REL_WAIT);
        waiting.get(0).assertContentEquals("b.2");
    }

    @Test
    public void testClearOldProperties() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(EnforceOrder.class);
        runner.setProperty(GROUP_IDENTIFIER, "${group}");
        runner.setProperty(ORDER_ATTRIBUTE, "index");
        runner.setProperty(INITIAL_ORDER, "1");
        runner.assertValid();
        TestEnforceOrder.Ordered.enqueue(runner, "a", 2);
        TestEnforceOrder.Ordered.enqueue(runner, "b", 1);
        runner.run();
        runner.assertTransferCount(REL_WAIT, 1);
        MockFlowFile a2 = runner.getFlowFilesForRelationship(REL_WAIT).get(0);
        a2.assertAttributeEquals(ATTR_RESULT, "wait");
        a2.assertAttributeExists(ATTR_STARTED_AT);
        a2.assertAttributeNotExists(ATTR_DETAIL);
        a2.assertAttributeEquals(ATTR_EXPECTED_ORDER, "1");
        a2.assertContentEquals("a.2");
        runner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile b1 = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        b1.assertAttributeEquals(ATTR_RESULT, "success");
        b1.assertAttributeExists(ATTR_STARTED_AT);
        b1.assertAttributeNotExists(ATTR_DETAIL);
        b1.assertAttributeNotExists(ATTR_EXPECTED_ORDER);
        b1.assertContentEquals("b.1");
        runner.clearTransferState();
        TestEnforceOrder.Ordered.enqueue(runner, "a", 1);
        runner.enqueue(a2);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        MockFlowFile a1 = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        a1.assertAttributeEquals(ATTR_RESULT, "success");
        a1.assertAttributeExists(ATTR_STARTED_AT);
        a1.assertAttributeNotExists(ATTR_DETAIL);
        a1.assertAttributeNotExists(ATTR_EXPECTED_ORDER);
        a1.assertContentEquals("a.1");
        a2 = runner.getFlowFilesForRelationship(REL_SUCCESS).get(1);
        a2.assertAttributeEquals(ATTR_RESULT, "success");
        a2.assertAttributeExists(ATTR_STARTED_AT);
        a2.assertAttributeNotExists(ATTR_DETAIL);
        a2.assertAttributeNotExists(ATTR_EXPECTED_ORDER);// Should be cleared.

        a2.assertContentEquals("a.2");
    }
}

