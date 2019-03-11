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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.fs.FsTracer;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.htrace.core.Tracer;
import org.junit.Assert;
import org.junit.Test;


public class TestTracing {
    private static MiniDFSCluster cluster;

    private static DistributedFileSystem dfs;

    private Tracer prevTracer;

    private static final Configuration TRACING_CONF;

    private static final Configuration NO_TRACING_CONF;

    static {
        NO_TRACING_CONF = new Configuration();
        TestTracing.NO_TRACING_CONF.setLong("dfs.blocksize", (100 * 1024));
        TRACING_CONF = new Configuration(TestTracing.NO_TRACING_CONF);
        TestTracing.TRACING_CONF.set(((CommonConfigurationKeys.FS_CLIENT_HTRACE_PREFIX) + (Tracer.SPAN_RECEIVER_CLASSES_KEY)), SetSpanReceiver.class.getName());
        TestTracing.TRACING_CONF.set(((CommonConfigurationKeys.FS_CLIENT_HTRACE_PREFIX) + (Tracer.SAMPLER_CLASSES_KEY)), "AlwaysSampler");
    }

    @Test
    public void testTracing() throws Exception {
        // write and read without tracing started
        String fileName = "testTracingDisabled.dat";
        writeTestFile(fileName);
        Assert.assertEquals(0, SetSpanReceiver.size());
        readTestFile(fileName);
        Assert.assertEquals(0, SetSpanReceiver.size());
        writeTestFile("testReadTraceHooks.dat");
        FsTracer.clear();
        Tracer tracer = FsTracer.get(TestTracing.TRACING_CONF);
        writeWithTracing(tracer);
        readWithTracing(tracer);
    }
}

