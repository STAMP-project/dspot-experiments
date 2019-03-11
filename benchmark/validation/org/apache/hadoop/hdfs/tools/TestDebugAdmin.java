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
package org.apache.hadoop.hdfs.tools;


import java.io.File;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsDatasetTestUtil;
import org.junit.Assert;
import org.junit.Test;


public class TestDebugAdmin {
    private static final String TEST_ROOT_DIR = new File(System.getProperty("test.build.data", "/tmp"), TestDebugAdmin.class.getSimpleName()).getAbsolutePath();

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    private DebugAdmin admin;

    private DataNode datanode;

    @Test(timeout = 60000)
    public void testRecoverLease() throws Exception {
        Assert.assertEquals("ret: 1, You must supply a -path argument to recoverLease.", runCmd(new String[]{ "recoverLease", "-retries", "1" }));
        FSDataOutputStream out = fs.create(new Path("/foo"));
        out.write(123);
        out.close();
        Assert.assertEquals("ret: 0, recoverLease SUCCEEDED on /foo", runCmd(new String[]{ "recoverLease", "-path", "/foo" }));
    }

    @Test(timeout = 60000)
    public void testVerifyMetaCommand() throws Exception {
        DFSTestUtil.createFile(fs, new Path("/bar"), 1234, ((short) (1)), -559038737);
        FsDatasetSpi<?> fsd = datanode.getFSDataset();
        ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, new Path("/bar"));
        File blockFile = FsDatasetTestUtil.getBlockFile(fsd, block.getBlockPoolId(), block.getLocalBlock());
        Assert.assertEquals("ret: 1, You must specify a meta file with -meta", runCmd(new String[]{ "verifyMeta", "-block", blockFile.getAbsolutePath() }));
        File metaFile = FsDatasetTestUtil.getMetaFile(fsd, block.getBlockPoolId(), block.getLocalBlock());
        Assert.assertEquals(("ret: 0, Checksum type: " + "DataChecksum(type=CRC32C, chunkSize=512)"), runCmd(new String[]{ "verifyMeta", "-meta", metaFile.getAbsolutePath() }));
        Assert.assertEquals((("ret: 0, Checksum type: " + ("DataChecksum(type=CRC32C, chunkSize=512)" + "Checksum verification succeeded on block file ")) + (blockFile.getAbsolutePath())), runCmd(new String[]{ "verifyMeta", "-meta", metaFile.getAbsolutePath(), "-block", blockFile.getAbsolutePath() }));
    }

    @Test(timeout = 60000)
    public void testComputeMetaCommand() throws Exception {
        DFSTestUtil.createFile(fs, new Path("/bar"), 1234, ((short) (1)), -559038737);
        FsDatasetSpi<?> fsd = datanode.getFSDataset();
        ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, new Path("/bar"));
        File blockFile = FsDatasetTestUtil.getBlockFile(fsd, block.getBlockPoolId(), block.getLocalBlock());
        Assert.assertEquals(("ret: 1, computeMeta -block <block-file> -out " + (((((("<output-metadata-file>  Compute HDFS metadata from the specified" + " block file, and save it to  the specified output metadata file.") + "**NOTE: Use at your own risk! If the block file is corrupt") + " and you overwrite it's meta file,  it will show up") + " as good in HDFS, but you can't read the data.") + " Only use as a last measure, and when you are 100% certain") + " the block file is good.")), runCmd(new String[]{ "computeMeta" }));
        Assert.assertEquals("ret: 2, You must specify a block file with -block", runCmd(new String[]{ "computeMeta", "-whatever" }));
        Assert.assertEquals("ret: 3, Block file <bla> does not exist or is not a file", runCmd(new String[]{ "computeMeta", "-block", "bla" }));
        Assert.assertEquals("ret: 4, You must specify a output file with -out", runCmd(new String[]{ "computeMeta", "-block", blockFile.getAbsolutePath() }));
        Assert.assertEquals("ret: 5, output file already exists!", runCmd(new String[]{ "computeMeta", "-block", blockFile.getAbsolutePath(), "-out", blockFile.getAbsolutePath() }));
        File outFile = new File(TestDebugAdmin.TEST_ROOT_DIR, "out.meta");
        outFile.delete();
        Assert.assertEquals(((("ret: 0, Checksum calculation succeeded on block file " + (blockFile.getAbsolutePath())) + " saved metadata to meta file ") + (outFile.getAbsolutePath())), runCmd(new String[]{ "computeMeta", "-block", blockFile.getAbsolutePath(), "-out", outFile.getAbsolutePath() }));
        Assert.assertTrue(outFile.exists());
        Assert.assertTrue(((outFile.length()) > 0));
    }

    @Test(timeout = 60000)
    public void testRecoverLeaseforFileNotFound() throws Exception {
        Assert.assertTrue(runCmd(new String[]{ "recoverLease", "-path", "/foo", "-retries", "2" }).contains("Giving up on recoverLease for /foo after 1 try"));
    }
}

