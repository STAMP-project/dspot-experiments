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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * To test {@link org.apache.hadoop.hdfs.ErasureCodeBenchmarkThroughput}.
 */
public class TestErasureCodeBenchmarkThroughput {
    private static MiniDFSCluster cluster;

    private static Configuration conf;

    private static FileSystem fs;

    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    @Test
    public void testReplicaReadWrite() throws Exception {
        Integer dataSize = 10;
        Integer numClient = 3;
        String[] args = new String[]{ "write", dataSize.toString(), "rep", numClient.toString() };
        TestErasureCodeBenchmarkThroughput.runBenchmark(args);
        args[0] = "gen";
        TestErasureCodeBenchmarkThroughput.runBenchmark(args);
        args[0] = "read";
        TestErasureCodeBenchmarkThroughput.runBenchmark(args);
    }

    @Test
    public void testECReadWrite() throws Exception {
        Integer dataSize = 5;
        Integer numClient = 5;
        String[] args = new String[]{ "write", dataSize.toString(), "ec", numClient.toString() };
        TestErasureCodeBenchmarkThroughput.runBenchmark(args);
        args[0] = "gen";
        TestErasureCodeBenchmarkThroughput.runBenchmark(args);
        args[0] = "read";
        TestErasureCodeBenchmarkThroughput.runBenchmark(args);
    }

    @Test
    public void testCleanUp() throws Exception {
        Integer dataSize = 5;
        Integer numClient = 5;
        String[] args = new String[]{ "gen", dataSize.toString(), "ec", numClient.toString() };
        TestErasureCodeBenchmarkThroughput.runBenchmark(args);
        args[0] = "clean";
        TestErasureCodeBenchmarkThroughput.runBenchmark(args);
        TestErasureCodeBenchmarkThroughput.verifyNumFile(dataSize, true, 0);
    }
}

