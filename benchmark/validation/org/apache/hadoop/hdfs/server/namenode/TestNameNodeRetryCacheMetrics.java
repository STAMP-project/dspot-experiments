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
package org.apache.hadoop.hdfs.server.namenode;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.ipc.metrics.RetryCacheMetrics;
import org.junit.Test;


/**
 * Tests for ensuring the namenode retry cache metrics works correctly for
 * non-idempotent requests.
 *
 * Retry cache works based on tracking previously received request based on the
 * ClientId and CallId received in RPC requests and storing the response. The
 * response is replayed on retry when the same request is received again.
 */
public class TestNameNodeRetryCacheMetrics {
    private MiniDFSCluster cluster;

    private FSNamesystem namesystem;

    private DistributedFileSystem filesystem;

    private final int namenodeId = 0;

    private Configuration conf;

    private RetryCacheMetrics metrics;

    private DFSClient client;

    @Test
    public void testRetryCacheMetrics() throws IOException {
        checkMetrics(0, 0, 0);
        // DFS_CLIENT_TEST_DROP_NAMENODE_RESPONSE_NUM_KEY is 2 ,
        // so 2 requests are dropped at first.
        // After that, 1 request will reach NameNode correctly.
        trySaveNamespace();
        checkMetrics(2, 0, 1);
        // RetryCache will be cleared after Namesystem#close()
        namesystem.close();
        checkMetrics(2, 1, 1);
    }
}

