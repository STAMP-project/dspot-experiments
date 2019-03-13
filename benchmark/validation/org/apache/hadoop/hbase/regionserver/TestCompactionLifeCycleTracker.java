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


import Store.PRIORITY_USER;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot;
import org.apache.hadoop.hbase.quotas.SpaceViolationPolicy;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequest;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Confirm that the function of CompactionLifeCycleTracker is OK as we do not use it in our own
 * code.
 */
@Category({ CoprocessorTests.class, MediumTests.class })
public class TestCompactionLifeCycleTracker {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompactionLifeCycleTracker.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final TableName NAME = TableName.valueOf(TestCompactionLifeCycleTracker.class.getSimpleName());

    private static final byte[] CF1 = Bytes.toBytes("CF1");

    private static final byte[] CF2 = Bytes.toBytes("CF2");

    private static final byte[] QUALIFIER = Bytes.toBytes("CQ");

    private HRegion region;

    private static CompactionLifeCycleTracker TRACKER = null;

    // make sure that we pass the correct CompactionLifeCycleTracker to CP hooks.
    public static final class CompactionObserver implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<? extends StoreFile> candidates, CompactionLifeCycleTracker tracker) throws IOException {
            if ((TestCompactionLifeCycleTracker.TRACKER) != null) {
                Assert.assertSame(tracker, TestCompactionLifeCycleTracker.TRACKER);
            }
        }

        @Override
        public void postCompactSelection(ObserverContext<RegionCoprocessorEnvironment> c, Store store, List<? extends StoreFile> selected, CompactionLifeCycleTracker tracker, CompactionRequest request) {
            if ((TestCompactionLifeCycleTracker.TRACKER) != null) {
                Assert.assertSame(tracker, TestCompactionLifeCycleTracker.TRACKER);
            }
        }

        @Override
        public InternalScanner preCompact(ObserverContext<RegionCoprocessorEnvironment> c, Store store, InternalScanner scanner, ScanType scanType, CompactionLifeCycleTracker tracker, CompactionRequest request) throws IOException {
            if ((TestCompactionLifeCycleTracker.TRACKER) != null) {
                Assert.assertSame(tracker, TestCompactionLifeCycleTracker.TRACKER);
            }
            return scanner;
        }

        @Override
        public void postCompact(ObserverContext<RegionCoprocessorEnvironment> c, Store store, StoreFile resultFile, CompactionLifeCycleTracker tracker, CompactionRequest request) throws IOException {
            if ((TestCompactionLifeCycleTracker.TRACKER) != null) {
                Assert.assertSame(tracker, TestCompactionLifeCycleTracker.TRACKER);
            }
        }
    }

    private static final class Tracker implements CompactionLifeCycleTracker {
        final List<Pair<Store, String>> notExecutedStores = new ArrayList<>();

        final List<Store> beforeExecuteStores = new ArrayList<>();

        final List<Store> afterExecuteStores = new ArrayList<>();

        private boolean completed = false;

        @Override
        public void notExecuted(Store store, String reason) {
            notExecutedStores.add(Pair.newPair(store, reason));
        }

        @Override
        public void beforeExecution(Store store) {
            beforeExecuteStores.add(store);
        }

        @Override
        public void afterExecution(Store store) {
            afterExecuteStores.add(store);
        }

        @Override
        public synchronized void completed() {
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
    public void testRequestOnRegion() throws IOException, InterruptedException {
        TestCompactionLifeCycleTracker.Tracker tracker = new TestCompactionLifeCycleTracker.Tracker();
        TestCompactionLifeCycleTracker.TRACKER = tracker;
        region.requestCompaction("test", PRIORITY_USER, false, tracker);
        tracker.await();
        Assert.assertEquals(1, tracker.notExecutedStores.size());
        Assert.assertEquals(Bytes.toString(TestCompactionLifeCycleTracker.CF2), tracker.notExecutedStores.get(0).getFirst().getColumnFamilyName());
        Assert.assertThat(tracker.notExecutedStores.get(0).getSecond(), CoreMatchers.containsString("compaction request was cancelled"));
        Assert.assertEquals(1, tracker.beforeExecuteStores.size());
        Assert.assertEquals(Bytes.toString(TestCompactionLifeCycleTracker.CF1), tracker.beforeExecuteStores.get(0).getColumnFamilyName());
        Assert.assertEquals(1, tracker.afterExecuteStores.size());
        Assert.assertEquals(Bytes.toString(TestCompactionLifeCycleTracker.CF1), tracker.afterExecuteStores.get(0).getColumnFamilyName());
    }

    @Test
    public void testRequestOnStore() throws IOException, InterruptedException {
        TestCompactionLifeCycleTracker.Tracker tracker = new TestCompactionLifeCycleTracker.Tracker();
        TestCompactionLifeCycleTracker.TRACKER = tracker;
        region.requestCompaction(TestCompactionLifeCycleTracker.CF1, "test", PRIORITY_USER, false, tracker);
        tracker.await();
        Assert.assertTrue(tracker.notExecutedStores.isEmpty());
        Assert.assertEquals(1, tracker.beforeExecuteStores.size());
        Assert.assertEquals(Bytes.toString(TestCompactionLifeCycleTracker.CF1), tracker.beforeExecuteStores.get(0).getColumnFamilyName());
        Assert.assertEquals(1, tracker.afterExecuteStores.size());
        Assert.assertEquals(Bytes.toString(TestCompactionLifeCycleTracker.CF1), tracker.afterExecuteStores.get(0).getColumnFamilyName());
        tracker = new TestCompactionLifeCycleTracker.Tracker();
        TestCompactionLifeCycleTracker.TRACKER = tracker;
        region.requestCompaction(TestCompactionLifeCycleTracker.CF2, "test", PRIORITY_USER, false, tracker);
        tracker.await();
        Assert.assertEquals(1, tracker.notExecutedStores.size());
        Assert.assertEquals(Bytes.toString(TestCompactionLifeCycleTracker.CF2), tracker.notExecutedStores.get(0).getFirst().getColumnFamilyName());
        Assert.assertThat(tracker.notExecutedStores.get(0).getSecond(), CoreMatchers.containsString("compaction request was cancelled"));
        Assert.assertTrue(tracker.beforeExecuteStores.isEmpty());
        Assert.assertTrue(tracker.afterExecuteStores.isEmpty());
    }

    @Test
    public void testSpaceQuotaViolation() throws IOException, InterruptedException {
        region.getRegionServerServices().getRegionServerSpaceQuotaManager().enforceViolationPolicy(TestCompactionLifeCycleTracker.NAME, new SpaceQuotaSnapshot(new org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus(SpaceViolationPolicy.NO_WRITES_COMPACTIONS), 10L, 100L));
        TestCompactionLifeCycleTracker.Tracker tracker = new TestCompactionLifeCycleTracker.Tracker();
        TestCompactionLifeCycleTracker.TRACKER = tracker;
        region.requestCompaction("test", PRIORITY_USER, false, tracker);
        tracker.await();
        Assert.assertEquals(2, tracker.notExecutedStores.size());
        tracker.notExecutedStores.sort(( p1, p2) -> p1.getFirst().getColumnFamilyName().compareTo(p2.getFirst().getColumnFamilyName()));
        Assert.assertEquals(Bytes.toString(TestCompactionLifeCycleTracker.CF1), tracker.notExecutedStores.get(0).getFirst().getColumnFamilyName());
        Assert.assertThat(tracker.notExecutedStores.get(0).getSecond(), CoreMatchers.containsString("space quota violation"));
        Assert.assertEquals(Bytes.toString(TestCompactionLifeCycleTracker.CF2), tracker.notExecutedStores.get(1).getFirst().getColumnFamilyName());
        Assert.assertThat(tracker.notExecutedStores.get(1).getSecond(), CoreMatchers.containsString("space quota violation"));
        Assert.assertTrue(tracker.beforeExecuteStores.isEmpty());
        Assert.assertTrue(tracker.afterExecuteStores.isEmpty());
    }
}

