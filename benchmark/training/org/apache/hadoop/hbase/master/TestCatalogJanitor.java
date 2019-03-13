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
package org.apache.hadoop.hbase.master;


import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.io.Reference;
import org.apache.hadoop.hbase.master.CatalogJanitor.SplitParentFirstComparator;
import org.apache.hadoop.hbase.master.assignment.MockMasterServices;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.regionserver.HStore;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.HFileArchiveTestingUtil;
import org.apache.hadoop.hbase.util.HFileArchiveUtil;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, SmallTests.class })
public class TestCatalogJanitor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCatalogJanitor.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCatalogJanitor.class);

    @Rule
    public final TestName name = new TestName();

    private static final HBaseTestingUtility HTU = new HBaseTestingUtility();

    private MockMasterServices masterServices;

    private CatalogJanitor janitor;

    /**
     * Test clearing a split parent.
     */
    @Test
    public void testCleanParent() throws IOException, InterruptedException {
        TableDescriptor td = createTableDescriptorForCurrentMethod();
        // Create regions.
        HRegionInfo parent = new HRegionInfo(td.getTableName(), Bytes.toBytes("aaa"), Bytes.toBytes("eee"));
        HRegionInfo splita = new HRegionInfo(td.getTableName(), Bytes.toBytes("aaa"), Bytes.toBytes("ccc"));
        HRegionInfo splitb = new HRegionInfo(td.getTableName(), Bytes.toBytes("ccc"), Bytes.toBytes("eee"));
        // Test that when both daughter regions are in place, that we do not remove the parent.
        Result r = createResult(parent, splita, splitb);
        // Add a reference under splitA directory so we don't clear out the parent.
        Path rootdir = this.masterServices.getMasterFileSystem().getRootDir();
        Path tabledir = FSUtils.getTableDir(rootdir, td.getTableName());
        Path parentdir = new Path(tabledir, parent.getEncodedName());
        Path storedir = HStore.getStoreHomedir(tabledir, splita, td.getColumnFamilies()[0].getName());
        Reference ref = Reference.createTopReference(Bytes.toBytes("ccc"));
        long now = System.currentTimeMillis();
        // Reference name has this format: StoreFile#REF_NAME_PARSER
        Path p = new Path(storedir, (((Long.toString(now)) + ".") + (parent.getEncodedName())));
        FileSystem fs = this.masterServices.getMasterFileSystem().getFileSystem();
        Path path = ref.write(fs, p);
        Assert.assertTrue(fs.exists(path));
        TestCatalogJanitor.LOG.info(("Created reference " + path));
        // Add a parentdir for kicks so can check it gets removed by the catalogjanitor.
        fs.mkdirs(parentdir);
        Assert.assertFalse(this.janitor.cleanParent(parent, r));
        ProcedureTestingUtility.waitAllProcedures(masterServices.getMasterProcedureExecutor());
        Assert.assertTrue(fs.exists(parentdir));
        // Remove the reference file and try again.
        Assert.assertTrue(fs.delete(p, true));
        Assert.assertTrue(this.janitor.cleanParent(parent, r));
        // Parent cleanup is run async as a procedure. Make sure parentdir is removed.
        ProcedureTestingUtility.waitAllProcedures(masterServices.getMasterProcedureExecutor());
        Assert.assertTrue((!(fs.exists(parentdir))));
    }

    /**
     * Make sure parent gets cleaned up even if daughter is cleaned up before it.
     */
    @Test
    public void testParentCleanedEvenIfDaughterGoneFirst() throws IOException, InterruptedException {
        parentWithSpecifiedEndKeyCleanedEvenIfDaughterGoneFirst(this.name.getMethodName(), Bytes.toBytes("eee"));
    }

    /**
     * Make sure last parent with empty end key gets cleaned up even if daughter is cleaned up before it.
     */
    @Test
    public void testLastParentCleanedEvenIfDaughterGoneFirst() throws IOException, InterruptedException {
        parentWithSpecifiedEndKeyCleanedEvenIfDaughterGoneFirst(this.name.getMethodName(), new byte[0]);
    }

    /**
     * CatalogJanitor.scan() should not clean parent regions if their own
     * parents are still referencing them. This ensures that grandparent regions
     * do not point to deleted parent regions.
     */
    @Test
    public void testScanDoesNotCleanRegionsWithExistingParents() throws Exception {
        TableDescriptor td = createTableDescriptorForCurrentMethod();
        // Create regions: aaa->{lastEndKey}, aaa->ccc, aaa->bbb, bbb->ccc, etc.
        // Parent
        HRegionInfo parent = new HRegionInfo(td.getTableName(), Bytes.toBytes("aaa"), HConstants.EMPTY_BYTE_ARRAY, true);
        // Sleep a second else the encoded name on these regions comes out
        // same for all with same start key and made in same second.
        Thread.sleep(1001);
        // Daughter a
        HRegionInfo splita = new HRegionInfo(td.getTableName(), Bytes.toBytes("aaa"), Bytes.toBytes("ccc"), true);
        Thread.sleep(1001);
        // Make daughters of daughter a; splitaa and splitab.
        HRegionInfo splitaa = new HRegionInfo(td.getTableName(), Bytes.toBytes("aaa"), Bytes.toBytes("bbb"), false);
        HRegionInfo splitab = new HRegionInfo(td.getTableName(), Bytes.toBytes("bbb"), Bytes.toBytes("ccc"), false);
        // Daughter b
        HRegionInfo splitb = new HRegionInfo(td.getTableName(), Bytes.toBytes("ccc"), HConstants.EMPTY_BYTE_ARRAY);
        Thread.sleep(1001);
        // Parent has daughters splita and splitb. Splita has daughters splitaa and splitab.
        final Map<HRegionInfo, Result> splitParents = new TreeMap(new SplitParentFirstComparator());
        splitParents.put(parent, createResult(parent, splita, splitb));
        splita.setOffline(true);// simulate that splita goes offline when it is split

        splitParents.put(splita, createResult(splita, splitaa, splitab));
        final Map<HRegionInfo, Result> mergedRegions = new TreeMap<>();
        CatalogJanitor spy = Mockito.spy(this.janitor);
        Mockito.doReturn(new org.apache.hadoop.hbase.util.Triple(10, mergedRegions, splitParents)).when(spy).getMergedRegionsAndSplitParents();
        // Create ref from splita to parent
        TestCatalogJanitor.LOG.info(((("parent=" + (parent.getShortNameToLog())) + ", splita=") + (splita.getShortNameToLog())));
        Path splitaRef = createReferences(this.masterServices, td, parent, splita, Bytes.toBytes("ccc"), false);
        TestCatalogJanitor.LOG.info(("Created reference " + splitaRef));
        // Parent and splita should not be removed because a reference from splita to parent.
        Assert.assertEquals(0, spy.scan());
        // Now delete the ref
        FileSystem fs = FileSystem.get(TestCatalogJanitor.HTU.getConfiguration());
        Assert.assertTrue(fs.delete(splitaRef, true));
        // now, both parent, and splita can be deleted
        Assert.assertEquals(2, spy.scan());
    }

    /**
     * Test that we correctly archive all the storefiles when a region is deleted
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSplitParentFirstComparator() {
        SplitParentFirstComparator comp = new SplitParentFirstComparator();
        TableDescriptor td = createTableDescriptorForCurrentMethod();
        /* Region splits:

         rootRegion --- firstRegion --- firstRegiona
                     |               |- firstRegionb
                     |
                     |- lastRegion --- lastRegiona  --- lastRegionaa
                                    |                |- lastRegionab
                                    |- lastRegionb

         rootRegion   :   []  - []
         firstRegion  :   []  - bbb
         lastRegion   :   bbb - []
         firstRegiona :   []  - aaa
         firstRegionb :   aaa - bbb
         lastRegiona  :   bbb - ddd
         lastRegionb  :   ddd - []
         */
        // root region
        HRegionInfo rootRegion = new HRegionInfo(td.getTableName(), HConstants.EMPTY_START_ROW, HConstants.EMPTY_END_ROW, true);
        HRegionInfo firstRegion = new HRegionInfo(td.getTableName(), HConstants.EMPTY_START_ROW, Bytes.toBytes("bbb"), true);
        HRegionInfo lastRegion = new HRegionInfo(td.getTableName(), Bytes.toBytes("bbb"), HConstants.EMPTY_END_ROW, true);
        Assert.assertTrue(((comp.compare(rootRegion, rootRegion)) == 0));
        Assert.assertTrue(((comp.compare(firstRegion, firstRegion)) == 0));
        Assert.assertTrue(((comp.compare(lastRegion, lastRegion)) == 0));
        Assert.assertTrue(((comp.compare(rootRegion, firstRegion)) < 0));
        Assert.assertTrue(((comp.compare(rootRegion, lastRegion)) < 0));
        Assert.assertTrue(((comp.compare(firstRegion, lastRegion)) < 0));
        // first region split into a, b
        HRegionInfo firstRegiona = new HRegionInfo(td.getTableName(), HConstants.EMPTY_START_ROW, Bytes.toBytes("aaa"), true);
        HRegionInfo firstRegionb = new HRegionInfo(td.getTableName(), Bytes.toBytes("aaa"), Bytes.toBytes("bbb"), true);
        // last region split into a, b
        HRegionInfo lastRegiona = new HRegionInfo(td.getTableName(), Bytes.toBytes("bbb"), Bytes.toBytes("ddd"), true);
        HRegionInfo lastRegionb = new HRegionInfo(td.getTableName(), Bytes.toBytes("ddd"), HConstants.EMPTY_END_ROW, true);
        Assert.assertTrue(((comp.compare(firstRegiona, firstRegiona)) == 0));
        Assert.assertTrue(((comp.compare(firstRegionb, firstRegionb)) == 0));
        Assert.assertTrue(((comp.compare(rootRegion, firstRegiona)) < 0));
        Assert.assertTrue(((comp.compare(rootRegion, firstRegionb)) < 0));
        Assert.assertTrue(((comp.compare(firstRegion, firstRegiona)) < 0));
        Assert.assertTrue(((comp.compare(firstRegion, firstRegionb)) < 0));
        Assert.assertTrue(((comp.compare(firstRegiona, firstRegionb)) < 0));
        Assert.assertTrue(((comp.compare(lastRegiona, lastRegiona)) == 0));
        Assert.assertTrue(((comp.compare(lastRegionb, lastRegionb)) == 0));
        Assert.assertTrue(((comp.compare(rootRegion, lastRegiona)) < 0));
        Assert.assertTrue(((comp.compare(rootRegion, lastRegionb)) < 0));
        Assert.assertTrue(((comp.compare(lastRegion, lastRegiona)) < 0));
        Assert.assertTrue(((comp.compare(lastRegion, lastRegionb)) < 0));
        Assert.assertTrue(((comp.compare(lastRegiona, lastRegionb)) < 0));
        Assert.assertTrue(((comp.compare(firstRegiona, lastRegiona)) < 0));
        Assert.assertTrue(((comp.compare(firstRegiona, lastRegionb)) < 0));
        Assert.assertTrue(((comp.compare(firstRegionb, lastRegiona)) < 0));
        Assert.assertTrue(((comp.compare(firstRegionb, lastRegionb)) < 0));
        HRegionInfo lastRegionaa = new HRegionInfo(td.getTableName(), Bytes.toBytes("bbb"), Bytes.toBytes("ccc"), false);
        HRegionInfo lastRegionab = new HRegionInfo(td.getTableName(), Bytes.toBytes("ccc"), Bytes.toBytes("ddd"), false);
        Assert.assertTrue(((comp.compare(lastRegiona, lastRegionaa)) < 0));
        Assert.assertTrue(((comp.compare(lastRegiona, lastRegionab)) < 0));
        Assert.assertTrue(((comp.compare(lastRegionaa, lastRegionab)) < 0));
    }

    @Test
    public void testArchiveOldRegion() throws Exception {
        // Create regions.
        TableDescriptor td = createTableDescriptorForCurrentMethod();
        HRegionInfo parent = new HRegionInfo(td.getTableName(), Bytes.toBytes("aaa"), Bytes.toBytes("eee"));
        HRegionInfo splita = new HRegionInfo(td.getTableName(), Bytes.toBytes("aaa"), Bytes.toBytes("ccc"));
        HRegionInfo splitb = new HRegionInfo(td.getTableName(), Bytes.toBytes("ccc"), Bytes.toBytes("eee"));
        // Test that when both daughter regions are in place, that we do not
        // remove the parent.
        Result parentMetaRow = createResult(parent, splita, splitb);
        FileSystem fs = FileSystem.get(TestCatalogJanitor.HTU.getConfiguration());
        Path rootdir = this.masterServices.getMasterFileSystem().getRootDir();
        // have to set the root directory since we use it in HFileDisposer to figure out to get to the
        // archive directory. Otherwise, it just seems to pick the first root directory it can find (so
        // the single test passes, but when the full suite is run, things get borked).
        FSUtils.setRootDir(fs.getConf(), rootdir);
        Path tabledir = FSUtils.getTableDir(rootdir, td.getTableName());
        Path storedir = HStore.getStoreHomedir(tabledir, parent, td.getColumnFamilies()[0].getName());
        Path storeArchive = HFileArchiveUtil.getStoreArchivePath(this.masterServices.getConfiguration(), parent, tabledir, td.getColumnFamilies()[0].getName());
        TestCatalogJanitor.LOG.debug(("Table dir:" + tabledir));
        TestCatalogJanitor.LOG.debug(("Store dir:" + storedir));
        TestCatalogJanitor.LOG.debug(("Store archive dir:" + storeArchive));
        // add a couple of store files that we can check for
        FileStatus[] mockFiles = addMockStoreFiles(2, this.masterServices, storedir);
        // get the current store files for comparison
        FileStatus[] storeFiles = fs.listStatus(storedir);
        int index = 0;
        for (FileStatus file : storeFiles) {
            TestCatalogJanitor.LOG.debug(("Have store file:" + (file.getPath())));
            Assert.assertEquals("Got unexpected store file", mockFiles[index].getPath(), storeFiles[index].getPath());
            index++;
        }
        // do the cleaning of the parent
        Assert.assertTrue(janitor.cleanParent(parent, parentMetaRow));
        Path parentDir = new Path(tabledir, parent.getEncodedName());
        // Cleanup procedure runs async. Wait till it done.
        ProcedureTestingUtility.waitAllProcedures(masterServices.getMasterProcedureExecutor());
        Assert.assertTrue((!(fs.exists(parentDir))));
        TestCatalogJanitor.LOG.debug("Finished cleanup of parent region");
        // and now check to make sure that the files have actually been archived
        FileStatus[] archivedStoreFiles = fs.listStatus(storeArchive);
        logFiles("archived files", storeFiles);
        logFiles("archived files", archivedStoreFiles);
        HFileArchiveTestingUtil.assertArchiveEqualToOriginal(storeFiles, archivedStoreFiles, fs);
        // cleanup
        FSUtils.delete(fs, rootdir, true);
    }

    /**
     * Test that if a store file with the same name is present as those already backed up cause the
     * already archived files to be timestamped backup
     */
    @Test
    public void testDuplicateHFileResolution() throws Exception {
        TableDescriptor td = createTableDescriptorForCurrentMethod();
        // Create regions.
        HRegionInfo parent = new HRegionInfo(td.getTableName(), Bytes.toBytes("aaa"), Bytes.toBytes("eee"));
        HRegionInfo splita = new HRegionInfo(td.getTableName(), Bytes.toBytes("aaa"), Bytes.toBytes("ccc"));
        HRegionInfo splitb = new HRegionInfo(td.getTableName(), Bytes.toBytes("ccc"), Bytes.toBytes("eee"));
        // Test that when both daughter regions are in place, that we do not
        // remove the parent.
        Result r = createResult(parent, splita, splitb);
        FileSystem fs = FileSystem.get(TestCatalogJanitor.HTU.getConfiguration());
        Path rootdir = this.masterServices.getMasterFileSystem().getRootDir();
        // Have to set the root directory since we use it in HFileDisposer to figure out to get to the
        // archive directory. Otherwise, it just seems to pick the first root directory it can find (so
        // the single test passes, but when the full suite is run, things get borked).
        FSUtils.setRootDir(fs.getConf(), rootdir);
        Path tabledir = FSUtils.getTableDir(rootdir, parent.getTable());
        Path storedir = HStore.getStoreHomedir(tabledir, parent, td.getColumnFamilies()[0].getName());
        System.out.println(("Old root:" + rootdir));
        System.out.println(("Old table:" + tabledir));
        System.out.println(("Old store:" + storedir));
        Path storeArchive = HFileArchiveUtil.getStoreArchivePath(this.masterServices.getConfiguration(), parent, tabledir, td.getColumnFamilies()[0].getName());
        System.out.println(("Old archive:" + storeArchive));
        // enable archiving, make sure that files get archived
        addMockStoreFiles(2, this.masterServices, storedir);
        // get the current store files for comparison
        FileStatus[] storeFiles = fs.listStatus(storedir);
        // Do the cleaning of the parent
        Assert.assertTrue(janitor.cleanParent(parent, r));
        Path parentDir = new Path(tabledir, parent.getEncodedName());
        ProcedureTestingUtility.waitAllProcedures(masterServices.getMasterProcedureExecutor());
        Assert.assertTrue((!(fs.exists(parentDir))));
        // And now check to make sure that the files have actually been archived
        FileStatus[] archivedStoreFiles = fs.listStatus(storeArchive);
        HFileArchiveTestingUtil.assertArchiveEqualToOriginal(storeFiles, archivedStoreFiles, fs);
        // now add store files with the same names as before to check backup
        // enable archiving, make sure that files get archived
        addMockStoreFiles(2, this.masterServices, storedir);
        // Do the cleaning of the parent
        Assert.assertTrue(janitor.cleanParent(parent, r));
        // Cleanup procedure runs async. Wait till it done.
        ProcedureTestingUtility.waitAllProcedures(masterServices.getMasterProcedureExecutor());
        Assert.assertTrue((!(fs.exists(parentDir))));
        // and now check to make sure that the files have actually been archived
        archivedStoreFiles = fs.listStatus(storeArchive);
        HFileArchiveTestingUtil.assertArchiveEqualToOriginal(storeFiles, archivedStoreFiles, fs, true);
    }
}

