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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test reading a striped file when some of its blocks are missing (not included
 * in the block locations returned by the NameNode).
 */
public class TestReadStripedFileWithMissingBlocks {
    public static final Logger LOG = LoggerFactory.getLogger(TestReadStripedFileWithMissingBlocks.class);

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    private DFSClient dfsClient;

    private Configuration conf = new HdfsConfiguration();

    private final ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();

    private final short dataBlocks = ((short) (ecPolicy.getNumDataUnits()));

    private final short parityBlocks = ((short) (ecPolicy.getNumParityUnits()));

    private final int cellSize = ecPolicy.getCellSize();

    private final int stripPerBlock = 4;

    private final int blockSize = (stripPerBlock) * (cellSize);

    private final int blockGroupSize = (blockSize) * (dataBlocks);

    // Starting with two more datanodes, minimum 9 should be up for
    // test to pass.
    private final int numDNs = ((dataBlocks) + (parityBlocks)) + 2;

    private final int fileLength = ((blockSize) * (dataBlocks)) + 123;

    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    @Test
    public void testReadFileWithMissingBlocks() throws Exception {
        try {
            setup();
            Path srcPath = new Path("/foo");
            final byte[] expected = StripedFileTestUtil.generateBytes(fileLength);
            DFSTestUtil.writeFile(fs, srcPath, new String(expected));
            StripedFileTestUtil.waitBlockGroupsReported(fs, srcPath.toUri().getPath());
            StripedFileTestUtil.verifyLength(fs, srcPath, fileLength);
            for (int missingData = 1; missingData <= (dataBlocks); missingData++) {
                for (int missingParity = 0; missingParity <= ((parityBlocks) - missingData); missingParity++) {
                    readFileWithMissingBlocks(srcPath, fileLength, missingData, missingParity, expected);
                }
            }
        } finally {
            tearDown();
        }
    }
}

