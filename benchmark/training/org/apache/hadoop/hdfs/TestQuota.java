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
package org.apache.hadoop.hdfs;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_CONTENT_SUMMARY_LIMIT_KEY;
import DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY;
import DFSOutputStream.LOG;
import HdfsConstants.HOT_STORAGE_POLICY_NAME;
import HdfsConstants.QUOTA_DONT_SET;
import StorageType.DEFAULT;
import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.client.impl.LeaseRenewer;
import org.apache.hadoop.hdfs.protocol.DSQuotaExceededException;
import org.apache.hadoop.hdfs.protocol.NSQuotaExceededException;
import org.apache.hadoop.hdfs.protocol.QuotaByStorageTypeExceededException;
import org.apache.hadoop.hdfs.protocol.QuotaExceededException;
import org.apache.hadoop.hdfs.server.namenode.FSImageTestUtil;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.hdfs.web.WebHdfsConstants;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PathUtils;
import org.apache.hadoop.util.ToolRunner;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * A class for testing quota-related commands
 */
public class TestQuota {
    private static final Logger LOG = LoggerFactory.getLogger(TestQuota.class);

    private static Configuration conf = null;

    private static final ByteArrayOutputStream OUT_STREAM = new ByteArrayOutputStream();

    private static final ByteArrayOutputStream ERR_STREAM = new ByteArrayOutputStream();

    private static final PrintStream OLD_OUT = System.out;

    private static final PrintStream OLD_ERR = System.err;

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem dfs;

    private static FileSystem webhdfs;

    /* set a smaller block size so that we can test with smaller space quotas */
    private static final int DEFAULT_BLOCK_SIZE = 512;

    @Rule
    public final Timeout testTestout = new Timeout(120000);

    /**
     * Tests to make sure we're getting human readable Quota exception messages
     * Test for @link{ NSQuotaExceededException, DSQuotaExceededException}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDSQuotaExceededExceptionIsHumanReadable() {
        Integer bytes = 1024;
        try {
            throw new DSQuotaExceededException(bytes, bytes);
        } catch (DSQuotaExceededException e) {
            Assert.assertEquals(("The DiskSpace quota is exceeded: quota = 1024 B = 1 KB" + " but diskspace consumed = 1024 B = 1 KB"), e.getMessage());
        }
    }

    /**
     * Test quota related commands:
     *    setQuota, clrQuota, setSpaceQuota, clrSpaceQuota, and count
     */
    @Test
    public void testQuotaCommands() throws Exception {
        DFSAdmin admin = new DFSAdmin(TestQuota.conf);
        final Path dir = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(dir));
        final int fileLen = 1024;
        final short replication = 5;
        final long spaceQuota = ((fileLen * replication) * 15) / 8;
        // 1: create a test directory
        final Path parent = new Path(dir, "test");
        Assert.assertTrue(TestQuota.dfs.mkdirs(parent));
        // Try setting name quota with suffixes
        String[] args;
        args = new String[]{ "-setQuota", "3K", parent.toString() };
        runCommand(admin, args, false);
        args = new String[]{ "-setQuota", "3m", parent.toString() };
        runCommand(admin, args, false);
        // Set the final name quota to 3
        args = new String[]{ "-setQuota", "3", parent.toString() };
        runCommand(admin, args, false);
        // try setting space quota with a 'binary prefix'
        runCommand(admin, false, "-setSpaceQuota", "2t", parent.toString());
        Assert.assertEquals((2L << 40), TestQuota.dfs.getContentSummary(parent).getSpaceQuota());
        // set diskspace quota to 10000
        runCommand(admin, false, "-setSpaceQuota", Long.toString(spaceQuota), parent.toString());
        // 2: create directory /test/data0
        final Path childDir0 = new Path(parent, "data0");
        Assert.assertTrue(TestQuota.dfs.mkdirs(childDir0));
        // 3: create a file /test/datafile0
        final Path childFile0 = new Path(parent, "datafile0");
        DFSTestUtil.createFile(TestQuota.dfs, childFile0, fileLen, replication, 0);
        // 4: count -q /test
        ContentSummary c = TestQuota.dfs.getContentSummary(parent);
        compareQuotaUsage(c, TestQuota.dfs, parent);
        Assert.assertEquals(((c.getFileCount()) + (c.getDirectoryCount())), 3);
        Assert.assertEquals(c.getQuota(), 3);
        Assert.assertEquals(c.getSpaceConsumed(), (fileLen * replication));
        Assert.assertEquals(c.getSpaceQuota(), spaceQuota);
        // 5: count -q /test/data0
        c = TestQuota.dfs.getContentSummary(childDir0);
        compareQuotaUsage(c, TestQuota.dfs, childDir0);
        Assert.assertEquals(((c.getFileCount()) + (c.getDirectoryCount())), 1);
        Assert.assertEquals(c.getQuota(), (-1));
        // check disk space consumed
        c = TestQuota.dfs.getContentSummary(parent);
        compareQuotaUsage(c, TestQuota.dfs, parent);
        Assert.assertEquals(c.getSpaceConsumed(), (fileLen * replication));
        // 6: create a directory /test/data1
        final Path childDir1 = new Path(parent, "data1");
        boolean hasException = false;
        try {
            Assert.assertFalse(TestQuota.dfs.mkdirs(childDir1));
        } catch (QuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        OutputStream fout;
        // 7: create a file /test/datafile1
        final Path childFile1 = new Path(parent, "datafile1");
        hasException = false;
        try {
            fout = TestQuota.dfs.create(childFile1);
        } catch (QuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        // 8: clear quota /test
        runCommand(admin, new String[]{ "-clrQuota", parent.toString() }, false);
        c = TestQuota.dfs.getContentSummary(parent);
        compareQuotaUsage(c, TestQuota.dfs, parent);
        Assert.assertEquals(c.getQuota(), (-1));
        Assert.assertEquals(c.getSpaceQuota(), spaceQuota);
        // 9: clear quota /test/data0
        runCommand(admin, new String[]{ "-clrQuota", childDir0.toString() }, false);
        c = TestQuota.dfs.getContentSummary(childDir0);
        compareQuotaUsage(c, TestQuota.dfs, childDir0);
        Assert.assertEquals(c.getQuota(), (-1));
        // 10: create a file /test/datafile1
        fout = TestQuota.dfs.create(childFile1, replication);
        // 10.s: but writing fileLen bytes should result in an quota exception
        try {
            fout.write(new byte[fileLen]);
            fout.close();
            Assert.fail();
        } catch (QuotaExceededException e) {
            IOUtils.closeStream(fout);
        }
        // delete the file
        TestQuota.dfs.delete(childFile1, false);
        // 9.s: clear diskspace quota
        runCommand(admin, false, "-clrSpaceQuota", parent.toString());
        c = TestQuota.dfs.getContentSummary(parent);
        compareQuotaUsage(c, TestQuota.dfs, parent);
        Assert.assertEquals(c.getQuota(), (-1));
        Assert.assertEquals(c.getSpaceQuota(), (-1));
        // now creating childFile1 should succeed
        DFSTestUtil.createFile(TestQuota.dfs, childFile1, fileLen, replication, 0);
        // 11: set the quota of /test to be 1
        // HADOOP-5872 - we can set quota even if it is immediately violated
        args = new String[]{ "-setQuota", "1", parent.toString() };
        runCommand(admin, args, false);
        // for space quota
        runCommand(admin, false, "-setSpaceQuota", Integer.toString(fileLen), args[2]);
        // 12: set the quota of /test/data0 to be 1
        args = new String[]{ "-setQuota", "1", childDir0.toString() };
        runCommand(admin, args, false);
        // 13: not able create a directory under data0
        hasException = false;
        try {
            Assert.assertFalse(TestQuota.dfs.mkdirs(new Path(childDir0, "in")));
        } catch (QuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        c = TestQuota.dfs.getContentSummary(childDir0);
        compareQuotaUsage(c, TestQuota.dfs, childDir0);
        Assert.assertEquals(((c.getDirectoryCount()) + (c.getFileCount())), 1);
        Assert.assertEquals(c.getQuota(), 1);
        // 14a: set quota on a non-existent directory
        Path nonExistentPath = new Path(dir, "test1");
        Assert.assertFalse(TestQuota.dfs.exists(nonExistentPath));
        try {
            compareQuotaUsage(null, TestQuota.dfs, nonExistentPath);
            Assert.fail("Expected FileNotFoundException");
        } catch (FileNotFoundException fnfe) {
            GenericTestUtils.assertExceptionContains(("File/Directory does not exist: " + nonExistentPath), fnfe);
        }
        args = new String[]{ "-setQuota", "1", nonExistentPath.toString() };
        runCommand(admin, args, true);
        // for space quota
        runCommand(admin, true, "-setSpaceQuota", "1g", nonExistentPath.toString());
        // 14b: set quota on a file
        Assert.assertTrue(TestQuota.dfs.isFile(childFile0));
        args[1] = childFile0.toString();
        runCommand(admin, args, true);
        // same for space quota
        runCommand(admin, true, "-setSpaceQuota", "1t", args[1]);
        // 15a: clear quota on a file
        args[0] = "-clrQuota";
        runCommand(admin, args, true);
        runCommand(admin, true, "-clrSpaceQuota", args[1]);
        // 15b: clear quota on a non-existent directory
        args[1] = nonExistentPath.toString();
        runCommand(admin, args, true);
        runCommand(admin, true, "-clrSpaceQuota", args[1]);
        // 16a: set the quota of /test to be 0
        args = new String[]{ "-setQuota", "0", parent.toString() };
        runCommand(admin, args, true);
        runCommand(admin, false, "-setSpaceQuota", "0", args[2]);
        // 16b: set the quota of /test to be -1
        args[1] = "-1";
        runCommand(admin, args, true);
        runCommand(admin, true, "-setSpaceQuota", args[1], args[2]);
        // 16c: set the quota of /test to be Long.MAX_VALUE+1
        args[1] = String.valueOf(((Long.MAX_VALUE) + 1L));
        runCommand(admin, args, true);
        runCommand(admin, true, "-setSpaceQuota", args[1], args[2]);
        // 16d: set the quota of /test to be a non integer
        args[1] = "33aa1.5";
        runCommand(admin, args, true);
        runCommand(admin, true, "-setSpaceQuota", args[1], args[2]);
        // 16e: set space quota with a value larger than Long.MAX_VALUE
        runCommand(admin, true, "-setSpaceQuota", (((((Long.MAX_VALUE) / 1024) / 1024) + 1024) + "m"), args[2]);
        // 17:  setQuota by a non-administrator
        final String username = "userxx";
        UserGroupInformation ugi = UserGroupInformation.createUserForTesting(username, new String[]{ "groupyy" });
        final String[] args2 = args.clone();// need final ref for doAs block

        ugi.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                Assert.assertEquals("Not running as new user", username, UserGroupInformation.getCurrentUser().getShortUserName());
                DFSAdmin userAdmin = new DFSAdmin(TestQuota.conf);
                args2[1] = "100";
                runCommand(userAdmin, args2, true);
                runCommand(userAdmin, true, "-setSpaceQuota", "1g", args2[2]);
                // 18: clrQuota by a non-administrator
                String[] args3 = new String[]{ "-clrQuota", parent.toString() };
                runCommand(userAdmin, args3, true);
                runCommand(userAdmin, true, "-clrSpaceQuota", args3[1]);
                return null;
            }
        });
        // 19: clrQuota on the root directory ("/") should pass.
        runCommand(admin, false, "-clrQuota", "/");
        // 20: setQuota on the root directory ("/") should succeed
        runCommand(admin, false, "-setQuota", "1000000", "/");
        runCommand(admin, false, "-clrQuota", "/");
        runCommand(admin, false, "-clrSpaceQuota", "/");
        runCommand(admin, new String[]{ "-clrQuota", parent.toString() }, false);
        runCommand(admin, false, "-clrSpaceQuota", parent.toString());
        // 2: create directory /test/data2
        final Path childDir2 = new Path(parent, "data2");
        Assert.assertTrue(TestQuota.dfs.mkdirs(childDir2));
        final Path childFile2 = new Path(childDir2, "datafile2");
        final Path childFile3 = new Path(childDir2, "datafile3");
        final long spaceQuota2 = (TestQuota.DEFAULT_BLOCK_SIZE) * replication;
        final long fileLen2 = TestQuota.DEFAULT_BLOCK_SIZE;
        // set space quota to a real low value
        runCommand(admin, false, "-setSpaceQuota", Long.toString(spaceQuota2), childDir2.toString());
        // clear space quota
        runCommand(admin, false, "-clrSpaceQuota", childDir2.toString());
        // create a file that is greater than the size of space quota
        DFSTestUtil.createFile(TestQuota.dfs, childFile2, fileLen2, replication, 0);
        // now set space quota again. This should succeed
        runCommand(admin, false, "-setSpaceQuota", Long.toString(spaceQuota2), childDir2.toString());
        hasException = false;
        try {
            DFSTestUtil.createFile(TestQuota.dfs, childFile3, fileLen2, replication, 0);
        } catch (DSQuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        // now test the same for root
        final Path childFile4 = new Path(dir, "datafile2");
        final Path childFile5 = new Path(dir, "datafile3");
        runCommand(admin, false, "-clrQuota", "/");
        runCommand(admin, false, "-clrSpaceQuota", "/");
        // set space quota to a real low value
        runCommand(admin, false, "-setSpaceQuota", Long.toString(spaceQuota2), "/");
        runCommand(admin, false, "-clrSpaceQuota", "/");
        DFSTestUtil.createFile(TestQuota.dfs, childFile4, fileLen2, replication, 0);
        runCommand(admin, false, "-setSpaceQuota", Long.toString(spaceQuota2), "/");
        hasException = false;
        try {
            DFSTestUtil.createFile(TestQuota.dfs, childFile5, fileLen2, replication, 0);
        } catch (DSQuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        Assert.assertEquals(5, TestQuota.cluster.getNamesystem().getFSDirectory().getYieldCount());
        /* clear sapce quota for root, otherwise other tests may fail due to
        insufficient space quota.
         */
        runCommand(admin, false, "-clrSpaceQuota", "/");
    }

    /**
     * Test commands that change the size of the name space:
     *  mkdirs, rename, and delete
     */
    @Test
    public void testNamespaceCommands() throws Exception {
        final Path parent = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(parent));
        // 1: create directory nqdir0/qdir1/qdir20/nqdir30
        Assert.assertTrue(TestQuota.dfs.mkdirs(new Path(parent, "nqdir0/qdir1/qdir20/nqdir30")));
        // 2: set the quota of nqdir0/qdir1 to be 6
        final Path quotaDir1 = new Path(parent, "nqdir0/qdir1");
        TestQuota.dfs.setQuota(quotaDir1, 6, QUOTA_DONT_SET);
        ContentSummary c = TestQuota.dfs.getContentSummary(quotaDir1);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir1);
        Assert.assertEquals(c.getDirectoryCount(), 3);
        Assert.assertEquals(c.getQuota(), 6);
        // 3: set the quota of nqdir0/qdir1/qdir20 to be 7
        final Path quotaDir2 = new Path(parent, "nqdir0/qdir1/qdir20");
        TestQuota.dfs.setQuota(quotaDir2, 7, QUOTA_DONT_SET);
        c = TestQuota.dfs.getContentSummary(quotaDir2);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir2);
        Assert.assertEquals(c.getDirectoryCount(), 2);
        Assert.assertEquals(c.getQuota(), 7);
        // 4: Create directory nqdir0/qdir1/qdir21 and set its quota to 2
        final Path quotaDir3 = new Path(parent, "nqdir0/qdir1/qdir21");
        Assert.assertTrue(TestQuota.dfs.mkdirs(quotaDir3));
        TestQuota.dfs.setQuota(quotaDir3, 2, QUOTA_DONT_SET);
        c = TestQuota.dfs.getContentSummary(quotaDir3);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir3);
        Assert.assertEquals(c.getDirectoryCount(), 1);
        Assert.assertEquals(c.getQuota(), 2);
        // 5: Create directory nqdir0/qdir1/qdir21/nqdir32
        Path tempPath = new Path(quotaDir3, "nqdir32");
        Assert.assertTrue(TestQuota.dfs.mkdirs(tempPath));
        c = TestQuota.dfs.getContentSummary(quotaDir3);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir3);
        Assert.assertEquals(c.getDirectoryCount(), 2);
        Assert.assertEquals(c.getQuota(), 2);
        // 6: Create directory nqdir0/qdir1/qdir21/nqdir33
        tempPath = new Path(quotaDir3, "nqdir33");
        boolean hasException = false;
        try {
            Assert.assertFalse(TestQuota.dfs.mkdirs(tempPath));
        } catch (NSQuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        c = TestQuota.dfs.getContentSummary(quotaDir3);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir3);
        Assert.assertEquals(c.getDirectoryCount(), 2);
        Assert.assertEquals(c.getQuota(), 2);
        // 7: Create directory nqdir0/qdir1/qdir20/nqdir31
        tempPath = new Path(quotaDir2, "nqdir31");
        Assert.assertTrue(TestQuota.dfs.mkdirs(tempPath));
        c = TestQuota.dfs.getContentSummary(quotaDir2);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir2);
        Assert.assertEquals(c.getDirectoryCount(), 3);
        Assert.assertEquals(c.getQuota(), 7);
        c = TestQuota.dfs.getContentSummary(quotaDir1);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir1);
        Assert.assertEquals(c.getDirectoryCount(), 6);
        Assert.assertEquals(c.getQuota(), 6);
        // 8: Create directory nqdir0/qdir1/qdir20/nqdir33
        tempPath = new Path(quotaDir2, "nqdir33");
        hasException = false;
        try {
            Assert.assertFalse(TestQuota.dfs.mkdirs(tempPath));
        } catch (NSQuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        // 9: Move nqdir0/qdir1/qdir21/nqdir32 nqdir0/qdir1/qdir20/nqdir30
        tempPath = new Path(quotaDir2, "nqdir30");
        TestQuota.dfs.rename(new Path(quotaDir3, "nqdir32"), tempPath);
        c = TestQuota.dfs.getContentSummary(quotaDir2);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir2);
        Assert.assertEquals(c.getDirectoryCount(), 4);
        Assert.assertEquals(c.getQuota(), 7);
        c = TestQuota.dfs.getContentSummary(quotaDir1);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir1);
        Assert.assertEquals(c.getDirectoryCount(), 6);
        Assert.assertEquals(c.getQuota(), 6);
        // 10: Move nqdir0/qdir1/qdir20/nqdir30 to nqdir0/qdir1/qdir21
        hasException = false;
        try {
            Assert.assertFalse(TestQuota.dfs.rename(tempPath, quotaDir3));
        } catch (NSQuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        Assert.assertTrue(TestQuota.dfs.exists(tempPath));
        Assert.assertFalse(TestQuota.dfs.exists(new Path(quotaDir3, "nqdir30")));
        // 10.a: Rename nqdir0/qdir1/qdir20/nqdir30 to nqdir0/qdir1/qdir21/nqdir32
        hasException = false;
        try {
            Assert.assertFalse(TestQuota.dfs.rename(tempPath, new Path(quotaDir3, "nqdir32")));
        } catch (QuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        Assert.assertTrue(TestQuota.dfs.exists(tempPath));
        Assert.assertFalse(TestQuota.dfs.exists(new Path(quotaDir3, "nqdir32")));
        // 11: Move nqdir0/qdir1/qdir20/nqdir30 to nqdir0
        Assert.assertTrue(TestQuota.dfs.rename(tempPath, new Path(parent, "nqdir0")));
        c = TestQuota.dfs.getContentSummary(quotaDir2);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir2);
        Assert.assertEquals(c.getDirectoryCount(), 2);
        Assert.assertEquals(c.getQuota(), 7);
        c = TestQuota.dfs.getContentSummary(quotaDir1);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir1);
        Assert.assertEquals(c.getDirectoryCount(), 4);
        Assert.assertEquals(c.getQuota(), 6);
        // 12: Create directory nqdir0/nqdir30/nqdir33
        Assert.assertTrue(TestQuota.dfs.mkdirs(new Path(parent, "nqdir0/nqdir30/nqdir33")));
        // 13: Move nqdir0/nqdir30 nqdir0/qdir1/qdir20/qdir30
        hasException = false;
        try {
            Assert.assertFalse(TestQuota.dfs.rename(new Path(parent, "nqdir0/nqdir30"), tempPath));
        } catch (NSQuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        // 14: Move nqdir0/qdir1/qdir21 nqdir0/qdir1/qdir20
        Assert.assertTrue(TestQuota.dfs.rename(quotaDir3, quotaDir2));
        c = TestQuota.dfs.getContentSummary(quotaDir1);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir1);
        Assert.assertEquals(c.getDirectoryCount(), 4);
        Assert.assertEquals(c.getQuota(), 6);
        c = TestQuota.dfs.getContentSummary(quotaDir2);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir2);
        Assert.assertEquals(c.getDirectoryCount(), 3);
        Assert.assertEquals(c.getQuota(), 7);
        tempPath = new Path(quotaDir2, "qdir21");
        c = TestQuota.dfs.getContentSummary(tempPath);
        compareQuotaUsage(c, TestQuota.dfs, tempPath);
        Assert.assertEquals(c.getDirectoryCount(), 1);
        Assert.assertEquals(c.getQuota(), 2);
        // 15: Delete nqdir0/qdir1/qdir20/qdir21
        TestQuota.dfs.delete(tempPath, true);
        c = TestQuota.dfs.getContentSummary(quotaDir2);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir2);
        Assert.assertEquals(c.getDirectoryCount(), 2);
        Assert.assertEquals(c.getQuota(), 7);
        c = TestQuota.dfs.getContentSummary(quotaDir1);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir1);
        Assert.assertEquals(c.getDirectoryCount(), 3);
        Assert.assertEquals(c.getQuota(), 6);
        // 16: Move nqdir0/qdir30 nqdir0/qdir1/qdir20
        Assert.assertTrue(TestQuota.dfs.rename(new Path(parent, "nqdir0/nqdir30"), quotaDir2));
        c = TestQuota.dfs.getContentSummary(quotaDir2);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir2);
        Assert.assertEquals(c.getDirectoryCount(), 5);
        Assert.assertEquals(c.getQuota(), 7);
        c = TestQuota.dfs.getContentSummary(quotaDir1);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir1);
        Assert.assertEquals(c.getDirectoryCount(), 6);
        Assert.assertEquals(c.getQuota(), 6);
    }

    /**
     * Test HDFS operations that change disk space consumed by a directory tree.
     * namely create, rename, delete, append, and setReplication.
     *
     * This is based on testNamespaceCommands() above.
     */
    @Test
    public void testSpaceCommands() throws Exception {
        final Path parent = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(parent));
        int fileLen = 1024;
        short replication = 3;
        int fileSpace = fileLen * replication;
        // create directory nqdir0/qdir1/qdir20/nqdir30
        Assert.assertTrue(TestQuota.dfs.mkdirs(new Path(parent, "nqdir0/qdir1/qdir20/nqdir30")));
        // set the quota of nqdir0/qdir1 to 4 * fileSpace
        final Path quotaDir1 = new Path(parent, "nqdir0/qdir1");
        TestQuota.dfs.setQuota(quotaDir1, QUOTA_DONT_SET, (4 * fileSpace));
        ContentSummary c = TestQuota.dfs.getContentSummary(quotaDir1);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir1);
        Assert.assertEquals(c.getSpaceQuota(), (4 * fileSpace));
        // set the quota of nqdir0/qdir1/qdir20 to 6 * fileSpace
        final Path quotaDir20 = new Path(parent, "nqdir0/qdir1/qdir20");
        TestQuota.dfs.setQuota(quotaDir20, QUOTA_DONT_SET, (6 * fileSpace));
        c = TestQuota.dfs.getContentSummary(quotaDir20);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir20);
        Assert.assertEquals(c.getSpaceQuota(), (6 * fileSpace));
        // Create nqdir0/qdir1/qdir21 and set its space quota to 2 * fileSpace
        final Path quotaDir21 = new Path(parent, "nqdir0/qdir1/qdir21");
        Assert.assertTrue(TestQuota.dfs.mkdirs(quotaDir21));
        TestQuota.dfs.setQuota(quotaDir21, QUOTA_DONT_SET, (2 * fileSpace));
        c = TestQuota.dfs.getContentSummary(quotaDir21);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir21);
        Assert.assertEquals(c.getSpaceQuota(), (2 * fileSpace));
        // 5: Create directory nqdir0/qdir1/qdir21/nqdir32
        Path tempPath = new Path(quotaDir21, "nqdir32");
        Assert.assertTrue(TestQuota.dfs.mkdirs(tempPath));
        // create a file under nqdir32/fileDir
        DFSTestUtil.createFile(TestQuota.dfs, new Path(tempPath, "fileDir/file1"), fileLen, replication, 0);
        c = TestQuota.dfs.getContentSummary(quotaDir21);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir21);
        Assert.assertEquals(c.getSpaceConsumed(), fileSpace);
        // Create a larger file nqdir0/qdir1/qdir21/nqdir33/
        boolean hasException = false;
        try {
            DFSTestUtil.createFile(TestQuota.dfs, new Path(quotaDir21, "nqdir33/file2"), (2 * fileLen), replication, 0);
        } catch (DSQuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        // delete nqdir33
        Assert.assertTrue(TestQuota.dfs.delete(new Path(quotaDir21, "nqdir33"), true));
        c = TestQuota.dfs.getContentSummary(quotaDir21);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir21);
        Assert.assertEquals(c.getSpaceConsumed(), fileSpace);
        Assert.assertEquals(c.getSpaceQuota(), (2 * fileSpace));
        // Verify space before the move:
        c = TestQuota.dfs.getContentSummary(quotaDir20);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir20);
        Assert.assertEquals(c.getSpaceConsumed(), 0);
        // Move nqdir0/qdir1/qdir21/nqdir32 nqdir0/qdir1/qdir20/nqdir30
        Path dstPath = new Path(quotaDir20, "nqdir30");
        Path srcPath = new Path(quotaDir21, "nqdir32");
        Assert.assertTrue(TestQuota.dfs.rename(srcPath, dstPath));
        // verify space after the move
        c = TestQuota.dfs.getContentSummary(quotaDir20);
        Assert.assertEquals(c.getSpaceConsumed(), fileSpace);
        // verify space for its parent
        c = TestQuota.dfs.getContentSummary(quotaDir1);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir1);
        Assert.assertEquals(c.getSpaceConsumed(), fileSpace);
        // verify space for source for the move
        c = TestQuota.dfs.getContentSummary(quotaDir21);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir21);
        Assert.assertEquals(c.getSpaceConsumed(), 0);
        final Path file2 = new Path(dstPath, "fileDir/file2");
        int file2Len = 2 * fileLen;
        // create a larger file under nqdir0/qdir1/qdir20/nqdir30
        DFSTestUtil.createFile(TestQuota.dfs, file2, file2Len, replication, 0);
        c = TestQuota.dfs.getContentSummary(quotaDir20);
        Assert.assertEquals(c.getSpaceConsumed(), (3 * fileSpace));
        c = TestQuota.dfs.getContentSummary(quotaDir21);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir21);
        Assert.assertEquals(c.getSpaceConsumed(), 0);
        // Reverse: Move nqdir0/qdir1/qdir20/nqdir30 to nqdir0/qdir1/qdir21/
        hasException = false;
        try {
            Assert.assertFalse(TestQuota.dfs.rename(dstPath, srcPath));
        } catch (DSQuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        // make sure no intermediate directories left by failed rename
        Assert.assertFalse(TestQuota.dfs.exists(srcPath));
        // directory should exist
        Assert.assertTrue(TestQuota.dfs.exists(dstPath));
        // verify space after the failed move
        c = TestQuota.dfs.getContentSummary(quotaDir20);
        Assert.assertEquals(c.getSpaceConsumed(), (3 * fileSpace));
        c = TestQuota.dfs.getContentSummary(quotaDir21);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir21);
        Assert.assertEquals(c.getSpaceConsumed(), 0);
        // Test Append :
        // verify space quota
        c = TestQuota.dfs.getContentSummary(quotaDir1);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir1);
        Assert.assertEquals(c.getSpaceQuota(), (4 * fileSpace));
        // verify space before append;
        c = TestQuota.dfs.getContentSummary(dstPath);
        compareQuotaUsage(c, TestQuota.dfs, dstPath);
        Assert.assertEquals(c.getSpaceConsumed(), (3 * fileSpace));
        OutputStream out = TestQuota.dfs.append(file2);
        // appending 1 fileLen should succeed
        out.write(new byte[fileLen]);
        out.close();
        file2Len += fileLen;// after append

        // verify space after append;
        c = TestQuota.dfs.getContentSummary(dstPath);
        compareQuotaUsage(c, TestQuota.dfs, dstPath);
        Assert.assertEquals(c.getSpaceConsumed(), (4 * fileSpace));
        // now increase the quota for quotaDir1
        TestQuota.dfs.setQuota(quotaDir1, QUOTA_DONT_SET, (5 * fileSpace));
        // Now, appending more than 1 fileLen should result in an error
        out = TestQuota.dfs.append(file2);
        hasException = false;
        try {
            out.write(new byte[fileLen + 1024]);
            out.flush();
            out.close();
        } catch (DSQuotaExceededException e) {
            hasException = true;
            IOUtils.closeStream(out);
        }
        Assert.assertTrue(hasException);
        file2Len += fileLen;// after partial append

        // verify space after partial append
        c = TestQuota.dfs.getContentSummary(dstPath);
        compareQuotaUsage(c, TestQuota.dfs, dstPath);
        Assert.assertEquals(c.getSpaceConsumed(), (5 * fileSpace));
        // Test set replication :
        // first reduce the replication
        TestQuota.dfs.setReplication(file2, ((short) (replication - 1)));
        // verify that space is reduced by file2Len
        c = TestQuota.dfs.getContentSummary(dstPath);
        compareQuotaUsage(c, TestQuota.dfs, dstPath);
        Assert.assertEquals(c.getSpaceConsumed(), ((5 * fileSpace) - file2Len));
        // now try to increase the replication and and expect an error.
        hasException = false;
        try {
            TestQuota.dfs.setReplication(file2, ((short) (replication + 1)));
        } catch (DSQuotaExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        // verify space consumed remains unchanged.
        c = TestQuota.dfs.getContentSummary(dstPath);
        compareQuotaUsage(c, TestQuota.dfs, dstPath);
        Assert.assertEquals(c.getSpaceConsumed(), ((5 * fileSpace) - file2Len));
        // now increase the quota for quotaDir1 and quotaDir20
        TestQuota.dfs.setQuota(quotaDir1, QUOTA_DONT_SET, (10 * fileSpace));
        TestQuota.dfs.setQuota(quotaDir20, QUOTA_DONT_SET, (10 * fileSpace));
        // then increasing replication should be ok.
        TestQuota.dfs.setReplication(file2, ((short) (replication + 1)));
        // verify increase in space
        c = TestQuota.dfs.getContentSummary(dstPath);
        compareQuotaUsage(c, TestQuota.dfs, dstPath);
        Assert.assertEquals(c.getSpaceConsumed(), ((5 * fileSpace) + file2Len));
        // Test HDFS-2053 :
        // Create directory hdfs-2053
        final Path quotaDir2053 = new Path(parent, "hdfs-2053");
        Assert.assertTrue(TestQuota.dfs.mkdirs(quotaDir2053));
        // Create subdirectories /hdfs-2053/{A,B,C}
        final Path quotaDir2053_A = new Path(quotaDir2053, "A");
        Assert.assertTrue(TestQuota.dfs.mkdirs(quotaDir2053_A));
        final Path quotaDir2053_B = new Path(quotaDir2053, "B");
        Assert.assertTrue(TestQuota.dfs.mkdirs(quotaDir2053_B));
        final Path quotaDir2053_C = new Path(quotaDir2053, "C");
        Assert.assertTrue(TestQuota.dfs.mkdirs(quotaDir2053_C));
        // Factors to vary the sizes of test files created in each subdir.
        // The actual factors are not really important but they allow us to create
        // identifiable file sizes per subdir, which helps during debugging.
        int sizeFactorA = 1;
        int sizeFactorB = 2;
        int sizeFactorC = 4;
        // Set space quota for subdirectory C
        TestQuota.dfs.setQuota(quotaDir2053_C, QUOTA_DONT_SET, ((sizeFactorC + 1) * fileSpace));
        c = TestQuota.dfs.getContentSummary(quotaDir2053_C);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir2053_C);
        Assert.assertEquals(c.getSpaceQuota(), ((sizeFactorC + 1) * fileSpace));
        // Create a file under subdirectory A
        DFSTestUtil.createFile(TestQuota.dfs, new Path(quotaDir2053_A, "fileA"), (sizeFactorA * fileLen), replication, 0);
        c = TestQuota.dfs.getContentSummary(quotaDir2053_A);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir2053_A);
        Assert.assertEquals(c.getSpaceConsumed(), (sizeFactorA * fileSpace));
        // Create a file under subdirectory B
        DFSTestUtil.createFile(TestQuota.dfs, new Path(quotaDir2053_B, "fileB"), (sizeFactorB * fileLen), replication, 0);
        c = TestQuota.dfs.getContentSummary(quotaDir2053_B);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir2053_B);
        Assert.assertEquals(c.getSpaceConsumed(), (sizeFactorB * fileSpace));
        // Create a file under subdirectory C (which has a space quota)
        DFSTestUtil.createFile(TestQuota.dfs, new Path(quotaDir2053_C, "fileC"), (sizeFactorC * fileLen), replication, 0);
        c = TestQuota.dfs.getContentSummary(quotaDir2053_C);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir2053_C);
        Assert.assertEquals(c.getSpaceConsumed(), (sizeFactorC * fileSpace));
        // Check space consumed for /hdfs-2053
        c = TestQuota.dfs.getContentSummary(quotaDir2053);
        compareQuotaUsage(c, TestQuota.dfs, quotaDir2053);
        Assert.assertEquals(c.getSpaceConsumed(), (((sizeFactorA + sizeFactorB) + sizeFactorC) * fileSpace));
    }

    /**
     * Test quota by storage type.
     */
    @Test
    public void testQuotaByStorageType() throws Exception {
        final Path parent = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(parent));
        int fileLen = 1024;
        short replication = 3;
        int fileSpace = fileLen * replication;
        final Path quotaDir20 = new Path(parent, "nqdir0/qdir1/qdir20");
        Assert.assertTrue(TestQuota.dfs.mkdirs(quotaDir20));
        TestQuota.dfs.setQuota(quotaDir20, QUOTA_DONT_SET, (6 * fileSpace));
        // Verify DirectoryWithQuotaFeature's storage type usage
        // is updated properly after deletion.
        // File creation followed by deletion shouldn't change storage type
        // usage regardless whether storage policy is set.
        Path file = new Path(quotaDir20, "fileDir/file1");
        DFSTestUtil.createFile(TestQuota.dfs, file, (fileLen * 3), replication, 0);
        TestQuota.dfs.delete(file, false);
        TestQuota.dfs.setStoragePolicy(quotaDir20, HOT_STORAGE_POLICY_NAME);
        TestQuota.dfs.setQuotaByStorageType(quotaDir20, DEFAULT, (2 * fileSpace));
        boolean hasException = false;
        try {
            DFSTestUtil.createFile(TestQuota.dfs, file, (fileLen * 3), replication, 0);
        } catch (QuotaByStorageTypeExceededException e) {
            hasException = true;
        }
        Assert.assertTrue(hasException);
        TestQuota.dfs.delete(file, false);
        TestQuota.dfs.setQuotaByStorageType(quotaDir20, DEFAULT, (6 * fileSpace));
    }

    /**
     * Test limit cases for setting space quotas.
     */
    @Test
    public void testMaxSpaceQuotas() throws Exception {
        final Path parent = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(parent));
        final FileSystem fs = TestQuota.cluster.getFileSystem();
        Assert.assertTrue(("Not a HDFS: " + (fs.getUri())), (fs instanceof DistributedFileSystem));
        final DistributedFileSystem dfs = ((DistributedFileSystem) (fs));
        // create test directory
        final Path testFolder = new Path(parent, "testFolder");
        Assert.assertTrue(dfs.mkdirs(testFolder));
        // setting namespace quota to Long.MAX_VALUE - 1 should work
        dfs.setQuota(testFolder, ((Long.MAX_VALUE) - 1), 10);
        ContentSummary c = dfs.getContentSummary(testFolder);
        compareQuotaUsage(c, dfs, testFolder);
        Assert.assertTrue("Quota not set properly", ((c.getQuota()) == ((Long.MAX_VALUE) - 1)));
        // setting diskspace quota to Long.MAX_VALUE - 1 should work
        dfs.setQuota(testFolder, 10, ((Long.MAX_VALUE) - 1));
        c = dfs.getContentSummary(testFolder);
        compareQuotaUsage(c, dfs, testFolder);
        Assert.assertTrue("Quota not set properly", ((c.getSpaceQuota()) == ((Long.MAX_VALUE) - 1)));
        // setting namespace quota to Long.MAX_VALUE should not work + no error
        dfs.setQuota(testFolder, Long.MAX_VALUE, 10);
        c = dfs.getContentSummary(testFolder);
        compareQuotaUsage(c, dfs, testFolder);
        Assert.assertTrue("Quota should not have changed", ((c.getQuota()) == 10));
        // setting diskspace quota to Long.MAX_VALUE should not work + no error
        dfs.setQuota(testFolder, 10, Long.MAX_VALUE);
        c = dfs.getContentSummary(testFolder);
        compareQuotaUsage(c, dfs, testFolder);
        Assert.assertTrue("Quota should not have changed", ((c.getSpaceQuota()) == 10));
        // setting namespace quota to Long.MAX_VALUE + 1 should not work + error
        try {
            dfs.setQuota(testFolder, ((Long.MAX_VALUE) + 1), 10);
            Assert.fail("Exception not thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        // setting diskspace quota to Long.MAX_VALUE + 1 should not work + error
        try {
            dfs.setQuota(testFolder, 10, ((Long.MAX_VALUE) + 1));
            Assert.fail("Exception not thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    /**
     * Violate a space quota using files of size < 1 block. Test that block
     * allocation conservatively assumes that for quota checking the entire
     * space of the block is used.
     */
    @Test
    public void testBlockAllocationAdjustsUsageConservatively() throws Exception {
        final Path parent = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(parent));
        DFSAdmin admin = new DFSAdmin(TestQuota.conf);
        Path dir = new Path(parent, "test");
        Path file1 = new Path(parent, "test/test1");
        Path file2 = new Path(parent, "test/test2");
        boolean exceededQuota = false;
        final int QUOTA_SIZE = 3 * (TestQuota.DEFAULT_BLOCK_SIZE);// total space usage including

        // repl.
        final int FILE_SIZE = (TestQuota.DEFAULT_BLOCK_SIZE) / 2;
        ContentSummary c;
        // Create the directory and set the quota
        Assert.assertTrue(TestQuota.dfs.mkdirs(dir));
        runCommand(admin, false, "-setSpaceQuota", Integer.toString(QUOTA_SIZE), dir.toString());
        // Creating a file should use half the quota
        DFSTestUtil.createFile(TestQuota.dfs, file1, FILE_SIZE, ((short) (3)), 1L);
        DFSTestUtil.waitReplication(TestQuota.dfs, file1, ((short) (3)));
        c = TestQuota.dfs.getContentSummary(dir);
        compareQuotaUsage(c, TestQuota.dfs, dir);
        TestQuota.checkContentSummary(c, TestQuota.webhdfs.getContentSummary(dir));
        Assert.assertEquals("Quota is half consumed", (QUOTA_SIZE / 2), c.getSpaceConsumed());
        // We can not create the 2nd file because even though the total spaced
        // used by two files (2 * 3 * 512/2) would fit within the quota (3 * 512)
        // when a block for a file is created the space used is adjusted
        // conservatively (3 * block size, ie assumes a full block is written)
        // which will violate the quota (3 * block size) since we've already
        // used half the quota for the first file.
        try {
            DFSTestUtil.createFile(TestQuota.dfs, file2, FILE_SIZE, ((short) (3)), 1L);
        } catch (QuotaExceededException e) {
            exceededQuota = true;
        }
        Assert.assertTrue("Quota not exceeded", exceededQuota);
    }

    /**
     * Like the previous test but create many files. This covers bugs where
     * the quota adjustment is incorrect but it takes many files to accrue
     * a big enough accounting error to violate the quota.
     */
    @Test
    public void testMultipleFilesSmallerThanOneBlock() throws Exception {
        final Path parent = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(parent));
        Configuration dfsConf = new HdfsConfiguration();
        final int BLOCK_SIZE = 6 * 1024;
        dfsConf.setInt(DFS_BLOCK_SIZE_KEY, BLOCK_SIZE);
        // Make it relinquish locks. When run serially, the result should
        // be identical.
        dfsConf.setInt(DFS_CONTENT_SUMMARY_LIMIT_KEY, 2);
        MiniDFSCluster dfsCluster = new MiniDFSCluster.Builder(dfsConf).numDataNodes(3).build();
        dfsCluster.waitActive();
        FileSystem fs = dfsCluster.getFileSystem();
        DFSAdmin admin = new DFSAdmin(dfsConf);
        final String nnAddr = dfsConf.get(DFS_NAMENODE_HTTP_ADDRESS_KEY);
        final String webhdfsuri = ((WebHdfsConstants.WEBHDFS_SCHEME) + "://") + nnAddr;
        System.out.println(("webhdfsuri=" + webhdfsuri));
        final FileSystem webHDFS = new Path(webhdfsuri).getFileSystem(dfsConf);
        try {
            // Test for deafult NameSpace Quota
            long nsQuota = FSImageTestUtil.getNSQuota(dfsCluster.getNameNode().getNamesystem());
            Assert.assertTrue(("Default namespace quota expected as long max. But the value is :" + nsQuota), (nsQuota == (Long.MAX_VALUE)));
            Path dir = new Path(parent, "test");
            boolean exceededQuota = false;
            ContentSummary c;
            // 1kb file
            // 6kb block
            // 192kb quota
            final int FILE_SIZE = 1024;
            final int QUOTA_SIZE = 32 * ((int) (fs.getDefaultBlockSize(dir)));
            Assert.assertEquals((6 * 1024), fs.getDefaultBlockSize(dir));
            Assert.assertEquals((192 * 1024), QUOTA_SIZE);
            // Create the dir and set the quota. We need to enable the quota before
            // writing the files as setting the quota afterwards will over-write
            // the cached disk space used for quota verification with the actual
            // amount used as calculated by INode#spaceConsumedInTree.
            Assert.assertTrue(fs.mkdirs(dir));
            runCommand(admin, false, "-setSpaceQuota", Integer.toString(QUOTA_SIZE), dir.toString());
            // We can create at most 59 files because block allocation is
            // conservative and initially assumes a full block is used, so we
            // need to leave at least 3 * BLOCK_SIZE free space when allocating
            // the last block: (58 * 3 * 1024) (3 * 6 * 1024) = 192kb
            for (int i = 0; i < 59; i++) {
                Path file = new Path(parent, ("test/test" + i));
                DFSTestUtil.createFile(fs, file, FILE_SIZE, ((short) (3)), 1L);
                DFSTestUtil.waitReplication(fs, file, ((short) (3)));
            }
            // Should account for all 59 files (almost QUOTA_SIZE)
            c = fs.getContentSummary(dir);
            compareQuotaUsage(c, fs, dir);
            TestQuota.checkContentSummary(c, webHDFS.getContentSummary(dir));
            Assert.assertEquals("Invalid space consumed", ((59 * FILE_SIZE) * 3), c.getSpaceConsumed());
            Assert.assertEquals("Invalid space consumed", (QUOTA_SIZE - ((59 * FILE_SIZE) * 3)), (3 * ((fs.getDefaultBlockSize(dir)) - FILE_SIZE)));
            // Now check that trying to create another file violates the quota
            try {
                Path file = new Path(parent, "test/test59");
                DFSTestUtil.createFile(fs, file, FILE_SIZE, ((short) (3)), 1L);
                DFSTestUtil.waitReplication(fs, file, ((short) (3)));
            } catch (QuotaExceededException e) {
                exceededQuota = true;
            }
            Assert.assertTrue("Quota not exceeded", exceededQuota);
            Assert.assertEquals(2, dfsCluster.getNamesystem().getFSDirectory().getYieldCount());
        } finally {
            dfsCluster.shutdown();
        }
    }

    @Test
    public void testSetSpaceQuotaWhenStorageTypeIsWrong() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, "hdfs://127.0.0.1:8020");
        DFSAdmin admin = new DFSAdmin(conf);
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PrintStream oldErr = System.err;
        try {
            System.setErr(new PrintStream(err));
            String[] args = new String[]{ "-setSpaceQuota", "100", "-storageType", "COLD", "/testDir" };
            admin.run(args);
            String errOutput = new String(err.toByteArray(), Charsets.UTF_8);
            Assert.assertTrue(errOutput.contains(StorageType.getTypesSupportingQuota().toString()));
        } finally {
            System.setErr(oldErr);
        }
    }

    /**
     * File count on root , should return total value of files in Filesystem
     * when one folder contains files more than "dfs.content-summary.limit".
     */
    @Test
    public void testHugeFileCount() throws IOException {
        final Path parent = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(parent));
        for (int i = 1; i <= 5; i++) {
            FSDataOutputStream out = TestQuota.dfs.create(new Path(parent, (("Folder1/" + "file") + i)), ((short) (1)));
            out.close();
        }
        FSDataOutputStream out = TestQuota.dfs.create(new Path(parent, "Folder2/file6"), ((short) (1)));
        out.close();
        ContentSummary contentSummary = TestQuota.dfs.getContentSummary(parent);
        compareQuotaUsage(contentSummary, TestQuota.dfs, parent);
        Assert.assertEquals(6, contentSummary.getFileCount());
    }

    /**
     * Test to set space quote using negative number.
     */
    @Test(timeout = 30000)
    public void testSetSpaceQuotaNegativeNumber() throws Exception {
        final DFSAdmin dfsAdmin = new DFSAdmin(TestQuota.conf);
        final Path dir = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(dir));
        final List<String> outs = Lists.newArrayList();
        /* set space quota */
        TestQuota.resetStream();
        outs.clear();
        final int ret = ToolRunner.run(dfsAdmin, new String[]{ "-setSpaceQuota", "-10", dir.toString() });
        Assert.assertEquals((-1), ret);
        TestQuota.scanIntoList(TestQuota.ERR_STREAM, outs);
        Assert.assertEquals(("It should be two lines of error messages," + (" the 1st one is about Illegal option," + " the 2nd one is about SetSpaceQuota usage.")), 2, outs.size());
        Assert.assertThat(outs.get(0), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("setSpaceQuota"), CoreMatchers.containsString("Illegal option"))));
    }

    /**
     * Test to set and clear space quote, regular usage.
     */
    @Test(timeout = 30000)
    public void testSetAndClearSpaceQuotaRegular() throws Exception {
        final Path dir = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(dir));
        /* set space quota */
        testSetAndClearSpaceQuotaRegularInternal(new String[]{ "-setSpaceQuota", "1024", dir.toString() }, dir, 0, 1024);
        /* clear space quota */
        testSetAndClearSpaceQuotaRegularInternal(new String[]{ "-clrSpaceQuota", dir.toString() }, dir, 0, (-1));
    }

    /**
     * Test to all the commands by passing the fully qualified path.
     */
    @Test(timeout = 30000)
    public void testQuotaCommandsWithURI() throws Exception {
        DFSAdmin dfsAdmin = new DFSAdmin(TestQuota.conf);
        final Path dir = new Path(("/" + (this.getClass().getSimpleName())), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(dir));
        /* set space quota */
        testSetAndClearSpaceQuotaRegularInternal(new String[]{ "-setSpaceQuota", "1024", ((TestQuota.dfs.getUri()) + "/") + (dir.toString()) }, dir, 0, 1024);
        /* clear space quota */
        testSetAndClearSpaceQuotaRegularInternal(new String[]{ "-clrSpaceQuota", ((TestQuota.dfs.getUri()) + "/") + (dir.toString()) }, dir, 0, (-1));
        runCommand(dfsAdmin, false, "-setQuota", "1000", (((TestQuota.dfs.getUri()) + "/") + (dir.toString())));
        runCommand(dfsAdmin, false, "-clrQuota", (((TestQuota.dfs.getUri()) + "/") + (dir.toString())));
    }

    /**
     * Test to set and clear space quote by storage type.
     */
    @Test(timeout = 30000)
    public void testSetAndClearSpaceQuotaByStorageType() throws Exception {
        final Path dir = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(dir));
        /* set space quota */
        testSetAndClearSpaceQuotaByStorageTypeInternal(new String[]{ "-setSpaceQuota", "2048", "-storageType", "DISK", dir.toString() }, dir, 0, (-1), 2048);
        /* clear space quota */
        testSetAndClearSpaceQuotaByStorageTypeInternal(new String[]{ "-clrSpaceQuota", "-storageType", "DISK", dir.toString() }, dir, 0, (-1), (-1));
    }

    /**
     * Test to set and clear space quote when directory doesn't exist.
     */
    @Test(timeout = 30000)
    public void testSetAndClearSpaceQuotaDirectoryNotExist() throws Exception {
        final Path dir = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        /* set space quota */
        testSetAndClearSpaceQuotaDirectoryNotExistInternal(new String[]{ "-setSpaceQuota", "1024", dir.toString() }, dir, (-1), "setSpaceQuota");
        /* clear space quota */
        testSetAndClearSpaceQuotaDirectoryNotExistInternal(new String[]{ "-clrSpaceQuota", dir.toString() }, dir, (-1), "clrSpaceQuota");
    }

    /**
     * Test to set and clear space quote when path is a file.
     */
    @Test(timeout = 30000)
    public void testSetAndClearSpaceQuotaPathIsFile() throws Exception {
        final Path parent = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        final Path file = new Path(parent, "path-is-file");
        DFSTestUtil.createFile(TestQuota.dfs, file, 1024L, ((short) (1L)), 0);
        Assert.assertTrue(TestQuota.dfs.isFile(file));
        /* set space quota */
        testSetAndClearSpaceQuotaPathIsFileInternal(new String[]{ "-setSpaceQuota", "1024", file.toString() }, file, (-1), "setSpaceQuota");
        /* clear space quota */
        testSetAndClearSpaceQuotaPathIsFileInternal(new String[]{ "-clrSpaceQuota", file.toString() }, file, (-1), "clrSpaceQuota");
    }

    /**
     * Test to set and clear space quote when user has no access right.
     */
    @Test(timeout = 30000)
    public void testSetAndClearSpaceQuotaNoAccess() throws Exception {
        final Path dir = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(dir));
        /* set space quota */
        testSetAndClearSpaceQuotaNoAccessInternal(new String[]{ "-setSpaceQuota", "2048", dir.toString() }, (-1), "setSpaceQuota");
        /* clear space quota */
        testSetAndClearSpaceQuotaNoAccessInternal(new String[]{ "-clrSpaceQuota", dir.toString() }, (-1), "clrSpaceQuota");
    }

    @Test
    public void testSpaceQuotaExceptionOnClose() throws Exception {
        GenericTestUtils.setLogLevel(DFSOutputStream.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(DataStreamer.LOG, Level.TRACE);
        final DFSAdmin dfsAdmin = new DFSAdmin(TestQuota.conf);
        final Path dir = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(dir));
        final String[] args = new String[]{ "-setSpaceQuota", "1", dir.toString() };
        Assert.assertEquals(0, ToolRunner.run(dfsAdmin, args));
        final Path testFile = new Path(dir, "file");
        final FSDataOutputStream stream = TestQuota.dfs.create(testFile);
        stream.write("whatever".getBytes());
        try {
            stream.close();
            Assert.fail("close should fail");
        } catch (DSQuotaExceededException expected) {
        }
        Assert.assertEquals(0, TestQuota.cluster.getNamesystem().getNumFilesUnderConstruction());
    }

    @Test
    public void testSpaceQuotaExceptionOnFlush() throws Exception {
        GenericTestUtils.setLogLevel(DFSOutputStream.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(DataStreamer.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(DFSClient.LOG, Level.TRACE);
        final DFSAdmin dfsAdmin = new DFSAdmin(TestQuota.conf);
        final Path dir = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        Assert.assertTrue(TestQuota.dfs.mkdirs(dir));
        final String[] args = new String[]{ "-setSpaceQuota", "1", dir.toString() };
        Assert.assertEquals(0, ToolRunner.run(dfsAdmin, args));
        Path testFile = new Path(dir, "file");
        FSDataOutputStream stream = TestQuota.dfs.create(testFile);
        // get the lease renewer now so we can verify it later without calling
        // getLeaseRenewer, which will automatically add the client into it.
        final LeaseRenewer leaseRenewer = TestQuota.dfs.getClient().getLeaseRenewer();
        stream.write("whatever".getBytes());
        try {
            stream.hflush();
            Assert.fail("flush should fail");
        } catch (DSQuotaExceededException expected) {
        }
        // even if we close the stream in finially, it won't help.
        try {
            stream.close();
            Assert.fail("close should fail too");
        } catch (DSQuotaExceededException expected) {
        }
        GenericTestUtils.setLogLevel(LeaseRenewer.LOG, Level.TRACE);
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                TestQuota.LOG.info("LeaseRenewer: {}", leaseRenewer);
                return leaseRenewer.isEmpty();
            }
        }, 100, 10000);
        Assert.assertEquals(0, TestQuota.cluster.getNamesystem().getNumFilesUnderConstruction());
    }

    @Test
    public void testClrQuotaOnRoot() throws Exception {
        long orignalQuota = TestQuota.dfs.getQuotaUsage(new Path("/")).getQuota();
        DFSAdmin admin = new DFSAdmin(TestQuota.conf);
        String[] args;
        args = new String[]{ "-setQuota", "3K", "/" };
        runCommand(admin, args, false);
        Assert.assertEquals((3 * 1024), TestQuota.dfs.getQuotaUsage(new Path("/")).getQuota());
        args = new String[]{ "-clrQuota", "/" };
        runCommand(admin, args, false);
        Assert.assertEquals(orignalQuota, TestQuota.dfs.getQuotaUsage(new Path("/")).getQuota());
    }

    @Test
    public void testSpaceQuotaExceptionOnAppend() throws Exception {
        GenericTestUtils.setLogLevel(DFSOutputStream.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(DataStreamer.LOG, Level.TRACE);
        final DFSAdmin dfsAdmin = new DFSAdmin(TestQuota.conf);
        final Path dir = new Path(PathUtils.getTestDir(getClass()).getPath(), GenericTestUtils.getMethodName());
        TestQuota.dfs.delete(dir, true);
        Assert.assertTrue(TestQuota.dfs.mkdirs(dir));
        final String[] args = new String[]{ "-setSpaceQuota", "4000", dir.toString() };
        ToolRunner.run(dfsAdmin, args);
        final Path testFile = new Path(dir, "file");
        OutputStream stream = TestQuota.dfs.create(testFile);
        stream.write("whatever".getBytes());
        stream.close();
        Assert.assertEquals(0, TestQuota.cluster.getNamesystem().getNumFilesUnderConstruction());
        stream = TestQuota.dfs.append(testFile);
        byte[] buf = AppendTestUtil.initBuffer(4096);
        stream.write(buf);
        try {
            stream.close();
            Assert.fail("close after append should fail");
        } catch (DSQuotaExceededException expected) {
        }
        Assert.assertEquals(0, TestQuota.cluster.getNamesystem().getNumFilesUnderConstruction());
    }
}

