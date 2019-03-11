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


import DataNode.LOG;
import java.util.Arrays;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.util.StripedBlockUtil;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.StringUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestLeaseRecoveryStriped {
    public static final Logger LOG = LoggerFactory.getLogger(TestLeaseRecoveryStriped.class);

    private final ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();

    private final int dataBlocks = ecPolicy.getNumDataUnits();

    private final int parityBlocks = ecPolicy.getNumParityUnits();

    private final int cellSize = ecPolicy.getCellSize();

    private final int stripeSize = (dataBlocks) * (cellSize);

    private final int stripesPerBlock = 4;

    private final int blockSize = (cellSize) * (stripesPerBlock);

    private final int blockGroupSize = (blockSize) * (dataBlocks);

    private static final int bytesPerChecksum = 512;

    static {
        GenericTestUtils.setLogLevel(DataNode.LOG, Level.ALL);
        GenericTestUtils.setLogLevel(DFSStripedOutputStream.LOG, Level.DEBUG);
        GenericTestUtils.setLogLevel(BlockRecoveryWorker.LOG, Level.DEBUG);
        GenericTestUtils.setLogLevel(DataStreamer.LOG, Level.DEBUG);
    }

    private static final String fakeUsername = "fakeUser1";

    private static final String fakeGroup = "supergroup";

    private MiniDFSCluster cluster;

    private DistributedFileSystem dfs;

    private Configuration conf;

    private final Path dir = new Path(("/" + (this.getClass().getSimpleName())));

    final Path p = new Path(dir, "testfile");

    private final int testFileLength = ((stripesPerBlock) - 1) * (stripeSize);

    private static class BlockLengths {
        private final int[] blockLengths;

        private final long safeLength;

        BlockLengths(ErasureCodingPolicy policy, int[] blockLengths) {
            this.blockLengths = blockLengths;
            long[] longArray = Arrays.stream(blockLengths).asLongStream().toArray();
            this.safeLength = StripedBlockUtil.getSafeLength(policy, longArray);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("blockLengths", getBlockLengths()).append("safeLength", getSafeLength()).toString();
        }

        /**
         * Length of each block in a block group.
         */
        public int[] getBlockLengths() {
            return blockLengths;
        }

        /**
         * Safe length, calculated by the block lengths.
         */
        public long getSafeLength() {
            return safeLength;
        }
    }

    private final TestLeaseRecoveryStriped.BlockLengths[] blockLengthsSuite = getBlockLengthsSuite();

    @Test
    public void testLeaseRecovery() throws Exception {
        TestLeaseRecoveryStriped.LOG.info(("blockLengthsSuite: " + (Arrays.toString(blockLengthsSuite))));
        for (int i = 0; i < (blockLengthsSuite.length); i++) {
            TestLeaseRecoveryStriped.BlockLengths blockLengths = blockLengthsSuite[i];
            try {
                runTest(blockLengths.getBlockLengths(), blockLengths.getSafeLength());
            } catch (Throwable e) {
                String msg = (((("failed testCase at i=" + i) + ", blockLengths=") + blockLengths) + "\n") + (StringUtils.stringifyException(e));
                Assert.fail(msg);
            }
        }
    }
}

