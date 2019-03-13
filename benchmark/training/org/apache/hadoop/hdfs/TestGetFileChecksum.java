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


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


public class TestGetFileChecksum {
    private static final int BLOCKSIZE = 1024;

    private static final short REPLICATION = 3;

    private Configuration conf;

    private MiniDFSCluster cluster;

    private DistributedFileSystem dfs;

    @Test
    public void testGetFileChecksumForBlocksUnderConstruction() {
        try {
            FSDataOutputStream file = dfs.create(new Path("/testFile"));
            file.write("Performance Testing".getBytes());
            dfs.getFileChecksum(new Path("/testFile"));
            Assert.fail(("getFileChecksum should fail for files " + "with blocks under construction"));
        } catch (IOException ie) {
            Assert.assertTrue(ie.getMessage().contains(("Fail to get checksum, since file /testFile " + "is under construction.")));
        }
    }

    @Test
    public void testGetFileChecksum() throws Exception {
        testGetFileChecksum(new Path("/foo"), ((TestGetFileChecksum.BLOCKSIZE) / 4));
        testGetFileChecksum(new Path("/bar"), (((TestGetFileChecksum.BLOCKSIZE) / 4) - 1));
    }
}

