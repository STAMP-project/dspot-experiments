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
package org.apache.hadoop.hdfs.server.namenode.ha;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HAUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.ipc.StandbyException;
import org.junit.Assert;
import org.junit.Test;


public class TestQuotasWithHA {
    private static final Path TEST_DIR = new Path("/test");

    private static final Path TEST_FILE = new Path(TestQuotasWithHA.TEST_DIR, "file");

    private static final String TEST_DIR_STR = TestQuotasWithHA.TEST_DIR.toUri().getPath();

    private static final long NS_QUOTA = 10000;

    private static final long DS_QUOTA = 10000;

    private static final long BLOCK_SIZE = 1024;// 1KB blocks


    private MiniDFSCluster cluster;

    private NameNode nn0;

    private NameNode nn1;

    private FileSystem fs;

    /**
     * Test that quotas are properly tracked by the standby through
     * create, append, delete.
     */
    @Test(timeout = 60000)
    public void testQuotasTrackedOnStandby() throws Exception {
        fs.mkdirs(TestQuotasWithHA.TEST_DIR);
        DistributedFileSystem dfs = ((DistributedFileSystem) (fs));
        dfs.setQuota(TestQuotasWithHA.TEST_DIR, TestQuotasWithHA.NS_QUOTA, TestQuotasWithHA.DS_QUOTA);
        long expectedSize = (3 * (TestQuotasWithHA.BLOCK_SIZE)) + ((TestQuotasWithHA.BLOCK_SIZE) / 2);
        DFSTestUtil.createFile(fs, TestQuotasWithHA.TEST_FILE, expectedSize, ((short) (1)), 1L);
        HATestUtil.waitForStandbyToCatchUp(nn0, nn1);
        ContentSummary cs = nn1.getRpcServer().getContentSummary(TestQuotasWithHA.TEST_DIR_STR);
        Assert.assertEquals(TestQuotasWithHA.NS_QUOTA, cs.getQuota());
        Assert.assertEquals(TestQuotasWithHA.DS_QUOTA, cs.getSpaceQuota());
        Assert.assertEquals(expectedSize, cs.getSpaceConsumed());
        Assert.assertEquals(1, cs.getDirectoryCount());
        Assert.assertEquals(1, cs.getFileCount());
        // Append to the file and make sure quota is updated correctly.
        FSDataOutputStream stm = fs.append(TestQuotasWithHA.TEST_FILE);
        try {
            byte[] data = new byte[((int) (((TestQuotasWithHA.BLOCK_SIZE) * 3) / 2))];
            stm.write(data);
            expectedSize += data.length;
        } finally {
            IOUtils.closeStream(stm);
        }
        HATestUtil.waitForStandbyToCatchUp(nn0, nn1);
        cs = nn1.getRpcServer().getContentSummary(TestQuotasWithHA.TEST_DIR_STR);
        Assert.assertEquals(TestQuotasWithHA.NS_QUOTA, cs.getQuota());
        Assert.assertEquals(TestQuotasWithHA.DS_QUOTA, cs.getSpaceQuota());
        Assert.assertEquals(expectedSize, cs.getSpaceConsumed());
        Assert.assertEquals(1, cs.getDirectoryCount());
        Assert.assertEquals(1, cs.getFileCount());
        fs.delete(TestQuotasWithHA.TEST_FILE, true);
        expectedSize = 0;
        HATestUtil.waitForStandbyToCatchUp(nn0, nn1);
        cs = nn1.getRpcServer().getContentSummary(TestQuotasWithHA.TEST_DIR_STR);
        Assert.assertEquals(TestQuotasWithHA.NS_QUOTA, cs.getQuota());
        Assert.assertEquals(TestQuotasWithHA.DS_QUOTA, cs.getSpaceQuota());
        Assert.assertEquals(expectedSize, cs.getSpaceConsumed());
        Assert.assertEquals(1, cs.getDirectoryCount());
        Assert.assertEquals(0, cs.getFileCount());
    }

    /**
     * Test that getContentSummary on Standby should should throw standby
     * exception.
     */
    @Test(expected = StandbyException.class)
    public void testGetContentSummaryOnStandby() throws Exception {
        Configuration nn1conf = cluster.getConfiguration(1);
        // just reset the standby reads to default i.e False on standby.
        HAUtil.setAllowStandbyReads(nn1conf, false);
        cluster.restartNameNode(1);
        cluster.getNameNodeRpc(1).getContentSummary("/");
    }

    /**
     * Test that getQuotaUsage on Standby should should throw standby exception.
     */
    @Test(expected = StandbyException.class)
    public void testGetQuotaUsageOnStandby() throws Exception {
        Configuration nn1conf = cluster.getConfiguration(1);
        // just reset the standby reads to default i.e False on standby.
        HAUtil.setAllowStandbyReads(nn1conf, false);
        cluster.restartNameNode(1);
        cluster.getNameNodeRpc(1).getQuotaUsage("/");
    }
}

