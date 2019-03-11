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
package org.apache.hadoop.metrics2.sink;


import MockSink.errored;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the {@link RollingFileSystemSink} class in the context of HDFS with
 * Kerberos enabled.
 */
public class TestRollingFileSystemSinkWithSecureHdfs extends RollingFileSystemSinkTestBase {
    private static final int NUM_DATANODES = 4;

    private static MiniKdc kdc;

    private static String sinkPrincipal;

    private static String sinkKeytab;

    private static String hdfsPrincipal;

    private static String hdfsKeytab;

    private static String spnegoPrincipal;

    private MiniDFSCluster cluster = null;

    private UserGroupInformation sink = null;

    /**
     * Do a basic write test against an HDFS cluster with Kerberos enabled. We
     * assume that if a basic write succeeds, more complex operations will also
     * succeed.
     *
     * @throws Exception
     * 		thrown if things break
     */
    @Test
    public void testWithSecureHDFS() throws Exception {
        final String path = ("hdfs://" + (cluster.getNameNode().getHostAndPort())) + "/tmp/test";
        final MetricsSystem ms = initMetricsSystem(path, true, false, true);
        assertMetricsContents(sink.doAs(new PrivilegedExceptionAction<String>() {
            @Override
            public String run() throws Exception {
                return doWriteTest(ms, path, 1);
            }
        }));
    }

    /**
     * Do a basic write test against an HDFS cluster with Kerberos enabled but
     * without the principal and keytab properties set.
     *
     * @throws Exception
     * 		thrown if things break
     */
    @Test
    public void testMissingPropertiesWithSecureHDFS() throws Exception {
        final String path = ("hdfs://" + (cluster.getNameNode().getHostAndPort())) + "/tmp/test";
        initMetricsSystem(path, true, false);
        Assert.assertTrue(("No exception was generated initializing the sink against a " + ("secure cluster even though the principal and keytab properties " + "were missing")), errored);
    }
}

