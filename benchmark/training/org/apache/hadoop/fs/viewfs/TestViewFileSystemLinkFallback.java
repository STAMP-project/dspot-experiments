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
package org.apache.hadoop.fs.viewfs;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsConstants;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Constants.CONFIG_VIEWFS_LINK_FALLBACK;
import static Constants.CONFIG_VIEWFS_PREFIX;


/**
 * Test for viewfs with LinkFallback mount table entries.
 */
public class TestViewFileSystemLinkFallback extends ViewFileSystemBaseTest {
    private static FileSystem fsDefault;

    private static MiniDFSCluster cluster;

    private static final int NAME_SPACES_COUNT = 3;

    private static final int DATA_NODES_COUNT = 3;

    private static final int FS_INDEX_DEFAULT = 0;

    private static final String LINK_FALLBACK_CLUSTER_1_NAME = "Cluster1";

    private static final FileSystem[] FS_HDFS = new FileSystem[TestViewFileSystemLinkFallback.NAME_SPACES_COUNT];

    private static final Configuration CONF = new Configuration();

    private static final File TEST_DIR = GenericTestUtils.getTestDir(TestViewFileSystemLinkFallback.class.getSimpleName());

    private static final String TEST_BASE_PATH = "/tmp/TestViewFileSystemLinkFallback";

    private static final Logger LOG = LoggerFactory.getLogger(TestViewFileSystemLinkFallback.class);

    @Test
    public void testConfLinkFallback() throws Exception {
        Path testBasePath = new Path(TestViewFileSystemLinkFallback.TEST_BASE_PATH);
        Path testLevel2Dir = new Path(TestViewFileSystemLinkFallback.TEST_BASE_PATH, "dir1/dirA");
        Path testBaseFile = new Path(testBasePath, "testBaseFile.log");
        Path testBaseFileRelative = new Path(testLevel2Dir, "../../testBaseFile.log");
        Path testLevel2File = new Path(testLevel2Dir, "testLevel2File.log");
        fsTarget.mkdirs(testLevel2Dir);
        fsTarget.createNewFile(testBaseFile);
        FSDataOutputStream dataOutputStream = fsTarget.append(testBaseFile);
        dataOutputStream.write(1);
        dataOutputStream.close();
        fsTarget.createNewFile(testLevel2File);
        dataOutputStream = fsTarget.append(testLevel2File);
        dataOutputStream.write("test link fallback".toString().getBytes());
        dataOutputStream.close();
        String clusterName = "ClusterFallback";
        URI viewFsUri = new URI(FsConstants.VIEWFS_SCHEME, clusterName, "/", null, null);
        Configuration conf = new Configuration();
        ConfigUtil.addLinkFallback(conf, clusterName, fsTarget.getUri());
        FileSystem vfs = FileSystem.get(viewFsUri, conf);
        Assert.assertEquals(ViewFileSystem.class, vfs.getClass());
        FileStatus baseFileStat = vfs.getFileStatus(new Path(((viewFsUri.toString()) + (testBaseFile.toUri().toString()))));
        TestViewFileSystemLinkFallback.LOG.info(("BaseFileStat: " + baseFileStat));
        FileStatus baseFileRelStat = vfs.getFileStatus(new Path(((viewFsUri.toString()) + (testBaseFileRelative.toUri().toString()))));
        TestViewFileSystemLinkFallback.LOG.info(("BaseFileRelStat: " + baseFileRelStat));
        Assert.assertEquals(("Unexpected file length for " + testBaseFile), 1, baseFileStat.getLen());
        Assert.assertEquals(("Unexpected file length for " + testBaseFileRelative), baseFileStat.getLen(), baseFileRelStat.getLen());
        FileStatus level2FileStat = vfs.getFileStatus(new Path(((viewFsUri.toString()) + (testLevel2File.toUri().toString()))));
        TestViewFileSystemLinkFallback.LOG.info(("Level2FileStat: " + level2FileStat));
        vfs.close();
    }

    @Test
    public void testConfLinkFallbackWithRegularLinks() throws Exception {
        Path testBasePath = new Path(TestViewFileSystemLinkFallback.TEST_BASE_PATH);
        Path testLevel2Dir = new Path(TestViewFileSystemLinkFallback.TEST_BASE_PATH, "dir1/dirA");
        Path testBaseFile = new Path(testBasePath, "testBaseFile.log");
        Path testLevel2File = new Path(testLevel2Dir, "testLevel2File.log");
        fsTarget.mkdirs(testLevel2Dir);
        fsTarget.createNewFile(testBaseFile);
        fsTarget.createNewFile(testLevel2File);
        FSDataOutputStream dataOutputStream = fsTarget.append(testLevel2File);
        dataOutputStream.write("test link fallback".toString().getBytes());
        dataOutputStream.close();
        String clusterName = "ClusterFallback";
        URI viewFsUri = new URI(FsConstants.VIEWFS_SCHEME, clusterName, "/", null, null);
        Configuration conf = new Configuration();
        ConfigUtil.addLink(conf, clusterName, "/internalDir/linkToDir2", toUri());
        ConfigUtil.addLink(conf, clusterName, "/internalDir/internalDirB/linkToDir3", toUri());
        ConfigUtil.addLink(conf, clusterName, "/danglingLink", toUri());
        ConfigUtil.addLink(conf, clusterName, "/linkToAFile", toUri());
        System.out.println(("ViewFs link fallback " + (fsTarget.getUri())));
        ConfigUtil.addLinkFallback(conf, clusterName, targetTestRoot.toUri());
        FileSystem vfs = FileSystem.get(viewFsUri, conf);
        Assert.assertEquals(ViewFileSystem.class, vfs.getClass());
        FileStatus baseFileStat = vfs.getFileStatus(new Path(((viewFsUri.toString()) + (testBaseFile.toUri().toString()))));
        TestViewFileSystemLinkFallback.LOG.info(("BaseFileStat: " + baseFileStat));
        Assert.assertEquals(("Unexpected file length for " + testBaseFile), 0, baseFileStat.getLen());
        FileStatus level2FileStat = vfs.getFileStatus(new Path(((viewFsUri.toString()) + (testLevel2File.toUri().toString()))));
        TestViewFileSystemLinkFallback.LOG.info(("Level2FileStat: " + level2FileStat));
        dataOutputStream = vfs.append(testLevel2File);
        dataOutputStream.write("Writing via viewfs fallback path".getBytes());
        dataOutputStream.close();
        FileStatus level2FileStatAfterWrite = vfs.getFileStatus(new Path(((viewFsUri.toString()) + (testLevel2File.toUri().toString()))));
        Assert.assertTrue(("Unexpected file length for " + testLevel2File), ((level2FileStatAfterWrite.getLen()) > (level2FileStat.getLen())));
        vfs.close();
    }

    @Test
    public void testConfLinkFallbackWithMountPoint() throws Exception {
        TestViewFileSystemLinkFallback.TEST_DIR.mkdirs();
        Configuration conf = new Configuration();
        String clusterName = "ClusterX";
        String mountPoint = "/user";
        URI viewFsUri = new URI(FsConstants.VIEWFS_SCHEME, clusterName, "/", null, null);
        String expectedErrorMsg = "Invalid linkFallback entry in config: " + "linkFallback./user";
        String mountTableEntry = ((((((CONFIG_VIEWFS_PREFIX) + ".") + clusterName) + ".") + (CONFIG_VIEWFS_LINK_FALLBACK)) + ".") + mountPoint;
        conf.set(mountTableEntry, TestViewFileSystemLinkFallback.TEST_DIR.toURI().toString());
        try {
            FileSystem.get(viewFsUri, conf);
            Assert.fail("Shouldn't allow linkMergeSlash to take extra mount points!");
        } catch (IOException e) {
            Assert.assertTrue(("Unexpected error: " + (e.getMessage())), e.getMessage().contains(expectedErrorMsg));
        }
    }
}

