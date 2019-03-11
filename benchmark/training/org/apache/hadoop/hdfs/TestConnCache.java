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


import HdfsClientConfigKeys.DFS_CLIENT_CONTEXT;
import HdfsClientConfigKeys.DFS_CLIENT_SOCKET_TIMEOUT_KEY;
import java.net.InetSocketAddress;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.impl.BlockReaderTestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests the client connection caching in a single node
 * mini-cluster.
 */
public class TestConnCache {
    static final Logger LOG = LoggerFactory.getLogger(TestConnCache.class);

    static final int BLOCK_SIZE = 4096;

    static final int FILE_SIZE = 3 * (TestConnCache.BLOCK_SIZE);

    /**
     * Read a file served entirely from one DN. Seek around and read from
     * different offsets. And verify that they all use the same socket.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReadFromOneDN() throws Exception {
        HdfsConfiguration configuration = new HdfsConfiguration();
        // One of the goals of this test is to verify that we don't open more
        // than one socket.  So use a different client context, so that we
        // get our own socket cache, rather than sharing with the other test
        // instances.  Also use a really long socket timeout so that nothing
        // gets closed before we get around to checking the cache size at the end.
        final String contextName = "testReadFromOneDNContext";
        configuration.set(DFS_CLIENT_CONTEXT, contextName);
        configuration.setLong(DFS_CLIENT_SOCKET_TIMEOUT_KEY, 100000000L);
        BlockReaderTestUtil util = new BlockReaderTestUtil(1, configuration);
        final Path testFile = new Path("/testConnCache.dat");
        byte[] authenticData = util.writeFile(testFile, ((TestConnCache.FILE_SIZE) / 1024));
        DFSClient client = new DFSClient(new InetSocketAddress("localhost", util.getCluster().getNameNodePort()), util.getConf());
        DFSInputStream in = client.open(testFile.toString());
        TestConnCache.LOG.info(("opened " + (testFile.toString())));
        byte[] dataBuf = new byte[TestConnCache.BLOCK_SIZE];
        // Initial read
        pread(in, 0, dataBuf, 0, dataBuf.length, authenticData);
        // Read again and verify that the socket is the same
        pread(in, ((TestConnCache.FILE_SIZE) - (dataBuf.length)), dataBuf, 0, dataBuf.length, authenticData);
        pread(in, 1024, dataBuf, 0, dataBuf.length, authenticData);
        // No seek; just read
        pread(in, (-1), dataBuf, 0, dataBuf.length, authenticData);
        pread(in, 64, dataBuf, 0, ((dataBuf.length) / 2), authenticData);
        in.close();
        client.close();
        Assert.assertEquals(1, ClientContext.getFromConf(configuration).getPeerCache().size());
    }
}

