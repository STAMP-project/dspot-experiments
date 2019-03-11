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


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import DFSConfigKeys.DFS_CHECKSUM_TYPE_KEY;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;


/**
 * This class tests if FSOutputSummer works correctly.
 */
public class TestFSOutputSummer {
    private static final long seed = 3735928559L;

    private static final int BYTES_PER_CHECKSUM = 10;

    private static final int BLOCK_SIZE = 2 * (TestFSOutputSummer.BYTES_PER_CHECKSUM);

    private static final int HALF_CHUNK_SIZE = (TestFSOutputSummer.BYTES_PER_CHECKSUM) / 2;

    private static final int FILE_SIZE = (2 * (TestFSOutputSummer.BLOCK_SIZE)) - 1;

    private static final short NUM_OF_DATANODES = 2;

    private final byte[] expected = new byte[TestFSOutputSummer.FILE_SIZE];

    private final byte[] actual = new byte[TestFSOutputSummer.FILE_SIZE];

    private FileSystem fileSys;

    /**
     * Test write operation for output stream in DFS.
     */
    @Test
    public void testFSOutputSummer() throws Exception {
        doTestFSOutputSummer("CRC32");
        doTestFSOutputSummer("CRC32C");
        doTestFSOutputSummer("NULL");
    }

    @Test
    public void TestDFSCheckSumType() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setLong(DFS_BLOCK_SIZE_KEY, TestFSOutputSummer.BLOCK_SIZE);
        conf.setInt(DFS_BYTES_PER_CHECKSUM_KEY, TestFSOutputSummer.BYTES_PER_CHECKSUM);
        conf.set(DFS_CHECKSUM_TYPE_KEY, "NULL");
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestFSOutputSummer.NUM_OF_DATANODES).build();
        fileSys = cluster.getFileSystem();
        try {
            Path file = new Path("try.dat");
            Random rand = new Random(TestFSOutputSummer.seed);
            rand.nextBytes(expected);
            writeFile1(file);
        } finally {
            fileSys.close();
            cluster.shutdown();
        }
    }
}

