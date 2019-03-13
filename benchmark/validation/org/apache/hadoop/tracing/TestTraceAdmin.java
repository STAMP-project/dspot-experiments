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


import DFSConfigKeys.DFS_DATANODE_KERBEROS_PRINCIPAL_KEY;
import DFSConfigKeys.DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY;
import java.io.File;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.datatransfer.sasl.SaslDataTransferTestCase;
import org.apache.hadoop.net.unix.TemporarySocketDirectory;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.htrace.core.Tracer;
import org.junit.Assert;
import org.junit.Test;

import static TraceUtils.DEFAULT_HADOOP_TRACE_PREFIX;


/**
 * Test cases for TraceAdmin.
 */
public class TestTraceAdmin extends SaslDataTransferTestCase {
    private static final String NEWLINE = System.getProperty("line.separator");

    private static final int ONE_DATANODE = 1;

    @Test
    public void testNoOperator() throws Exception {
        TraceAdmin trace = new TraceAdmin();
        trace.setConf(new Configuration());
        Assert.assertEquals(("ret:1, You must specify an operation." + (TestTraceAdmin.NEWLINE)), runTraceCommand(trace, "-host", "127.0.0.1:12346"));
    }

    @Test
    public void testCreateAndDestroySpanReceiver() throws Exception {
        Configuration conf = new Configuration();
        conf = new Configuration();
        conf.set(((DEFAULT_HADOOP_TRACE_PREFIX) + (Tracer.SPAN_RECEIVER_CLASSES_KEY)), "");
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build();
        cluster.waitActive();
        TemporarySocketDirectory tempDir = new TemporarySocketDirectory();
        String tracePath = new File(tempDir.getDir(), "tracefile").getAbsolutePath();
        try {
            TraceAdmin trace = new TraceAdmin();
            trace.setConf(conf);
            Assert.assertEquals(("ret:0, [no span receivers found]" + (TestTraceAdmin.NEWLINE)), runTraceCommand(trace, "-list", "-host", getHostPortForNN(cluster)));
            Assert.assertEquals(((("ret:0, Added trace span receiver 1 with " + "configuration hadoop.htrace.local.file.span.receiver.path = ") + tracePath) + (TestTraceAdmin.NEWLINE)), runTraceCommand(trace, "-add", "-host", getHostPortForNN(cluster), "-class", "org.apache.htrace.core.LocalFileSpanReceiver", ("-Chadoop.htrace.local.file.span.receiver.path=" + tracePath)));
            String list = runTraceCommand(trace, "-list", "-host", getHostPortForNN(cluster));
            Assert.assertTrue(list.startsWith("ret:0"));
            Assert.assertTrue(list.contains("1   org.apache.htrace.core.LocalFileSpanReceiver"));
            Assert.assertEquals(("ret:0, Removed trace span receiver 1" + (TestTraceAdmin.NEWLINE)), runTraceCommand(trace, "-remove", "1", "-host", getHostPortForNN(cluster)));
            Assert.assertEquals(("ret:0, [no span receivers found]" + (TestTraceAdmin.NEWLINE)), runTraceCommand(trace, "-list", "-host", getHostPortForNN(cluster)));
            Assert.assertEquals(((("ret:0, Added trace span receiver 2 with " + "configuration hadoop.htrace.local.file.span.receiver.path = ") + tracePath) + (TestTraceAdmin.NEWLINE)), runTraceCommand(trace, "-add", "-host", getHostPortForNN(cluster), "-class", "LocalFileSpanReceiver", ("-Chadoop.htrace.local.file.span.receiver.path=" + tracePath)));
            Assert.assertEquals(("ret:0, Removed trace span receiver 2" + (TestTraceAdmin.NEWLINE)), runTraceCommand(trace, "-remove", "2", "-host", getHostPortForNN(cluster)));
        } finally {
            cluster.shutdown();
            tempDir.close();
        }
    }

    /**
     * Test running hadoop trace commands with -principal option against
     * Kerberized NN and DN.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testKerberizedTraceAdmin() throws Exception {
        MiniDFSCluster cluster = null;
        final HdfsConfiguration conf = createSecureConfig("authentication,privacy");
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestTraceAdmin.ONE_DATANODE).build();
            cluster.waitActive();
            final String nnHost = getHostPortForNN(cluster);
            final String dnHost = getHostPortForDN(cluster, 0);
            // login using keytab and run commands
            UserGroupInformation.loginUserFromKeytabAndReturnUGI(SaslDataTransferTestCase.getHdfsPrincipal(), SaslDataTransferTestCase.getHdfsKeytab()).doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    // send trace command to NN
                    TraceAdmin trace = new TraceAdmin();
                    trace.setConf(conf);
                    final String[] nnTraceCmd = new String[]{ "-list", "-host", nnHost, "-principal", conf.get(DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY) };
                    int ret = trace.run(nnTraceCmd);
                    Assert.assertEquals(0, ret);
                    // send trace command to DN
                    final String[] dnTraceCmd = new String[]{ "-list", "-host", dnHost, "-principal", conf.get(DFS_DATANODE_KERBEROS_PRINCIPAL_KEY) };
                    ret = trace.run(dnTraceCmd);
                    Assert.assertEquals(0, ret);
                    return null;
                }
            });
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

