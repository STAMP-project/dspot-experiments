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


import Cell.Type.Put;
import CellBuilderType.DEEP_COPY;
import CellComparatorImpl.COMPARATOR;
import CompactingMemStore.IN_MEMORY_FLUSH_THRESHOLD_FACTOR_KEY;
import Compression.Algorithm.GZ;
import DataBlockEncoding.DIFF;
import Filter.ReturnCode;
import FlushLifeCycleTracker.DUMMY;
import HConstants.HREGION_MEMSTORE_FLUSH_SIZE;
import HFile.Reader;
import HStore.MEMSTORE_CLASS_NAME;
import MemStoreCompactionStrategy.COMPACTING_MEMSTORE_THRESHOLD_KEY;
import MemoryCompactionPolicy.BASIC;
import MutableSegment.DEEP_OVERHEAD;
import NoLimitThroughputController.INSTANCE;
import StoreEngine.STORE_ENGINE_CLASS_KEY;
import StoreScanner.STORESCANNER_PREAD_MAX_BYTES;
import TableDescriptorBuilder.DEFAULT_MEMSTORE_FLUSH_SIZE;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FilterFileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellBuilderFactory;
import org.apache.hadoop.hbase.CellComparator;
import org.apache.hadoop.hbase.CellComparatorImpl;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MemoryCompactionPolicy;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.exceptions.IllegalArgumentIOException;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.io.hfile.HFile;
import org.apache.hadoop.hbase.io.hfile.HFileContext;
import org.apache.hadoop.hbase.io.hfile.HFileContextBuilder;
import org.apache.hadoop.hbase.monitoring.MonitoredTask;
import org.apache.hadoop.hbase.quotas.RegionSizeStoreImpl;
import org.apache.hadoop.hbase.regionserver.compactions.DefaultCompactor;
import org.apache.hadoop.hbase.regionserver.querymatcher.ScanQueryMatcher;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.ManualEnvironmentEdge;
import org.apache.hadoop.util.Progressable;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CSLMImmutableSegment.DEEP_OVERHEAD_CSLM;
import static MutableSegment.DEEP_OVERHEAD;
import static ReturnCode.INCLUDE;
import static ReturnCode.NEXT_ROW;


/**
 * Test class for the HStore
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestHStore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHStore.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHStore.class);

    @Rule
    public TestName name = new TestName();

    HRegion region;

    HStore store;

    byte[] table = Bytes.toBytes("table");

    byte[] family = Bytes.toBytes("family");

    byte[] row = Bytes.toBytes("row");

    byte[] row2 = Bytes.toBytes("row2");

    byte[] qf1 = Bytes.toBytes("qf1");

    byte[] qf2 = Bytes.toBytes("qf2");

    byte[] qf3 = Bytes.toBytes("qf3");

    byte[] qf4 = Bytes.toBytes("qf4");

    byte[] qf5 = Bytes.toBytes("qf5");

    byte[] qf6 = Bytes.toBytes("qf6");

    NavigableSet<byte[]> qualifiers = new java.util.concurrent.ConcurrentSkipListSet(Bytes.BYTES_COMPARATOR);

    List<Cell> expected = new ArrayList<>();

    List<Cell> result = new ArrayList<>();

    long id = System.currentTimeMillis();

    Get get = new Get(row);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final String DIR = getDataTestDir("TestStore").toString();

    /**
     * Test we do not lose data if we fail a flush and then close.
     * Part of HBase-10466
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFlushSizeSizing() throws Exception {
        TestHStore.LOG.info(("Setting up a faulty file system that cannot write in " + (this.name.getMethodName())));
        final Configuration conf = HBaseConfiguration.create(TestHStore.TEST_UTIL.getConfiguration());
        // Only retry once.
        conf.setInt("hbase.hstore.flush.retries.number", 1);
        User user = User.createUserForTesting(conf, this.name.getMethodName(), new String[]{ "foo" });
        // Inject our faulty LocalFileSystem
        conf.setClass("fs.file.impl", TestHStore.FaultyFileSystem.class, FileSystem.class);
        user.runAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                // Make sure it worked (above is sensitive to caching details in hadoop core)
                FileSystem fs = FileSystem.get(conf);
                Assert.assertEquals(TestHStore.FaultyFileSystem.class, fs.getClass());
                TestHStore.FaultyFileSystem ffs = ((TestHStore.FaultyFileSystem) (fs));
                // Initialize region
                init(name.getMethodName(), conf);
                MemStoreSize mss = store.memstore.getFlushableSize();
                Assert.assertEquals(0, mss.getDataSize());
                TestHStore.LOG.info("Adding some data");
                MemStoreSizing kvSize = new NonThreadSafeMemStoreSizing();
                store.add(new KeyValue(row, family, qf1, 1, ((byte[]) (null))), kvSize);
                // add the heap size of active (mutable) segment
                kvSize.incMemStoreSize(0, DEEP_OVERHEAD, 0, 0);
                mss = store.memstore.getFlushableSize();
                Assert.assertEquals(kvSize.getMemStoreSize(), mss);
                // Flush.  Bug #1 from HBASE-10466.  Make sure size calculation on failed flush is right.
                try {
                    TestHStore.LOG.info("Flushing");
                    TestHStore.flushStore(store, ((id)++));
                    Assert.fail("Didn't bubble up IOE!");
                } catch (IOException ioe) {
                    Assert.assertTrue(ioe.getMessage().contains("Fault injected"));
                }
                // due to snapshot, change mutable to immutable segment
                kvSize.incMemStoreSize(0, ((DEEP_OVERHEAD_CSLM) - (DEEP_OVERHEAD)), 0, 0);
                mss = store.memstore.getFlushableSize();
                Assert.assertEquals(kvSize.getMemStoreSize(), mss);
                MemStoreSizing kvSize2 = new NonThreadSafeMemStoreSizing();
                store.add(new KeyValue(row, family, qf2, 2, ((byte[]) (null))), kvSize2);
                kvSize2.incMemStoreSize(0, DEEP_OVERHEAD, 0, 0);
                // Even though we add a new kv, we expect the flushable size to be 'same' since we have
                // not yet cleared the snapshot -- the above flush failed.
                Assert.assertEquals(kvSize.getMemStoreSize(), mss);
                ffs.fault.set(false);
                TestHStore.flushStore(store, ((id)++));
                mss = store.memstore.getFlushableSize();
                // Size should be the foreground kv size.
                Assert.assertEquals(kvSize2.getMemStoreSize(), mss);
                TestHStore.flushStore(store, ((id)++));
                mss = store.memstore.getFlushableSize();
                Assert.assertEquals(0, mss.getDataSize());
                Assert.assertEquals(DEEP_OVERHEAD, mss.getHeapSize());
                return null;
            }
        });
    }

    /**
     * Verify that compression and data block encoding are respected by the
     * Store.createWriterInTmp() method, used on store flush.
     */
    @Test
    public void testCreateWriter() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        FileSystem fs = FileSystem.get(conf);
        ColumnFamilyDescriptor hcd = ColumnFamilyDescriptorBuilder.newBuilder(family).setCompressionType(GZ).setDataBlockEncoding(DIFF).build();
        init(name.getMethodName(), conf, hcd);
        // Test createWriterInTmp()
        StoreFileWriter writer = store.createWriterInTmp(4, hcd.getCompressionType(), false, true, false, false);
        Path path = writer.getPath();
        writer.append(new KeyValue(row, family, qf1, Bytes.toBytes(1)));
        writer.append(new KeyValue(row, family, qf2, Bytes.toBytes(2)));
        writer.append(new KeyValue(row2, family, qf1, Bytes.toBytes(3)));
        writer.append(new KeyValue(row2, family, qf2, Bytes.toBytes(4)));
        writer.close();
        // Verify that compression and encoding settings are respected
        HFile.Reader reader = HFile.createReader(fs, path, new org.apache.hadoop.hbase.io.hfile.CacheConfig(conf), true, conf);
        Assert.assertEquals(hcd.getCompressionType(), reader.getCompressionAlgorithm());
        Assert.assertEquals(hcd.getDataBlockEncoding(), reader.getDataBlockEncoding());
        reader.close();
    }

    @Test
    public void testDeleteExpiredStoreFiles() throws Exception {
        testDeleteExpiredStoreFiles(0);
        testDeleteExpiredStoreFiles(1);
    }

    @Test
    public void testLowestModificationTime() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        FileSystem fs = FileSystem.get(conf);
        // Initialize region
        init(name.getMethodName(), conf);
        int storeFileNum = 4;
        for (int i = 1; i <= storeFileNum; i++) {
            TestHStore.LOG.info(("Adding some data for the store file #" + i));
            this.store.add(new KeyValue(row, family, qf1, i, ((byte[]) (null))), null);
            this.store.add(new KeyValue(row, family, qf2, i, ((byte[]) (null))), null);
            this.store.add(new KeyValue(row, family, qf3, i, ((byte[]) (null))), null);
            flush(i);
        }
        // after flush; check the lowest time stamp
        long lowestTimeStampFromManager = StoreUtils.getLowestTimestamp(store.getStorefiles());
        long lowestTimeStampFromFS = TestHStore.getLowestTimeStampFromFS(fs, store.getStorefiles());
        Assert.assertEquals(lowestTimeStampFromManager, lowestTimeStampFromFS);
        // after compact; check the lowest time stamp
        store.compact(store.requestCompaction().get(), INSTANCE, null);
        lowestTimeStampFromManager = StoreUtils.getLowestTimestamp(store.getStorefiles());
        lowestTimeStampFromFS = TestHStore.getLowestTimeStampFromFS(fs, store.getStorefiles());
        Assert.assertEquals(lowestTimeStampFromManager, lowestTimeStampFromFS);
    }

    // ////////////////////////////////////////////////////////////////////////////
    // Get tests
    // ////////////////////////////////////////////////////////////////////////////
    private static final int BLOCKSIZE_SMALL = 8192;

    /**
     * Test for hbase-1686.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testEmptyStoreFile() throws IOException {
        init(this.name.getMethodName());
        // Write a store file.
        this.store.add(new KeyValue(row, family, qf1, 1, ((byte[]) (null))), null);
        this.store.add(new KeyValue(row, family, qf2, 1, ((byte[]) (null))), null);
        flush(1);
        // Now put in place an empty store file.  Its a little tricky.  Have to
        // do manually with hacked in sequence id.
        HStoreFile f = this.store.getStorefiles().iterator().next();
        Path storedir = f.getPath().getParent();
        long seqid = f.getMaxSequenceId();
        Configuration c = HBaseConfiguration.create();
        FileSystem fs = FileSystem.get(c);
        HFileContext meta = new HFileContextBuilder().withBlockSize(TestHStore.BLOCKSIZE_SMALL).build();
        StoreFileWriter w = new StoreFileWriter.Builder(c, new org.apache.hadoop.hbase.io.hfile.CacheConfig(c), fs).withOutputDir(storedir).withFileContext(meta).build();
        w.appendMetadata((seqid + 1), false);
        w.close();
        this.store.close();
        // Reopen it... should pick up two files
        this.store = new HStore(this.store.getHRegion(), this.store.getColumnFamilyDescriptor(), c);
        Assert.assertEquals(2, this.store.getStorefilesCount());
        result = HBaseTestingUtility.getFromStoreFile(store, get.getRow(), qualifiers);
        Assert.assertEquals(1, result.size());
    }

    /**
     * Getting data from memstore only
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testGet_FromMemStoreOnly() throws IOException {
        init(this.name.getMethodName());
        // Put data in memstore
        this.store.add(new KeyValue(row, family, qf1, 1, ((byte[]) (null))), null);
        this.store.add(new KeyValue(row, family, qf2, 1, ((byte[]) (null))), null);
        this.store.add(new KeyValue(row, family, qf3, 1, ((byte[]) (null))), null);
        this.store.add(new KeyValue(row, family, qf4, 1, ((byte[]) (null))), null);
        this.store.add(new KeyValue(row, family, qf5, 1, ((byte[]) (null))), null);
        this.store.add(new KeyValue(row, family, qf6, 1, ((byte[]) (null))), null);
        // Get
        result = HBaseTestingUtility.getFromStoreFile(store, get.getRow(), qualifiers);
        // Compare
        assertCheck();
    }

    @Test
    public void testTimeRangeIfSomeCellsAreDroppedInFlush() throws IOException {
        testTimeRangeIfSomeCellsAreDroppedInFlush(1);
        testTimeRangeIfSomeCellsAreDroppedInFlush(3);
        testTimeRangeIfSomeCellsAreDroppedInFlush(5);
    }

    /**
     * Getting data from files only
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testGet_FromFilesOnly() throws IOException {
        init(this.name.getMethodName());
        // Put data in memstore
        this.store.add(new KeyValue(row, family, qf1, 1, ((byte[]) (null))), null);
        this.store.add(new KeyValue(row, family, qf2, 1, ((byte[]) (null))), null);
        // flush
        flush(1);
        // Add more data
        this.store.add(new KeyValue(row, family, qf3, 1, ((byte[]) (null))), null);
        this.store.add(new KeyValue(row, family, qf4, 1, ((byte[]) (null))), null);
        // flush
        flush(2);
        // Add more data
        this.store.add(new KeyValue(row, family, qf5, 1, ((byte[]) (null))), null);
        this.store.add(new KeyValue(row, family, qf6, 1, ((byte[]) (null))), null);
        // flush
        flush(3);
        // Get
        result = HBaseTestingUtility.getFromStoreFile(store, get.getRow(), qualifiers);
        // this.store.get(get, qualifiers, result);
        // Need to sort the result since multiple files
        Collections.sort(result, COMPARATOR);
        // Compare
        assertCheck();
    }

    /**
     * Getting data from memstore and files
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testGet_FromMemStoreAndFiles() throws IOException {
        init(this.name.getMethodName());
        // Put data in memstore
        this.store.add(new KeyValue(row, family, qf1, 1, ((byte[]) (null))), null);
        this.store.add(new KeyValue(row, family, qf2, 1, ((byte[]) (null))), null);
        // flush
        flush(1);
        // Add more data
        this.store.add(new KeyValue(row, family, qf3, 1, ((byte[]) (null))), null);
        this.store.add(new KeyValue(row, family, qf4, 1, ((byte[]) (null))), null);
        // flush
        flush(2);
        // Add more data
        this.store.add(new KeyValue(row, family, qf5, 1, ((byte[]) (null))), null);
        this.store.add(new KeyValue(row, family, qf6, 1, ((byte[]) (null))), null);
        // Get
        result = HBaseTestingUtility.getFromStoreFile(store, get.getRow(), qualifiers);
        // Need to sort the result since multiple files
        Collections.sort(result, COMPARATOR);
        // Compare
        assertCheck();
    }

    @Test
    public void testHandleErrorsInFlush() throws Exception {
        TestHStore.LOG.info("Setting up a faulty file system that cannot write");
        final Configuration conf = HBaseConfiguration.create(TestHStore.TEST_UTIL.getConfiguration());
        User user = User.createUserForTesting(conf, "testhandleerrorsinflush", new String[]{ "foo" });
        // Inject our faulty LocalFileSystem
        conf.setClass("fs.file.impl", TestHStore.FaultyFileSystem.class, FileSystem.class);
        user.runAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                // Make sure it worked (above is sensitive to caching details in hadoop core)
                FileSystem fs = FileSystem.get(conf);
                Assert.assertEquals(TestHStore.FaultyFileSystem.class, fs.getClass());
                // Initialize region
                init(name.getMethodName(), conf);
                TestHStore.LOG.info("Adding some data");
                store.add(new KeyValue(row, family, qf1, 1, ((byte[]) (null))), null);
                store.add(new KeyValue(row, family, qf2, 1, ((byte[]) (null))), null);
                store.add(new KeyValue(row, family, qf3, 1, ((byte[]) (null))), null);
                TestHStore.LOG.info("Before flush, we should have no files");
                Collection<StoreFileInfo> files = store.getRegionFileSystem().getStoreFiles(store.getColumnFamilyName());
                Assert.assertEquals(0, (files != null ? files.size() : 0));
                // flush
                try {
                    TestHStore.LOG.info("Flushing");
                    flush(1);
                    Assert.fail("Didn't bubble up IOE!");
                } catch (IOException ioe) {
                    Assert.assertTrue(ioe.getMessage().contains("Fault injected"));
                }
                TestHStore.LOG.info("After failed flush, we should still have no files!");
                files = store.getRegionFileSystem().getStoreFiles(store.getColumnFamilyName());
                Assert.assertEquals(0, (files != null ? files.size() : 0));
                store.getHRegion().getWAL().close();
                return null;
            }
        });
        FileSystem.closeAllForUGI(user.getUGI());
    }

    /**
     * Faulty file system that will fail if you write past its fault position the FIRST TIME
     * only; thereafter it will succeed.  Used by {@link TestHRegion} too.
     */
    static class FaultyFileSystem extends FilterFileSystem {
        List<SoftReference<TestHStore.FaultyOutputStream>> outStreams = new ArrayList<>();

        private long faultPos = 200;

        AtomicBoolean fault = new AtomicBoolean(true);

        public FaultyFileSystem() {
            super(new LocalFileSystem());
            System.err.println("Creating faulty!");
        }

        @Override
        public FSDataOutputStream create(Path p) throws IOException {
            return new TestHStore.FaultyOutputStream(super.create(p), faultPos, fault);
        }

        @Override
        public FSDataOutputStream create(Path f, FsPermission permission, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress) throws IOException {
            return new TestHStore.FaultyOutputStream(super.create(f, permission, overwrite, bufferSize, replication, blockSize, progress), faultPos, fault);
        }

        @Override
        public FSDataOutputStream createNonRecursive(Path f, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress) throws IOException {
            // Fake it.  Call create instead.  The default implementation throws an IOE
            // that this is not supported.
            return create(f, overwrite, bufferSize, replication, blockSize, progress);
        }
    }

    static class FaultyOutputStream extends FSDataOutputStream {
        volatile long faultPos = Long.MAX_VALUE;

        private final AtomicBoolean fault;

        public FaultyOutputStream(FSDataOutputStream out, long faultPos, final AtomicBoolean fault) throws IOException {
            super(out, null);
            this.faultPos = faultPos;
            this.fault = fault;
        }

        @Override
        public synchronized void write(byte[] buf, int offset, int length) throws IOException {
            System.err.println(("faulty stream write at pos " + (getPos())));
            injectFault();
            super.write(buf, offset, length);
        }

        private void injectFault() throws IOException {
            if ((this.fault.get()) && ((getPos()) >= (faultPos))) {
                throw new IOException("Fault injected");
            }
        }
    }

    /**
     * Test to ensure correctness when using Stores with multiple timestamps
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testMultipleTimestamps() throws IOException {
        int numRows = 1;
        long[] timestamps1 = new long[]{ 1, 5, 10, 20 };
        long[] timestamps2 = new long[]{ 30, 80 };
        init(this.name.getMethodName());
        List<Cell> kvList1 = getKeyValueSet(timestamps1, numRows, qf1, family);
        for (Cell kv : kvList1) {
            this.store.add(kv, null);
        }
        this.store.snapshot();
        TestHStore.flushStore(store, ((id)++));
        List<Cell> kvList2 = getKeyValueSet(timestamps2, numRows, qf1, family);
        for (Cell kv : kvList2) {
            this.store.add(kv, null);
        }
        List<Cell> result;
        Get get = new Get(Bytes.toBytes(1));
        get.addColumn(family, qf1);
        get.setTimeRange(0, 15);
        result = HBaseTestingUtility.getFromStoreFile(store, get);
        Assert.assertTrue(((result.size()) > 0));
        get.setTimeRange(40, 90);
        result = HBaseTestingUtility.getFromStoreFile(store, get);
        Assert.assertTrue(((result.size()) > 0));
        get.setTimeRange(10, 45);
        result = HBaseTestingUtility.getFromStoreFile(store, get);
        Assert.assertTrue(((result.size()) > 0));
        get.setTimeRange(80, 145);
        result = HBaseTestingUtility.getFromStoreFile(store, get);
        Assert.assertTrue(((result.size()) > 0));
        get.setTimeRange(1, 2);
        result = HBaseTestingUtility.getFromStoreFile(store, get);
        Assert.assertTrue(((result.size()) > 0));
        get.setTimeRange(90, 200);
        result = HBaseTestingUtility.getFromStoreFile(store, get);
        Assert.assertTrue(((result.size()) == 0));
    }

    /**
     * Test for HBASE-3492 - Test split on empty colfam (no store files).
     *
     * @throws IOException
     * 		When the IO operations fail.
     */
    @Test
    public void testSplitWithEmptyColFam() throws IOException {
        init(this.name.getMethodName());
        Assert.assertFalse(store.getSplitPoint().isPresent());
        store.getHRegion().forceSplit(null);
        Assert.assertFalse(store.getSplitPoint().isPresent());
        store.getHRegion().clearSplit();
    }

    @Test
    public void testStoreUsesConfigurationFromHcdAndHtd() throws Exception {
        final String CONFIG_KEY = "hbase.regionserver.thread.compaction.throttle";
        long anyValue = 10;
        // We'll check that it uses correct config and propagates it appropriately by going thru
        // the simplest "real" path I can find - "throttleCompaction", which just checks whether
        // a number we pass in is higher than some config value, inside compactionPolicy.
        Configuration conf = HBaseConfiguration.create();
        conf.setLong(CONFIG_KEY, anyValue);
        init(((name.getMethodName()) + "-xml"), conf);
        Assert.assertTrue(store.throttleCompaction((anyValue + 1)));
        Assert.assertFalse(store.throttleCompaction(anyValue));
        // HTD overrides XML.
        --anyValue;
        init(((name.getMethodName()) + "-htd"), conf, TableDescriptorBuilder.newBuilder(TableName.valueOf(table)).setValue(CONFIG_KEY, Long.toString(anyValue)), ColumnFamilyDescriptorBuilder.of(family));
        Assert.assertTrue(store.throttleCompaction((anyValue + 1)));
        Assert.assertFalse(store.throttleCompaction(anyValue));
        // HCD overrides them both.
        --anyValue;
        init(((name.getMethodName()) + "-hcd"), conf, TableDescriptorBuilder.newBuilder(TableName.valueOf(table)).setValue(CONFIG_KEY, Long.toString(anyValue)), ColumnFamilyDescriptorBuilder.newBuilder(family).setValue(CONFIG_KEY, Long.toString(anyValue)).build());
        Assert.assertTrue(store.throttleCompaction((anyValue + 1)));
        Assert.assertFalse(store.throttleCompaction(anyValue));
    }

    public static class DummyStoreEngine extends DefaultStoreEngine {
        public static DefaultCompactor lastCreatedCompactor = null;

        @Override
        protected void createComponents(Configuration conf, HStore store, CellComparator comparator) throws IOException {
            super.createComponents(conf, store, comparator);
            TestHStore.DummyStoreEngine.lastCreatedCompactor = this.compactor;
        }
    }

    @Test
    public void testStoreUsesSearchEngineOverride() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set(STORE_ENGINE_CLASS_KEY, TestHStore.DummyStoreEngine.class.getName());
        init(this.name.getMethodName(), conf);
        Assert.assertEquals(TestHStore.DummyStoreEngine.lastCreatedCompactor, this.store.storeEngine.getCompactor());
    }

    @Test
    public void testRefreshStoreFiles() throws Exception {
        init(name.getMethodName());
        Assert.assertEquals(0, this.store.getStorefilesCount());
        // Test refreshing store files when no store files are there
        store.refreshStoreFiles();
        Assert.assertEquals(0, this.store.getStorefilesCount());
        // add some data, flush
        this.store.add(new KeyValue(row, family, qf1, 1, ((byte[]) (null))), null);
        flush(1);
        Assert.assertEquals(1, this.store.getStorefilesCount());
        // add one more file
        addStoreFile();
        Assert.assertEquals(1, this.store.getStorefilesCount());
        store.refreshStoreFiles();
        Assert.assertEquals(2, this.store.getStorefilesCount());
        // add three more files
        addStoreFile();
        addStoreFile();
        addStoreFile();
        Assert.assertEquals(2, this.store.getStorefilesCount());
        store.refreshStoreFiles();
        Assert.assertEquals(5, this.store.getStorefilesCount());
        closeCompactedFile(0);
        archiveStoreFile(0);
        Assert.assertEquals(5, this.store.getStorefilesCount());
        store.refreshStoreFiles();
        Assert.assertEquals(4, this.store.getStorefilesCount());
        archiveStoreFile(0);
        archiveStoreFile(1);
        archiveStoreFile(2);
        Assert.assertEquals(4, this.store.getStorefilesCount());
        store.refreshStoreFiles();
        Assert.assertEquals(1, this.store.getStorefilesCount());
        archiveStoreFile(0);
        store.refreshStoreFiles();
        Assert.assertEquals(0, this.store.getStorefilesCount());
    }

    @Test
    public void testRefreshStoreFilesNotChanged() throws IOException {
        init(name.getMethodName());
        Assert.assertEquals(0, this.store.getStorefilesCount());
        // add some data, flush
        this.store.add(new KeyValue(row, family, qf1, 1, ((byte[]) (null))), null);
        flush(1);
        // add one more file
        addStoreFile();
        HStore spiedStore = Mockito.spy(store);
        // call first time after files changed
        spiedStore.refreshStoreFiles();
        Assert.assertEquals(2, this.store.getStorefilesCount());
        Mockito.verify(spiedStore, Mockito.times(1)).replaceStoreFiles(ArgumentMatchers.any(), ArgumentMatchers.any());
        // call second time
        spiedStore.refreshStoreFiles();
        // ensure that replaceStoreFiles is not called if files are not refreshed
        Mockito.verify(spiedStore, Mockito.times(0)).replaceStoreFiles(null, null);
    }

    @Test
    public void testNumberOfMemStoreScannersAfterFlush() throws IOException {
        long seqId = 100;
        long timestamp = System.currentTimeMillis();
        Cell cell0 = CellBuilderFactory.create(DEEP_COPY).setRow(row).setFamily(family).setQualifier(qf1).setTimestamp(timestamp).setType(Put).setValue(qf1).build();
        PrivateCellUtil.setSequenceId(cell0, seqId);
        testNumberOfMemStoreScannersAfterFlush(Arrays.asList(cell0), Collections.emptyList());
        Cell cell1 = CellBuilderFactory.create(DEEP_COPY).setRow(row).setFamily(family).setQualifier(qf2).setTimestamp(timestamp).setType(Put).setValue(qf1).build();
        PrivateCellUtil.setSequenceId(cell1, seqId);
        testNumberOfMemStoreScannersAfterFlush(Arrays.asList(cell0), Arrays.asList(cell1));
        seqId = 101;
        timestamp = System.currentTimeMillis();
        Cell cell2 = CellBuilderFactory.create(DEEP_COPY).setRow(row2).setFamily(family).setQualifier(qf2).setTimestamp(timestamp).setType(Put).setValue(qf1).build();
        PrivateCellUtil.setSequenceId(cell2, seqId);
        testNumberOfMemStoreScannersAfterFlush(Arrays.asList(cell0), Arrays.asList(cell1, cell2));
    }

    @Test
    public void testFlushBeforeCompletingScanWoFilter() throws IOException, InterruptedException {
        final AtomicBoolean timeToGoNextRow = new AtomicBoolean(false);
        final int expectedSize = 3;
        testFlushBeforeCompletingScan(new TestHStore.MyListHook() {
            @Override
            public void hook(int currentSize) {
                if (currentSize == (expectedSize - 1)) {
                    try {
                        TestHStore.flushStore(store, ((id)++));
                        timeToGoNextRow.set(true);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new FilterBase() {
            @Override
            public ReturnCode filterCell(final Cell c) throws IOException {
                return INCLUDE;
            }
        }, expectedSize);
    }

    @Test
    public void testFlushBeforeCompletingScanWithFilter() throws IOException, InterruptedException {
        final AtomicBoolean timeToGoNextRow = new AtomicBoolean(false);
        final int expectedSize = 2;
        testFlushBeforeCompletingScan(new TestHStore.MyListHook() {
            @Override
            public void hook(int currentSize) {
                if (currentSize == (expectedSize - 1)) {
                    try {
                        TestHStore.flushStore(store, ((id)++));
                        timeToGoNextRow.set(true);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new FilterBase() {
            @Override
            public ReturnCode filterCell(final Cell c) throws IOException {
                if (timeToGoNextRow.get()) {
                    timeToGoNextRow.set(false);
                    return NEXT_ROW;
                } else {
                    return ReturnCode.INCLUDE;
                }
            }
        }, expectedSize);
    }

    @Test
    public void testFlushBeforeCompletingScanWithFilterHint() throws IOException, InterruptedException {
        final AtomicBoolean timeToGetHint = new AtomicBoolean(false);
        final int expectedSize = 2;
        testFlushBeforeCompletingScan(new TestHStore.MyListHook() {
            @Override
            public void hook(int currentSize) {
                if (currentSize == (expectedSize - 1)) {
                    try {
                        TestHStore.flushStore(store, ((id)++));
                        timeToGetHint.set(true);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new FilterBase() {
            @Override
            public ReturnCode filterCell(final Cell c) throws IOException {
                if (timeToGetHint.get()) {
                    timeToGetHint.set(false);
                    return ReturnCode.SEEK_NEXT_USING_HINT;
                } else {
                    return ReturnCode.INCLUDE;
                }
            }

            @Override
            public Cell getNextCellHint(Cell currentCell) throws IOException {
                return currentCell;
            }
        }, expectedSize);
    }

    @Test
    public void testCreateScannerAndSnapshotConcurrently() throws IOException, InterruptedException {
        Configuration conf = HBaseConfiguration.create();
        conf.set(MEMSTORE_CLASS_NAME, TestHStore.MyCompactingMemStore.class.getName());
        init(name.getMethodName(), conf, ColumnFamilyDescriptorBuilder.newBuilder(family).setInMemoryCompaction(BASIC).build());
        byte[] value = Bytes.toBytes("value");
        MemStoreSizing memStoreSizing = new NonThreadSafeMemStoreSizing();
        long ts = EnvironmentEdgeManager.currentTime();
        long seqId = 100;
        // older data whihc shouldn't be "seen" by client
        store.add(createCell(qf1, ts, seqId, value), memStoreSizing);
        store.add(createCell(qf2, ts, seqId, value), memStoreSizing);
        store.add(createCell(qf3, ts, seqId, value), memStoreSizing);
        TreeSet<byte[]> quals = new TreeSet(Bytes.BYTES_COMPARATOR);
        quals.add(qf1);
        quals.add(qf2);
        quals.add(qf3);
        StoreFlushContext storeFlushCtx = store.createFlushContext(((id)++), DUMMY);
        TestHStore.MyCompactingMemStore.START_TEST.set(true);
        Runnable flush = () -> {
            // this is blocked until we create first scanner from pipeline and snapshot -- phase (1/5)
            // recreate the active memstore -- phase (4/5)
            storeFlushCtx.prepare();
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(flush);
        // we get scanner from pipeline and snapshot but they are empty. -- phase (2/5)
        // this is blocked until we recreate the active memstore -- phase (3/5)
        // we get scanner from active memstore but it is empty -- phase (5/5)
        InternalScanner scanner = ((InternalScanner) (store.getScanner(new Scan(new Get(row)), quals, (seqId + 1))));
        service.shutdown();
        service.awaitTermination(20, TimeUnit.SECONDS);
        try {
            try {
                List<Cell> results = new ArrayList<>();
                scanner.next(results);
                Assert.assertEquals(3, results.size());
                for (Cell c : results) {
                    byte[] actualValue = CellUtil.cloneValue(c);
                    Assert.assertTrue(((("expected:" + (Bytes.toStringBinary(value))) + ", actual:") + (Bytes.toStringBinary(actualValue))), Bytes.equals(actualValue, value));
                }
            } finally {
                scanner.close();
            }
        } finally {
            TestHStore.MyCompactingMemStore.START_TEST.set(false);
            storeFlushCtx.flushCache(Mockito.mock(MonitoredTask.class));
            storeFlushCtx.commit(Mockito.mock(MonitoredTask.class));
        }
    }

    @Test
    public void testScanWithDoubleFlush() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        // Initialize region
        TestHStore.MyStore myStore = initMyStore(name.getMethodName(), conf, new TestHStore.MyStoreHook() {
            @Override
            public void getScanners(TestHStore.MyStore store) throws IOException {
                final long tmpId = (id)++;
                ExecutorService s = Executors.newSingleThreadExecutor();
                s.submit(() -> {
                    try {
                        // flush the store before storescanner updates the scanners from store.
                        // The current data will be flushed into files, and the memstore will
                        // be clear.
                        // -- phase (4/4)
                        TestHStore.flushStore(store, tmpId);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                s.shutdown();
                try {
                    // wait for the flush, the thread will be blocked in HStore#notifyChangedReadersObservers.
                    s.awaitTermination(3, TimeUnit.SECONDS);
                } catch (InterruptedException ex) {
                }
            }
        });
        byte[] oldValue = Bytes.toBytes("oldValue");
        byte[] currentValue = Bytes.toBytes("currentValue");
        MemStoreSizing memStoreSizing = new NonThreadSafeMemStoreSizing();
        long ts = EnvironmentEdgeManager.currentTime();
        long seqId = 100;
        // older data whihc shouldn't be "seen" by client
        myStore.add(createCell(qf1, ts, seqId, oldValue), memStoreSizing);
        myStore.add(createCell(qf2, ts, seqId, oldValue), memStoreSizing);
        myStore.add(createCell(qf3, ts, seqId, oldValue), memStoreSizing);
        long snapshotId = (id)++;
        // push older data into snapshot -- phase (1/4)
        StoreFlushContext storeFlushCtx = store.createFlushContext(snapshotId, DUMMY);
        storeFlushCtx.prepare();
        // insert current data into active -- phase (2/4)
        myStore.add(createCell(qf1, (ts + 1), (seqId + 1), currentValue), memStoreSizing);
        myStore.add(createCell(qf2, (ts + 1), (seqId + 1), currentValue), memStoreSizing);
        myStore.add(createCell(qf3, (ts + 1), (seqId + 1), currentValue), memStoreSizing);
        TreeSet<byte[]> quals = new TreeSet(Bytes.BYTES_COMPARATOR);
        quals.add(qf1);
        quals.add(qf2);
        quals.add(qf3);
        try (InternalScanner scanner = ((InternalScanner) (getScanner(new Scan(new Get(row)), quals, (seqId + 1))))) {
            // complete the flush -- phase (3/4)
            storeFlushCtx.flushCache(Mockito.mock(MonitoredTask.class));
            storeFlushCtx.commit(Mockito.mock(MonitoredTask.class));
            List<Cell> results = new ArrayList<>();
            scanner.next(results);
            Assert.assertEquals(3, results.size());
            for (Cell c : results) {
                byte[] actualValue = CellUtil.cloneValue(c);
                Assert.assertTrue(((("expected:" + (Bytes.toStringBinary(currentValue))) + ", actual:") + (Bytes.toStringBinary(actualValue))), Bytes.equals(actualValue, currentValue));
            }
        }
    }

    @Test
    public void testReclaimChunkWhenScaning() throws IOException {
        init("testReclaimChunkWhenScaning");
        long ts = EnvironmentEdgeManager.currentTime();
        long seqId = 100;
        byte[] value = Bytes.toBytes("value");
        // older data whihc shouldn't be "seen" by client
        store.add(createCell(qf1, ts, seqId, value), null);
        store.add(createCell(qf2, ts, seqId, value), null);
        store.add(createCell(qf3, ts, seqId, value), null);
        TreeSet<byte[]> quals = new TreeSet(Bytes.BYTES_COMPARATOR);
        quals.add(qf1);
        quals.add(qf2);
        quals.add(qf3);
        try (InternalScanner scanner = ((InternalScanner) (store.getScanner(new Scan(new Get(row)), quals, seqId)))) {
            List<Cell> results = new TestHStore.MyList<>(( size) -> {
                switch (size) {
                    // 1) we get the first cell (qf1)
                    // 2) flush the data to have StoreScanner update inner scanners
                    // 3) the chunk will be reclaimed after updaing
                    case 1 :
                        try {
                            TestHStore.flushStore(store, ((id)++));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                        // 1) we get the second cell (qf2)
                        // 2) add some cell to fill some byte into the chunk (we have only one chunk)
                    case 2 :
                        try {
                            byte[] newValue = Bytes.toBytes("newValue");
                            // older data whihc shouldn't be "seen" by client
                            store.add(createCell(qf1, (ts + 1), (seqId + 1), newValue), null);
                            store.add(createCell(qf2, (ts + 1), (seqId + 1), newValue), null);
                            store.add(createCell(qf3, (ts + 1), (seqId + 1), newValue), null);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    default :
                        break;
                }
            });
            scanner.next(results);
            Assert.assertEquals(3, results.size());
            for (Cell c : results) {
                byte[] actualValue = CellUtil.cloneValue(c);
                Assert.assertTrue(((("expected:" + (Bytes.toStringBinary(value))) + ", actual:") + (Bytes.toStringBinary(actualValue))), Bytes.equals(actualValue, value));
            }
        }
    }

    /**
     * If there are two running InMemoryFlushRunnable, the later InMemoryFlushRunnable
     * may change the versionedList. And the first InMemoryFlushRunnable will use the chagned
     * versionedList to remove the corresponding segments.
     * In short, there will be some segements which isn't in merge are removed.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testRunDoubleMemStoreCompactors() throws IOException, InterruptedException {
        int flushSize = 500;
        Configuration conf = HBaseConfiguration.create();
        conf.set(MEMSTORE_CLASS_NAME, TestHStore.MyCompactingMemStoreWithCustomCompactor.class.getName());
        conf.setDouble(IN_MEMORY_FLUSH_THRESHOLD_FACTOR_KEY, 0.25);
        TestHStore.MyCompactingMemStoreWithCustomCompactor.RUNNER_COUNT.set(0);
        conf.set(HREGION_MEMSTORE_FLUSH_SIZE, String.valueOf(flushSize));
        // Set the lower threshold to invoke the "MERGE" policy
        conf.set(COMPACTING_MEMSTORE_THRESHOLD_KEY, String.valueOf(0));
        init(name.getMethodName(), conf, ColumnFamilyDescriptorBuilder.newBuilder(family).setInMemoryCompaction(BASIC).build());
        byte[] value = Bytes.toBytes("thisisavarylargevalue");
        MemStoreSizing memStoreSizing = new NonThreadSafeMemStoreSizing();
        long ts = EnvironmentEdgeManager.currentTime();
        long seqId = 100;
        // older data whihc shouldn't be "seen" by client
        store.add(createCell(qf1, ts, seqId, value), memStoreSizing);
        store.add(createCell(qf2, ts, seqId, value), memStoreSizing);
        store.add(createCell(qf3, ts, seqId, value), memStoreSizing);
        Assert.assertEquals(1, TestHStore.MyCompactingMemStoreWithCustomCompactor.RUNNER_COUNT.get());
        StoreFlushContext storeFlushCtx = store.createFlushContext(((id)++), DUMMY);
        storeFlushCtx.prepare();
        // This shouldn't invoke another in-memory flush because the first compactor thread
        // hasn't accomplished the in-memory compaction.
        store.add(createCell(qf1, (ts + 1), (seqId + 1), value), memStoreSizing);
        store.add(createCell(qf1, (ts + 1), (seqId + 1), value), memStoreSizing);
        store.add(createCell(qf1, (ts + 1), (seqId + 1), value), memStoreSizing);
        Assert.assertEquals(1, TestHStore.MyCompactingMemStoreWithCustomCompactor.RUNNER_COUNT.get());
        // okay. Let the compaction be completed
        TestHStore.MyMemStoreCompactor.START_COMPACTOR_LATCH.countDown();
        CompactingMemStore mem = ((CompactingMemStore) (((HStore) (store)).memstore));
        while (mem.isMemStoreFlushingInMemory()) {
            TimeUnit.SECONDS.sleep(1);
        } 
        // This should invoke another in-memory flush.
        store.add(createCell(qf1, (ts + 2), (seqId + 2), value), memStoreSizing);
        store.add(createCell(qf1, (ts + 2), (seqId + 2), value), memStoreSizing);
        store.add(createCell(qf1, (ts + 2), (seqId + 2), value), memStoreSizing);
        Assert.assertEquals(2, TestHStore.MyCompactingMemStoreWithCustomCompactor.RUNNER_COUNT.get());
        conf.set(HREGION_MEMSTORE_FLUSH_SIZE, String.valueOf(DEFAULT_MEMSTORE_FLUSH_SIZE));
        storeFlushCtx.flushCache(Mockito.mock(MonitoredTask.class));
        storeFlushCtx.commit(Mockito.mock(MonitoredTask.class));
    }

    @Test
    public void testAge() throws IOException {
        long currentTime = System.currentTimeMillis();
        ManualEnvironmentEdge edge = new ManualEnvironmentEdge();
        edge.setValue(currentTime);
        EnvironmentEdgeManager.injectEdge(edge);
        Configuration conf = TestHStore.TEST_UTIL.getConfiguration();
        ColumnFamilyDescriptor hcd = ColumnFamilyDescriptorBuilder.of(family);
        initHRegion(name.getMethodName(), conf, TableDescriptorBuilder.newBuilder(TableName.valueOf(table)), hcd, null, false);
        HStore store = new HStore(region, hcd, conf) {
            @Override
            protected StoreEngine<?, ?, ?, ?> createStoreEngine(HStore store, Configuration conf, CellComparator kvComparator) throws IOException {
                List<HStoreFile> storefiles = Arrays.asList(mockStoreFile((currentTime - 10)), mockStoreFile((currentTime - 100)), mockStoreFile((currentTime - 1000)), mockStoreFile((currentTime - 10000)));
                StoreFileManager sfm = Mockito.mock(StoreFileManager.class);
                Mockito.when(sfm.getStorefiles()).thenReturn(storefiles);
                StoreEngine<?, ?, ?, ?> storeEngine = Mockito.mock(StoreEngine.class);
                Mockito.when(storeEngine.getStoreFileManager()).thenReturn(sfm);
                return storeEngine;
            }
        };
        Assert.assertEquals(10L, store.getMinStoreFileAge().getAsLong());
        Assert.assertEquals(10000L, store.getMaxStoreFileAge().getAsLong());
        Assert.assertEquals(((((10 + 100) + 1000) + 10000) / 4.0), store.getAvgStoreFileAge().getAsDouble(), 1.0E-4);
    }

    private static class MyStore extends HStore {
        private final TestHStore.MyStoreHook hook;

        MyStore(final HRegion region, final ColumnFamilyDescriptor family, final Configuration confParam, TestHStore.MyStoreHook hook, boolean switchToPread) throws IOException {
            super(region, family, confParam);
            this.hook = hook;
        }

        @Override
        public List<KeyValueScanner> getScanners(List<HStoreFile> files, boolean cacheBlocks, boolean usePread, boolean isCompaction, ScanQueryMatcher matcher, byte[] startRow, boolean includeStartRow, byte[] stopRow, boolean includeStopRow, long readPt, boolean includeMemstoreScanner) throws IOException {
            hook.getScanners(this);
            return super.getScanners(files, cacheBlocks, usePread, isCompaction, matcher, startRow, true, stopRow, false, readPt, includeMemstoreScanner);
        }

        @Override
        public long getSmallestReadPoint() {
            return hook.getSmallestReadPoint(this);
        }
    }

    private abstract static class MyStoreHook {
        void getScanners(TestHStore.MyStore store) throws IOException {
        }

        long getSmallestReadPoint(HStore store) {
            return store.getHRegion().getSmallestReadPoint();
        }
    }

    @Test
    public void testSwitchingPreadtoStreamParallelyWithCompactionDischarger() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.hstore.engine.class", TestHStore.DummyStoreEngine.class.getName());
        conf.setLong(STORESCANNER_PREAD_MAX_BYTES, 0);
        // Set the lower threshold to invoke the "MERGE" policy
        TestHStore.MyStore store = initMyStore(name.getMethodName(), conf, new TestHStore.MyStoreHook() {});
        MemStoreSizing memStoreSizing = new NonThreadSafeMemStoreSizing();
        long ts = System.currentTimeMillis();
        long seqID = 1L;
        // Add some data to the region and do some flushes
        for (int i = 1; i < 10; i++) {
            store.add(createCell(Bytes.toBytes(("row" + i)), qf1, ts, (seqID++), Bytes.toBytes("")), memStoreSizing);
        }
        // flush them
        TestHStore.flushStore(store, seqID);
        for (int i = 11; i < 20; i++) {
            store.add(createCell(Bytes.toBytes(("row" + i)), qf1, ts, (seqID++), Bytes.toBytes("")), memStoreSizing);
        }
        // flush them
        TestHStore.flushStore(store, seqID);
        for (int i = 21; i < 30; i++) {
            store.add(createCell(Bytes.toBytes(("row" + i)), qf1, ts, (seqID++), Bytes.toBytes("")), memStoreSizing);
        }
        // flush them
        TestHStore.flushStore(store, seqID);
        Assert.assertEquals(3, getStorefilesCount());
        Scan scan = new Scan();
        scan.addFamily(family);
        Collection<HStoreFile> storefiles2 = getStorefiles();
        ArrayList<HStoreFile> actualStorefiles = Lists.newArrayList(storefiles2);
        StoreScanner storeScanner = ((StoreScanner) (store.getScanner(scan, scan.getFamilyMap().get(family), Long.MAX_VALUE)));
        // get the current heap
        KeyValueHeap heap = storeScanner.heap;
        // create more store files
        for (int i = 31; i < 40; i++) {
            store.add(createCell(Bytes.toBytes(("row" + i)), qf1, ts, (seqID++), Bytes.toBytes("")), memStoreSizing);
        }
        // flush them
        TestHStore.flushStore(store, seqID);
        for (int i = 41; i < 50; i++) {
            store.add(createCell(Bytes.toBytes(("row" + i)), qf1, ts, (seqID++), Bytes.toBytes("")), memStoreSizing);
        }
        // flush them
        TestHStore.flushStore(store, seqID);
        storefiles2 = store.getStorefiles();
        ArrayList<HStoreFile> actualStorefiles1 = Lists.newArrayList(storefiles2);
        actualStorefiles1.removeAll(actualStorefiles);
        // Do compaction
        TestHStore.MyThread thread = new TestHStore.MyThread(storeScanner);
        thread.start();
        store.replaceStoreFiles(actualStorefiles, actualStorefiles1);
        thread.join();
        KeyValueHeap heap2 = thread.getHeap();
        Assert.assertFalse(heap.equals(heap2));
    }

    @Test
    public void testSpaceQuotaChangeAfterReplacement() throws IOException {
        final TableName tn = TableName.valueOf(name.getMethodName());
        init(name.getMethodName());
        RegionSizeStoreImpl sizeStore = new RegionSizeStoreImpl();
        HStoreFile sf1 = mockStoreFileWithLength(1024L);
        HStoreFile sf2 = mockStoreFileWithLength(2048L);
        HStoreFile sf3 = mockStoreFileWithLength(4096L);
        HStoreFile sf4 = mockStoreFileWithLength(8192L);
        RegionInfo regionInfo = RegionInfoBuilder.newBuilder(tn).setStartKey(Bytes.toBytes("a")).setEndKey(Bytes.toBytes("b")).build();
        // Compacting two files down to one, reducing size
        sizeStore.put(regionInfo, (1024L + 4096L));
        store.updateSpaceQuotaAfterFileReplacement(sizeStore, regionInfo, Arrays.asList(sf1, sf3), Arrays.asList(sf2));
        Assert.assertEquals(2048L, sizeStore.getRegionSize(regionInfo).getSize());
        // The same file length in and out should have no change
        store.updateSpaceQuotaAfterFileReplacement(sizeStore, regionInfo, Arrays.asList(sf2), Arrays.asList(sf2));
        Assert.assertEquals(2048L, sizeStore.getRegionSize(regionInfo).getSize());
        // Increase the total size used
        store.updateSpaceQuotaAfterFileReplacement(sizeStore, regionInfo, Arrays.asList(sf2), Arrays.asList(sf3));
        Assert.assertEquals(4096L, sizeStore.getRegionSize(regionInfo).getSize());
        RegionInfo regionInfo2 = RegionInfoBuilder.newBuilder(tn).setStartKey(Bytes.toBytes("b")).setEndKey(Bytes.toBytes("c")).build();
        store.updateSpaceQuotaAfterFileReplacement(sizeStore, regionInfo2, null, Arrays.asList(sf4));
        Assert.assertEquals(8192L, sizeStore.getRegionSize(regionInfo2).getSize());
    }

    private static class MyThread extends Thread {
        private StoreScanner scanner;

        private KeyValueHeap heap;

        public MyThread(StoreScanner scanner) {
            this.scanner = scanner;
        }

        public KeyValueHeap getHeap() {
            return this.heap;
        }

        @Override
        public void run() {
            scanner.trySwitchToStreamRead();
            heap = scanner.heap;
        }
    }

    private static class MyMemStoreCompactor extends MemStoreCompactor {
        private static final AtomicInteger RUNNER_COUNT = new AtomicInteger(0);

        private static final CountDownLatch START_COMPACTOR_LATCH = new CountDownLatch(1);

        public MyMemStoreCompactor(CompactingMemStore compactingMemStore, MemoryCompactionPolicy compactionPolicy) throws IllegalArgumentIOException {
            super(compactingMemStore, compactionPolicy);
        }

        @Override
        public boolean start() throws IOException {
            boolean isFirst = (TestHStore.MyMemStoreCompactor.RUNNER_COUNT.getAndIncrement()) == 0;
            if (isFirst) {
                try {
                    TestHStore.MyMemStoreCompactor.START_COMPACTOR_LATCH.await();
                    return super.start();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return super.start();
        }
    }

    public static class MyCompactingMemStoreWithCustomCompactor extends CompactingMemStore {
        private static final AtomicInteger RUNNER_COUNT = new AtomicInteger(0);

        public MyCompactingMemStoreWithCustomCompactor(Configuration conf, CellComparatorImpl c, HStore store, RegionServicesForStores regionServices, MemoryCompactionPolicy compactionPolicy) throws IOException {
            super(conf, c, store, regionServices, compactionPolicy);
        }

        @Override
        protected MemStoreCompactor createMemStoreCompactor(MemoryCompactionPolicy compactionPolicy) throws IllegalArgumentIOException {
            return new TestHStore.MyMemStoreCompactor(this, compactionPolicy);
        }

        @Override
        protected boolean setInMemoryCompactionFlag() {
            boolean rval = super.setInMemoryCompactionFlag();
            if (rval) {
                TestHStore.MyCompactingMemStoreWithCustomCompactor.RUNNER_COUNT.incrementAndGet();
                if (TestHStore.LOG.isDebugEnabled()) {
                    TestHStore.LOG.debug(("runner count: " + (TestHStore.MyCompactingMemStoreWithCustomCompactor.RUNNER_COUNT.get())));
                }
            }
            return rval;
        }
    }

    public static class MyCompactingMemStore extends CompactingMemStore {
        private static final AtomicBoolean START_TEST = new AtomicBoolean(false);

        private final CountDownLatch getScannerLatch = new CountDownLatch(1);

        private final CountDownLatch snapshotLatch = new CountDownLatch(1);

        public MyCompactingMemStore(Configuration conf, CellComparatorImpl c, HStore store, RegionServicesForStores regionServices, MemoryCompactionPolicy compactionPolicy) throws IOException {
            super(conf, c, store, regionServices, compactionPolicy);
        }

        @Override
        protected List<KeyValueScanner> createList(int capacity) {
            if (TestHStore.MyCompactingMemStore.START_TEST.get()) {
                try {
                    getScannerLatch.countDown();
                    snapshotLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return new ArrayList<>(capacity);
        }

        @Override
        protected void pushActiveToPipeline(MutableSegment active) {
            if (TestHStore.MyCompactingMemStore.START_TEST.get()) {
                try {
                    getScannerLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            super.pushActiveToPipeline(active);
            if (TestHStore.MyCompactingMemStore.START_TEST.get()) {
                snapshotLatch.countDown();
            }
        }
    }

    interface MyListHook {
        void hook(int currentSize);
    }

    private static class MyList<T> implements List<T> {
        private final List<T> delegatee = new ArrayList<>();

        private final TestHStore.MyListHook hookAtAdd;

        MyList(final TestHStore.MyListHook hookAtAdd) {
            this.hookAtAdd = hookAtAdd;
        }

        @Override
        public int size() {
            return delegatee.size();
        }

        @Override
        public boolean isEmpty() {
            return delegatee.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return delegatee.contains(o);
        }

        @Override
        public Iterator<T> iterator() {
            return delegatee.iterator();
        }

        @Override
        public Object[] toArray() {
            return delegatee.toArray();
        }

        @Override
        public <R> R[] toArray(R[] a) {
            return delegatee.toArray(a);
        }

        @Override
        public boolean add(T e) {
            hookAtAdd.hook(size());
            return delegatee.add(e);
        }

        @Override
        public boolean remove(Object o) {
            return delegatee.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return delegatee.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            return delegatee.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            return delegatee.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return delegatee.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return delegatee.retainAll(c);
        }

        @Override
        public void clear() {
            delegatee.clear();
        }

        @Override
        public T get(int index) {
            return delegatee.get(index);
        }

        @Override
        public T set(int index, T element) {
            return delegatee.set(index, element);
        }

        @Override
        public void add(int index, T element) {
            delegatee.add(index, element);
        }

        @Override
        public T remove(int index) {
            return delegatee.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return delegatee.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return delegatee.lastIndexOf(o);
        }

        @Override
        public ListIterator<T> listIterator() {
            return delegatee.listIterator();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            return delegatee.listIterator(index);
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            return delegatee.subList(fromIndex, toIndex);
        }
    }
}

