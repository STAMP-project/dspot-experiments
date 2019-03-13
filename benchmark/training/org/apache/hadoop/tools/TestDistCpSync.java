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
package org.apache.hadoop.tools;


import CopyMapper.Counter.BYTESCOPIED;
import DistCpOptionSwitch.APPEND;
import Mapper.Context;
import java.util.Collections;
import java.util.Map;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.HdfsConstants;
import org.apache.hadoop.hdfs.protocol.SnapshotDiffReport;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.tools.mapred.CopyMapper;
import org.junit.Assert;
import org.junit.Test;


public class TestDistCpSync {
    private MiniDFSCluster cluster;

    private final Configuration conf = new HdfsConfiguration();

    private DistributedFileSystem dfs;

    private DistCpContext context;

    private final Path source = new Path("/source");

    private final Path target = new Path("/target");

    private final long BLOCK_SIZE = 1024;

    private final short DATA_NUM = 1;

    /**
     * Test the sync returns false in the following scenarios:
     * 1. the source/target dir are not snapshottable dir
     * 2. the source/target does not have the given snapshots
     * 3. changes have been made in target
     */
    @Test
    public void testFallback() throws Exception {
        // the source/target dir are not snapshottable dir
        Assert.assertFalse(sync());
        // make sure the source path has been updated to the snapshot path
        final Path spath = new Path(source, (((HdfsConstants.DOT_SNAPSHOT_DIR) + (Path.SEPARATOR)) + "s2"));
        Assert.assertEquals(spath, context.getSourcePaths().get(0));
        // reset source path in options
        context.setSourcePaths(Collections.singletonList(source));
        // the source/target does not have the given snapshots
        dfs.allowSnapshot(source);
        dfs.allowSnapshot(target);
        Assert.assertFalse(sync());
        Assert.assertEquals(spath, context.getSourcePaths().get(0));
        // reset source path in options
        context.setSourcePaths(Collections.singletonList(source));
        dfs.createSnapshot(source, "s1");
        dfs.createSnapshot(source, "s2");
        dfs.createSnapshot(target, "s1");
        Assert.assertTrue(sync());
        // reset source paths in options
        context.setSourcePaths(Collections.singletonList(source));
        // changes have been made in target
        final Path subTarget = new Path(target, "sub");
        dfs.mkdirs(subTarget);
        Assert.assertFalse(sync());
        // make sure the source path has been updated to the snapshot path
        Assert.assertEquals(spath, context.getSourcePaths().get(0));
        // reset source paths in options
        context.setSourcePaths(Collections.singletonList(source));
        dfs.delete(subTarget, true);
        Assert.assertTrue(sync());
    }

    /**
     * Test the basic functionality.
     */
    @Test
    public void testSync() throws Exception {
        initData(source);
        initData(target);
        enableAndCreateFirstSnapshot();
        // make changes under source
        int numCreatedModified = changeData(source);
        dfs.createSnapshot(source, "s2");
        // before sync, make some further changes on source. this should not affect
        // the later distcp since we're copying (s2-s1) to target
        final Path toDelete = new Path(source, "foo/d1/foo/f1");
        dfs.delete(toDelete, true);
        final Path newdir = new Path(source, "foo/d1/foo/newdir");
        dfs.mkdirs(newdir);
        SnapshotDiffReport report = dfs.getSnapshotDiffReport(source, "s1", "s2");
        System.out.println(report);
        DistCpSync distCpSync = new DistCpSync(context, conf);
        // do the sync
        Assert.assertTrue(distCpSync.sync());
        // make sure the source path has been updated to the snapshot path
        final Path spath = new Path(source, (((HdfsConstants.DOT_SNAPSHOT_DIR) + (Path.SEPARATOR)) + "s2"));
        Assert.assertEquals(spath, context.getSourcePaths().get(0));
        // build copy listing
        final Path listingPath = new Path("/tmp/META/fileList.seq");
        CopyListing listing = new SimpleCopyListing(conf, new Credentials(), distCpSync);
        listing.buildListing(listingPath, context);
        Map<Text, CopyListingFileStatus> copyListing = getListing(listingPath);
        CopyMapper copyMapper = new CopyMapper();
        StubContext stubContext = new StubContext(conf, null, 0);
        Context mapContext = getContext();
        // Enable append
        mapContext.getConfiguration().setBoolean(APPEND.getConfigLabel(), true);
        copyMapper.setup(mapContext);
        for (Map.Entry<Text, CopyListingFileStatus> entry : copyListing.entrySet()) {
            copyMapper.map(entry.getKey(), entry.getValue(), mapContext);
        }
        // verify that we only list modified and created files/directories
        Assert.assertEquals(numCreatedModified, copyListing.size());
        // verify that we only copied new appended data of f2 and the new file f1
        Assert.assertEquals(((BLOCK_SIZE) * 3), stubContext.getReporter().getCounter(BYTESCOPIED).getValue());
        // verify the source and target now has the same structure
        verifyCopy(dfs.getFileStatus(spath), dfs.getFileStatus(target), false);
    }

    /**
     * Similar test with testSync, but the "to" snapshot is specified as "."
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSyncWithCurrent() throws Exception {
        final DistCpOptions options = withSyncFolder(true).withUseDiff("s1", ".").build();
        context = new DistCpContext(options);
        initData(source);
        initData(target);
        enableAndCreateFirstSnapshot();
        // make changes under source
        changeData(source);
        // do the sync
        sync();
        // make sure the source path is still unchanged
        Assert.assertEquals(source, context.getSourcePaths().get(0));
    }

    @Test
    public void testSync2() throws Exception {
        initData2(source);
        initData2(target);
        enableAndCreateFirstSnapshot();
        // make changes under source
        changeData2(source);
        dfs.createSnapshot(source, "s2");
        SnapshotDiffReport report = dfs.getSnapshotDiffReport(source, "s1", "s2");
        System.out.println(report);
        syncAndVerify();
    }

    /**
     * Test a case where there are multiple source files with the same name.
     */
    @Test
    public void testSync3() throws Exception {
        initData3(source);
        initData3(target);
        enableAndCreateFirstSnapshot();
        // make changes under source
        changeData3(source);
        dfs.createSnapshot(source, "s2");
        SnapshotDiffReport report = dfs.getSnapshotDiffReport(source, "s1", "s2");
        System.out.println(report);
        syncAndVerify();
    }

    /**
     * Test a case where multiple level dirs are renamed.
     */
    @Test
    public void testSync4() throws Exception {
        initData4(source);
        initData4(target);
        enableAndCreateFirstSnapshot();
        // make changes under source
        changeData4(source);
        dfs.createSnapshot(source, "s2");
        SnapshotDiffReport report = dfs.getSnapshotDiffReport(source, "s1", "s2");
        System.out.println(report);
        syncAndVerify();
    }

    /**
     * Test a case with different delete and rename sequences.
     */
    @Test
    public void testSync5() throws Exception {
        initData5(source);
        initData5(target);
        enableAndCreateFirstSnapshot();
        // make changes under source
        changeData5(source);
        dfs.createSnapshot(source, "s2");
        SnapshotDiffReport report = dfs.getSnapshotDiffReport(source, "s1", "s2");
        System.out.println(report);
        syncAndVerify();
    }

    /**
     * Test a case where there is a cycle in renaming dirs.
     */
    @Test
    public void testSync6() throws Exception {
        initData6(source);
        initData6(target);
        enableAndCreateFirstSnapshot();
        int numCreatedModified = changeData6(source);
        dfs.createSnapshot(source, "s2");
        testAndVerify(numCreatedModified);
    }

    /**
     * Test a case where rename a dir, then create a new dir with the same name
     * and sub dir.
     */
    @Test
    public void testSync7() throws Exception {
        initData7(source);
        initData7(target);
        enableAndCreateFirstSnapshot();
        int numCreatedModified = changeData7(source);
        dfs.createSnapshot(source, "s2");
        testAndVerify(numCreatedModified);
    }

    /**
     * Test a case where create a dir, then mv a existed dir into it.
     */
    @Test
    public void testSync8() throws Exception {
        initData8(source);
        initData8(target);
        enableAndCreateFirstSnapshot();
        int numCreatedModified = changeData8(source);
        dfs.createSnapshot(source, "s2");
        testAndVerify(numCreatedModified);
    }

    /**
     * Test a case where the source path is relative.
     */
    @Test
    public void testSync9() throws Exception {
        // use /user/$USER/source for source directory
        Path sourcePath = new Path(dfs.getWorkingDirectory(), "source");
        initData9(sourcePath);
        initData9(target);
        dfs.allowSnapshot(sourcePath);
        dfs.allowSnapshot(target);
        dfs.createSnapshot(sourcePath, "s1");
        dfs.createSnapshot(target, "s1");
        changeData9(sourcePath);
        dfs.createSnapshot(sourcePath, "s2");
        String[] args = new String[]{ "-update", "-diff", "s1", "s2", "source", target.toString() };
        execute();
        verifyCopy(dfs.getFileStatus(sourcePath), dfs.getFileStatus(target), false);
    }

    @Test
    public void testSyncSnapshotTimeStampChecking() throws Exception {
        initData(source);
        initData(target);
        dfs.allowSnapshot(source);
        dfs.allowSnapshot(target);
        dfs.createSnapshot(source, "s2");
        dfs.createSnapshot(target, "s1");
        // Sleep one second to make snapshot s1 created later than s2
        Thread.sleep(1000);
        dfs.createSnapshot(source, "s1");
        boolean threwException = false;
        try {
            DistCpSync distCpSync = new DistCpSync(context, conf);
            // do the sync
            distCpSync.sync();
        } catch (HadoopIllegalArgumentException e) {
            threwException = true;
            GenericTestUtils.assertExceptionContains("Snapshot s2 should be newer than s1", e);
        }
        Assert.assertTrue(threwException);
    }
}

