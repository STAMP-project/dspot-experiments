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
package org.apache.hadoop.hbase.regionserver.compactions;


import CompactionConfiguration.HBASE_HSTORE_COMPACTION_RATIO_KEY;
import NoLimitThroughputController.INSTANCE;
import StripeStoreConfig.FLUSH_TO_L0_KEY;
import StripeStoreConfig.MAX_FILES_KEY;
import StripeStoreConfig.MIN_FILES_KEY;
import StripeStoreConfig.MIN_FILES_L0_KEY;
import StripeStoreConfig.SIZE_TO_SPLIT_KEY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.regionserver.HStoreFile;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.regionserver.ScannerContext;
import org.apache.hadoop.hbase.regionserver.StoreConfigInformation;
import org.apache.hadoop.hbase.regionserver.StripeStoreConfig;
import org.apache.hadoop.hbase.regionserver.StripeStoreFileManager;
import org.apache.hadoop.hbase.regionserver.compactions.StripeCompactionPolicy.StripeInformationProvider;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.ConcatenatedLists;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.ManualEnvironmentEdge;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, SmallTests.class })
public class TestStripeCompactionPolicy {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStripeCompactionPolicy.class);

    private static final byte[] KEY_A = Bytes.toBytes("aaa");

    private static final byte[] KEY_B = Bytes.toBytes("bbb");

    private static final byte[] KEY_C = Bytes.toBytes("ccc");

    private static final byte[] KEY_D = Bytes.toBytes("ddd");

    private static final byte[] KEY_E = Bytes.toBytes("eee");

    private static final KeyValue KV_A = new KeyValue(TestStripeCompactionPolicy.KEY_A, 0L);

    private static final KeyValue KV_B = new KeyValue(TestStripeCompactionPolicy.KEY_B, 0L);

    private static final KeyValue KV_C = new KeyValue(TestStripeCompactionPolicy.KEY_C, 0L);

    private static final KeyValue KV_D = new KeyValue(TestStripeCompactionPolicy.KEY_D, 0L);

    private static final KeyValue KV_E = new KeyValue(TestStripeCompactionPolicy.KEY_E, 0L);

    private static long defaultSplitSize = 18;

    private static float defaultSplitCount = 1.8F;

    private static final int defaultInitialCount = 1;

    private static long defaultTtl = 1000 * 1000;

    @Parameterized.Parameter
    public boolean usePrivateReaders;

    @Test
    public void testNoStripesFromFlush() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.setBoolean(FLUSH_TO_L0_KEY, true);
        StripeCompactionPolicy policy = TestStripeCompactionPolicy.createPolicy(conf);
        StripeInformationProvider si = TestStripeCompactionPolicy.createStripesL0Only(0, 0);
        KeyValue[] input = new KeyValue[]{ TestStripeCompactionPolicy.KV_A, TestStripeCompactionPolicy.KV_B, TestStripeCompactionPolicy.KV_C, TestStripeCompactionPolicy.KV_D, TestStripeCompactionPolicy.KV_E };
        KeyValue[][] expected = new KeyValue[][]{ input };
        verifyFlush(policy, si, input, expected, null);
    }

    @Test
    public void testOldStripesFromFlush() throws Exception {
        StripeCompactionPolicy policy = TestStripeCompactionPolicy.createPolicy(HBaseConfiguration.create());
        StripeInformationProvider si = TestStripeCompactionPolicy.createStripes(0, TestStripeCompactionPolicy.KEY_C, TestStripeCompactionPolicy.KEY_D);
        KeyValue[] input = new KeyValue[]{ TestStripeCompactionPolicy.KV_B, TestStripeCompactionPolicy.KV_C, TestStripeCompactionPolicy.KV_C, TestStripeCompactionPolicy.KV_D, TestStripeCompactionPolicy.KV_E };
        KeyValue[][] expected = new KeyValue[][]{ new KeyValue[]{ TestStripeCompactionPolicy.KV_B }, new KeyValue[]{ TestStripeCompactionPolicy.KV_C, TestStripeCompactionPolicy.KV_C }, new KeyValue[]{ TestStripeCompactionPolicy.KV_D, TestStripeCompactionPolicy.KV_E } };
        verifyFlush(policy, si, input, expected, new byte[][]{ StripeStoreFileManager.OPEN_KEY, TestStripeCompactionPolicy.KEY_C, TestStripeCompactionPolicy.KEY_D, StripeStoreFileManager.OPEN_KEY });
    }

    @Test
    public void testNewStripesFromFlush() throws Exception {
        StripeCompactionPolicy policy = TestStripeCompactionPolicy.createPolicy(HBaseConfiguration.create());
        StripeInformationProvider si = TestStripeCompactionPolicy.createStripesL0Only(0, 0);
        KeyValue[] input = new KeyValue[]{ TestStripeCompactionPolicy.KV_B, TestStripeCompactionPolicy.KV_C, TestStripeCompactionPolicy.KV_C, TestStripeCompactionPolicy.KV_D, TestStripeCompactionPolicy.KV_E };
        // Starts with one stripe; unlike flush results, must have metadata
        KeyValue[][] expected = new KeyValue[][]{ input };
        verifyFlush(policy, si, input, expected, new byte[][]{ StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY });
    }

    @Test
    public void testSingleStripeCompaction() throws Exception {
        // Create a special policy that only compacts single stripes, using standard methods.
        Configuration conf = HBaseConfiguration.create();
        // Test depends on this not being set to pass.  Default breaks test.  TODO: Revisit.
        conf.unset("hbase.hstore.compaction.min.size");
        conf.setFloat(HBASE_HSTORE_COMPACTION_RATIO_KEY, 1.0F);
        conf.setInt(MIN_FILES_KEY, 3);
        conf.setInt(MAX_FILES_KEY, 4);
        conf.setLong(SIZE_TO_SPLIT_KEY, 1000);// make sure the are no splits

        StoreConfigInformation sci = Mockito.mock(StoreConfigInformation.class);
        StripeStoreConfig ssc = new StripeStoreConfig(conf, sci);
        StripeCompactionPolicy policy = new StripeCompactionPolicy(conf, sci, ssc) {
            @Override
            public StripeCompactionRequest selectCompaction(StripeInformationProvider si, List<HStoreFile> filesCompacting, boolean isOffpeak) throws IOException {
                if (!(filesCompacting.isEmpty()))
                    return null;

                return selectSingleStripeCompaction(si, false, false, isOffpeak);
            }

            @Override
            public boolean needsCompactions(StripeInformationProvider si, List<HStoreFile> filesCompacting) {
                if (!(filesCompacting.isEmpty()))
                    return false;

                return needsSingleStripeCompaction(si);
            }
        };
        // No compaction due to min files or ratio
        StripeInformationProvider si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, new Long[]{ 2L }, new Long[]{ 3L, 3L }, new Long[]{ 5L, 1L });
        verifyNoCompaction(policy, si);
        // No compaction due to min files or ratio - will report needed, but not do any.
        si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, new Long[]{ 2L }, new Long[]{ 3L, 3L }, new Long[]{ 5L, 1L, 1L });
        Assert.assertNull(policy.selectCompaction(si, TestStripeCompactionPolicy.al(), false));
        Assert.assertTrue(policy.needsCompactions(si, TestStripeCompactionPolicy.al()));
        // One stripe has possible compaction
        si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, new Long[]{ 2L }, new Long[]{ 3L, 3L }, new Long[]{ 5L, 4L, 3L });
        verifySingleStripeCompaction(policy, si, 2, null);
        // Several stripes have possible compactions; choose best quality (removes most files)
        si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, new Long[]{ 3L, 2L, 2L }, new Long[]{ 2L, 2L, 1L }, new Long[]{ 3L, 2L, 2L, 1L });
        verifySingleStripeCompaction(policy, si, 2, null);
        si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, new Long[]{ 5L }, new Long[]{ 3L, 2L, 2L, 1L }, new Long[]{ 3L, 2L, 2L });
        verifySingleStripeCompaction(policy, si, 1, null);
        // Or with smallest files, if the count is the same
        si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, new Long[]{ 3L, 3L, 3L }, new Long[]{ 3L, 1L, 2L }, new Long[]{ 3L, 2L, 2L });
        verifySingleStripeCompaction(policy, si, 1, null);
        // Verify max count is respected.
        si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, new Long[]{ 5L }, new Long[]{ 5L, 4L, 4L, 4L, 4L });
        List<HStoreFile> sfs = si.getStripes().get(1).subList(1, 5);
        verifyCompaction(policy, si, sfs, null, 1, null, si.getStartRow(1), si.getEndRow(1), true);
        // Verify ratio is applied.
        si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, new Long[]{ 5L }, new Long[]{ 50L, 4L, 4L, 4L, 4L });
        sfs = si.getStripes().get(1).subList(1, 5);
        verifyCompaction(policy, si, sfs, null, 1, null, si.getStartRow(1), si.getEndRow(1), true);
    }

    @Test
    public void testWithParallelCompaction() throws Exception {
        // TODO: currently only one compaction at a time per store is allowed. If this changes,
        // the appropriate file exclusion testing would need to be done in respective tests.
        Assert.assertNull(TestStripeCompactionPolicy.createPolicy(HBaseConfiguration.create()).selectCompaction(Mockito.mock(StripeInformationProvider.class), TestStripeCompactionPolicy.al(TestStripeCompactionPolicy.createFile()), false));
    }

    @Test
    public void testWithReferences() throws Exception {
        StripeCompactionPolicy policy = TestStripeCompactionPolicy.createPolicy(HBaseConfiguration.create());
        StripeCompactor sc = Mockito.mock(StripeCompactor.class);
        HStoreFile ref = TestStripeCompactionPolicy.createFile();
        Mockito.when(ref.isReference()).thenReturn(true);
        StripeInformationProvider si = Mockito.mock(StripeInformationProvider.class);
        Collection<HStoreFile> sfs = TestStripeCompactionPolicy.al(ref, TestStripeCompactionPolicy.createFile());
        Mockito.when(si.getStorefiles()).thenReturn(sfs);
        Assert.assertTrue(policy.needsCompactions(si, TestStripeCompactionPolicy.al()));
        StripeCompactionPolicy.StripeCompactionRequest scr = policy.selectCompaction(si, TestStripeCompactionPolicy.al(), false);
        // UnmodifiableCollection does not implement equals so we need to change it here to a
        // collection that implements it.
        Assert.assertEquals(si.getStorefiles(), new ArrayList(scr.getRequest().getFiles()));
        scr.execute(sc, INSTANCE, null);
        Mockito.verify(sc, Mockito.only()).compact(ArgumentMatchers.eq(scr.getRequest()), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), AdditionalMatchers.aryEq(StripeStoreFileManager.OPEN_KEY), AdditionalMatchers.aryEq(StripeStoreFileManager.OPEN_KEY), AdditionalMatchers.aryEq(StripeStoreFileManager.OPEN_KEY), AdditionalMatchers.aryEq(StripeStoreFileManager.OPEN_KEY), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testInitialCountFromL0() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.setInt(MIN_FILES_L0_KEY, 2);
        StripeCompactionPolicy policy = TestStripeCompactionPolicy.createPolicy(conf, TestStripeCompactionPolicy.defaultSplitSize, TestStripeCompactionPolicy.defaultSplitCount, 2, false);
        StripeCompactionPolicy.StripeInformationProvider si = TestStripeCompactionPolicy.createStripesL0Only(3, 8);
        verifyCompaction(policy, si, si.getStorefiles(), true, 2, 12L, StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY, true);
        si = TestStripeCompactionPolicy.createStripesL0Only(3, 10);// If result would be too large, split into smaller parts.

        verifyCompaction(policy, si, si.getStorefiles(), true, 3, 10L, StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY, true);
        policy = TestStripeCompactionPolicy.createPolicy(conf, TestStripeCompactionPolicy.defaultSplitSize, TestStripeCompactionPolicy.defaultSplitCount, 6, false);
        verifyCompaction(policy, si, si.getStorefiles(), true, 6, 5L, StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY, true);
    }

    @Test
    public void testExistingStripesFromL0() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.setInt(MIN_FILES_L0_KEY, 3);
        StripeCompactionPolicy.StripeInformationProvider si = TestStripeCompactionPolicy.createStripes(3, TestStripeCompactionPolicy.KEY_A);
        verifyCompaction(TestStripeCompactionPolicy.createPolicy(conf), si, si.getLevel0Files(), null, null, si.getStripeBoundaries());
    }

    @Test
    public void testNothingToCompactFromL0() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.setInt(MIN_FILES_L0_KEY, 4);
        StripeCompactionPolicy.StripeInformationProvider si = TestStripeCompactionPolicy.createStripesL0Only(3, 10);
        StripeCompactionPolicy policy = TestStripeCompactionPolicy.createPolicy(conf);
        verifyNoCompaction(policy, si);
        si = TestStripeCompactionPolicy.createStripes(3, TestStripeCompactionPolicy.KEY_A);
        verifyNoCompaction(policy, si);
    }

    @Test
    public void testSplitOffStripe() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        // Test depends on this not being set to pass.  Default breaks test.  TODO: Revisit.
        conf.unset("hbase.hstore.compaction.min.size");
        // First test everything with default split count of 2, then split into more.
        conf.setInt(MIN_FILES_KEY, 2);
        Long[] toSplit = new Long[]{ (TestStripeCompactionPolicy.defaultSplitSize) - 2, 1L, 1L };
        Long[] noSplit = new Long[]{ (TestStripeCompactionPolicy.defaultSplitSize) - 2, 1L };
        long splitTargetSize = ((long) ((TestStripeCompactionPolicy.defaultSplitSize) / (TestStripeCompactionPolicy.defaultSplitCount)));
        // Don't split if not eligible for compaction.
        StripeCompactionPolicy.StripeInformationProvider si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, new Long[]{ (TestStripeCompactionPolicy.defaultSplitSize) - 2, 2L });
        Assert.assertNull(TestStripeCompactionPolicy.createPolicy(conf).selectCompaction(si, TestStripeCompactionPolicy.al(), false));
        // Make sure everything is eligible.
        conf.setFloat(HBASE_HSTORE_COMPACTION_RATIO_KEY, 500.0F);
        StripeCompactionPolicy policy = TestStripeCompactionPolicy.createPolicy(conf);
        verifyWholeStripesCompaction(policy, si, 0, 0, null, 2, splitTargetSize);
        // Add some extra stripes...
        si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, noSplit, noSplit, toSplit);
        verifyWholeStripesCompaction(policy, si, 2, 2, null, 2, splitTargetSize);
        // In the middle.
        si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, noSplit, toSplit, noSplit);
        verifyWholeStripesCompaction(policy, si, 1, 1, null, 2, splitTargetSize);
        // No split-off with different config (larger split size).
        // However, in this case some eligible stripe will just be compacted alone.
        StripeCompactionPolicy specPolicy = TestStripeCompactionPolicy.createPolicy(conf, ((TestStripeCompactionPolicy.defaultSplitSize) + 1), TestStripeCompactionPolicy.defaultSplitCount, TestStripeCompactionPolicy.defaultInitialCount, false);
        verifySingleStripeCompaction(specPolicy, si, 1, null);
    }

    @Test
    public void testSplitOffStripeOffPeak() throws Exception {
        // for HBASE-11439
        Configuration conf = HBaseConfiguration.create();
        // Test depends on this not being set to pass.  Default breaks test.  TODO: Revisit.
        conf.unset("hbase.hstore.compaction.min.size");
        conf.setInt(MIN_FILES_KEY, 2);
        // Select the last 2 files.
        StripeCompactionPolicy.StripeInformationProvider si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, new Long[]{ (TestStripeCompactionPolicy.defaultSplitSize) - 2, 1L, 1L });
        Assert.assertEquals(2, TestStripeCompactionPolicy.createPolicy(conf).selectCompaction(si, TestStripeCompactionPolicy.al(), false).getRequest().getFiles().size());
        // Make sure everything is eligible in offpeak.
        conf.setFloat("hbase.hstore.compaction.ratio.offpeak", 500.0F);
        Assert.assertEquals(3, TestStripeCompactionPolicy.createPolicy(conf).selectCompaction(si, TestStripeCompactionPolicy.al(), true).getRequest().getFiles().size());
    }

    @Test
    public void testSplitOffStripeDropDeletes() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.setInt(MIN_FILES_KEY, 2);
        StripeCompactionPolicy policy = TestStripeCompactionPolicy.createPolicy(conf);
        Long[] toSplit = new Long[]{ (TestStripeCompactionPolicy.defaultSplitSize) / 2, (TestStripeCompactionPolicy.defaultSplitSize) / 2 };
        Long[] noSplit = new Long[]{ 1L };
        long splitTargetSize = ((long) ((TestStripeCompactionPolicy.defaultSplitSize) / (TestStripeCompactionPolicy.defaultSplitCount)));
        // Verify the deletes can be dropped if there are no L0 files.
        StripeCompactionPolicy.StripeInformationProvider si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, noSplit, toSplit);
        verifyWholeStripesCompaction(policy, si, 1, 1, true, null, splitTargetSize);
        // But cannot be dropped if there are.
        si = TestStripeCompactionPolicy.createStripesWithSizes(2, 2, noSplit, toSplit);
        verifyWholeStripesCompaction(policy, si, 1, 1, false, null, splitTargetSize);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMergeExpiredFiles() throws Exception {
        ManualEnvironmentEdge edge = new ManualEnvironmentEdge();
        long now = (TestStripeCompactionPolicy.defaultTtl) + 2;
        edge.setValue(now);
        EnvironmentEdgeManager.injectEdge(edge);
        try {
            HStoreFile expiredFile = TestStripeCompactionPolicy.createFile();
            HStoreFile notExpiredFile = TestStripeCompactionPolicy.createFile();
            Mockito.when(expiredFile.getReader().getMaxTimestamp()).thenReturn(((now - (TestStripeCompactionPolicy.defaultTtl)) - 1));
            Mockito.when(notExpiredFile.getReader().getMaxTimestamp()).thenReturn(((now - (TestStripeCompactionPolicy.defaultTtl)) + 1));
            List<HStoreFile> expired = Lists.newArrayList(expiredFile, expiredFile);
            List<HStoreFile> notExpired = Lists.newArrayList(notExpiredFile, notExpiredFile);
            List<HStoreFile> mixed = Lists.newArrayList(expiredFile, notExpiredFile);
            StripeCompactionPolicy policy = TestStripeCompactionPolicy.createPolicy(HBaseConfiguration.create(), TestStripeCompactionPolicy.defaultSplitSize, TestStripeCompactionPolicy.defaultSplitCount, TestStripeCompactionPolicy.defaultInitialCount, true);
            // Merge expired if there are eligible stripes.
            StripeCompactionPolicy.StripeInformationProvider si = TestStripeCompactionPolicy.createStripesWithFiles(expired, expired, expired);
            verifyWholeStripesCompaction(policy, si, 0, 2, null, 1, Long.MAX_VALUE, false);
            // Don't merge if nothing expired.
            si = TestStripeCompactionPolicy.createStripesWithFiles(notExpired, notExpired, notExpired);
            Assert.assertNull(policy.selectCompaction(si, TestStripeCompactionPolicy.al(), false));
            // Merge one expired stripe with next.
            si = TestStripeCompactionPolicy.createStripesWithFiles(notExpired, expired, notExpired);
            verifyWholeStripesCompaction(policy, si, 1, 2, null, 1, Long.MAX_VALUE, false);
            // Merge the biggest run out of multiple options.
            // Merge one expired stripe with next.
            si = TestStripeCompactionPolicy.createStripesWithFiles(notExpired, expired, notExpired, expired, expired, notExpired);
            verifyWholeStripesCompaction(policy, si, 3, 4, null, 1, Long.MAX_VALUE, false);
            // Stripe with a subset of expired files is not merged.
            si = TestStripeCompactionPolicy.createStripesWithFiles(expired, expired, notExpired, expired, mixed);
            verifyWholeStripesCompaction(policy, si, 0, 1, null, 1, Long.MAX_VALUE, false);
        } finally {
            EnvironmentEdgeManager.reset();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMergeExpiredStripes() throws Exception {
        // HBASE-11397
        ManualEnvironmentEdge edge = new ManualEnvironmentEdge();
        long now = (TestStripeCompactionPolicy.defaultTtl) + 2;
        edge.setValue(now);
        EnvironmentEdgeManager.injectEdge(edge);
        try {
            HStoreFile expiredFile = TestStripeCompactionPolicy.createFile();
            HStoreFile notExpiredFile = TestStripeCompactionPolicy.createFile();
            Mockito.when(expiredFile.getReader().getMaxTimestamp()).thenReturn(((now - (TestStripeCompactionPolicy.defaultTtl)) - 1));
            Mockito.when(notExpiredFile.getReader().getMaxTimestamp()).thenReturn(((now - (TestStripeCompactionPolicy.defaultTtl)) + 1));
            List<HStoreFile> expired = Lists.newArrayList(expiredFile, expiredFile);
            List<HStoreFile> notExpired = Lists.newArrayList(notExpiredFile, notExpiredFile);
            StripeCompactionPolicy policy = TestStripeCompactionPolicy.createPolicy(HBaseConfiguration.create(), TestStripeCompactionPolicy.defaultSplitSize, TestStripeCompactionPolicy.defaultSplitCount, TestStripeCompactionPolicy.defaultInitialCount, true);
            // Merge all three expired stripes into one.
            StripeCompactionPolicy.StripeInformationProvider si = TestStripeCompactionPolicy.createStripesWithFiles(expired, expired, expired);
            verifyMergeCompatcion(policy, si, 0, 2);
            // Merge two adjacent expired stripes into one.
            si = TestStripeCompactionPolicy.createStripesWithFiles(notExpired, expired, notExpired, expired, expired, notExpired);
            verifyMergeCompatcion(policy, si, 3, 4);
        } finally {
            EnvironmentEdgeManager.reset();
        }
    }

    @Test
    public void testSingleStripeDropDeletes() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        // Test depends on this not being set to pass.  Default breaks test.  TODO: Revisit.
        conf.unset("hbase.hstore.compaction.min.size");
        StripeCompactionPolicy policy = TestStripeCompactionPolicy.createPolicy(conf);
        // Verify the deletes can be dropped if there are no L0 files.
        Long[][] stripes = new Long[][]{ new Long[]{ 3L, 2L, 2L, 2L }, new Long[]{ 6L } };
        StripeInformationProvider si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, stripes);
        verifySingleStripeCompaction(policy, si, 0, true);
        // But cannot be dropped if there are.
        si = TestStripeCompactionPolicy.createStripesWithSizes(2, 2, stripes);
        verifySingleStripeCompaction(policy, si, 0, false);
        // Unless there are enough to cause L0 compaction.
        si = TestStripeCompactionPolicy.createStripesWithSizes(6, 2, stripes);
        ConcatenatedLists<HStoreFile> sfs = new ConcatenatedLists();
        sfs.addSublist(si.getLevel0Files());
        sfs.addSublist(si.getStripes().get(0));
        verifyCompaction(policy, si, sfs, si.getStartRow(0), si.getEndRow(0), si.getStripeBoundaries());
        // If we cannot actually compact all files in some stripe, L0 is chosen.
        si = TestStripeCompactionPolicy.createStripesWithSizes(6, 2, new Long[][]{ new Long[]{ 10L, 1L, 1L, 1L, 1L }, new Long[]{ 12L } });
        verifyCompaction(policy, si, si.getLevel0Files(), null, null, si.getStripeBoundaries());
        // even if L0 has no file
        // if all files of stripe aren't selected, delete must not be dropped.
        stripes = new Long[][]{ new Long[]{ 100L, 3L, 2L, 2L, 2L }, new Long[]{ 6L } };
        si = TestStripeCompactionPolicy.createStripesWithSizes(0, 0, stripes);
        List<HStoreFile> compactFile = new ArrayList<>();
        Iterator<HStoreFile> iter = si.getStripes().get(0).listIterator(1);
        while (iter.hasNext()) {
            compactFile.add(iter.next());
        } 
        verifyCompaction(policy, si, compactFile, false, 1, null, si.getStartRow(0), si.getEndRow(0), true);
    }

    private static class Scanner implements InternalScanner {
        private final ArrayList<KeyValue> kvs;

        public Scanner(KeyValue... kvs) {
            this.kvs = new ArrayList(Arrays.asList(kvs));
        }

        @Override
        public boolean next(List<Cell> result, ScannerContext scannerContext) throws IOException {
            if (kvs.isEmpty())
                return false;

            result.add(kvs.remove(0));
            return !(kvs.isEmpty());
        }

        @Override
        public void close() throws IOException {
        }
    }
}

