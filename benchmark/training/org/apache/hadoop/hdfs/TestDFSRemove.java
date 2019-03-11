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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;

import static DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_DEFAULT;


public class TestDFSRemove {
    final Path dir = new Path("/test/remove/");

    @Test
    public void testRemove() throws Exception {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).build();
        try {
            FileSystem fs = cluster.getFileSystem();
            Assert.assertTrue(fs.mkdirs(dir));
            long dfsUsedStart = TestDFSRemove.getTotalDfsUsed(cluster);
            {
                // Create 100 files
                final int fileCount = 100;
                for (int i = 0; i < fileCount; i++) {
                    Path a = new Path(dir, ("a" + i));
                    TestDFSRemove.createFile(fs, a);
                }
                long dfsUsedMax = TestDFSRemove.getTotalDfsUsed(cluster);
                // Remove 100 files
                for (int i = 0; i < fileCount; i++) {
                    Path a = new Path(dir, ("a" + i));
                    fs.delete(a, false);
                }
                // wait 3 heartbeat intervals, so that all blocks are deleted.
                Thread.sleep(((3 * (DFS_HEARTBEAT_INTERVAL_DEFAULT)) * 1000));
                // all blocks should be gone now.
                long dfsUsedFinal = TestDFSRemove.getTotalDfsUsed(cluster);
                Assert.assertEquals(((((("All blocks should be gone. start=" + dfsUsedStart) + " max=") + dfsUsedMax) + " final=") + dfsUsedFinal), dfsUsedStart, dfsUsedFinal);
            }
            fs.delete(dir, true);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

