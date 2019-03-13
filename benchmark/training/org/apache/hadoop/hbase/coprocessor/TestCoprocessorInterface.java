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
package org.apache.hadoop.hbase.coprocessor;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.Coprocessor;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestCase;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.regionserver.FlushLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.regionserver.RegionScanner;
import org.apache.hadoop.hbase.regionserver.ScanType;
import org.apache.hadoop.hbase.regionserver.ScannerContext;
import org.apache.hadoop.hbase.regionserver.Store;
import org.apache.hadoop.hbase.regionserver.StoreFile;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequest;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ CoprocessorTests.class, SmallTests.class })
public class TestCoprocessorInterface {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCoprocessorInterface.class);

    @Rule
    public TestName name = new TestName();

    private static final Logger LOG = LoggerFactory.getLogger(TestCoprocessorInterface.class);

    private static final HBaseTestingUtility TEST_UTIL = HBaseTestingUtility.createLocalHTU();

    static final Path DIR = getDataTestDir();

    private static class CustomScanner implements RegionScanner {
        private RegionScanner delegate;

        public CustomScanner(RegionScanner delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean next(List<Cell> results) throws IOException {
            return delegate.next(results);
        }

        @Override
        public boolean next(List<Cell> result, ScannerContext scannerContext) throws IOException {
            return delegate.next(result, scannerContext);
        }

        @Override
        public boolean nextRaw(List<Cell> result) throws IOException {
            return delegate.nextRaw(result);
        }

        @Override
        public boolean nextRaw(List<Cell> result, ScannerContext context) throws IOException {
            return delegate.nextRaw(result, context);
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }

        @Override
        public RegionInfo getRegionInfo() {
            return delegate.getRegionInfo();
        }

        @Override
        public boolean isFilterDone() throws IOException {
            return delegate.isFilterDone();
        }

        @Override
        public boolean reseek(byte[] row) throws IOException {
            return false;
        }

        @Override
        public long getMaxResultSize() {
            return delegate.getMaxResultSize();
        }

        @Override
        public long getMvccReadPoint() {
            return delegate.getMvccReadPoint();
        }

        @Override
        public int getBatch() {
            return delegate.getBatch();
        }
    }

    public static class CoprocessorImpl implements RegionCoprocessor , RegionObserver {
        private boolean startCalled;

        private boolean stopCalled;

        private boolean preOpenCalled;

        private boolean postOpenCalled;

        private boolean preCloseCalled;

        private boolean postCloseCalled;

        private boolean preCompactCalled;

        private boolean postCompactCalled;

        private boolean preFlushCalled;

        private boolean postFlushCalled;

        private ConcurrentMap<String, Object> sharedData;

        @Override
        public void start(CoprocessorEnvironment e) {
            sharedData = ((RegionCoprocessorEnvironment) (e)).getSharedData();
            // using new String here, so that there will be new object on each invocation
            sharedData.putIfAbsent("test1", new Object());
            startCalled = true;
        }

        @Override
        public void stop(CoprocessorEnvironment e) {
            sharedData = null;
            stopCalled = true;
        }

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preOpen(ObserverContext<RegionCoprocessorEnvironment> e) {
            preOpenCalled = true;
        }

        @Override
        public void postOpen(ObserverContext<RegionCoprocessorEnvironment> e) {
            postOpenCalled = true;
        }

        @Override
        public void preClose(ObserverContext<RegionCoprocessorEnvironment> e, boolean abortRequested) {
            preCloseCalled = true;
        }

        @Override
        public void postClose(ObserverContext<RegionCoprocessorEnvironment> e, boolean abortRequested) {
            postCloseCalled = true;
        }

        @Override
        public InternalScanner preCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, InternalScanner scanner, ScanType scanType, CompactionLifeCycleTracker tracker, CompactionRequest request) {
            preCompactCalled = true;
            return scanner;
        }

        @Override
        public void postCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, StoreFile resultFile, CompactionLifeCycleTracker tracker, CompactionRequest request) {
            postCompactCalled = true;
        }

        @Override
        public void preFlush(ObserverContext<RegionCoprocessorEnvironment> e, FlushLifeCycleTracker tracker) {
            preFlushCalled = true;
        }

        @Override
        public void postFlush(ObserverContext<RegionCoprocessorEnvironment> e, FlushLifeCycleTracker tracker) {
            postFlushCalled = true;
        }

        @Override
        public RegionScanner postScannerOpen(final ObserverContext<RegionCoprocessorEnvironment> e, final Scan scan, final RegionScanner s) throws IOException {
            return new TestCoprocessorInterface.CustomScanner(s);
        }

        boolean wasStarted() {
            return startCalled;
        }

        boolean wasStopped() {
            return stopCalled;
        }

        boolean wasOpened() {
            return (preOpenCalled) && (postOpenCalled);
        }

        boolean wasClosed() {
            return (preCloseCalled) && (postCloseCalled);
        }

        boolean wasFlushed() {
            return (preFlushCalled) && (postFlushCalled);
        }

        boolean wasCompacted() {
            return (preCompactCalled) && (postCompactCalled);
        }

        Map<String, Object> getSharedData() {
            return sharedData;
        }
    }

    public static class CoprocessorII implements RegionCoprocessor {
        private ConcurrentMap<String, Object> sharedData;

        @Override
        public void start(CoprocessorEnvironment e) {
            sharedData = ((RegionCoprocessorEnvironment) (e)).getSharedData();
            sharedData.putIfAbsent("test2", new Object());
        }

        @Override
        public void stop(CoprocessorEnvironment e) {
            sharedData = null;
        }

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(new RegionObserver() {
                @Override
                public void preGetOp(final ObserverContext<RegionCoprocessorEnvironment> e, final Get get, final List<Cell> results) throws IOException {
                    throw new RuntimeException();
                }
            });
        }

        Map<String, Object> getSharedData() {
            return sharedData;
        }
    }

    @Test
    public void testSharedData() throws IOException {
        TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] families = new byte[][]{ HBaseTestingUtility.fam1, HBaseTestingUtility.fam2, HBaseTestingUtility.fam3 };
        Configuration hc = initConfig();
        HRegion region = initHRegion(tableName, name.getMethodName(), hc, new Class<?>[]{  }, families);
        for (int i = 0; i < 3; i++) {
            HBaseTestCase.addContent(region, HBaseTestingUtility.fam3);
            region.flush(true);
        }
        region.compact(false);
        region = reopenRegion(region, TestCoprocessorInterface.CoprocessorImpl.class, TestCoprocessorInterface.CoprocessorII.class);
        Coprocessor c = region.getCoprocessorHost().findCoprocessor(TestCoprocessorInterface.CoprocessorImpl.class);
        Coprocessor c2 = region.getCoprocessorHost().findCoprocessor(TestCoprocessorInterface.CoprocessorII.class);
        Object o = ((TestCoprocessorInterface.CoprocessorImpl) (c)).getSharedData().get("test1");
        Object o2 = ((TestCoprocessorInterface.CoprocessorII) (c2)).getSharedData().get("test2");
        Assert.assertNotNull(o);
        Assert.assertNotNull(o2);
        // to coprocessors get different sharedDatas
        Assert.assertFalse(((((TestCoprocessorInterface.CoprocessorImpl) (c)).getSharedData()) == (((TestCoprocessorInterface.CoprocessorII) (c2)).getSharedData())));
        c = region.getCoprocessorHost().findCoprocessor(TestCoprocessorInterface.CoprocessorImpl.class);
        c2 = region.getCoprocessorHost().findCoprocessor(TestCoprocessorInterface.CoprocessorII.class);
        // make sure that all coprocessor of a class have identical sharedDatas
        Assert.assertTrue(((((TestCoprocessorInterface.CoprocessorImpl) (c)).getSharedData().get("test1")) == o));
        Assert.assertTrue(((((TestCoprocessorInterface.CoprocessorII) (c2)).getSharedData().get("test2")) == o2));
        // now have all Environments fail
        try {
            byte[] r = region.getRegionInfo().getStartKey();
            if ((r == null) || ((r.length) <= 0)) {
                // Its the start row.  Can't ask for null.  Ask for minimal key instead.
                r = new byte[]{ 0 };
            }
            Get g = new Get(r);
            region.get(g);
            Assert.fail();
        } catch (org.apache.hadoop.hbase.DoNotRetryIOException xc) {
        }
        Assert.assertNull(region.getCoprocessorHost().findCoprocessor(TestCoprocessorInterface.CoprocessorII.class));
        c = region.getCoprocessorHost().findCoprocessor(TestCoprocessorInterface.CoprocessorImpl.class);
        Assert.assertTrue(((((TestCoprocessorInterface.CoprocessorImpl) (c)).getSharedData().get("test1")) == o));
        c = c2 = null;
        // perform a GC
        System.gc();
        // reopen the region
        region = reopenRegion(region, TestCoprocessorInterface.CoprocessorImpl.class, TestCoprocessorInterface.CoprocessorII.class);
        c = region.getCoprocessorHost().findCoprocessor(TestCoprocessorInterface.CoprocessorImpl.class);
        // CPimpl is unaffected, still the same reference
        Assert.assertTrue(((((TestCoprocessorInterface.CoprocessorImpl) (c)).getSharedData().get("test1")) == o));
        c2 = region.getCoprocessorHost().findCoprocessor(TestCoprocessorInterface.CoprocessorII.class);
        // new map and object created, hence the reference is different
        // hence the old entry was indeed removed by the GC and new one has been created
        Object o3 = ((TestCoprocessorInterface.CoprocessorII) (c2)).getSharedData().get("test2");
        Assert.assertFalse((o3 == o2));
        HBaseTestingUtility.closeRegionAndWAL(region);
    }

    @Test
    public void testCoprocessorInterface() throws IOException {
        TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] families = new byte[][]{ HBaseTestingUtility.fam1, HBaseTestingUtility.fam2, HBaseTestingUtility.fam3 };
        Configuration hc = initConfig();
        HRegion region = initHRegion(tableName, name.getMethodName(), hc, new Class<?>[]{ TestCoprocessorInterface.CoprocessorImpl.class }, families);
        for (int i = 0; i < 3; i++) {
            HBaseTestCase.addContent(region, HBaseTestingUtility.fam3);
            region.flush(true);
        }
        region.compact(false);
        // HBASE-4197
        Scan s = new Scan();
        RegionScanner scanner = region.getCoprocessorHost().postScannerOpen(s, region.getScanner(s));
        Assert.assertTrue((scanner instanceof TestCoprocessorInterface.CustomScanner));
        // this would throw an exception before HBASE-4197
        scanner.next(new ArrayList());
        HBaseTestingUtility.closeRegionAndWAL(region);
        Coprocessor c = region.getCoprocessorHost().findCoprocessor(TestCoprocessorInterface.CoprocessorImpl.class);
        Assert.assertTrue("Coprocessor not started", ((TestCoprocessorInterface.CoprocessorImpl) (c)).wasStarted());
        Assert.assertTrue("Coprocessor not stopped", ((TestCoprocessorInterface.CoprocessorImpl) (c)).wasStopped());
        Assert.assertTrue(((TestCoprocessorInterface.CoprocessorImpl) (c)).wasOpened());
        Assert.assertTrue(((TestCoprocessorInterface.CoprocessorImpl) (c)).wasClosed());
        Assert.assertTrue(((TestCoprocessorInterface.CoprocessorImpl) (c)).wasFlushed());
        Assert.assertTrue(((TestCoprocessorInterface.CoprocessorImpl) (c)).wasCompacted());
    }
}

