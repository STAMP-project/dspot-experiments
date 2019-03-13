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
package org.apache.hadoop.hdfs.server.namenode;


import DFSConfigKeys.DFS_NAMENODE_CHECKED_VOLUMES_MINIMUM_KEY;
import DFSConfigKeys.DFS_NAMENODE_EDITS_DIR_MINIMUM_KEY;
import DFSConfigKeys.DFS_NAMENODE_EDITS_DIR_REQUIRED_KEY;
import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.JournalSet.JournalAndStream;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.LambdaTestUtils;
import org.apache.hadoop.test.PathUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class TestEditLogJournalFailures {
    private int editsPerformed = 0;

    private MiniDFSCluster cluster;

    private FileSystem fs;

    private boolean useAsyncEdits;

    public TestEditLogJournalFailures(boolean useAsyncEdits) {
        this.useAsyncEdits = useAsyncEdits;
    }

    @Test
    public void testSingleFailedEditsDirOnFlush() throws IOException {
        Assert.assertTrue(doAnEdit());
        // Invalidate one edits journal.
        invalidateEditsDirAtIndex(0, true, false);
        // The NN has not terminated (no ExitException thrown)
        Assert.assertTrue(doAnEdit());
        // A single journal failure should not result in a call to terminate
        Assert.assertFalse(cluster.getNameNode().isInSafeMode());
    }

    @Test
    public void testAllEditsDirsFailOnFlush() throws IOException {
        Assert.assertTrue(doAnEdit());
        // Invalidate both edits journals.
        invalidateEditsDirAtIndex(0, true, false);
        invalidateEditsDirAtIndex(1, true, false);
        // The NN has not terminated (no ExitException thrown)
        try {
            doAnEdit();
            Assert.fail(("The previous edit could not be synced to any persistent storage, " + "should have halted the NN"));
        } catch (RemoteException re) {
            Assert.assertTrue(re.getClassName().contains("ExitException"));
            GenericTestUtils.assertExceptionContains(("Could not sync enough journals to persistent storage. " + "Unsynced transactions: 1"), re);
        }
    }

    @Test
    public void testAllEditsDirFailOnWrite() throws IOException {
        Assert.assertTrue(doAnEdit());
        // Invalidate both edits journals.
        invalidateEditsDirAtIndex(0, true, true);
        invalidateEditsDirAtIndex(1, true, true);
        // The NN has not terminated (no ExitException thrown)
        try {
            doAnEdit();
            Assert.fail(("The previous edit could not be synced to any persistent storage, " + " should have halted the NN"));
        } catch (RemoteException re) {
            Assert.assertTrue(re.getClassName().contains("ExitException"));
            GenericTestUtils.assertExceptionContains(("Could not sync enough journals to persistent storage due to " + ("No journals available to flush. " + "Unsynced transactions: 1")), re);
        }
    }

    @Test
    public void testSingleFailedEditsDirOnSetReadyToFlush() throws IOException {
        Assert.assertTrue(doAnEdit());
        // Invalidate one edits journal.
        invalidateEditsDirAtIndex(0, false, false);
        // The NN has not terminated (no ExitException thrown)
        Assert.assertTrue(doAnEdit());
        // A single journal failure should not result in a call to terminate
        Assert.assertFalse(cluster.getNameNode().isInSafeMode());
    }

    @Test
    public void testSingleRequiredFailedEditsDirOnSetReadyToFlush() throws IOException {
        // Set one of the edits dirs to be required.
        String[] editsDirs = cluster.getConfiguration(0).getTrimmedStrings(DFS_NAMENODE_NAME_DIR_KEY);
        shutDownMiniCluster();
        Configuration conf = getConf();
        conf.set(DFS_NAMENODE_EDITS_DIR_REQUIRED_KEY, editsDirs[0]);
        conf.setInt(DFS_NAMENODE_EDITS_DIR_MINIMUM_KEY, 0);
        conf.setInt(DFS_NAMENODE_CHECKED_VOLUMES_MINIMUM_KEY, 0);
        setUpMiniCluster(conf, true);
        Assert.assertTrue(doAnEdit());
        // Invalidated the one required edits journal.
        invalidateEditsDirAtIndex(0, false, false);
        JournalAndStream nonRequiredJas = getJournalAndStream(1);
        EditLogFileOutputStream nonRequiredSpy = spyOnStream(nonRequiredJas);
        // The NN has not terminated (no ExitException thrown)
        // ..and that the other stream is active.
        Assert.assertTrue(nonRequiredJas.isActive());
        try {
            doAnEdit();
            Assert.fail("A single failure of a required journal should have halted the NN");
        } catch (RemoteException re) {
            Assert.assertTrue(re.getClassName().contains("ExitException"));
            GenericTestUtils.assertExceptionContains("setReadyToFlush failed for required journal", re);
        }
        // Since the required directory failed setReadyToFlush, and that
        // directory was listed prior to the non-required directory,
        // we should not call setReadyToFlush on the non-required
        // directory. Regression test for HDFS-2874.
        Mockito.verify(nonRequiredSpy, Mockito.never()).setReadyToFlush();
        Assert.assertFalse(nonRequiredJas.isActive());
    }

    @Test
    public void testMultipleRedundantFailedEditsDirOnSetReadyToFlush() throws IOException {
        // Set up 4 name/edits dirs.
        shutDownMiniCluster();
        Configuration conf = getConf();
        String[] nameDirs = new String[4];
        for (int i = 0; i < (nameDirs.length); i++) {
            File nameDir = new File(PathUtils.getTestDir(getClass()), ("name-dir" + i));
            nameDir.mkdirs();
            nameDirs[i] = nameDir.getAbsolutePath();
        }
        conf.set(DFS_NAMENODE_NAME_DIR_KEY, StringUtils.join(nameDirs, ","));
        // Keep running unless there are less than 2 edits dirs remaining.
        conf.setInt(DFS_NAMENODE_EDITS_DIR_MINIMUM_KEY, 2);
        setUpMiniCluster(conf, false);
        // All journals active.
        Assert.assertTrue(doAnEdit());
        // The NN has not terminated (no ExitException thrown)
        // Invalidate 1/4 of the redundant journals.
        invalidateEditsDirAtIndex(0, false, false);
        Assert.assertTrue(doAnEdit());
        // The NN has not terminated (no ExitException thrown)
        // Invalidate 2/4 of the redundant journals.
        invalidateEditsDirAtIndex(1, false, false);
        Assert.assertTrue(doAnEdit());
        // The NN has not terminated (no ExitException thrown)
        // Invalidate 3/4 of the redundant journals.
        invalidateEditsDirAtIndex(2, false, false);
        try {
            doAnEdit();
            Assert.fail(("A failure of more than the minimum number of redundant journals " + "should have halted "));
        } catch (RemoteException re) {
            Assert.assertTrue(re.getClassName().contains("ExitException"));
            GenericTestUtils.assertExceptionContains(("Could not sync enough journals to persistent storage due to " + ("setReadyToFlush failed for too many journals. " + "Unsynced transactions: 1")), re);
        }
    }

    @Test
    public void testMultipleRedundantFailedEditsDirOnStartLogSegment() throws Exception {
        // Set up 4 name/edits dirs.
        shutDownMiniCluster();
        Configuration conf = getConf();
        String[] nameDirs = new String[4];
        for (int i = 0; i < (nameDirs.length); i++) {
            File nameDir = new File(PathUtils.getTestDir(getClass()), ("name-dir" + i));
            nameDir.mkdirs();
            nameDirs[i] = nameDir.getAbsolutePath();
        }
        conf.set(DFS_NAMENODE_NAME_DIR_KEY, StringUtils.join(nameDirs, ","));
        conf.set(DFS_NAMENODE_EDITS_DIR_REQUIRED_KEY, StringUtils.join(nameDirs, ",", 0, 3));
        setUpMiniCluster(conf, false);
        // All journals active.
        Assert.assertTrue(doAnEdit());
        // The NN has not terminated (no ExitException thrown)
        spyOnJASjournal(3);
        RemoteException re = LambdaTestUtils.intercept(RemoteException.class, "too few journals successfully started.", () -> ((DistributedFileSystem) (fs)).rollEdits());
        GenericTestUtils.assertExceptionContains("ExitException", re);
    }
}

