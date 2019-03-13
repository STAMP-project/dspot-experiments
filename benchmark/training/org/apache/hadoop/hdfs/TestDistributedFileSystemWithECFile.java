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


import FileSystem.Statistics;
import FileSystem.Statistics.StatisticsData;
import SystemErasureCodingPolicies.RS_3_2_POLICY_ID;
import SystemErasureCodingPolicies.RS_6_3_POLICY_ID;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.SystemErasureCodingPolicies;
import org.apache.hadoop.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Testing correctness of FileSystem.getFileBlockLocations and
 * FileSystem.listFiles for erasure coded files.
 */
public class TestDistributedFileSystemWithECFile {
    private ErasureCodingPolicy ecPolicy;

    private int cellSize;

    private short dataBlocks;

    private short parityBlocks;

    private int numDNs;

    private int stripesPerBlock;

    private int blockSize;

    private int blockGroupSize;

    private MiniDFSCluster cluster;

    private FileContext fileContext;

    private DistributedFileSystem fs;

    private Configuration conf = new HdfsConfiguration();

    @Rule
    public final Timeout globalTimeout = new Timeout((60000 * 3));

    @Test(timeout = 60000)
    public void testListECFilesSmallerThanOneCell() throws Exception {
        createFile("/ec/smallcell", 1);
        final List<LocatedFileStatus> retVal = new ArrayList<>();
        final RemoteIterator<LocatedFileStatus> iter = cluster.getFileSystem().listFiles(new Path("/ec"), true);
        while (iter.hasNext()) {
            retVal.add(iter.next());
        } 
        Assert.assertTrue(((retVal.size()) == 1));
        LocatedFileStatus fileStatus = retVal.get(0);
        assertSmallerThanOneCell(fileStatus.getBlockLocations());
        BlockLocation[] locations = cluster.getFileSystem().getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
        assertSmallerThanOneCell(locations);
        // Test FileContext
        fileStatus = fileContext.listLocatedStatus(new Path("/ec")).next();
        assertSmallerThanOneCell(fileStatus.getBlockLocations());
        locations = fileContext.getFileBlockLocations(new Path("/ec/smallcell"), 0, fileStatus.getLen());
        assertSmallerThanOneCell(locations);
    }

    @Test(timeout = 60000)
    public void testListECFilesSmallerThanOneStripe() throws Exception {
        int dataBlocksNum = dataBlocks;
        createFile("/ec/smallstripe", ((cellSize) * dataBlocksNum));
        RemoteIterator<LocatedFileStatus> iter = cluster.getFileSystem().listFiles(new Path("/ec"), true);
        LocatedFileStatus fileStatus = iter.next();
        assertSmallerThanOneStripe(fileStatus.getBlockLocations(), dataBlocksNum);
        BlockLocation[] locations = cluster.getFileSystem().getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
        assertSmallerThanOneStripe(locations, dataBlocksNum);
        // Test FileContext
        fileStatus = fileContext.listLocatedStatus(new Path("/ec")).next();
        assertSmallerThanOneStripe(fileStatus.getBlockLocations(), dataBlocksNum);
        locations = fileContext.getFileBlockLocations(new Path("/ec/smallstripe"), 0, fileStatus.getLen());
        assertSmallerThanOneStripe(locations, dataBlocksNum);
    }

    @Test(timeout = 60000)
    public void testListECFilesMoreThanOneBlockGroup() throws Exception {
        createFile("/ec/group", ((blockGroupSize) + 123));
        RemoteIterator<LocatedFileStatus> iter = cluster.getFileSystem().listFiles(new Path("/ec"), true);
        LocatedFileStatus fileStatus = iter.next();
        assertMoreThanOneBlockGroup(fileStatus.getBlockLocations(), 123);
        BlockLocation[] locations = cluster.getFileSystem().getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
        assertMoreThanOneBlockGroup(locations, 123);
        // Test FileContext
        iter = fileContext.listLocatedStatus(new Path("/ec"));
        fileStatus = iter.next();
        assertMoreThanOneBlockGroup(fileStatus.getBlockLocations(), 123);
        locations = fileContext.getFileBlockLocations(new Path("/ec/group"), 0, fileStatus.getLen());
        assertMoreThanOneBlockGroup(locations, 123);
    }

    @Test(timeout = 60000)
    public void testReplayEditLogsForReplicatedFile() throws Exception {
        cluster.shutdown();
        ErasureCodingPolicy rs63 = SystemErasureCodingPolicies.getByID(RS_6_3_POLICY_ID);
        ErasureCodingPolicy rs32 = SystemErasureCodingPolicies.getByID(RS_3_2_POLICY_ID);
        // Test RS(6,3) as default policy
        int numDataNodes = (rs63.getNumDataUnits()) + (rs63.getNumParityUnits());
        cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(numDataNodes).build();
        cluster.transitionToActive(0);
        fs = cluster.getFileSystem(0);
        fs.enableErasureCodingPolicy(rs63.getName());
        fs.enableErasureCodingPolicy(rs32.getName());
        Path dir = new Path("/ec");
        fs.mkdirs(dir);
        fs.setErasureCodingPolicy(dir, rs63.getName());
        // Create an erasure coded file with the default policy.
        Path ecFile = new Path(dir, "ecFile");
        createFile(ecFile.toString(), 10);
        // Create a replicated file.
        Path replicatedFile = new Path(dir, "replicated");
        try (FSDataOutputStream out = fs.createFile(replicatedFile).replicate().build()) {
            out.write(123);
        }
        // Create an EC file with a different policy.
        Path ecFile2 = new Path(dir, "RS-3-2");
        try (FSDataOutputStream out = fs.createFile(ecFile2).ecPolicyName(rs32.getName()).build()) {
            out.write(456);
        }
        cluster.transitionToStandby(0);
        cluster.transitionToActive(1);
        fs = cluster.getFileSystem(1);
        Assert.assertNull(fs.getErasureCodingPolicy(replicatedFile));
        Assert.assertEquals(rs63, fs.getErasureCodingPolicy(ecFile));
        Assert.assertEquals(rs32, fs.getErasureCodingPolicy(ecFile2));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testStatistics() throws Exception {
        final String fileName = "/ec/file";
        final int size = 3200;
        createFile(fileName, size);
        InputStream in = null;
        try {
            in = fs.open(new Path(fileName));
            IOUtils.copyBytes(in, System.out, 4096, false);
        } finally {
            IOUtils.closeStream(in);
        }
        // verify stats are correct
        Long totalBytesRead = 0L;
        Long ecBytesRead = 0L;
        for (FileSystem.Statistics stat : FileSystem.getAllStatistics()) {
            totalBytesRead += stat.getBytesRead();
            ecBytesRead += stat.getBytesReadErasureCoded();
        }
        Assert.assertEquals(Long.valueOf(size), totalBytesRead);
        Assert.assertEquals(Long.valueOf(size), ecBytesRead);
        // verify thread local stats are correct
        Long totalBytesReadThread = 0L;
        Long ecBytesReadThread = 0L;
        for (FileSystem.Statistics stat : FileSystem.getAllStatistics()) {
            FileSystem.Statistics.StatisticsData data = stat.getThreadStatistics();
            totalBytesReadThread += data.getBytesRead();
            ecBytesReadThread += data.getBytesReadErasureCoded();
        }
        Assert.assertEquals(Long.valueOf(size), totalBytesReadThread);
        Assert.assertEquals(Long.valueOf(size), ecBytesReadThread);
    }
}

