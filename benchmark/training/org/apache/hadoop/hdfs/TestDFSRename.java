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


import Rename.OVERWRITE;
import java.io.DataOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.namenode.NameNodeAdapter;
import org.junit.Assert;
import org.junit.Test;


public class TestDFSRename {
    final Path dir = new Path("/test/rename/");

    @Test
    public void testRename() throws Exception {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).build();
        try {
            FileSystem fs = cluster.getFileSystem();
            Assert.assertTrue(fs.mkdirs(dir));
            {
                // test lease
                Path a = new Path(dir, "a");
                Path aa = new Path(dir, "aa");
                Path b = new Path(dir, "b");
                TestDFSRename.createFile(fs, a);
                // should not have any lease
                Assert.assertEquals(0, TestDFSRename.countLease(cluster));
                DataOutputStream aa_out = fs.create(aa);
                aa_out.writeBytes("something");
                // should have 1 lease
                Assert.assertEquals(1, TestDFSRename.countLease(cluster));
                list(fs, "rename0");
                fs.rename(a, b);
                list(fs, "rename1");
                aa_out.writeBytes(" more");
                aa_out.close();
                list(fs, "rename2");
                // should not have any lease
                Assert.assertEquals(0, TestDFSRename.countLease(cluster));
            }
            {
                // test non-existent destination
                Path dstPath = new Path("/c/d");
                Assert.assertFalse(fs.exists(dstPath));
                Assert.assertFalse(fs.rename(dir, dstPath));
            }
            {
                // dst cannot be a file or directory under src
                // test rename /a/b/foo to /a/b/c
                Path src = new Path("/a/b");
                Path dst = new Path("/a/b/c");
                TestDFSRename.createFile(fs, new Path(src, "foo"));
                // dst cannot be a file under src
                Assert.assertFalse(fs.rename(src, dst));
                // dst cannot be a directory under src
                Assert.assertFalse(fs.rename(src.getParent(), dst.getParent()));
            }
            {
                // dst can start with src, if it is not a directory or file under src
                // test rename /test /testfile
                Path src = new Path("/testPrefix");
                Path dst = new Path("/testPrefixfile");
                TestDFSRename.createFile(fs, src);
                Assert.assertTrue(fs.rename(src, dst));
            }
            {
                // dst should not be same as src test rename /a/b/c to /a/b/c
                Path src = new Path("/a/b/c");
                TestDFSRename.createFile(fs, src);
                Assert.assertTrue(fs.rename(src, src));
                Assert.assertFalse(fs.rename(new Path("/a/b"), new Path("/a/b/")));
                Assert.assertTrue(fs.rename(src, new Path("/a/b/c/")));
            }
            fs.delete(dir, true);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Check the blocks of dst file are cleaned after rename with overwrite
     * Restart NN to check the rename successfully
     */
    @Test(timeout = 120000)
    public void testRenameWithOverwrite() throws Exception {
        final short replFactor = 2;
        final long blockSize = 512;
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(replFactor).build();
        DistributedFileSystem dfs = cluster.getFileSystem();
        try {
            long fileLen = blockSize * 3;
            String src = "/foo/src";
            String dst = "/foo/dst";
            Path srcPath = new Path(src);
            Path dstPath = new Path(dst);
            DFSTestUtil.createFile(dfs, srcPath, fileLen, replFactor, 1);
            DFSTestUtil.createFile(dfs, dstPath, fileLen, replFactor, 1);
            LocatedBlocks lbs = NameNodeAdapter.getBlockLocations(cluster.getNameNode(), dst, 0, fileLen);
            BlockManager bm = NameNodeAdapter.getNamesystem(cluster.getNameNode()).getBlockManager();
            Assert.assertTrue(((bm.getStoredBlock(lbs.getLocatedBlocks().get(0).getBlock().getLocalBlock())) != null));
            dfs.rename(srcPath, dstPath, OVERWRITE);
            Assert.assertTrue(((bm.getStoredBlock(lbs.getLocatedBlocks().get(0).getBlock().getLocalBlock())) == null));
            // Restart NN and check the rename successfully
            cluster.restartNameNodes();
            Assert.assertFalse(dfs.exists(srcPath));
            Assert.assertTrue(dfs.exists(dstPath));
        } finally {
            if (dfs != null) {
                dfs.close();
            }
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

