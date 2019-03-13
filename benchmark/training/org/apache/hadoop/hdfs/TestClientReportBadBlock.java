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


import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class is used to test client reporting corrupted block replica to name node.
 * The reporting policy is if block replica is more than one, if all replicas
 * are corrupted, client does not report (since the client can handicapped). If
 * some of the replicas are corrupted, client reports the corrupted block
 * replicas. In case of only one block replica, client always reports corrupted
 * replica.
 */
public class TestClientReportBadBlock {
    private static final Logger LOG = LoggerFactory.getLogger(TestClientReportBadBlock.class);

    static final long BLOCK_SIZE = 64 * 1024;

    private static int buffersize;

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem dfs;

    private static final int numDataNodes = 3;

    private static final Configuration conf = new HdfsConfiguration();

    Random rand = new Random();

    /* This test creates a file with one block replica. Corrupt the block. Make
    DFSClient read the corrupted file. Corrupted block is expected to be
    reported to name node.
     */
    @Test
    public void testOneBlockReplica() throws Exception {
        final short repl = 1;
        final int corruptBlockNumber = 1;
        for (int i = 0; i < 2; i++) {
            // create a file
            String fileName = "/tmp/testClientReportBadBlock/OneBlockReplica" + i;
            Path filePath = new Path(fileName);
            createAFileWithCorruptedBlockReplicas(filePath, repl, corruptBlockNumber);
            if (i == 0) {
                dfsClientReadFile(filePath);
            } else {
                dfsClientReadFileFromPosition(filePath);
            }
            // the only block replica is corrupted. The LocatedBlock should be marked
            // as corrupted. But the corrupted replica is expected to be returned
            // when calling Namenode#getBlockLocations() since all(one) replicas are
            // corrupted.
            int expectedReplicaCount = 1;
            verifyCorruptedBlockCount(filePath, expectedReplicaCount);
            verifyFirstBlockCorrupted(filePath, true);
            TestClientReportBadBlock.verifyFsckBlockCorrupted();
            TestClientReportBadBlock.testFsckListCorruptFilesBlocks(filePath, (-1));
        }
    }

    /**
     * This test creates a file with three block replicas. Corrupt all of the
     * replicas. Make dfs client read the file. No block corruption should be
     * reported.
     */
    @Test
    public void testCorruptAllOfThreeReplicas() throws Exception {
        final short repl = 3;
        final int corruptBlockNumber = 3;
        for (int i = 0; i < 2; i++) {
            // create a file
            String fileName = "/tmp/testClientReportBadBlock/testCorruptAllReplicas" + i;
            Path filePath = new Path(fileName);
            createAFileWithCorruptedBlockReplicas(filePath, repl, corruptBlockNumber);
            // ask dfs client to read the file
            if (i == 0) {
                dfsClientReadFile(filePath);
            } else {
                dfsClientReadFileFromPosition(filePath);
            }
            // As all replicas are corrupted. We expect DFSClient does NOT report
            // corrupted replicas to the name node.
            int expectedReplicasReturned = repl;
            verifyCorruptedBlockCount(filePath, expectedReplicasReturned);
            // LocatedBlock should not have the block marked as corrupted.
            verifyFirstBlockCorrupted(filePath, false);
            TestClientReportBadBlock.verifyFsckHealth("");
            TestClientReportBadBlock.testFsckListCorruptFilesBlocks(filePath, 0);
        }
    }

    /**
     * This test creates a file with three block replicas. Corrupt two of the
     * replicas. Make dfs client read the file. The corrupted blocks with their
     * owner data nodes should be reported to the name node.
     */
    @Test
    public void testCorruptTwoOutOfThreeReplicas() throws Exception {
        final short repl = 3;
        final int corruptBlocReplicas = 2;
        for (int i = 0; i < 2; i++) {
            String fileName = "/tmp/testClientReportBadBlock/CorruptTwoOutOfThreeReplicas" + i;
            Path filePath = new Path(fileName);
            createAFileWithCorruptedBlockReplicas(filePath, repl, corruptBlocReplicas);
            int replicaCount = 0;
            /* The order of data nodes in LocatedBlock returned by name node is sorted 
            by NetworkToplology#pseudoSortByDistance. In current MiniDFSCluster, 
            when LocatedBlock is returned, the sorting is based on a random order.
            That is to say, the DFS client and simulated data nodes in mini DFS
            cluster are considered not on the same host nor the same rack.
            Therefore, even we corrupted the first two block replicas based in 
            order. When DFSClient read some block replicas, it is not guaranteed 
            which block replicas (good/bad) will be returned first. So we try to 
            re-read the file until we know the expected replicas numbers is 
            returned.
             */
            while (replicaCount != (repl - corruptBlocReplicas)) {
                if (i == 0) {
                    dfsClientReadFile(filePath);
                } else {
                    dfsClientReadFileFromPosition(filePath);
                }
                LocatedBlocks blocks = TestClientReportBadBlock.dfs.dfs.getNamenode().getBlockLocations(filePath.toString(), 0, Long.MAX_VALUE);
                replicaCount = blocks.get(0).getLocations().length;
            } 
            verifyFirstBlockCorrupted(filePath, false);
            int expectedReplicaCount = repl - corruptBlocReplicas;
            verifyCorruptedBlockCount(filePath, expectedReplicaCount);
            TestClientReportBadBlock.verifyFsckHealth("Target Replicas is 3 but found 1 live replica");
            TestClientReportBadBlock.testFsckListCorruptFilesBlocks(filePath, 0);
        }
    }
}

