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
package org.apache.hadoop.hbase.backup;


import HConstants.RECOVERED_EDITS_DIR;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.hbase.ChoreService;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.Stoppable;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.master.cleaner.HFileCleaner;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.HFileArchiveTestingUtil;
import org.apache.hadoop.hbase.util.StoppableImplementation;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that the {@link HFileArchiver} correctly removes all the parts of a region when cleaning up
 * a region
 */
@Category({ MediumTests.class, MiscTests.class })
public class TestHFileArchiving {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHFileArchiving.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHFileArchiving.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final byte[] TEST_FAM = Bytes.toBytes("fam");

    @Rule
    public TestName name = new TestName();

    @Test
    public void testRemoveRegionDirOnArchive() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestHFileArchiving.UTIL.createTable(tableName, TestHFileArchiving.TEST_FAM);
        final Admin admin = TestHFileArchiving.UTIL.getAdmin();
        // get the current store files for the region
        List<HRegion> servingRegions = TestHFileArchiving.UTIL.getHBaseCluster().getRegions(tableName);
        // make sure we only have 1 region serving this table
        Assert.assertEquals(1, servingRegions.size());
        HRegion region = servingRegions.get(0);
        // and load the table
        TestHFileArchiving.UTIL.loadRegion(region, TestHFileArchiving.TEST_FAM);
        // shutdown the table so we can manipulate the files
        admin.disableTable(tableName);
        FileSystem fs = TestHFileArchiving.UTIL.getTestFileSystem();
        // now attempt to depose the region
        Path rootDir = region.getRegionFileSystem().getTableDir().getParent();
        Path regionDir = HRegion.getRegionDir(rootDir, region.getRegionInfo());
        HFileArchiver.archiveRegion(TestHFileArchiving.UTIL.getConfiguration(), fs, region.getRegionInfo());
        // check for the existence of the archive directory and some files in it
        Path archiveDir = HFileArchiveTestingUtil.getRegionArchiveDir(TestHFileArchiving.UTIL.getConfiguration(), region);
        Assert.assertTrue(fs.exists(archiveDir));
        // check to make sure the store directory was copied
        FileStatus[] stores = fs.listStatus(archiveDir, new PathFilter() {
            @Override
            public boolean accept(Path p) {
                if (p.getName().contains(RECOVERED_EDITS_DIR)) {
                    return false;
                }
                return true;
            }
        });
        Assert.assertTrue(((stores.length) == 1));
        // make sure we archived the store files
        FileStatus[] storeFiles = fs.listStatus(stores[0].getPath());
        Assert.assertTrue(((storeFiles.length) > 0));
        // then ensure the region's directory isn't present
        Assert.assertFalse(fs.exists(regionDir));
        TestHFileArchiving.UTIL.deleteTable(tableName);
    }

    /**
     * Test that the region directory is removed when we archive a region without store files, but
     * still has hidden files.
     *
     * @throws IOException
     * 		throws an IOException if there's problem creating a table
     * 		or if there's an issue with accessing FileSystem.
     */
    @Test
    public void testDeleteRegionWithNoStoreFiles() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestHFileArchiving.UTIL.createTable(tableName, TestHFileArchiving.TEST_FAM);
        // get the current store files for the region
        List<HRegion> servingRegions = TestHFileArchiving.UTIL.getHBaseCluster().getRegions(tableName);
        // make sure we only have 1 region serving this table
        Assert.assertEquals(1, servingRegions.size());
        HRegion region = servingRegions.get(0);
        FileSystem fs = region.getRegionFileSystem().getFileSystem();
        // make sure there are some files in the regiondir
        Path rootDir = FSUtils.getRootDir(fs.getConf());
        Path regionDir = HRegion.getRegionDir(rootDir, region.getRegionInfo());
        FileStatus[] regionFiles = FSUtils.listStatus(fs, regionDir, null);
        Assert.assertNotNull("No files in the region directory", regionFiles);
        if (TestHFileArchiving.LOG.isDebugEnabled()) {
            List<Path> files = new ArrayList<>();
            for (FileStatus file : regionFiles) {
                files.add(file.getPath());
            }
            TestHFileArchiving.LOG.debug(("Current files:" + files));
        }
        // delete the visible folders so we just have hidden files/folders
        final PathFilter dirFilter = new FSUtils.DirFilter(fs);
        PathFilter nonHidden = new PathFilter() {
            @Override
            public boolean accept(Path file) {
                return (dirFilter.accept(file)) && (!(file.getName().startsWith(".")));
            }
        };
        FileStatus[] storeDirs = FSUtils.listStatus(fs, regionDir, nonHidden);
        for (FileStatus store : storeDirs) {
            TestHFileArchiving.LOG.debug("Deleting store for test");
            fs.delete(store.getPath(), true);
        }
        // then archive the region
        HFileArchiver.archiveRegion(TestHFileArchiving.UTIL.getConfiguration(), fs, region.getRegionInfo());
        // and check to make sure the region directoy got deleted
        Assert.assertFalse((("Region directory (" + regionDir) + "), still exists."), fs.exists(regionDir));
        TestHFileArchiving.UTIL.deleteTable(tableName);
    }

    @Test
    public void testArchiveRegions() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        List<HRegion> regions = initTableForArchivingRegions(tableName);
        FileSystem fs = TestHFileArchiving.UTIL.getTestFileSystem();
        // now attempt to depose the regions
        Path rootDir = FSUtils.getRootDir(TestHFileArchiving.UTIL.getConfiguration());
        Path tableDir = FSUtils.getTableDir(rootDir, regions.get(0).getRegionInfo().getTable());
        List<Path> regionDirList = regions.stream().map(( region) -> FSUtils.getRegionDir(tableDir, region.getRegionInfo())).collect(Collectors.toList());
        HFileArchiver.archiveRegions(TestHFileArchiving.UTIL.getConfiguration(), fs, rootDir, tableDir, regionDirList);
        // check for the existence of the archive directory and some files in it
        for (HRegion region : regions) {
            Path archiveDir = HFileArchiveTestingUtil.getRegionArchiveDir(TestHFileArchiving.UTIL.getConfiguration(), region);
            Assert.assertTrue(fs.exists(archiveDir));
            // check to make sure the store directory was copied
            FileStatus[] stores = fs.listStatus(archiveDir, ( p) -> !(p.getName().contains(HConstants.RECOVERED_EDITS_DIR)));
            Assert.assertTrue(((stores.length) == 1));
            // make sure we archived the store files
            FileStatus[] storeFiles = fs.listStatus(stores[0].getPath());
            Assert.assertTrue(((storeFiles.length) > 0));
        }
        // then ensure the region's directories aren't present
        for (Path regionDir : regionDirList) {
            Assert.assertFalse(fs.exists(regionDir));
        }
        TestHFileArchiving.UTIL.deleteTable(tableName);
    }

    @Test(expected = IOException.class)
    public void testArchiveRegionsWhenPermissionDenied() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        List<HRegion> regions = initTableForArchivingRegions(tableName);
        // now attempt to depose the regions
        Path rootDir = FSUtils.getRootDir(TestHFileArchiving.UTIL.getConfiguration());
        Path tableDir = FSUtils.getTableDir(rootDir, regions.get(0).getRegionInfo().getTable());
        List<Path> regionDirList = regions.stream().map(( region) -> FSUtils.getRegionDir(tableDir, region.getRegionInfo())).collect(Collectors.toList());
        // To create a permission denied error, we do archive regions as a non-current user
        UserGroupInformation ugi = UserGroupInformation.createUserForTesting("foo1234", new String[]{ "group1" });
        try {
            ugi.doAs(((PrivilegedExceptionAction<Void>) (() -> {
                FileSystem fs = TestHFileArchiving.UTIL.getTestFileSystem();
                HFileArchiver.archiveRegions(TestHFileArchiving.UTIL.getConfiguration(), fs, rootDir, tableDir, regionDirList);
                return null;
            })));
        } catch (IOException e) {
            Assert.assertTrue(e.getCause().getMessage().contains("Permission denied"));
            throw e;
        } finally {
            TestHFileArchiving.UTIL.deleteTable(tableName);
        }
    }

    @Test
    public void testArchiveOnTableDelete() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestHFileArchiving.UTIL.createTable(tableName, TestHFileArchiving.TEST_FAM);
        List<HRegion> servingRegions = TestHFileArchiving.UTIL.getHBaseCluster().getRegions(tableName);
        // make sure we only have 1 region serving this table
        Assert.assertEquals(1, servingRegions.size());
        HRegion region = servingRegions.get(0);
        // get the parent RS and monitor
        HRegionServer hrs = TestHFileArchiving.UTIL.getRSForFirstRegionInTable(tableName);
        FileSystem fs = hrs.getFileSystem();
        // put some data on the region
        TestHFileArchiving.LOG.debug("-------Loading table");
        TestHFileArchiving.UTIL.loadRegion(region, TestHFileArchiving.TEST_FAM);
        // get the hfiles in the region
        List<HRegion> regions = hrs.getRegions(tableName);
        Assert.assertEquals("More that 1 region for test table.", 1, regions.size());
        region = regions.get(0);
        // wait for all the compactions to complete
        region.waitForFlushesAndCompactions();
        // disable table to prevent new updates
        TestHFileArchiving.UTIL.getAdmin().disableTable(tableName);
        TestHFileArchiving.LOG.debug("Disabled table");
        // remove all the files from the archive to get a fair comparison
        clearArchiveDirectory();
        // then get the current store files
        byte[][] columns = region.getTableDescriptor().getColumnFamilyNames().toArray(new byte[0][]);
        List<String> storeFiles = region.getStoreFileList(columns);
        // then delete the table so the hfiles get archived
        TestHFileArchiving.UTIL.deleteTable(tableName);
        TestHFileArchiving.LOG.debug("Deleted table");
        assertArchiveFiles(fs, storeFiles, 30000);
    }

    /**
     * Test that the store files are archived when a column family is removed.
     *
     * @throws java.io.IOException
     * 		if there's a problem creating a table.
     * @throws java.lang.InterruptedException
     * 		problem getting a RegionServer.
     */
    @Test
    public void testArchiveOnTableFamilyDelete() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestHFileArchiving.UTIL.createTable(tableName, new byte[][]{ TestHFileArchiving.TEST_FAM, Bytes.toBytes("fam2") });
        List<HRegion> servingRegions = TestHFileArchiving.UTIL.getHBaseCluster().getRegions(tableName);
        // make sure we only have 1 region serving this table
        Assert.assertEquals(1, servingRegions.size());
        HRegion region = servingRegions.get(0);
        // get the parent RS and monitor
        HRegionServer hrs = TestHFileArchiving.UTIL.getRSForFirstRegionInTable(tableName);
        FileSystem fs = hrs.getFileSystem();
        // put some data on the region
        TestHFileArchiving.LOG.debug("-------Loading table");
        TestHFileArchiving.UTIL.loadRegion(region, TestHFileArchiving.TEST_FAM);
        // get the hfiles in the region
        List<HRegion> regions = hrs.getRegions(tableName);
        Assert.assertEquals("More that 1 region for test table.", 1, regions.size());
        region = regions.get(0);
        // wait for all the compactions to complete
        region.waitForFlushesAndCompactions();
        // disable table to prevent new updates
        TestHFileArchiving.UTIL.getAdmin().disableTable(tableName);
        TestHFileArchiving.LOG.debug("Disabled table");
        // remove all the files from the archive to get a fair comparison
        clearArchiveDirectory();
        // then get the current store files
        byte[][] columns = region.getTableDescriptor().getColumnFamilyNames().toArray(new byte[0][]);
        List<String> storeFiles = region.getStoreFileList(columns);
        // then delete the table so the hfiles get archived
        TestHFileArchiving.UTIL.getAdmin().deleteColumnFamily(tableName, TestHFileArchiving.TEST_FAM);
        assertArchiveFiles(fs, storeFiles, 30000);
        TestHFileArchiving.UTIL.deleteTable(tableName);
    }

    /**
     * Test HFileArchiver.resolveAndArchive() race condition HBASE-7643
     */
    @Test
    public void testCleaningRace() throws Exception {
        final long TEST_TIME = 20 * 1000;
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        Configuration conf = TestHFileArchiving.UTIL.getMiniHBaseCluster().getMaster().getConfiguration();
        Path rootDir = TestHFileArchiving.UTIL.getDataTestDirOnTestFS("testCleaningRace");
        FileSystem fs = TestHFileArchiving.UTIL.getTestFileSystem();
        Path archiveDir = new Path(rootDir, HConstants.HFILE_ARCHIVE_DIRECTORY);
        Path regionDir = new Path(FSUtils.getTableDir(new Path("./"), TableName.valueOf(name.getMethodName())), "abcdef");
        Path familyDir = new Path(regionDir, "cf");
        Path sourceRegionDir = new Path(rootDir, regionDir);
        fs.mkdirs(sourceRegionDir);
        Stoppable stoppable = new StoppableImplementation();
        // The cleaner should be looping without long pauses to reproduce the race condition.
        HFileCleaner cleaner = getHFileCleaner(stoppable, conf, fs, archiveDir);
        Assert.assertNotNull("cleaner should not be null", cleaner);
        try {
            choreService.scheduleChore(cleaner);
            // Keep creating/archiving new files while the cleaner is running in the other thread
            long startTime = System.currentTimeMillis();
            for (long fid = 0; ((System.currentTimeMillis()) - startTime) < TEST_TIME; ++fid) {
                Path file = new Path(familyDir, String.valueOf(fid));
                Path sourceFile = new Path(rootDir, file);
                Path archiveFile = new Path(archiveDir, file);
                fs.createNewFile(sourceFile);
                try {
                    // Try to archive the file
                    HFileArchiver.archiveRegion(fs, rootDir, sourceRegionDir.getParent(), sourceRegionDir);
                    // The archiver succeded, the file is no longer in the original location
                    // but it's in the archive location.
                    TestHFileArchiving.LOG.debug((("hfile=" + fid) + " should be in the archive"));
                    Assert.assertTrue(fs.exists(archiveFile));
                    Assert.assertFalse(fs.exists(sourceFile));
                } catch (IOException e) {
                    // The archiver is unable to archive the file. Probably HBASE-7643 race condition.
                    // in this case, the file should not be archived, and we should have the file
                    // in the original location.
                    TestHFileArchiving.LOG.debug((("hfile=" + fid) + " should be in the source location"));
                    Assert.assertFalse(fs.exists(archiveFile));
                    Assert.assertTrue(fs.exists(sourceFile));
                    // Avoid to have this file in the next run
                    fs.delete(sourceFile, false);
                }
            }
        } finally {
            stoppable.stop("test end");
            cleaner.cancel(true);
            choreService.shutdown();
            fs.delete(rootDir, true);
        }
    }
}

