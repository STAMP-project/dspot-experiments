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
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.junit.Test;


/**
 * This class tests if FSInputChecker works correctly.
 */
public class TestFSInputChecker {
    static final long seed = 3735928559L;

    static final int BYTES_PER_SUM = 10;

    static final int BLOCK_SIZE = 2 * (TestFSInputChecker.BYTES_PER_SUM);

    static final int HALF_CHUNK_SIZE = (TestFSInputChecker.BYTES_PER_SUM) / 2;

    static final int FILE_SIZE = (2 * (TestFSInputChecker.BLOCK_SIZE)) - 1;

    static final short NUM_OF_DATANODES = 2;

    final byte[] expected = new byte[TestFSInputChecker.FILE_SIZE];

    byte[] actual;

    FSDataInputStream stm;

    final Random rand = new Random(TestFSInputChecker.seed);

    @Test
    public void testFSInputChecker() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setLong(DFS_BLOCK_SIZE_KEY, TestFSInputChecker.BLOCK_SIZE);
        conf.setInt(DFS_BYTES_PER_CHECKSUM_KEY, TestFSInputChecker.BYTES_PER_SUM);
        rand.nextBytes(expected);
        // test DFS
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        FileSystem fileSys = cluster.getFileSystem();
        try {
            testChecker(fileSys, true);
            testChecker(fileSys, false);
            testSeekAndRead(fileSys);
        } finally {
            fileSys.close();
            cluster.shutdown();
        }
        // test Local FS
        fileSys = FileSystem.getLocal(conf);
        try {
            testChecker(fileSys, true);
            testChecker(fileSys, false);
            testFileCorruption(((LocalFileSystem) (fileSys)));
            testSeekAndRead(fileSys);
        } finally {
            fileSys.close();
        }
    }
}

