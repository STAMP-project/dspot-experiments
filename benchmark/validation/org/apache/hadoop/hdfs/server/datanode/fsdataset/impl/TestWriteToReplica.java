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
package org.apache.hadoop.hdfs.server.datanode.fsdataset.impl;


import FsDatasetSpi.FsVolumeReferences;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.server.datanode.ReplicaBeingWritten;
import org.apache.hadoop.hdfs.server.datanode.ReplicaNotFoundException;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.AutoCloseableLock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test if FSDataset#append, writeToRbw, and writeToTmp
 */
public class TestWriteToReplica {
    private static final int FINALIZED = 0;

    private static final int TEMPORARY = 1;

    private static final int RBW = 2;

    private static final int RWR = 3;

    private static final int RUR = 4;

    private static final int NON_EXISTENT = 5;

    // test close
    @Test
    public void testClose() throws Exception {
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(new HdfsConfiguration(), new File(GenericTestUtils.getRandomizedTempPath())).build();
        try {
            cluster.waitActive();
            DataNode dn = cluster.getDataNodes().get(0);
            FsDatasetSpi<?> dataSet = DataNodeTestUtils.getFSDataset(dn);
            // set up replicasMap
            String bpid = cluster.getNamesystem().getBlockPoolId();
            ExtendedBlock[] blocks = setup(bpid, cluster.getFsDatasetTestUtils(dn));
            // test close
            testClose(dataSet, blocks);
        } finally {
            cluster.shutdown();
        }
    }

    // test append
    @Test
    public void testAppend() throws Exception {
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(new HdfsConfiguration(), new File(GenericTestUtils.getRandomizedTempPath())).build();
        try {
            cluster.waitActive();
            DataNode dn = cluster.getDataNodes().get(0);
            FsDatasetSpi<?> dataSet = DataNodeTestUtils.getFSDataset(dn);
            // set up replicasMap
            String bpid = cluster.getNamesystem().getBlockPoolId();
            ExtendedBlock[] blocks = setup(bpid, cluster.getFsDatasetTestUtils(dn));
            // test append
            testAppend(bpid, dataSet, blocks);
        } finally {
            cluster.shutdown();
        }
    }

    // test writeToRbw
    @Test
    public void testWriteToRbw() throws Exception {
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(new HdfsConfiguration(), new File(GenericTestUtils.getRandomizedTempPath())).build();
        try {
            cluster.waitActive();
            DataNode dn = cluster.getDataNodes().get(0);
            FsDatasetImpl dataSet = ((FsDatasetImpl) (DataNodeTestUtils.getFSDataset(dn)));
            // set up replicasMap
            String bpid = cluster.getNamesystem().getBlockPoolId();
            ExtendedBlock[] blocks = setup(bpid, cluster.getFsDatasetTestUtils(dn));
            // test writeToRbw
            testWriteToRbw(dataSet, blocks);
        } finally {
            cluster.shutdown();
        }
    }

    // test writeToTemporary
    @Test
    public void testWriteToTemporary() throws Exception {
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(new HdfsConfiguration(), new File(GenericTestUtils.getRandomizedTempPath())).build();
        try {
            cluster.waitActive();
            DataNode dn = cluster.getDataNodes().get(0);
            FsDatasetImpl dataSet = ((FsDatasetImpl) (DataNodeTestUtils.getFSDataset(dn)));
            // set up replicasMap
            String bpid = cluster.getNamesystem().getBlockPoolId();
            ExtendedBlock[] blocks = setup(bpid, cluster.getFsDatasetTestUtils(dn));
            // test writeToTemporary
            testWriteToTemporary(dataSet, blocks);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * This is a test to check the replica map before and after the datanode
     * quick restart (less than 5 minutes)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReplicaMapAfterDatanodeRestart() throws Exception {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf, new File(GenericTestUtils.getRandomizedTempPath())).nnTopology(MiniDFSNNTopology.simpleFederatedTopology(2)).build();
        try {
            cluster.waitActive();
            NameNode nn1 = cluster.getNameNode(0);
            NameNode nn2 = cluster.getNameNode(1);
            Assert.assertNotNull("cannot create nn1", nn1);
            Assert.assertNotNull("cannot create nn2", nn2);
            // check number of volumes in fsdataset
            DataNode dn = cluster.getDataNodes().get(0);
            FsDatasetImpl dataSet = ((FsDatasetImpl) (DataNodeTestUtils.getFSDataset(dn)));
            List<FsVolumeSpi> volumes = null;
            try (FsDatasetSpi.FsVolumeReferences referredVols = dataSet.getFsVolumeReferences()) {
                // number of volumes should be 2 - [data1, data2]
                Assert.assertEquals("number of volumes is wrong", 2, referredVols.size());
                volumes = new ArrayList(referredVols.size());
                for (FsVolumeSpi vol : referredVols) {
                    volumes.add(vol);
                }
            }
            ArrayList<String> bpList = new ArrayList(Arrays.asList(cluster.getNamesystem(0).getBlockPoolId(), cluster.getNamesystem(1).getBlockPoolId()));
            Assert.assertTrue("Cluster should have 2 block pools", ((bpList.size()) == 2));
            createReplicas(bpList, volumes, cluster.getFsDatasetTestUtils(dn));
            ReplicaMap oldReplicaMap = new ReplicaMap(new AutoCloseableLock());
            oldReplicaMap.addAll(dataSet.volumeMap);
            cluster.restartDataNode(0);
            cluster.waitActive();
            dn = cluster.getDataNodes().get(0);
            dataSet = ((FsDatasetImpl) (dn.getFSDataset()));
            testEqualityOfReplicaMap(oldReplicaMap, dataSet.volumeMap, bpList);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test that we can successfully recover a {@link ReplicaBeingWritten}
     * which has inconsistent metadata (bytes were written to disk but bytesOnDisk
     * was not updated) but that recovery fails when the block is actually
     * corrupt (bytes are not present on disk).
     */
    @Test
    public void testRecoverInconsistentRbw() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf, new File(GenericTestUtils.getRandomizedTempPath())).build();
        cluster.waitActive();
        DataNode dn = cluster.getDataNodes().get(0);
        FsDatasetImpl fsDataset = ((FsDatasetImpl) (DataNodeTestUtils.getFSDataset(dn)));
        // set up replicasMap
        String bpid = cluster.getNamesystem().getBlockPoolId();
        ExtendedBlock[] blocks = setup(bpid, cluster.getFsDatasetTestUtils(dn));
        ReplicaBeingWritten rbw = ((ReplicaBeingWritten) (fsDataset.getReplicaInfo(bpid, blocks[TestWriteToReplica.RBW].getBlockId())));
        long bytesOnDisk = rbw.getBytesOnDisk();
        // simulate an inconsistent replica length update by reducing in-memory
        // value of on disk length
        rbw.setLastChecksumAndDataLen((bytesOnDisk - 1), null);
        fsDataset.recoverRbw(blocks[TestWriteToReplica.RBW], blocks[TestWriteToReplica.RBW].getGenerationStamp(), 0L, rbw.getNumBytes());
        // after the recovery, on disk length should equal acknowledged length.
        Assert.assertTrue(((rbw.getBytesOnDisk()) == (rbw.getBytesAcked())));
        // reduce on disk length again; this time actually truncate the file to
        // simulate the data not being present
        rbw.setLastChecksumAndDataLen((bytesOnDisk - 1), null);
        try (RandomAccessFile blockRAF = rbw.getFileIoProvider().getRandomAccessFile(rbw.getVolume(), rbw.getBlockFile(), "rw")) {
            // truncate blockFile
            blockRAF.setLength((bytesOnDisk - 1));
            fsDataset.recoverRbw(blocks[TestWriteToReplica.RBW], blocks[TestWriteToReplica.RBW].getGenerationStamp(), 0L, rbw.getNumBytes());
            Assert.fail("recovery should have failed");
        } catch (ReplicaNotFoundException rnfe) {
            GenericTestUtils.assertExceptionContains(("Found fewer bytesOnDisk than " + "bytesAcked for replica"), rnfe);
        }
    }
}

