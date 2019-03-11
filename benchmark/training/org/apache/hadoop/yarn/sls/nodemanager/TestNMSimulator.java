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
package org.apache.hadoop.yarn.sls.nodemanager;


import com.google.common.base.Supplier;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestNMSimulator {
    private final int GB = 1024;

    private ResourceManager rm;

    private YarnConfiguration conf;

    private Class slsScheduler;

    private Class scheduler;

    public TestNMSimulator(Class slsScheduler, Class scheduler) {
        this.slsScheduler = slsScheduler;
        this.scheduler = scheduler;
    }

    @Test
    public void testNMSimulator() throws Exception {
        // Register one node
        NMSimulator node1 = new NMSimulator();
        node1.init("/rack1/node1", Resources.createResource(((GB) * 10), 10), 0, 1000, rm, (-1.0F));
        node1.middleStep();
        int numClusterNodes = rm.getResourceScheduler().getNumClusterNodes();
        int cumulativeSleepTime = 0;
        int sleepInterval = 100;
        while ((numClusterNodes != 1) && (cumulativeSleepTime < 5000)) {
            Thread.sleep(sleepInterval);
            cumulativeSleepTime = cumulativeSleepTime + sleepInterval;
            numClusterNodes = rm.getResourceScheduler().getNumClusterNodes();
        } 
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return (rm.getResourceScheduler().getRootQueueMetrics().getAvailableMB()) > 0;
            }
        }, 500, 10000);
        Assert.assertEquals(1, rm.getResourceScheduler().getNumClusterNodes());
        Assert.assertEquals(((GB) * 10), rm.getResourceScheduler().getRootQueueMetrics().getAvailableMB());
        Assert.assertEquals(10, rm.getResourceScheduler().getRootQueueMetrics().getAvailableVirtualCores());
        // Allocate one container on node1
        ContainerId cId1 = newContainerId(1, 1, 1);
        Container container1 = Container.newInstance(cId1, null, null, Resources.createResource(GB, 1), null, null);
        node1.addNewContainer(container1, 100000L);
        Assert.assertTrue("Node1 should have one running container.", node1.getRunningContainers().containsKey(cId1));
        // Allocate one AM container on node1
        ContainerId cId2 = newContainerId(2, 1, 1);
        Container container2 = Container.newInstance(cId2, null, null, Resources.createResource(GB, 1), null, null);
        node1.addNewContainer(container2, (-1L));
        Assert.assertTrue("Node1 should have one running AM container", node1.getAMContainers().contains(cId2));
        // Remove containers
        node1.cleanupContainer(cId1);
        Assert.assertTrue("Container1 should be removed from Node1.", node1.getCompletedContainers().contains(cId1));
        node1.cleanupContainer(cId2);
        Assert.assertFalse("Container2 should be removed from Node1.", node1.getAMContainers().contains(cId2));
    }
}

