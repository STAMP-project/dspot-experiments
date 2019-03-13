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
package org.apache.hadoop.tools;


import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.tools.JMXGet;
import org.apache.hadoop.test.MetricsAsserts;
import org.junit.Assert;
import org.junit.Test;


/**
 * Startup and checkpoint tests
 */
public class TestJMXGet {
    public static final String WRONG_METRIC_VALUE_ERROR_MSG = "Unable to get the correct value for %s.";

    private Configuration config;

    private MiniDFSCluster cluster;

    static final long seed = 178958063L;

    static final int blockSize = 4096;

    static final int fileSize = 8192;

    /**
     * test JMX connection to NameNode..
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNameNode() throws Exception {
        int numDatanodes = 2;
        cluster = new MiniDFSCluster.Builder(config).numDataNodes(numDatanodes).build();
        cluster.waitActive();
        DFSTestUtil.createFile(cluster.getFileSystem(), new Path("/test1"), TestJMXGet.fileSize, TestJMXGet.fileSize, TestJMXGet.blockSize, ((short) (2)), TestJMXGet.seed);
        JMXGet jmx = new JMXGet();
        String serviceName = "NameNode";
        jmx.setService(serviceName);
        jmx.init();// default lists namenode mbeans only

        Assert.assertTrue("error printAllValues", TestJMXGet.checkPrintAllValues(jmx));
        // get some data from different source
        try {
            DFSTestUtil.waitForMetric(jmx, "NumLiveDataNodes", numDatanodes);
        } catch (TimeoutException e) {
            Assert.assertEquals(String.format(TestJMXGet.WRONG_METRIC_VALUE_ERROR_MSG, "NumLiveDataNodes"), numDatanodes, Integer.parseInt(jmx.getValue("NumLiveDataNodes")));
        }
        MetricsAsserts.assertGauge("CorruptBlocks", Long.parseLong(jmx.getValue("CorruptBlocks")), MetricsAsserts.getMetrics("FSNamesystem"));
        cluster.shutdown();
        MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
        ObjectName query = new ObjectName((("Hadoop:service=" + serviceName) + ",*"));
        Set<ObjectName> names = mbsc.queryNames(query, null);
        Assert.assertTrue(("No beans should be registered for " + serviceName), names.isEmpty());
    }

    /**
     * test JMX connection to DataNode..
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDataNode() throws Exception {
        int numDatanodes = 2;
        cluster = new MiniDFSCluster.Builder(config).numDataNodes(numDatanodes).build();
        cluster.waitActive();
        DFSTestUtil.createFile(cluster.getFileSystem(), new Path("/test"), TestJMXGet.fileSize, TestJMXGet.fileSize, TestJMXGet.blockSize, ((short) (2)), TestJMXGet.seed);
        JMXGet jmx = new JMXGet();
        String serviceName = "DataNode";
        jmx.setService(serviceName);
        jmx.init();
        try {
            DFSTestUtil.waitForMetric(jmx, "BytesWritten", TestJMXGet.fileSize);
        } catch (TimeoutException e) {
            Assert.assertEquals(String.format(TestJMXGet.WRONG_METRIC_VALUE_ERROR_MSG, "BytesWritten"), TestJMXGet.fileSize, Integer.parseInt(jmx.getValue("BytesWritten")));
        }
        cluster.shutdown();
        MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
        ObjectName query = new ObjectName((("Hadoop:service=" + serviceName) + ",*"));
        Set<ObjectName> names = mbsc.queryNames(query, null);
        Assert.assertTrue(("No beans should be registered for " + serviceName), names.isEmpty());
    }
}

