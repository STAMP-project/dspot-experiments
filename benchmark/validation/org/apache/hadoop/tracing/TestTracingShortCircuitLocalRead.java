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
package org.apache.hadoop.tracing;


import DFSConfigKeys.DFS_CHECKSUM_TYPE_KEY;
import DFSConfigKeys.DFS_DOMAIN_SOCKET_PATH_KEY;
import HdfsClientConfigKeys.Read.ShortCircuit.KEY;
import HdfsClientConfigKeys.Read.ShortCircuit.SKIP_CHECKSUM_KEY;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FsTracer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.net.unix.TemporarySocketDirectory;
import org.apache.hadoop.test.PlatformAssumptions;
import org.apache.hadoop.util.NativeCodeLoader;
import org.apache.htrace.core.TraceScope;
import org.apache.htrace.core.Tracer;
import org.junit.Assume;
import org.junit.Test;

import static TraceUtils.DEFAULT_HADOOP_TRACE_PREFIX;


public class TestTracingShortCircuitLocalRead {
    private static Configuration conf;

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem dfs;

    private static TemporarySocketDirectory sockDir;

    static final Path TEST_PATH = new Path("testShortCircuitTraceHooks");

    static final int TEST_LENGTH = 1234;

    @Test
    public void testShortCircuitTraceHooks() throws IOException {
        Assume.assumeTrue(NativeCodeLoader.isNativeCodeLoaded());
        PlatformAssumptions.assumeNotWindows();
        TestTracingShortCircuitLocalRead.conf = new Configuration();
        TestTracingShortCircuitLocalRead.conf.set(((DEFAULT_HADOOP_TRACE_PREFIX) + (Tracer.SPAN_RECEIVER_CLASSES_KEY)), SetSpanReceiver.class.getName());
        TestTracingShortCircuitLocalRead.conf.set(((DEFAULT_HADOOP_TRACE_PREFIX) + (Tracer.SAMPLER_CLASSES_KEY)), "AlwaysSampler");
        TestTracingShortCircuitLocalRead.conf.setLong("dfs.blocksize", (100 * 1024));
        TestTracingShortCircuitLocalRead.conf.setBoolean(KEY, true);
        TestTracingShortCircuitLocalRead.conf.setBoolean(SKIP_CHECKSUM_KEY, false);
        TestTracingShortCircuitLocalRead.conf.set(DFS_DOMAIN_SOCKET_PATH_KEY, new File(TestTracingShortCircuitLocalRead.sockDir.getDir(), "testShortCircuitTraceHooks._PORT.sock").getAbsolutePath());
        TestTracingShortCircuitLocalRead.conf.set(DFS_CHECKSUM_TYPE_KEY, "CRC32C");
        TestTracingShortCircuitLocalRead.cluster = new MiniDFSCluster.Builder(TestTracingShortCircuitLocalRead.conf).numDataNodes(1).build();
        TestTracingShortCircuitLocalRead.dfs = TestTracingShortCircuitLocalRead.cluster.getFileSystem();
        try {
            DFSTestUtil.createFile(TestTracingShortCircuitLocalRead.dfs, TestTracingShortCircuitLocalRead.TEST_PATH, TestTracingShortCircuitLocalRead.TEST_LENGTH, ((short) (1)), 5678L);
            TraceScope ts = FsTracer.get(TestTracingShortCircuitLocalRead.conf).newScope("testShortCircuitTraceHooks");
            FSDataInputStream stream = TestTracingShortCircuitLocalRead.dfs.open(TestTracingShortCircuitLocalRead.TEST_PATH);
            byte[] buf = new byte[TestTracingShortCircuitLocalRead.TEST_LENGTH];
            IOUtils.readFully(stream, buf, 0, TestTracingShortCircuitLocalRead.TEST_LENGTH);
            stream.close();
            ts.close();
            String[] expectedSpanNames = new String[]{ "OpRequestShortCircuitAccessProto", "ShortCircuitShmRequestProto" };
            SetSpanReceiver.assertSpanNamesFound(expectedSpanNames);
        } finally {
            TestTracingShortCircuitLocalRead.dfs.close();
            TestTracingShortCircuitLocalRead.cluster.shutdown();
        }
    }
}

