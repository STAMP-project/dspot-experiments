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
package org.apache.hadoop.hbase.regionserver;


import CellBuilderType.SHALLOW_COPY;
import HConstants.LATEST_TIMESTAMP;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.hbase.CellBuilderFactory;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Confirm that the function of FlushLifeCycleTracker is OK as we do not use it in our own code.
 */
@Category({ CoprocessorTests.class, MediumTests.class })
public class TestFlushLifeCycleTracker {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFlushLifeCycleTracker.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final TableName NAME = TableName.valueOf(TestFlushLifeCycleTracker.class.getSimpleName());

    private static final byte[] CF = Bytes.toBytes("CF");

    private static final byte[] QUALIFIER = Bytes.toBytes("CQ");

    private HRegion region;

    private static FlushLifeCycleTracker TRACKER;

    private static volatile CountDownLatch ARRIVE;

    private static volatile CountDownLatch BLOCK;

    public static final class FlushObserver implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preFlush(ObserverContext<RegionCoprocessorEnvironment> c, FlushLifeCycleTracker tracker) throws IOException {
            if ((TestFlushLifeCycleTracker.TRACKER) != null) {
                Assert.assertSame(tracker, TestFlushLifeCycleTracker.TRACKER);
            }
        }

        @Override
        public InternalScanner preFlush(ObserverContext<RegionCoprocessorEnvironment> c, Store store, InternalScanner scanner, FlushLifeCycleTracker tracker) throws IOException {
            if ((TestFlushLifeCycleTracker.TRACKER) != null) {
                Assert.assertSame(tracker, TestFlushLifeCycleTracker.TRACKER);
            }
            return scanner;
        }

        @Override
        public void postFlush(ObserverContext<RegionCoprocessorEnvironment> c, FlushLifeCycleTracker tracker) throws IOException {
            if ((TestFlushLifeCycleTracker.TRACKER) != null) {
                Assert.assertSame(tracker, TestFlushLifeCycleTracker.TRACKER);
            }
        }

        @Override
        public void postFlush(ObserverContext<RegionCoprocessorEnvironment> c, Store store, StoreFile resultFile, FlushLifeCycleTracker tracker) throws IOException {
            if ((TestFlushLifeCycleTracker.TRACKER) != null) {
                Assert.assertSame(tracker, TestFlushLifeCycleTracker.TRACKER);
            }
            // inject here so we can make a flush request to fail because of we already have a flush
            // ongoing.
            CountDownLatch arrive = TestFlushLifeCycleTracker.ARRIVE;
            if (arrive != null) {
                arrive.countDown();
                try {
                    TestFlushLifeCycleTracker.BLOCK.await();
                } catch (InterruptedException e) {
                    throw new InterruptedIOException();
                }
            }
        }
    }

    private static final class Tracker implements FlushLifeCycleTracker {
        private String reason;

        private boolean beforeExecutionCalled;

        private boolean afterExecutionCalled;

        private boolean completed = false;

        @Override
        public synchronized void notExecuted(String reason) {
            this.reason = reason;
            completed = true;
            notifyAll();
        }

        @Override
        public void beforeExecution() {
            this.beforeExecutionCalled = true;
        }

        @Override
        public synchronized void afterExecution() {
            this.afterExecutionCalled = true;
            completed = true;
            notifyAll();
        }

        public synchronized void await() throws InterruptedException {
            while (!(completed)) {
                wait();
            } 
        }
    }

    @Test
    public void test() throws IOException, InterruptedException {
        try (Table table = TestFlushLifeCycleTracker.UTIL.getConnection().getTable(TestFlushLifeCycleTracker.NAME)) {
            for (int i = 0; i < 100; i++) {
                byte[] row = Bytes.toBytes(i);
                table.put(new Put(row, true).add(CellBuilderFactory.create(SHALLOW_COPY).setRow(row).setFamily(TestFlushLifeCycleTracker.CF).setQualifier(TestFlushLifeCycleTracker.QUALIFIER).setTimestamp(LATEST_TIMESTAMP).setType(Type.Put).setValue(Bytes.toBytes(i)).build()));
            }
        }
        TestFlushLifeCycleTracker.Tracker tracker = new TestFlushLifeCycleTracker.Tracker();
        TestFlushLifeCycleTracker.TRACKER = tracker;
        region.requestFlush(tracker);
        tracker.await();
        Assert.assertNull(tracker.reason);
        Assert.assertTrue(tracker.beforeExecutionCalled);
        Assert.assertTrue(tracker.afterExecutionCalled);
        // request flush on a region with empty memstore should still success
        tracker = new TestFlushLifeCycleTracker.Tracker();
        TestFlushLifeCycleTracker.TRACKER = tracker;
        region.requestFlush(tracker);
        tracker.await();
        Assert.assertNull(tracker.reason);
        Assert.assertTrue(tracker.beforeExecutionCalled);
        Assert.assertTrue(tracker.afterExecutionCalled);
    }

    @Test
    public void testNotExecuted() throws IOException, InterruptedException {
        try (Table table = TestFlushLifeCycleTracker.UTIL.getConnection().getTable(TestFlushLifeCycleTracker.NAME)) {
            for (int i = 0; i < 100; i++) {
                byte[] row = Bytes.toBytes(i);
                table.put(new Put(row, true).add(CellBuilderFactory.create(SHALLOW_COPY).setRow(row).setFamily(TestFlushLifeCycleTracker.CF).setQualifier(TestFlushLifeCycleTracker.QUALIFIER).setTimestamp(LATEST_TIMESTAMP).setType(Type.Put).setValue(Bytes.toBytes(i)).build()));
            }
        }
        // here we may have overlap when calling the CP hooks so we do not assert on TRACKER
        TestFlushLifeCycleTracker.Tracker tracker1 = new TestFlushLifeCycleTracker.Tracker();
        TestFlushLifeCycleTracker.ARRIVE = new CountDownLatch(1);
        TestFlushLifeCycleTracker.BLOCK = new CountDownLatch(1);
        region.requestFlush(tracker1);
        TestFlushLifeCycleTracker.ARRIVE.await();
        TestFlushLifeCycleTracker.Tracker tracker2 = new TestFlushLifeCycleTracker.Tracker();
        region.requestFlush(tracker2);
        tracker2.await();
        Assert.assertNotNull(tracker2.reason);
        Assert.assertFalse(tracker2.beforeExecutionCalled);
        Assert.assertFalse(tracker2.afterExecutionCalled);
        TestFlushLifeCycleTracker.BLOCK.countDown();
        tracker1.await();
        Assert.assertNull(tracker1.reason);
        Assert.assertTrue(tracker1.beforeExecutionCalled);
        Assert.assertTrue(tracker1.afterExecutionCalled);
    }
}

