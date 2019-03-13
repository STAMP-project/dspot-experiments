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
package org.apache.hadoop.ozone.scm;


import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.TabularData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.junit.Test;


/**
 * Class which tests the SCMNodeManagerInfo Bean.
 */
public class TestSCMNodeManagerMXBean {
    public static final Log LOG = LogFactory.getLog(TestSCMMXBean.class);

    private static int numOfDatanodes = 3;

    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration conf;

    private static StorageContainerManager scm;

    private static MBeanServer mbs;

    @Test
    public void testDiskUsage() throws Exception {
        ObjectName bean = new ObjectName(("Hadoop:service=SCMNodeManager," + "name=SCMNodeManagerInfo"));
        TabularData data = ((TabularData) (TestSCMNodeManagerMXBean.mbs.getAttribute(bean, "NodeInfo")));
        Map<String, Long> datanodeInfo = TestSCMNodeManagerMXBean.scm.getScmNodeManager().getNodeInfo();
        verifyEquals(data, datanodeInfo);
    }

    @Test
    public void testNodeCount() throws Exception {
        ObjectName bean = new ObjectName(("Hadoop:service=SCMNodeManager," + "name=SCMNodeManagerInfo"));
        TabularData data = ((TabularData) (TestSCMNodeManagerMXBean.mbs.getAttribute(bean, "NodeCount")));
        Map<String, Integer> nodeCount = TestSCMNodeManagerMXBean.scm.getScmNodeManager().getNodeCount();
        Map<String, Long> nodeCountLong = new HashMap<>();
        nodeCount.forEach(( k, v) -> nodeCountLong.put(k, new Long(v)));
        verifyEquals(data, nodeCountLong);
    }
}

