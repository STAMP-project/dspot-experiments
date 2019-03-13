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
package org.apache.hadoop.hbase.io;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.ipc.RemoteException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test that FileLink switches between alternate locations
 * when the current location moves or gets deleted.
 */
@Category({ IOTests.class, MediumTests.class })
public class TestFileLink {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFileLink.class);

    @Test
    public void testEquals() {
        Path p1 = new Path("/p1");
        Path p2 = new Path("/p2");
        Path p3 = new Path("/p3");
        Assert.assertEquals(new FileLink(), new FileLink());
        Assert.assertEquals(new FileLink(p1), new FileLink(p1));
        Assert.assertEquals(new FileLink(p1, p2), new FileLink(p1, p2));
        Assert.assertEquals(new FileLink(p1, p2, p3), new FileLink(p1, p2, p3));
        Assert.assertNotEquals(new FileLink(p1), new FileLink(p3));
        Assert.assertNotEquals(new FileLink(p1, p2), new FileLink(p1));
        Assert.assertNotEquals(new FileLink(p1, p2), new FileLink(p2));
        Assert.assertNotEquals(new FileLink(p1, p2), new FileLink(p2, p1));// ordering important!

    }

    @Test
    public void testHashCode() {
        Path p1 = new Path("/p1");
        Path p2 = new Path("/p2");
        Path p3 = new Path("/p3");
        Assert.assertEquals(new FileLink().hashCode(), new FileLink().hashCode());
        Assert.assertEquals(new FileLink(p1).hashCode(), new FileLink(p1).hashCode());
        Assert.assertEquals(new FileLink(p1, p2).hashCode(), new FileLink(p1, p2).hashCode());
        Assert.assertEquals(new FileLink(p1, p2, p3).hashCode(), new FileLink(p1, p2, p3).hashCode());
        Assert.assertNotEquals(new FileLink(p1).hashCode(), new FileLink(p3).hashCode());
        Assert.assertNotEquals(new FileLink(p1, p2).hashCode(), new FileLink(p1).hashCode());
        Assert.assertNotEquals(new FileLink(p1, p2).hashCode(), new FileLink(p2).hashCode());
        Assert.assertNotEquals(new FileLink(p1, p2).hashCode(), new FileLink(p2, p1).hashCode());// ordering

    }

    /**
     * Test, on HDFS, that the FileLink is still readable
     * even when the current file gets renamed.
     */
    @Test
    public void testHDFSLinkReadDuringRename() throws Exception {
        HBaseTestingUtility testUtil = new HBaseTestingUtility();
        Configuration conf = testUtil.getConfiguration();
        conf.setInt("dfs.blocksize", (1024 * 1024));
        conf.setInt("dfs.client.read.prefetch.size", ((2 * 1024) * 1024));
        testUtil.startMiniDFSCluster(1);
        MiniDFSCluster cluster = testUtil.getDFSCluster();
        FileSystem fs = cluster.getFileSystem();
        Assert.assertEquals("hdfs", fs.getUri().getScheme());
        try {
            testLinkReadDuringRename(fs, testUtil.getDefaultRootDirPath());
        } finally {
            testUtil.shutdownMiniCluster();
        }
    }

    private static class MyDistributedFileSystem extends DistributedFileSystem {
        MyDistributedFileSystem() {
        }

        @Override
        public FSDataInputStream open(Path f, final int bufferSize) throws IOException {
            throw new RemoteException(FileNotFoundException.class.getName(), "");
        }

        @Override
        public Configuration getConf() {
            return new Configuration();
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void testLinkReadWithMissingFile() throws Exception {
        HBaseTestingUtility testUtil = new HBaseTestingUtility();
        FileSystem fs = new TestFileLink.MyDistributedFileSystem();
        Path originalPath = new Path(testUtil.getDefaultRootDirPath(), "test.file");
        Path archivedPath = new Path(testUtil.getDefaultRootDirPath(), "archived.file");
        List<Path> files = new ArrayList<Path>();
        files.add(originalPath);
        files.add(archivedPath);
        FileLink link = new FileLink(files);
        link.open(fs);
    }

    /**
     * Test, on a local filesystem, that the FileLink is still readable
     * even when the current file gets renamed.
     */
    @Test
    public void testLocalLinkReadDuringRename() throws IOException {
        HBaseTestingUtility testUtil = new HBaseTestingUtility();
        FileSystem fs = testUtil.getTestFileSystem();
        Assert.assertEquals("file", fs.getUri().getScheme());
        testLinkReadDuringRename(fs, getDataTestDir());
    }

    /**
     * Test that link is still readable even when the current file gets deleted.
     *
     * NOTE: This test is valid only on HDFS.
     * When a file is deleted from a local file-system, it is simply 'unlinked'.
     * The inode, which contains the file's data, is not deleted until all
     * processes have finished with it.
     * In HDFS when the request exceed the cached block locations,
     * a query to the namenode is performed, using the filename,
     * and the deleted file doesn't exists anymore (FileNotFoundException).
     */
    @Test
    public void testHDFSLinkReadDuringDelete() throws Exception {
        HBaseTestingUtility testUtil = new HBaseTestingUtility();
        Configuration conf = testUtil.getConfiguration();
        conf.setInt("dfs.blocksize", (1024 * 1024));
        conf.setInt("dfs.client.read.prefetch.size", ((2 * 1024) * 1024));
        testUtil.startMiniDFSCluster(1);
        MiniDFSCluster cluster = testUtil.getDFSCluster();
        FileSystem fs = cluster.getFileSystem();
        Assert.assertEquals("hdfs", fs.getUri().getScheme());
        try {
            List<Path> files = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Path path = new Path(String.format("test-data-%d", i));
                writeSomeData(fs, path, (1 << 20), ((byte) (i)));
                files.add(path);
            }
            FileLink link = new FileLink(files);
            FSDataInputStream in = link.open(fs);
            try {
                byte[] data = new byte[8192];
                int n;
                // Switch to file 1
                n = in.read(data);
                TestFileLink.dataVerify(data, n, ((byte) (0)));
                fs.delete(files.get(0), true);
                TestFileLink.skipBuffer(in, ((byte) (0)));
                // Switch to file 2
                n = in.read(data);
                TestFileLink.dataVerify(data, n, ((byte) (1)));
                fs.delete(files.get(1), true);
                TestFileLink.skipBuffer(in, ((byte) (1)));
                // Switch to file 3
                n = in.read(data);
                TestFileLink.dataVerify(data, n, ((byte) (2)));
                fs.delete(files.get(2), true);
                TestFileLink.skipBuffer(in, ((byte) (2)));
                // No more files available
                try {
                    n = in.read(data);
                    assert n <= 0;
                } catch (FileNotFoundException e) {
                    Assert.assertTrue(true);
                }
            } finally {
                in.close();
            }
        } finally {
            testUtil.shutdownMiniCluster();
        }
    }
}

