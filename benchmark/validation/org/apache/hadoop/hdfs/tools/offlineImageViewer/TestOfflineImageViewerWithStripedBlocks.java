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
package org.apache.hadoop.hdfs.tools.offlineImageViewer;


import java.io.IOException;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.junit.Test;


public class TestOfflineImageViewerWithStripedBlocks {
    private final ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();

    private int dataBlocks = ecPolicy.getNumDataUnits();

    private int parityBlocks = ecPolicy.getNumParityUnits();

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem fs;

    private final int cellSize = ecPolicy.getCellSize();

    private final int stripesPerBlock = 3;

    private final int blockSize = (cellSize) * (stripesPerBlock);

    @Test(timeout = 60000)
    public void testFileEqualToOneStripe() throws Exception {
        int numBytes = cellSize;
        testFileSize(numBytes);
    }

    @Test(timeout = 60000)
    public void testFileLessThanOneStripe() throws Exception {
        int numBytes = (cellSize) - 100;
        testFileSize(numBytes);
    }

    @Test(timeout = 60000)
    public void testFileHavingMultipleBlocks() throws Exception {
        int numBytes = (blockSize) * 3;
        testFileSize(numBytes);
    }

    @Test(timeout = 60000)
    public void testFileLargerThanABlockGroup1() throws IOException {
        testFileSize(((((blockSize) * (dataBlocks)) + (cellSize)) + 123));
    }

    @Test(timeout = 60000)
    public void testFileLargerThanABlockGroup2() throws IOException {
        testFileSize(((((((blockSize) * (dataBlocks)) * 3) + ((cellSize) * (dataBlocks))) + (cellSize)) + 123));
    }

    @Test(timeout = 60000)
    public void testFileFullBlockGroup() throws IOException {
        testFileSize(((blockSize) * (dataBlocks)));
    }

    @Test(timeout = 60000)
    public void testFileMoreThanOneStripe() throws Exception {
        int numBytes = (blockSize) + ((blockSize) / 2);
        testFileSize(numBytes);
    }
}

