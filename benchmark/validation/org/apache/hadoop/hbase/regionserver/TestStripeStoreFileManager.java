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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestStripeStoreFileManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStripeStoreFileManager.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Path BASEDIR = getDataTestDir(TestStripeStoreFileManager.class.getSimpleName());

    private static final Path CFDIR = HStore.getStoreHomedir(TestStripeStoreFileManager.BASEDIR, "region", Bytes.toBytes("cf"));

    private static final byte[] KEY_A = Bytes.toBytes("aaa");

    private static final byte[] KEY_B = Bytes.toBytes("aab");

    private static final byte[] KEY_C = Bytes.toBytes("aac");

    private static final byte[] KEY_D = Bytes.toBytes("aad");

    private static final KeyValue KV_A = new KeyValue(TestStripeStoreFileManager.KEY_A, 0L);

    private static final KeyValue KV_B = new KeyValue(TestStripeStoreFileManager.KEY_B, 0L);

    private static final KeyValue KV_C = new KeyValue(TestStripeStoreFileManager.KEY_C, 0L);

    private static final KeyValue KV_D = new KeyValue(TestStripeStoreFileManager.KEY_D, 0L);

    @Test
    public void testInsertFilesIntoL0() throws Exception {
        StripeStoreFileManager manager = TestStripeStoreFileManager.createManager();
        MockHStoreFile sf = TestStripeStoreFileManager.createFile();
        manager.insertNewFiles(TestStripeStoreFileManager.al(sf));
        Assert.assertEquals(1, manager.getStorefileCount());
        Collection<HStoreFile> filesForGet = manager.getFilesForScan(TestStripeStoreFileManager.KEY_A, true, TestStripeStoreFileManager.KEY_A, true);
        Assert.assertEquals(1, filesForGet.size());
        Assert.assertTrue(filesForGet.contains(sf));
        // Add some stripes and make sure we get this file for every stripe.
        manager.addCompactionResults(TestStripeStoreFileManager.al(), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, StripeStoreFileManager.OPEN_KEY)));
        Assert.assertTrue(manager.getFilesForScan(TestStripeStoreFileManager.KEY_A, true, TestStripeStoreFileManager.KEY_A, true).contains(sf));
        Assert.assertTrue(manager.getFilesForScan(TestStripeStoreFileManager.KEY_C, true, TestStripeStoreFileManager.KEY_C, true).contains(sf));
    }

    @Test
    public void testClearFiles() throws Exception {
        StripeStoreFileManager manager = TestStripeStoreFileManager.createManager();
        manager.insertNewFiles(TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile()));
        manager.insertNewFiles(TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile()));
        manager.addCompactionResults(TestStripeStoreFileManager.al(), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, StripeStoreFileManager.OPEN_KEY)));
        Assert.assertEquals(4, manager.getStorefileCount());
        Collection<HStoreFile> allFiles = manager.clearFiles();
        Assert.assertEquals(4, allFiles.size());
        Assert.assertEquals(0, manager.getStorefileCount());
        Assert.assertEquals(0, manager.getStorefiles().size());
    }

    @Test
    public void testRowKeyBefore() throws Exception {
        StripeStoreFileManager manager = TestStripeStoreFileManager.createManager();
        HStoreFile l0File = TestStripeStoreFileManager.createFile();
        HStoreFile l0File2 = TestStripeStoreFileManager.createFile();
        manager.insertNewFiles(TestStripeStoreFileManager.al(l0File));
        manager.insertNewFiles(TestStripeStoreFileManager.al(l0File2));
        // Get candidate files.
        Iterator<HStoreFile> sfs = manager.getCandidateFilesForRowKeyBefore(TestStripeStoreFileManager.KV_B);
        sfs.next();
        sfs.remove();
        // Suppose we found a candidate in this file... make sure L0 file remaining is not removed.
        sfs = manager.updateCandidateFilesForRowKeyBefore(sfs, TestStripeStoreFileManager.KV_B, TestStripeStoreFileManager.KV_A);
        Assert.assertTrue(sfs.hasNext());
        // Now add some stripes (remove L0 file too)
        MockHStoreFile stripe0a = TestStripeStoreFileManager.createFile(0, 100, StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B);
        MockHStoreFile stripe1 = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, StripeStoreFileManager.OPEN_KEY);
        manager.addCompactionResults(TestStripeStoreFileManager.al(l0File), TestStripeStoreFileManager.al(stripe0a, stripe1));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(l0File));
        // If we want a key <= KEY_A, we should get everything except stripe1.
        ArrayList<HStoreFile> sfsDump = TestStripeStoreFileManager.dumpIterator(manager.getCandidateFilesForRowKeyBefore(TestStripeStoreFileManager.KV_A));
        Assert.assertEquals(2, sfsDump.size());
        Assert.assertTrue(sfsDump.contains(stripe0a));
        Assert.assertFalse(sfsDump.contains(stripe1));
        // If we want a key <= KEY_B, we should get everything since lower bound is inclusive.
        sfsDump = TestStripeStoreFileManager.dumpIterator(manager.getCandidateFilesForRowKeyBefore(TestStripeStoreFileManager.KV_B));
        Assert.assertEquals(3, sfsDump.size());
        Assert.assertTrue(sfsDump.contains(stripe1));
        // For KEY_D, we should also get everything.
        sfsDump = TestStripeStoreFileManager.dumpIterator(manager.getCandidateFilesForRowKeyBefore(TestStripeStoreFileManager.KV_D));
        Assert.assertEquals(3, sfsDump.size());
        // Suppose in the first file we found candidate with KEY_C.
        // Then, stripe0 no longer matters and should be removed, but stripe1 should stay.
        sfs = manager.getCandidateFilesForRowKeyBefore(TestStripeStoreFileManager.KV_D);
        sfs.next();// Skip L0 file.

        sfs.remove();
        sfs = manager.updateCandidateFilesForRowKeyBefore(sfs, TestStripeStoreFileManager.KV_D, TestStripeStoreFileManager.KV_C);
        Assert.assertEquals(stripe1, sfs.next());
        Assert.assertFalse(sfs.hasNext());
        // Add one more, later, file to stripe0, remove the last annoying L0 file.
        // This file should be returned in preference to older L0 file; also, after we get
        // a candidate from the first file, the old one should not be removed.
        HStoreFile stripe0b = TestStripeStoreFileManager.createFile(0, 101, StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B);
        manager.addCompactionResults(TestStripeStoreFileManager.al(l0File2), TestStripeStoreFileManager.al(stripe0b));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(l0File2));
        sfs = manager.getCandidateFilesForRowKeyBefore(TestStripeStoreFileManager.KV_A);
        Assert.assertEquals(stripe0b, sfs.next());
        sfs.remove();
        sfs = manager.updateCandidateFilesForRowKeyBefore(sfs, TestStripeStoreFileManager.KV_A, TestStripeStoreFileManager.KV_A);
        Assert.assertEquals(stripe0a, sfs.next());
    }

    @Test
    public void testGetSplitPointEdgeCases() throws Exception {
        StripeStoreFileManager manager = TestStripeStoreFileManager.createManager();
        // No files => no split.
        Assert.assertFalse(manager.getSplitPoint().isPresent());
        // If there are no stripes, should pick midpoint from the biggest file in L0.
        MockHStoreFile sf5 = TestStripeStoreFileManager.createFile(5, 0);
        sf5.splitPoint = new byte[]{ 1 };
        manager.insertNewFiles(TestStripeStoreFileManager.al(sf5));
        manager.insertNewFiles(TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(1, 0)));
        Assert.assertArrayEquals(sf5.splitPoint, manager.getSplitPoint().get());
        // Same if there's one stripe but the biggest file is still in L0.
        manager.addCompactionResults(TestStripeStoreFileManager.al(), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(2, 0, StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY)));
        Assert.assertArrayEquals(sf5.splitPoint, manager.getSplitPoint().get());
        // If the biggest file is in the stripe, should get from it.
        MockHStoreFile sf6 = TestStripeStoreFileManager.createFile(6, 0, StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY);
        sf6.splitPoint = new byte[]{ 2 };
        manager.addCompactionResults(TestStripeStoreFileManager.al(), TestStripeStoreFileManager.al(sf6));
        Assert.assertArrayEquals(sf6.splitPoint, manager.getSplitPoint().get());
    }

    @Test
    public void testGetStripeBoundarySplits() throws Exception {
        /* First number - split must be after this stripe; further numbers - stripes */
        verifySplitPointScenario(5, false, 0.0F, 2, 1, 1, 1, 1, 1, 10);
        verifySplitPointScenario(0, false, 0.0F, 6, 3, 1, 1, 2);
        verifySplitPointScenario(2, false, 0.0F, 1, 1, 1, 1, 2);
        verifySplitPointScenario(0, false, 0.0F, 5, 4);
        verifySplitPointScenario(2, false, 0.0F, 5, 2, 5, 5, 5);
    }

    @Test
    public void testGetUnbalancedSplits() throws Exception {
        /* First number - split must be inside/after this stripe; further numbers - stripes */
        verifySplitPointScenario(0, false, 2.1F, 4, 4, 4);// 8/4 is less than 2.1f

        verifySplitPointScenario(1, true, 1.5F, 4, 4, 4);// 8/4 > 6/6

        verifySplitPointScenario(1, false, 1.1F, 3, 4, 1, 1, 2, 2);// 7/6 < 8/5

        verifySplitPointScenario(1, false, 1.1F, 3, 6, 1, 1, 2, 2);// 9/6 == 9/6

        verifySplitPointScenario(1, true, 1.1F, 3, 8, 1, 1, 2, 2);// 11/6 > 10/7

        verifySplitPointScenario(3, false, 1.1F, 2, 2, 1, 1, 4, 3);// reverse order

        verifySplitPointScenario(4, true, 1.1F, 2, 2, 1, 1, 8, 3);// reverse order

        verifySplitPointScenario(0, true, 1.5F, 10, 4);// 10/4 > 9/5

        verifySplitPointScenario(0, false, 1.4F, 6, 4);// 6/4 == 6/4

        verifySplitPointScenario(1, true, 1.5F, 4, 10);// reverse just in case

        verifySplitPointScenario(0, false, 1.4F, 4, 6);// reverse just in case

    }

    @Test
    public void testGetFilesForGetAndScan() throws Exception {
        StripeStoreFileManager manager = TestStripeStoreFileManager.createManager();
        verifyGetAndScanScenario(manager, null, null);
        verifyGetAndScanScenario(manager, TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C);
        // Populate one L0 file.
        MockHStoreFile sf0 = TestStripeStoreFileManager.createFile();
        manager.insertNewFiles(TestStripeStoreFileManager.al(sf0));
        verifyGetAndScanScenario(manager, null, null, sf0);
        verifyGetAndScanScenario(manager, null, TestStripeStoreFileManager.KEY_C, sf0);
        verifyGetAndScanScenario(manager, TestStripeStoreFileManager.KEY_B, null, sf0);
        verifyGetAndScanScenario(manager, TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C, sf0);
        // Populate a bunch of files for stripes, keep L0.
        MockHStoreFile sfA = TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_A);
        MockHStoreFile sfB = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_A, TestStripeStoreFileManager.KEY_B);
        MockHStoreFile sfC = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C);
        MockHStoreFile sfD = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_C, TestStripeStoreFileManager.KEY_D);
        MockHStoreFile sfE = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_D, StripeStoreFileManager.OPEN_KEY);
        manager.addCompactionResults(TestStripeStoreFileManager.al(), TestStripeStoreFileManager.al(sfA, sfB, sfC, sfD, sfE));
        verifyGetAndScanScenario(manager, null, null, sf0, sfA, sfB, sfC, sfD, sfE);
        verifyGetAndScanScenario(manager, TestStripeStoreFileManager.keyAfter(TestStripeStoreFileManager.KEY_A), null, sf0, sfB, sfC, sfD, sfE);
        verifyGetAndScanScenario(manager, null, TestStripeStoreFileManager.keyAfter(TestStripeStoreFileManager.KEY_C), sf0, sfA, sfB, sfC, sfD);
        verifyGetAndScanScenario(manager, TestStripeStoreFileManager.KEY_B, null, sf0, sfC, sfD, sfE);
        verifyGetAndScanScenario(manager, null, TestStripeStoreFileManager.KEY_C, sf0, sfA, sfB, sfC, sfD);
        verifyGetAndScanScenario(manager, TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.keyAfter(TestStripeStoreFileManager.KEY_B), sf0, sfC);
        verifyGetAndScanScenario(manager, TestStripeStoreFileManager.keyAfter(TestStripeStoreFileManager.KEY_A), TestStripeStoreFileManager.KEY_B, sf0, sfB, sfC);
        verifyGetAndScanScenario(manager, TestStripeStoreFileManager.KEY_D, TestStripeStoreFileManager.KEY_D, sf0, sfE);
        verifyGetAndScanScenario(manager, TestStripeStoreFileManager.keyAfter(TestStripeStoreFileManager.KEY_B), TestStripeStoreFileManager.keyAfter(TestStripeStoreFileManager.KEY_C), sf0, sfC, sfD);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadFilesWithRecoverableBadFiles() throws Exception {
        // In L0, there will be file w/o metadata (real L0, 3 files with invalid metadata, and 3
        // files that overlap valid stripes in various ways). Note that the 4th way to overlap the
        // stripes will cause the structure to be mostly scraped, and is tested separately.
        ArrayList<HStoreFile> validStripeFiles = TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_C, StripeStoreFileManager.OPEN_KEY), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_C, StripeStoreFileManager.OPEN_KEY));
        ArrayList<HStoreFile> filesToGoToL0 = TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(), TestStripeStoreFileManager.createFile(null, TestStripeStoreFileManager.KEY_A), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_D, null), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_D, TestStripeStoreFileManager.KEY_A), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.keyAfter(TestStripeStoreFileManager.KEY_A), TestStripeStoreFileManager.KEY_C), TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_D), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_D, TestStripeStoreFileManager.keyAfter(TestStripeStoreFileManager.KEY_D)));
        ArrayList<HStoreFile> allFilesToGo = TestStripeStoreFileManager.flattenLists(validStripeFiles, filesToGoToL0);
        Collections.shuffle(allFilesToGo);
        StripeStoreFileManager manager = TestStripeStoreFileManager.createManager(allFilesToGo);
        List<HStoreFile> l0Files = manager.getLevel0Files();
        Assert.assertEquals(filesToGoToL0.size(), l0Files.size());
        for (HStoreFile sf : filesToGoToL0) {
            Assert.assertTrue(l0Files.contains(sf));
        }
        verifyAllFiles(manager, allFilesToGo);
    }

    @Test
    public void testLoadFilesWithBadStripe() throws Exception {
        // Current "algorithm" will see the after-B key before C key, add it as valid stripe,
        // and then fail all other stripes. So everything would end up in L0.
        ArrayList<HStoreFile> allFilesToGo = TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_C, StripeStoreFileManager.OPEN_KEY), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.keyAfter(TestStripeStoreFileManager.KEY_B)));
        Collections.shuffle(allFilesToGo);
        StripeStoreFileManager manager = TestStripeStoreFileManager.createManager(allFilesToGo);
        Assert.assertEquals(allFilesToGo.size(), manager.getLevel0Files().size());
    }

    @Test
    public void testLoadFilesWithGaps() throws Exception {
        // Stripes must not have gaps. If they do, everything goes to L0.
        StripeStoreFileManager manager = TestStripeStoreFileManager.createManager(TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_C, StripeStoreFileManager.OPEN_KEY)));
        Assert.assertEquals(2, manager.getLevel0Files().size());
        // Just one open stripe should be ok.
        manager = TestStripeStoreFileManager.createManager(TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY)));
        Assert.assertEquals(0, manager.getLevel0Files().size());
        Assert.assertEquals(1, manager.getStorefileCount());
    }

    @Test
    public void testLoadFilesAfterSplit() throws Exception {
        // If stripes are good but have non-open ends, they must be treated as open ends.
        MockHStoreFile sf = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C);
        StripeStoreFileManager manager = TestStripeStoreFileManager.createManager(TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B), sf));
        Assert.assertEquals(0, manager.getLevel0Files().size());
        // Here, [B, C] is logically [B, inf), so we should be able to compact it to that only.
        verifyInvalidCompactionScenario(manager, TestStripeStoreFileManager.al(sf), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C)));
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, StripeStoreFileManager.OPEN_KEY)));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf));
        // Do the same for other variants.
        manager = TestStripeStoreFileManager.createManager(TestStripeStoreFileManager.al(sf, TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_C, StripeStoreFileManager.OPEN_KEY)));
        verifyInvalidCompactionScenario(manager, TestStripeStoreFileManager.al(sf), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C)));
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_C)));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf));
        manager = TestStripeStoreFileManager.createManager(TestStripeStoreFileManager.al(sf));
        verifyInvalidCompactionScenario(manager, TestStripeStoreFileManager.al(sf), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C)));
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY)));
    }

    @Test
    public void testAddingCompactionResults() throws Exception {
        StripeStoreFileManager manager = TestStripeStoreFileManager.createManager();
        // First, add some L0 files and "compact" one with new stripe creation.
        HStoreFile sf_L0_0a = TestStripeStoreFileManager.createFile();
        HStoreFile sf_L0_0b = TestStripeStoreFileManager.createFile();
        manager.insertNewFiles(TestStripeStoreFileManager.al(sf_L0_0a, sf_L0_0b));
        // Try compacting with invalid new branches (gaps, overlaps) - no effect.
        verifyInvalidCompactionScenario(manager, TestStripeStoreFileManager.al(sf_L0_0a), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B)));
        verifyInvalidCompactionScenario(manager, TestStripeStoreFileManager.al(sf_L0_0a), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_C, StripeStoreFileManager.OPEN_KEY)));
        verifyInvalidCompactionScenario(manager, TestStripeStoreFileManager.al(sf_L0_0a), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, StripeStoreFileManager.OPEN_KEY), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_A, TestStripeStoreFileManager.KEY_D)));
        verifyInvalidCompactionScenario(manager, TestStripeStoreFileManager.al(sf_L0_0a), TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_A, TestStripeStoreFileManager.KEY_B), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, StripeStoreFileManager.OPEN_KEY)));
        HStoreFile sf_i2B_0 = TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B);
        HStoreFile sf_B2C_0 = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C);
        HStoreFile sf_C2i_0 = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_C, StripeStoreFileManager.OPEN_KEY);
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf_L0_0a), TestStripeStoreFileManager.al(sf_i2B_0, sf_B2C_0, sf_C2i_0));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf_L0_0a));
        verifyAllFiles(manager, TestStripeStoreFileManager.al(sf_L0_0b, sf_i2B_0, sf_B2C_0, sf_C2i_0));
        // Add another l0 file, "compact" both L0 into two stripes
        HStoreFile sf_L0_1 = TestStripeStoreFileManager.createFile();
        HStoreFile sf_i2B_1 = TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B);
        HStoreFile sf_B2C_1 = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C);
        manager.insertNewFiles(TestStripeStoreFileManager.al(sf_L0_1));
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf_L0_0b, sf_L0_1), TestStripeStoreFileManager.al(sf_i2B_1, sf_B2C_1));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf_L0_0b, sf_L0_1));
        verifyAllFiles(manager, TestStripeStoreFileManager.al(sf_i2B_0, sf_B2C_0, sf_C2i_0, sf_i2B_1, sf_B2C_1));
        // Try compacting with invalid file (no metadata) - should add files to L0.
        HStoreFile sf_L0_2 = TestStripeStoreFileManager.createFile(null, null);
        manager.addCompactionResults(TestStripeStoreFileManager.al(), TestStripeStoreFileManager.al(sf_L0_2));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al());
        verifyAllFiles(manager, TestStripeStoreFileManager.al(sf_i2B_0, sf_B2C_0, sf_C2i_0, sf_i2B_1, sf_B2C_1, sf_L0_2));
        // Remove it...
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf_L0_2), TestStripeStoreFileManager.al());
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf_L0_2));
        // Do regular compaction in the first stripe.
        HStoreFile sf_i2B_3 = TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B);
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf_i2B_0, sf_i2B_1), TestStripeStoreFileManager.al(sf_i2B_3));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf_i2B_0, sf_i2B_1));
        verifyAllFiles(manager, TestStripeStoreFileManager.al(sf_B2C_0, sf_C2i_0, sf_B2C_1, sf_i2B_3));
        // Rebalance two stripes.
        HStoreFile sf_B2D_4 = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_D);
        HStoreFile sf_D2i_4 = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_D, StripeStoreFileManager.OPEN_KEY);
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf_B2C_0, sf_C2i_0, sf_B2C_1), TestStripeStoreFileManager.al(sf_B2D_4, sf_D2i_4));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf_B2C_0, sf_C2i_0, sf_B2C_1));
        verifyAllFiles(manager, TestStripeStoreFileManager.al(sf_i2B_3, sf_B2D_4, sf_D2i_4));
        // Split the first stripe.
        HStoreFile sf_i2A_5 = TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_A);
        HStoreFile sf_A2B_5 = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_A, TestStripeStoreFileManager.KEY_B);
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf_i2B_3), TestStripeStoreFileManager.al(sf_i2A_5, sf_A2B_5));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf_i2B_3));
        verifyAllFiles(manager, TestStripeStoreFileManager.al(sf_B2D_4, sf_D2i_4, sf_i2A_5, sf_A2B_5));
        // Split the middle stripe.
        HStoreFile sf_B2C_6 = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C);
        HStoreFile sf_C2D_6 = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_C, TestStripeStoreFileManager.KEY_D);
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf_B2D_4), TestStripeStoreFileManager.al(sf_B2C_6, sf_C2D_6));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf_B2D_4));
        verifyAllFiles(manager, TestStripeStoreFileManager.al(sf_D2i_4, sf_i2A_5, sf_A2B_5, sf_B2C_6, sf_C2D_6));
        // Merge two different middle stripes.
        HStoreFile sf_A2C_7 = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_A, TestStripeStoreFileManager.KEY_C);
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf_A2B_5, sf_B2C_6), TestStripeStoreFileManager.al(sf_A2C_7));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf_A2B_5, sf_B2C_6));
        verifyAllFiles(manager, TestStripeStoreFileManager.al(sf_D2i_4, sf_i2A_5, sf_C2D_6, sf_A2C_7));
        // Merge lower half.
        HStoreFile sf_i2C_8 = TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_C);
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf_i2A_5, sf_A2C_7), TestStripeStoreFileManager.al(sf_i2C_8));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf_i2A_5, sf_A2C_7));
        verifyAllFiles(manager, TestStripeStoreFileManager.al(sf_D2i_4, sf_C2D_6, sf_i2C_8));
        // Merge all.
        HStoreFile sf_i2i_9 = TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY);
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf_D2i_4, sf_C2D_6, sf_i2C_8), TestStripeStoreFileManager.al(sf_i2i_9));
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf_D2i_4, sf_C2D_6, sf_i2C_8));
        verifyAllFiles(manager, TestStripeStoreFileManager.al(sf_i2i_9));
    }

    @Test
    public void testCompactionAndFlushConflict() throws Exception {
        // Add file flush into stripes
        StripeStoreFileManager sfm = TestStripeStoreFileManager.createManager();
        Assert.assertEquals(0, sfm.getStripeCount());
        HStoreFile sf_i2c = TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_C);
        HStoreFile sf_c2i = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_C, StripeStoreFileManager.OPEN_KEY);
        sfm.insertNewFiles(TestStripeStoreFileManager.al(sf_i2c, sf_c2i));
        Assert.assertEquals(2, sfm.getStripeCount());
        // Now try to add conflicting flush - should throw.
        HStoreFile sf_i2d = TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_D);
        HStoreFile sf_d2i = TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_D, StripeStoreFileManager.OPEN_KEY);
        sfm.insertNewFiles(TestStripeStoreFileManager.al(sf_i2d, sf_d2i));
        Assert.assertEquals(2, sfm.getStripeCount());
        Assert.assertEquals(2, sfm.getLevel0Files().size());
        verifyGetAndScanScenario(sfm, TestStripeStoreFileManager.KEY_C, TestStripeStoreFileManager.KEY_C, sf_i2d, sf_d2i, sf_c2i);
        // Remove these files.
        sfm.addCompactionResults(TestStripeStoreFileManager.al(sf_i2d, sf_d2i), TestStripeStoreFileManager.al());
        sfm.removeCompactedFiles(TestStripeStoreFileManager.al(sf_i2d, sf_d2i));
        Assert.assertEquals(0, sfm.getLevel0Files().size());
        // Add another file to stripe; then "rebalance" stripes w/o it - the file, which was
        // presumably flushed during compaction, should go to L0.
        HStoreFile sf_i2c_2 = TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_C);
        sfm.insertNewFiles(TestStripeStoreFileManager.al(sf_i2c_2));
        sfm.addCompactionResults(TestStripeStoreFileManager.al(sf_i2c, sf_c2i), TestStripeStoreFileManager.al(sf_i2d, sf_d2i));
        sfm.removeCompactedFiles(TestStripeStoreFileManager.al(sf_i2c, sf_c2i));
        Assert.assertEquals(1, sfm.getLevel0Files().size());
        verifyGetAndScanScenario(sfm, TestStripeStoreFileManager.KEY_C, TestStripeStoreFileManager.KEY_C, sf_i2d, sf_i2c_2);
    }

    @Test
    public void testEmptyResultsForStripes() throws Exception {
        // Test that we can compact L0 into a subset of stripes.
        StripeStoreFileManager manager = TestStripeStoreFileManager.createManager();
        HStoreFile sf0a = TestStripeStoreFileManager.createFile();
        HStoreFile sf0b = TestStripeStoreFileManager.createFile();
        manager.insertNewFiles(TestStripeStoreFileManager.al(sf0a));
        manager.insertNewFiles(TestStripeStoreFileManager.al(sf0b));
        ArrayList<HStoreFile> compacted = TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_B, TestStripeStoreFileManager.KEY_C), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_C, StripeStoreFileManager.OPEN_KEY));
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf0a), compacted);
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf0a));
        // Next L0 compaction only produces file for the first and last stripe.
        ArrayList<HStoreFile> compacted2 = TestStripeStoreFileManager.al(TestStripeStoreFileManager.createFile(StripeStoreFileManager.OPEN_KEY, TestStripeStoreFileManager.KEY_B), TestStripeStoreFileManager.createFile(TestStripeStoreFileManager.KEY_C, StripeStoreFileManager.OPEN_KEY));
        manager.addCompactionResults(TestStripeStoreFileManager.al(sf0b), compacted2);
        manager.removeCompactedFiles(TestStripeStoreFileManager.al(sf0b));
        compacted.addAll(compacted2);
        verifyAllFiles(manager, compacted);
    }

    @Test
    public void testPriority() throws Exception {
        // Expected priority, file limit, stripe count, files per stripe, l0 files.
        testPriorityScenario(5, 5, 0, 0, 0);
        testPriorityScenario(2, 5, 0, 0, 3);
        testPriorityScenario(4, 25, 5, 1, 0);// example case.

        testPriorityScenario(3, 25, 5, 1, 1);// L0 files counts for all stripes.

        testPriorityScenario(3, 25, 5, 2, 0);// file to each stripe - same as one L0 file.

        testPriorityScenario(2, 25, 5, 4, 0);// 1 is priority user, so 2 is returned.

        testPriorityScenario(2, 25, 5, 4, 4);// don't return higher than user unless over limit.

        testPriorityScenario(2, 25, 5, 1, 10);// same.

        testPriorityScenario(0, 25, 5, 4, 5);// at limit.

        testPriorityScenario((-5), 25, 5, 6, 0);// over limit!

        testPriorityScenario((-1), 25, 0, 0, 26);// over limit with just L0

    }
}

