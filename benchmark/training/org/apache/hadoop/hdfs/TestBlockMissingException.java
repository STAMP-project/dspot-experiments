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


import HdfsClientConfigKeys.Retry.WINDOW_BASE_KEY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestBlockMissingException {
    static final Logger LOG = LoggerFactory.getLogger("org.apache.hadoop.hdfs.TestBlockMissing");

    static final int NUM_DATANODES = 3;

    Configuration conf;

    MiniDFSCluster dfs = null;

    DistributedFileSystem fileSys = null;

    /**
     * Test DFS Raid
     */
    @Test
    public void testBlockMissingException() throws Exception {
        TestBlockMissingException.LOG.info("Test testBlockMissingException started.");
        long blockSize = 1024L;
        int numBlocks = 4;
        conf = new HdfsConfiguration();
        // Set short retry timeouts so this test runs faster
        conf.setInt(WINDOW_BASE_KEY, 10);
        try {
            dfs = new MiniDFSCluster.Builder(conf).numDataNodes(TestBlockMissingException.NUM_DATANODES).build();
            dfs.waitActive();
            fileSys = dfs.getFileSystem();
            Path file1 = new Path("/user/dhruba/raidtest/file1");
            createOldFile(fileSys, file1, 1, numBlocks, blockSize);
            // extract block locations from File system. Wait till file is closed.
            LocatedBlocks locations = null;
            locations = fileSys.dfs.getNamenode().getBlockLocations(file1.toString(), 0, (numBlocks * blockSize));
            // remove block of file
            TestBlockMissingException.LOG.info("Remove first block of file");
            dfs.corruptBlockOnDataNodesByDeletingBlockFile(locations.get(0).getBlock());
            // validate that the system throws BlockMissingException
            validateFile(fileSys, file1);
        } finally {
            if ((fileSys) != null)
                fileSys.close();

            if ((dfs) != null)
                dfs.shutdown();

        }
        TestBlockMissingException.LOG.info("Test testBlockMissingException completed.");
    }
}

