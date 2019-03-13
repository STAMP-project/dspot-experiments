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


import CommonConfigurationKeysPublic.HADOOP_SECURITY_KEY_PROVIDER_PATH;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockStoragePolicySpi;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hdfs.protocol.BlockStoragePolicy;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockStoragePolicySuite;
import org.junit.Assert;
import org.junit.Test;


public class TestHdfsAdmin {
    private static final Path TEST_PATH = new Path("/test");

    private static final short REPL = 1;

    private static final int SIZE = 128;

    private static final int OPEN_FILES_BATCH_SIZE = 5;

    private final Configuration conf = new Configuration();

    private MiniDFSCluster cluster;

    /**
     * Test that we can set and clear quotas via {@link HdfsAdmin}.
     */
    @Test
    public void testHdfsAdminSetQuota() throws Exception {
        HdfsAdmin dfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
            Assert.assertTrue(fs.mkdirs(TestHdfsAdmin.TEST_PATH));
            Assert.assertEquals((-1), fs.getContentSummary(TestHdfsAdmin.TEST_PATH).getQuota());
            Assert.assertEquals((-1), fs.getContentSummary(TestHdfsAdmin.TEST_PATH).getSpaceQuota());
            dfsAdmin.setSpaceQuota(TestHdfsAdmin.TEST_PATH, 10);
            Assert.assertEquals((-1), fs.getContentSummary(TestHdfsAdmin.TEST_PATH).getQuota());
            Assert.assertEquals(10, fs.getContentSummary(TestHdfsAdmin.TEST_PATH).getSpaceQuota());
            dfsAdmin.setQuota(TestHdfsAdmin.TEST_PATH, 10);
            Assert.assertEquals(10, fs.getContentSummary(TestHdfsAdmin.TEST_PATH).getQuota());
            Assert.assertEquals(10, fs.getContentSummary(TestHdfsAdmin.TEST_PATH).getSpaceQuota());
            dfsAdmin.clearSpaceQuota(TestHdfsAdmin.TEST_PATH);
            Assert.assertEquals(10, fs.getContentSummary(TestHdfsAdmin.TEST_PATH).getQuota());
            Assert.assertEquals((-1), fs.getContentSummary(TestHdfsAdmin.TEST_PATH).getSpaceQuota());
            dfsAdmin.clearQuota(TestHdfsAdmin.TEST_PATH);
            Assert.assertEquals((-1), fs.getContentSummary(TestHdfsAdmin.TEST_PATH).getQuota());
            Assert.assertEquals((-1), fs.getContentSummary(TestHdfsAdmin.TEST_PATH).getSpaceQuota());
        } finally {
            if (fs != null) {
                fs.close();
            }
        }
    }

    /**
     * Make sure that a non-HDFS URI throws a helpful error.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testHdfsAdminWithBadUri() throws IOException, URISyntaxException {
        new HdfsAdmin(new URI("file:///bad-scheme"), conf);
    }

    /**
     * Test that we can set, get, unset storage policies via {@link HdfsAdmin}.
     */
    @Test
    public void testHdfsAdminStoragePolicies() throws Exception {
        HdfsAdmin hdfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
        FileSystem fs = FileSystem.get(conf);
        final Path foo = new Path("/foo");
        final Path bar = new Path(foo, "bar");
        final Path wow = new Path(bar, "wow");
        DFSTestUtil.createFile(fs, wow, TestHdfsAdmin.SIZE, TestHdfsAdmin.REPL, 0);
        final BlockStoragePolicySuite suite = BlockStoragePolicySuite.createDefaultSuite();
        final BlockStoragePolicy warm = suite.getPolicy("WARM");
        final BlockStoragePolicy cold = suite.getPolicy("COLD");
        final BlockStoragePolicy hot = suite.getPolicy("HOT");
        /* test: set storage policy */
        hdfsAdmin.setStoragePolicy(foo, warm.getName());
        hdfsAdmin.setStoragePolicy(bar, cold.getName());
        hdfsAdmin.setStoragePolicy(wow, hot.getName());
        /* test: get storage policy after set */
        Assert.assertEquals(hdfsAdmin.getStoragePolicy(foo), warm);
        Assert.assertEquals(hdfsAdmin.getStoragePolicy(bar), cold);
        Assert.assertEquals(hdfsAdmin.getStoragePolicy(wow), hot);
        /* test: unset storage policy */
        hdfsAdmin.unsetStoragePolicy(foo);
        hdfsAdmin.unsetStoragePolicy(bar);
        hdfsAdmin.unsetStoragePolicy(wow);
        /* test: get storage policy after unset. HOT by default. */
        Assert.assertEquals(hdfsAdmin.getStoragePolicy(foo), hot);
        Assert.assertEquals(hdfsAdmin.getStoragePolicy(bar), hot);
        Assert.assertEquals(hdfsAdmin.getStoragePolicy(wow), hot);
        /* test: get all storage policies */
        // Get policies via HdfsAdmin
        Set<String> policyNamesSet1 = new HashSet<>();
        for (BlockStoragePolicySpi policy : hdfsAdmin.getAllStoragePolicies()) {
            policyNamesSet1.add(policy.getName());
        }
        // Get policies via BlockStoragePolicySuite
        Set<String> policyNamesSet2 = new HashSet<>();
        for (BlockStoragePolicy policy : suite.getAllPolicies()) {
            policyNamesSet2.add(policy.getName());
        }
        // Ensure that we got the same set of policies in both cases.
        Assert.assertTrue(Sets.difference(policyNamesSet1, policyNamesSet2).isEmpty());
        Assert.assertTrue(Sets.difference(policyNamesSet2, policyNamesSet1).isEmpty());
    }

    @Test
    public void testGetKeyProvider() throws IOException {
        HdfsAdmin hdfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
        Assert.assertNull("should return null for an non-encrypted cluster", hdfsAdmin.getKeyProvider());
        shutDownCluster();
        Configuration conf = new Configuration();
        conf.set(HADOOP_SECURITY_KEY_PROVIDER_PATH, TestHdfsAdmin.getKeyProviderURI());
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).build();
        cluster.waitActive();
        hdfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
        Assert.assertNotNull("should not return null for an encrypted cluster", hdfsAdmin.getKeyProvider());
    }

    @Test(timeout = 120000L)
    public void testListOpenFiles() throws IOException {
        HashSet<Path> closedFileSet = new HashSet<>();
        HashMap<Path, FSDataOutputStream> openFileMap = new HashMap<>();
        FileSystem fs = FileSystem.get(conf);
        verifyOpenFiles(closedFileSet, openFileMap);
        int numClosedFiles = (TestHdfsAdmin.OPEN_FILES_BATCH_SIZE) * 4;
        int numOpenFiles = ((TestHdfsAdmin.OPEN_FILES_BATCH_SIZE) * 3) + 1;
        for (int i = 0; i < numClosedFiles; i++) {
            Path filePath = new Path(("/closed-file-" + i));
            DFSTestUtil.createFile(fs, filePath, TestHdfsAdmin.SIZE, TestHdfsAdmin.REPL, 0);
            closedFileSet.add(filePath);
        }
        verifyOpenFiles(closedFileSet, openFileMap);
        openFileMap.putAll(DFSTestUtil.createOpenFiles(fs, "open-file-1", numOpenFiles));
        verifyOpenFiles(closedFileSet, openFileMap);
        closedFileSet.addAll(DFSTestUtil.closeOpenFiles(openFileMap, ((openFileMap.size()) / 2)));
        verifyOpenFiles(closedFileSet, openFileMap);
        openFileMap.putAll(DFSTestUtil.createOpenFiles(fs, "open-file-2", 10));
        verifyOpenFiles(closedFileSet, openFileMap);
        while ((openFileMap.size()) > 0) {
            closedFileSet.addAll(DFSTestUtil.closeOpenFiles(openFileMap, 1));
            verifyOpenFiles(closedFileSet, openFileMap);
        } 
    }
}

