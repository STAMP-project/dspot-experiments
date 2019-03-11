/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.ozone.scm;


import HddsProtos.LifeCycleEvent.CLOSE;
import HddsProtos.LifeCycleEvent.FINALIZE;
import HddsProtos.LifeCycleState.CLOSED;
import HddsProtos.LifeCycleState.CLOSING;
import HddsProtos.ReplicationFactor.ONE;
import HddsProtos.ReplicationType.STAND_ALONE;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.container.ContainerID;
import org.apache.hadoop.hdds.scm.container.ContainerInfo;
import org.apache.hadoop.hdds.scm.container.ContainerManager;
import org.apache.hadoop.hdds.scm.container.placement.metrics.ContainerStat;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class is to test JMX management interface for scm information.
 */
public class TestSCMMXBean {
    public static final Log LOG = LogFactory.getLog(TestSCMMXBean.class);

    private static int numOfDatanodes = 1;

    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration conf;

    private static StorageContainerManager scm;

    private static MBeanServer mbs;

    @Test
    public void testSCMMXBean() throws Exception {
        ObjectName bean = new ObjectName(("Hadoop:service=StorageContainerManager," + ("name=StorageContainerManagerInfo," + "component=ServerRuntime")));
        String dnRpcPort = ((String) (TestSCMMXBean.mbs.getAttribute(bean, "DatanodeRpcPort")));
        Assert.assertEquals(TestSCMMXBean.scm.getDatanodeRpcPort(), dnRpcPort);
        String clientRpcPort = ((String) (TestSCMMXBean.mbs.getAttribute(bean, "ClientRpcPort")));
        Assert.assertEquals(TestSCMMXBean.scm.getClientRpcPort(), clientRpcPort);
        ConcurrentMap<String, ContainerStat> map = TestSCMMXBean.scm.getContainerReportCache();
        ContainerStat stat = new ContainerStat(1, 2, 3, 4, 5, 6, 7);
        map.put("nodeID", stat);
        TabularData data = ((TabularData) (TestSCMMXBean.mbs.getAttribute(bean, "ContainerReport")));
        // verify report info
        Assert.assertEquals(1, data.values().size());
        for (Object obj : data.values()) {
            Assert.assertTrue((obj instanceof CompositeData));
            CompositeData d = ((CompositeData) (obj));
            Iterator<?> it = d.values().iterator();
            String key = it.next().toString();
            String value = it.next().toString();
            Assert.assertEquals("nodeID", key);
            Assert.assertEquals(stat.toJsonString(), value);
        }
        boolean inChillMode = ((boolean) (TestSCMMXBean.mbs.getAttribute(bean, "InChillMode")));
        Assert.assertEquals(TestSCMMXBean.scm.isInChillMode(), inChillMode);
        double containerThreshold = ((double) (TestSCMMXBean.mbs.getAttribute(bean, "ChillModeCurrentContainerThreshold")));
        Assert.assertEquals(TestSCMMXBean.scm.getCurrentContainerThreshold(), containerThreshold, 0);
    }

    @Test
    public void testSCMContainerStateCount() throws Exception {
        ObjectName bean = new ObjectName(("Hadoop:service=StorageContainerManager," + ("name=StorageContainerManagerInfo," + "component=ServerRuntime")));
        TabularData data = ((TabularData) (TestSCMMXBean.mbs.getAttribute(bean, "ContainerStateCount")));
        Map<String, Integer> containerStateCount = TestSCMMXBean.scm.getContainerStateCount();
        verifyEquals(data, containerStateCount);
        // Do some changes like allocate containers and change the container states
        ContainerManager scmContainerManager = TestSCMMXBean.scm.getContainerManager();
        List<ContainerInfo> containerInfoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            containerInfoList.add(scmContainerManager.allocateContainer(STAND_ALONE, ONE, UUID.randomUUID().toString()));
        }
        long containerID;
        for (int i = 0; i < 10; i++) {
            if ((i % 2) == 0) {
                containerID = containerInfoList.get(i).getContainerID();
                scmContainerManager.updateContainerState(new ContainerID(containerID), FINALIZE);
                Assert.assertEquals(scmContainerManager.getContainer(new ContainerID(containerID)).getState(), CLOSING);
            } else {
                containerID = containerInfoList.get(i).getContainerID();
                scmContainerManager.updateContainerState(new ContainerID(containerID), FINALIZE);
                scmContainerManager.updateContainerState(new ContainerID(containerID), CLOSE);
                Assert.assertEquals(scmContainerManager.getContainer(new ContainerID(containerID)).getState(), CLOSED);
            }
        }
        data = ((TabularData) (TestSCMMXBean.mbs.getAttribute(bean, "ContainerStateCount")));
        containerStateCount = TestSCMMXBean.scm.getContainerStateCount();
        containerStateCount.forEach(( k, v) -> {
            if (k == (CLOSING.toString())) {
                Assert.assertEquals(((int) (v)), 5);
            } else
                if (k == (CLOSED.toString())) {
                    Assert.assertEquals(((int) (v)), 5);
                } else {
                    // Remaining all container state count should be zero.
                    Assert.assertEquals(((int) (v)), 0);
                }

        });
        verifyEquals(data, containerStateCount);
    }
}

