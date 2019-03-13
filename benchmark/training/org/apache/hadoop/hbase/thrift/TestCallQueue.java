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
package org.apache.hadoop.hbase.thrift;


import java.util.concurrent.LinkedBlockingQueue;
import org.apache.hadoop.hbase.CompatibilitySingletonFactory;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.test.MetricsAssertHelper;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit testing for CallQueue, a part of the
 * org.apache.hadoop.hbase.thrift package.
 */
@Category({ ClientTests.class, SmallTests.class })
@RunWith(Parameterized.class)
public class TestCallQueue {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCallQueue.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCallQueue.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final MetricsAssertHelper metricsHelper = CompatibilitySingletonFactory.getInstance(MetricsAssertHelper.class);

    private int elementsAdded;

    private int elementsRemoved;

    public TestCallQueue(int elementsAdded, int elementsRemoved) {
        this.elementsAdded = elementsAdded;
        this.elementsRemoved = elementsRemoved;
        TestCallQueue.LOG.debug(((("elementsAdded:" + elementsAdded) + " elementsRemoved:") + elementsRemoved));
    }

    @Test
    public void testPutTake() throws Exception {
        ThriftMetrics metrics = TestCallQueue.createMetrics();
        CallQueue callQueue = new CallQueue(new LinkedBlockingQueue(), metrics);
        for (int i = 0; i < (elementsAdded); ++i) {
            callQueue.put(TestCallQueue.createDummyRunnable());
        }
        for (int i = 0; i < (elementsRemoved); ++i) {
            callQueue.take();
        }
        TestCallQueue.verifyMetrics(metrics, "timeInQueue_num_ops", elementsRemoved);
    }

    @Test
    public void testOfferPoll() throws Exception {
        ThriftMetrics metrics = TestCallQueue.createMetrics();
        CallQueue callQueue = new CallQueue(new LinkedBlockingQueue(), metrics);
        for (int i = 0; i < (elementsAdded); ++i) {
            callQueue.offer(TestCallQueue.createDummyRunnable());
        }
        for (int i = 0; i < (elementsRemoved); ++i) {
            callQueue.poll();
        }
        TestCallQueue.verifyMetrics(metrics, "timeInQueue_num_ops", elementsRemoved);
    }
}

