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


import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.server.datanode.SimulatedFSDataset;
import org.junit.Test;


/**
 * This class tests the creation of files with block-size
 * smaller than the default buffer size of 4K.
 */
public class TestSmallBlock {
    static final long seed = 3735928559L;

    static final int blockSize = 1;

    static final int fileSize = 20;

    boolean simulatedStorage = false;

    /**
     * Tests small block size in in DFS.
     */
    @Test
    public void testSmallBlock() throws IOException {
        Configuration conf = new HdfsConfiguration();
        if (simulatedStorage) {
            SimulatedFSDataset.setFactory(conf);
        }
        conf.set(DFS_BYTES_PER_CHECKSUM_KEY, "1");
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        DistributedFileSystem fileSys = cluster.getFileSystem();
        try {
            Path file1 = new Path("/smallblocktest.dat");
            DFSTestUtil.createFile(fileSys, file1, TestSmallBlock.fileSize, TestSmallBlock.fileSize, TestSmallBlock.blockSize, ((short) (1)), TestSmallBlock.seed);
            checkFile(fileSys, file1);
            cleanupFile(fileSys, file1);
        } finally {
            fileSys.close();
            cluster.shutdown();
        }
    }

    @Test
    public void testSmallBlockSimulatedStorage() throws IOException {
        simulatedStorage = true;
        testSmallBlock();
        simulatedStorage = false;
    }
}

