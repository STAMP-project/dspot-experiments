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
package org.apache.hadoop.hdfs.server.datanode;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This file tests the erasure coding metrics in DataNode.
 */
public class TestDataNodeErasureCodingMetrics {
    public static final Logger LOG = LoggerFactory.getLogger(TestDataNodeErasureCodingMetrics.class);

    private final ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();

    private final int dataBlocks = ecPolicy.getNumDataUnits();

    private final int parityBlocks = ecPolicy.getNumParityUnits();

    private final int cellSize = ecPolicy.getCellSize();

    private final int blockSize = (cellSize) * 2;

    private final int groupSize = (dataBlocks) + (parityBlocks);

    private final int blockGroupSize = (blockSize) * (dataBlocks);

    private final int numDNs = (groupSize) + 1;

    private MiniDFSCluster cluster;

    private Configuration conf;

    private DistributedFileSystem fs;

    @Test(timeout = 120000)
    public void testFullBlock() throws Exception {
        Assert.assertEquals(0, getLongMetric("EcReconstructionReadTimeMillis"));
        Assert.assertEquals(0, getLongMetric("EcReconstructionDecodingTimeMillis"));
        Assert.assertEquals(0, getLongMetric("EcReconstructionWriteTimeMillis"));
        doTest("/testEcMetrics", blockGroupSize, 0);
        Assert.assertEquals("EcReconstructionTasks should be ", 1, getLongMetric("EcReconstructionTasks"));
        Assert.assertEquals("EcFailedReconstructionTasks should be ", 0, getLongMetric("EcFailedReconstructionTasks"));
        Assert.assertTrue(((getLongMetric("EcDecodingTimeNanos")) > 0));
        Assert.assertEquals("EcReconstructionBytesRead should be ", blockGroupSize, getLongMetric("EcReconstructionBytesRead"));
        Assert.assertEquals("EcReconstructionBytesWritten should be ", blockSize, getLongMetric("EcReconstructionBytesWritten"));
        Assert.assertEquals("EcReconstructionRemoteBytesRead should be ", 0, getLongMetricWithoutCheck("EcReconstructionRemoteBytesRead"));
        Assert.assertTrue(((getLongMetric("EcReconstructionReadTimeMillis")) > 0));
        Assert.assertTrue(((getLongMetric("EcReconstructionDecodingTimeMillis")) > 0));
        Assert.assertTrue(((getLongMetric("EcReconstructionWriteTimeMillis")) > 0));
    }

    // A partial block, reconstruct the partial block
    @Test(timeout = 120000)
    public void testReconstructionBytesPartialGroup1() throws Exception {
        final int fileLen = (blockSize) / 10;
        doTest("/testEcBytes", fileLen, 0);
        Assert.assertEquals("EcReconstructionBytesRead should be ", fileLen, getLongMetric("EcReconstructionBytesRead"));
        Assert.assertEquals("EcReconstructionBytesWritten should be ", fileLen, getLongMetric("EcReconstructionBytesWritten"));
        Assert.assertEquals("EcReconstructionRemoteBytesRead should be ", 0, getLongMetricWithoutCheck("EcReconstructionRemoteBytesRead"));
    }

    // 1 full block + 5 partial block, reconstruct the full block
    @Test(timeout = 120000)
    public void testReconstructionBytesPartialGroup2() throws Exception {
        final int fileLen = (((cellSize) * (dataBlocks)) + (cellSize)) + ((cellSize) / 10);
        doTest("/testEcBytes", fileLen, 0);
        Assert.assertEquals("ecReconstructionBytesRead should be ", ((((cellSize) * (dataBlocks)) + (cellSize)) + ((cellSize) / 10)), getLongMetric("EcReconstructionBytesRead"));
        Assert.assertEquals("EcReconstructionBytesWritten should be ", blockSize, getLongMetric("EcReconstructionBytesWritten"));
        Assert.assertEquals("EcReconstructionRemoteBytesRead should be ", 0, getLongMetricWithoutCheck("EcReconstructionRemoteBytesRead"));
    }

    // 1 full block + 5 partial block, reconstruct the partial block
    @Test(timeout = 120000)
    public void testReconstructionBytesPartialGroup3() throws Exception {
        final int fileLen = (((cellSize) * (dataBlocks)) + (cellSize)) + ((cellSize) / 10);
        doTest("/testEcBytes", fileLen, 1);
        Assert.assertEquals("ecReconstructionBytesRead should be ", (((cellSize) * (dataBlocks)) + (((cellSize) / 10) * 2)), getLongMetric("EcReconstructionBytesRead"));
        Assert.assertEquals("ecReconstructionBytesWritten should be ", ((cellSize) + ((cellSize) / 10)), getLongMetric("EcReconstructionBytesWritten"));
        Assert.assertEquals("EcReconstructionRemoteBytesRead should be ", 0, getLongMetricWithoutCheck("EcReconstructionRemoteBytesRead"));
    }
}

