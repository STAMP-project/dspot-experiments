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


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the presence of seek bug as described
 * in HADOOP-508
 */
public class TestSeekBug {
    static final long seed = 3735928559L;

    static final int ONEMB = 1 << 20;

    /**
     * Test if the seek bug exists in FSDataInputStream in DFS.
     */
    @Test
    public void testSeekBugDFS() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        FileSystem fileSys = cluster.getFileSystem();
        try {
            Path file1 = new Path("seektest.dat");
            DFSTestUtil.createFile(fileSys, file1, TestSeekBug.ONEMB, TestSeekBug.ONEMB, fileSys.getDefaultBlockSize(file1), fileSys.getDefaultReplication(file1), TestSeekBug.seed);
            seekReadFile(fileSys, file1);
            smallReadSeek(fileSys, file1);
            cleanupFile(fileSys, file1);
        } finally {
            fileSys.close();
            cluster.shutdown();
        }
    }

    /**
     * Test (expected to throw IOE) for negative
     * <code>FSDataInpuStream#seek</code> argument
     */
    @Test(expected = IOException.class)
    public void testNegativeSeek() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        FileSystem fs = cluster.getFileSystem();
        try {
            Path seekFile = new Path("seekboundaries.dat");
            DFSTestUtil.createFile(fs, seekFile, TestSeekBug.ONEMB, TestSeekBug.ONEMB, fs.getDefaultBlockSize(seekFile), fs.getDefaultReplication(seekFile), TestSeekBug.seed);
            FSDataInputStream stream = fs.open(seekFile);
            // Perform "safe seek" (expected to pass)
            stream.seek(65536);
            Assert.assertEquals(65536, stream.getPos());
            // expect IOE for this call
            stream.seek((-73));
        } finally {
            fs.close();
            cluster.shutdown();
        }
    }

    /**
     * Test (expected to throw IOE) for <code>FSDataInpuStream#seek</code>
     * when the position argument is larger than the file size.
     */
    @Test(expected = IOException.class)
    public void testSeekPastFileSize() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        FileSystem fs = cluster.getFileSystem();
        try {
            Path seekFile = new Path("seekboundaries.dat");
            DFSTestUtil.createFile(fs, seekFile, TestSeekBug.ONEMB, TestSeekBug.ONEMB, fs.getDefaultBlockSize(seekFile), fs.getDefaultReplication(seekFile), TestSeekBug.seed);
            FSDataInputStream stream = fs.open(seekFile);
            // Perform "safe seek" (expected to pass)
            stream.seek(65536);
            Assert.assertEquals(65536, stream.getPos());
            // expect IOE for this call
            stream.seek((((TestSeekBug.ONEMB) + (TestSeekBug.ONEMB)) + (TestSeekBug.ONEMB)));
        } finally {
            fs.close();
            cluster.shutdown();
        }
    }

    /**
     * Tests if the seek bug exists in FSDataInputStream in LocalFS.
     */
    @Test
    public void testSeekBugLocalFS() throws IOException {
        Configuration conf = new HdfsConfiguration();
        FileSystem fileSys = FileSystem.getLocal(conf);
        try {
            Path file1 = new Path(GenericTestUtils.getTempPath("seektest.dat"));
            DFSTestUtil.createFile(fileSys, file1, TestSeekBug.ONEMB, TestSeekBug.ONEMB, fileSys.getDefaultBlockSize(file1), fileSys.getDefaultReplication(file1), TestSeekBug.seed);
            seekReadFile(fileSys, file1);
            cleanupFile(fileSys, file1);
        } finally {
            fileSys.close();
        }
    }
}

