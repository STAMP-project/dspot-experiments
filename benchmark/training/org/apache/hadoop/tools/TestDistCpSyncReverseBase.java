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
import DistCpOptions.Builder;
import Mapper.Context;
import java.util.Arrays;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.HdfsConstants;
import org.apache.hadoop.hdfs.protocol.SnapshotDiffReport;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.tools.mapred.CopyMapper;
import org.junit.Assert;
import org.junit.Test;


/**
 * Base class to test "-rdiff s2 s1".
 * Shared by "-rdiff s2 s1 src tgt" and "-rdiff s2 s1 tgt tgt"
 */
public abstract class TestDistCpSyncReverseBase {
    private MiniDFSCluster cluster;

    private final Configuration conf = new HdfsConfiguration();

    private DistributedFileSystem dfs;

    private Builder optionsBuilder;

    private DistCpContext distCpContext;

    private Path source;

    private boolean isSrcNotSameAsTgt = true;

    private final Path target = new Path("/target");

    private final long blockSize = 1024;

    private final short dataNum = 1;

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
        final Path spath = new Path(source, (((HdfsConstants.DOT_SNAPSHOT_DIR) + (Path.SEPARATOR)) + "s1"));
        Assert.assertEquals(spath, distCpContext.getSourcePaths().get(0));
        // reset source path in options
        optionsBuilder.withSourcePaths(Arrays.asList(source));
        // the source/target does not have the given snapshots
        dfs.allowSnapshot(source);
        dfs.allowSnapshot(target);
        Assert.assertFalse(sync());
        Assert.assertEquals(spath, distCpContext.getSourcePaths().get(0));
        // reset source path in options
        optionsBuilder.withSourcePaths(Arrays.asList(source));
        this.enableAndCreateFirstSnapshot();
        dfs.createSnapshot(target, "s2");
        Assert.assertTrue(sync());
        // reset source paths in options
        optionsBuilder.withSourcePaths(Arrays.asList(source));
        // changes have been made in target
        final Path subTarget = new Path(target, "sub");
        dfs.mkdirs(subTarget);
        Assert.assertFalse(sync());
        // make sure the source path has been updated to the snapshot path
        Assert.assertEquals(spath, distCpContext.getSourcePaths().get(0));
        // reset source paths in options
        optionsBuilder.withSourcePaths(Arrays.asList(source));
        dfs.delete(subTarget, true);
        Assert.assertTrue(sync());
    }

    /**
     * Test the basic functionality.
     */
    @Test
    public void testSync() throws Exception {
        if (isSrcNotSameAsTgt) {
            initData(source);
        }
        initData(target);
        enableAndCreateFirstSnapshot();
        final FsShell shell = new FsShell(conf);
        lsrSource("Before source: ", shell, source);
        TestDistCpSyncReverseBase.lsr("Before target: ", shell, target);
        // make changes under target
        int numDeletedModified = changeData(target);
        createSecondSnapshotAtTarget();
        SnapshotDiffReport report = dfs.getSnapshotDiffReport(target, "s2", "s1");
        System.out.println(report);
        final DistCpSync distCpSync = new DistCpSync(distCpContext, conf);
        TestDistCpSyncReverseBase.lsr("Before sync target: ", shell, target);
        // do the sync
        Assert.assertTrue(distCpSync.sync());
        TestDistCpSyncReverseBase.lsr("After sync target: ", shell, target);
        // make sure the source path has been updated to the snapshot path
        final Path spath = new Path(source, (((HdfsConstants.DOT_SNAPSHOT_DIR) + (Path.SEPARATOR)) + "s1"));
        Assert.assertEquals(spath, distCpContext.getSourcePaths().get(0));
        // build copy listing
        final Path listingPath = new Path("/tmp/META/fileList.seq");
        CopyListing listing = new SimpleCopyListing(conf, new Credentials(), distCpSync);
        listing.buildListing(listingPath, distCpContext);
        Map<Text, CopyListingFileStatus> copyListing = getListing(listingPath);
        CopyMapper copyMapper = new CopyMapper();
        StubContext stubContext = new StubContext(conf, null, 0);
        Context context = getContext();
        // Enable append
        context.getConfiguration().setBoolean(APPEND.getConfigLabel(), true);
        copyMapper.setup(context);
        for (Map.Entry<Text, CopyListingFileStatus> entry : copyListing.entrySet()) {
            copyMapper.map(entry.getKey(), entry.getValue(), context);
        }
        lsrSource("After mapper source: ", shell, source);
        TestDistCpSyncReverseBase.lsr("After mapper target: ", shell, target);
        // verify that we only list modified and created files/directories
        Assert.assertEquals(numDeletedModified, copyListing.size());
        // verify that we only copied new appended data of f2 and the new file f1
        Assert.assertEquals(((blockSize) * 3), stubContext.getReporter().getCounter(BYTESCOPIED).getValue());
        // verify the source and target now has the same structure
        verifyCopy(dfs.getFileStatus(spath), dfs.getFileStatus(target), false);
    }

    /**
     * Test the case that "current" is snapshotted as "s2".
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSyncWithCurrent() throws Exception {
        optionsBuilder.withUseRdiff(".", "s1");
        if (isSrcNotSameAsTgt) {
            initData(source);
        }
        initData(target);
        enableAndCreateFirstSnapshot();
        // make changes under target
        changeData(target);
        // do the sync
        Assert.assertTrue(sync());
        final Path spath = new Path(source, (((HdfsConstants.DOT_SNAPSHOT_DIR) + (Path.SEPARATOR)) + "s1"));
        // make sure the source path is still unchanged
        Assert.assertEquals(spath, distCpContext.getSourcePaths().get(0));
    }

    @Test
    public void testSync2() throws Exception {
        if (isSrcNotSameAsTgt) {
            initData2(source);
        }
        initData2(target);
        enableAndCreateFirstSnapshot();
        // make changes under target
        changeData2(target);
        createSecondSnapshotAtTarget();
        SnapshotDiffReport report = dfs.getSnapshotDiffReport(target, "s2", "s1");
        System.out.println(report);
        syncAndVerify();
    }

    /**
     * Test a case where there are multiple source files with the same name.
     */
    @Test
    public void testSync3() throws Exception {
        if (isSrcNotSameAsTgt) {
            initData3(source);
        }
        initData3(target);
        enableAndCreateFirstSnapshot();
        // make changes under target
        changeData3(target);
        createSecondSnapshotAtTarget();
        SnapshotDiffReport report = dfs.getSnapshotDiffReport(target, "s2", "s1");
        System.out.println(report);
        syncAndVerify();
    }

    /**
     * Test a case where multiple level dirs are renamed.
     */
    @Test
    public void testSync4() throws Exception {
        if (isSrcNotSameAsTgt) {
            initData4(source);
        }
        initData4(target);
        enableAndCreateFirstSnapshot();
        final FsShell shell = new FsShell(conf);
        TestDistCpSyncReverseBase.lsr("Before change target: ", shell, target);
        // make changes under target
        int numDeletedAndModified = changeData4(target);
        createSecondSnapshotAtTarget();
        SnapshotDiffReport report = dfs.getSnapshotDiffReport(target, "s2", "s1");
        System.out.println(report);
        testAndVerify(numDeletedAndModified);
    }

    /**
     * Test a case with different delete and rename sequences.
     */
    @Test
    public void testSync5() throws Exception {
        if (isSrcNotSameAsTgt) {
            initData5(source);
        }
        initData5(target);
        enableAndCreateFirstSnapshot();
        // make changes under target
        int numDeletedAndModified = changeData5(target);
        createSecondSnapshotAtTarget();
        SnapshotDiffReport report = dfs.getSnapshotDiffReport(target, "s2", "s1");
        System.out.println(report);
        testAndVerify(numDeletedAndModified);
    }

    /**
     * Test a case where there is a cycle in renaming dirs.
     */
    @Test
    public void testSync6() throws Exception {
        if (isSrcNotSameAsTgt) {
            initData6(source);
        }
        initData6(target);
        enableAndCreateFirstSnapshot();
        int numDeletedModified = changeData6(target);
        createSecondSnapshotAtTarget();
        testAndVerify(numDeletedModified);
    }

    /**
     * Test a case where rename a dir, then create a new dir with the same name
     * and sub dir.
     */
    @Test
    public void testSync7() throws Exception {
        if (isSrcNotSameAsTgt) {
            initData7(source);
        }
        initData7(target);
        enableAndCreateFirstSnapshot();
        int numDeletedAndModified = changeData7(target);
        createSecondSnapshotAtTarget();
        testAndVerify(numDeletedAndModified);
    }

    /**
     * Test a case where create a dir, then mv a existed dir into it.
     */
    @Test
    public void testSync8() throws Exception {
        if (isSrcNotSameAsTgt) {
            initData8(source);
        }
        initData8(target);
        enableAndCreateFirstSnapshot();
        int numDeletedModified = changeData8(target, false);
        createSecondSnapshotAtTarget();
        testAndVerify(numDeletedModified);
    }

    /**
     * Test a case where create a dir, then mv a existed dir into it.
     * The difference between this one and testSync8 is, this one
     * also creates a snapshot s1.5 in between s1 and s2.
     */
    @Test
    public void testSync9() throws Exception {
        if (isSrcNotSameAsTgt) {
            initData8(source);
        }
        initData8(target);
        enableAndCreateFirstSnapshot();
        int numDeletedModified = changeData8(target, true);
        createSecondSnapshotAtTarget();
        testAndVerify(numDeletedModified);
    }
}

