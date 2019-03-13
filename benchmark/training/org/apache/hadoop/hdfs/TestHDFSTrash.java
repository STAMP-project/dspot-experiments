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


import CommonConfigurationKeys.FS_DEFAULT_NAME_KEY;
import CommonConfigurationKeys.FS_TRASH_INTERVAL_KEY;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.TestTrash;
import org.apache.hadoop.fs.Trash;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test trash using HDFS
 */
public class TestHDFSTrash {
    public static final Logger LOG = LoggerFactory.getLogger(TestHDFSTrash.class);

    private static MiniDFSCluster cluster = null;

    private static FileSystem fs;

    private static Configuration conf = new HdfsConfiguration();

    private static final Path TEST_ROOT = new Path("/TestHDFSTrash-ROOT");

    private static final Path TRASH_ROOT = new Path("/TestHDFSTrash-TRASH");

    private static final String GROUP1_NAME = "group1";

    private static final String GROUP2_NAME = "group2";

    private static final String GROUP3_NAME = "group3";

    private static final String USER1_NAME = "user1";

    private static final String USER2_NAME = "user2";

    private static UserGroupInformation superUser;

    private static UserGroupInformation user1;

    private static UserGroupInformation user2;

    @Test
    public void testTrash() throws IOException {
        TestTrash.trashShell(TestHDFSTrash.cluster.getFileSystem(), new Path("/"));
    }

    @Test
    public void testNonDefaultFS() throws IOException {
        FileSystem fileSystem = TestHDFSTrash.cluster.getFileSystem();
        Configuration config = fileSystem.getConf();
        config.set(FS_DEFAULT_NAME_KEY, fileSystem.getUri().toString());
        TestTrash.trashNonDefaultFS(config);
    }

    @Test
    public void testHDFSTrashPermission() throws IOException {
        FileSystem fileSystem = TestHDFSTrash.cluster.getFileSystem();
        Configuration config = fileSystem.getConf();
        config.set(FS_TRASH_INTERVAL_KEY, "0.2");
        TestTrash.verifyTrashPermission(fileSystem, config);
    }

    @Test
    public void testMoveEmptyDirToTrash() throws IOException {
        FileSystem fileSystem = TestHDFSTrash.cluster.getFileSystem();
        Configuration config = fileSystem.getConf();
        config.set(FS_TRASH_INTERVAL_KEY, "1");
        TestTrash.verifyMoveEmptyDirToTrash(fileSystem, config);
    }

    @Test
    public void testDeleteTrash() throws Exception {
        Configuration testConf = new Configuration(TestHDFSTrash.conf);
        testConf.set(FS_TRASH_INTERVAL_KEY, "10");
        Path user1Tmp = new Path(TestHDFSTrash.TEST_ROOT, "test-del-u1");
        Path user2Tmp = new Path(TestHDFSTrash.TEST_ROOT, "test-del-u2");
        // login as user1, move something to trash
        // verify user1 can remove its own trash dir
        TestHDFSTrash.fs = DFSTestUtil.login(TestHDFSTrash.fs, testConf, TestHDFSTrash.user1);
        TestHDFSTrash.fs.mkdirs(user1Tmp);
        Trash u1Trash = getPerUserTrash(TestHDFSTrash.user1, TestHDFSTrash.fs, testConf);
        Path u1t = u1Trash.getCurrentTrashDir(user1Tmp);
        Assert.assertTrue(String.format("Failed to move %s to trash", user1Tmp), u1Trash.moveToTrash(user1Tmp));
        Assert.assertTrue(String.format("%s should be allowed to remove its own trash directory %s", TestHDFSTrash.user1.getUserName(), u1t), TestHDFSTrash.fs.delete(u1t, true));
        Assert.assertFalse(TestHDFSTrash.fs.exists(u1t));
        // login as user2, move something to trash
        TestHDFSTrash.fs = DFSTestUtil.login(TestHDFSTrash.fs, testConf, TestHDFSTrash.user2);
        TestHDFSTrash.fs.mkdirs(user2Tmp);
        Trash u2Trash = getPerUserTrash(TestHDFSTrash.user2, TestHDFSTrash.fs, testConf);
        u2Trash.moveToTrash(user2Tmp);
        Path u2t = u2Trash.getCurrentTrashDir(user2Tmp);
        try {
            // user1 should not be able to remove user2's trash dir
            TestHDFSTrash.fs = DFSTestUtil.login(TestHDFSTrash.fs, testConf, TestHDFSTrash.user1);
            TestHDFSTrash.fs.delete(u2t, true);
            Assert.fail(String.format("%s should not be able to remove %s trash directory", TestHDFSTrash.USER1_NAME, TestHDFSTrash.USER2_NAME));
        } catch (AccessControlException e) {
            Assert.assertTrue((e instanceof AccessControlException));
            Assert.assertTrue("Permission denied messages must carry the username", e.getMessage().contains(TestHDFSTrash.USER1_NAME));
        }
    }
}

