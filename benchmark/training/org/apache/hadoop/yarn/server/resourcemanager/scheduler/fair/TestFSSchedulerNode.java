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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;


import java.util.ArrayList;
import java.util.Collections;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test scheduler node, especially preemption reservations.
 */
public class TestFSSchedulerNode {
    private final ArrayList<RMContainer> containers = new ArrayList<>();

    /**
     * Allocate and release a single container.
     */
    @Test
    public void testSimpleAllocation() {
        RMNode node = createNode();
        FSSchedulerNode schedulerNode = new FSSchedulerNode(node, false);
        createDefaultContainer();
        Assert.assertEquals("Nothing should have been allocated, yet", Resources.none(), schedulerNode.getAllocatedResource());
        schedulerNode.allocateContainer(containers.get(0));
        Assert.assertEquals("Container should be allocated", containers.get(0).getContainer().getResource(), schedulerNode.getAllocatedResource());
        schedulerNode.releaseContainer(containers.get(0).getContainerId(), true);
        Assert.assertEquals("Everything should have been released", Resources.none(), schedulerNode.getAllocatedResource());
        // Check that we are error prone
        schedulerNode.releaseContainer(containers.get(0).getContainerId(), true);
        finalValidation(schedulerNode);
    }

    /**
     * Allocate and release three containers with launch.
     */
    @Test
    public void testMultipleAllocations() {
        RMNode node = createNode();
        FSSchedulerNode schedulerNode = new FSSchedulerNode(node, false);
        createDefaultContainer();
        createDefaultContainer();
        createDefaultContainer();
        Assert.assertEquals("Nothing should have been allocated, yet", Resources.none(), schedulerNode.getAllocatedResource());
        schedulerNode.allocateContainer(containers.get(0));
        schedulerNode.containerStarted(containers.get(0).getContainerId());
        schedulerNode.allocateContainer(containers.get(1));
        schedulerNode.containerStarted(containers.get(1).getContainerId());
        schedulerNode.allocateContainer(containers.get(2));
        Assert.assertEquals("Container should be allocated", Resources.multiply(containers.get(0).getContainer().getResource(), 3.0), schedulerNode.getAllocatedResource());
        schedulerNode.releaseContainer(containers.get(1).getContainerId(), true);
        schedulerNode.releaseContainer(containers.get(2).getContainerId(), true);
        schedulerNode.releaseContainer(containers.get(0).getContainerId(), true);
        finalValidation(schedulerNode);
    }

    /**
     * Allocate and release a single container.
     */
    @Test
    public void testSimplePreemption() {
        RMNode node = createNode();
        FSSchedulerNode schedulerNode = new FSSchedulerNode(node, false);
        // Launch containers and saturate the cluster
        saturateCluster(schedulerNode);
        Assert.assertEquals("Container should be allocated", Resources.multiply(containers.get(0).getContainer().getResource(), containers.size()), schedulerNode.getAllocatedResource());
        // Request preemption
        FSAppAttempt starvingApp = createStarvingApp(schedulerNode, Resource.newInstance(1024, 1));
        schedulerNode.addContainersForPreemption(Collections.singletonList(containers.get(0)), starvingApp);
        Assert.assertEquals("No resource amount should be reserved for preemptees", containers.get(0).getAllocatedResource(), schedulerNode.getTotalReserved());
        // Preemption occurs release one container
        schedulerNode.releaseContainer(containers.get(0).getContainerId(), true);
        allocateContainers(schedulerNode);
        Assert.assertEquals("Container should be allocated", schedulerNode.getTotalResource(), schedulerNode.getAllocatedResource());
        // Release all remaining containers
        for (int i = 1; i < (containers.size()); ++i) {
            schedulerNode.releaseContainer(containers.get(i).getContainerId(), true);
        }
        finalValidation(schedulerNode);
    }

    /**
     * Allocate a single container twice and release.
     */
    @Test
    public void testDuplicatePreemption() {
        RMNode node = createNode();
        FSSchedulerNode schedulerNode = new FSSchedulerNode(node, false);
        // Launch containers and saturate the cluster
        saturateCluster(schedulerNode);
        Assert.assertEquals("Container should be allocated", Resources.multiply(containers.get(0).getContainer().getResource(), containers.size()), schedulerNode.getAllocatedResource());
        // Request preemption twice
        FSAppAttempt starvingApp = createStarvingApp(schedulerNode, Resource.newInstance(1024, 1));
        schedulerNode.addContainersForPreemption(Collections.singletonList(containers.get(0)), starvingApp);
        schedulerNode.addContainersForPreemption(Collections.singletonList(containers.get(0)), starvingApp);
        Assert.assertEquals("No resource amount should be reserved for preemptees", containers.get(0).getAllocatedResource(), schedulerNode.getTotalReserved());
        // Preemption occurs release one container
        schedulerNode.releaseContainer(containers.get(0).getContainerId(), true);
        allocateContainers(schedulerNode);
        Assert.assertEquals("Container should be allocated", schedulerNode.getTotalResource(), schedulerNode.getAllocatedResource());
        // Release all remaining containers
        for (int i = 1; i < (containers.size()); ++i) {
            schedulerNode.releaseContainer(containers.get(i).getContainerId(), true);
        }
        finalValidation(schedulerNode);
    }

    /**
     * Allocate and release three containers requested by two apps.
     */
    @Test
    public void testComplexPreemption() {
        RMNode node = createNode();
        FSSchedulerNode schedulerNode = new FSSchedulerNode(node, false);
        // Launch containers and saturate the cluster
        saturateCluster(schedulerNode);
        Assert.assertEquals("Container should be allocated", Resources.multiply(containers.get(0).getContainer().getResource(), containers.size()), schedulerNode.getAllocatedResource());
        // Preempt a container
        FSAppAttempt starvingApp1 = createStarvingApp(schedulerNode, Resource.newInstance(2048, 2));
        FSAppAttempt starvingApp2 = createStarvingApp(schedulerNode, Resource.newInstance(1024, 1));
        // Preemption thread kicks in
        schedulerNode.addContainersForPreemption(Collections.singletonList(containers.get(0)), starvingApp1);
        schedulerNode.addContainersForPreemption(Collections.singletonList(containers.get(1)), starvingApp1);
        schedulerNode.addContainersForPreemption(Collections.singletonList(containers.get(2)), starvingApp2);
        // Preemption happens
        schedulerNode.releaseContainer(containers.get(0).getContainerId(), true);
        schedulerNode.releaseContainer(containers.get(2).getContainerId(), true);
        schedulerNode.releaseContainer(containers.get(1).getContainerId(), true);
        allocateContainers(schedulerNode);
        Assert.assertEquals("Container should be allocated", schedulerNode.getTotalResource(), schedulerNode.getAllocatedResource());
        // Release all containers
        for (int i = 3; i < (containers.size()); ++i) {
            schedulerNode.releaseContainer(containers.get(i).getContainerId(), true);
        }
        finalValidation(schedulerNode);
    }

    /**
     * Allocate and release three containers requested by two apps in two rounds.
     */
    @Test
    public void testMultiplePreemptionEvents() {
        RMNode node = createNode();
        FSSchedulerNode schedulerNode = new FSSchedulerNode(node, false);
        // Launch containers and saturate the cluster
        saturateCluster(schedulerNode);
        Assert.assertEquals("Container should be allocated", Resources.multiply(containers.get(0).getContainer().getResource(), containers.size()), schedulerNode.getAllocatedResource());
        // Preempt a container
        FSAppAttempt starvingApp1 = createStarvingApp(schedulerNode, Resource.newInstance(2048, 2));
        FSAppAttempt starvingApp2 = createStarvingApp(schedulerNode, Resource.newInstance(1024, 1));
        // Preemption thread kicks in
        schedulerNode.addContainersForPreemption(Collections.singletonList(containers.get(0)), starvingApp1);
        schedulerNode.addContainersForPreemption(Collections.singletonList(containers.get(1)), starvingApp1);
        schedulerNode.addContainersForPreemption(Collections.singletonList(containers.get(2)), starvingApp2);
        // Preemption happens
        schedulerNode.releaseContainer(containers.get(1).getContainerId(), true);
        allocateContainers(schedulerNode);
        schedulerNode.releaseContainer(containers.get(2).getContainerId(), true);
        schedulerNode.releaseContainer(containers.get(0).getContainerId(), true);
        allocateContainers(schedulerNode);
        Assert.assertEquals("Container should be allocated", schedulerNode.getTotalResource(), schedulerNode.getAllocatedResource());
        // Release all containers
        for (int i = 3; i < (containers.size()); ++i) {
            schedulerNode.releaseContainer(containers.get(i).getContainerId(), true);
        }
        finalValidation(schedulerNode);
    }

    /**
     * Allocate and release a single container and delete the app in between.
     */
    @Test
    public void testPreemptionToCompletedApp() {
        RMNode node = createNode();
        FSSchedulerNode schedulerNode = new FSSchedulerNode(node, false);
        // Launch containers and saturate the cluster
        saturateCluster(schedulerNode);
        Assert.assertEquals("Container should be allocated", Resources.multiply(containers.get(0).getContainer().getResource(), containers.size()), schedulerNode.getAllocatedResource());
        // Preempt a container
        FSAppAttempt starvingApp = createStarvingApp(schedulerNode, Resource.newInstance(1024, 1));
        schedulerNode.addContainersForPreemption(Collections.singletonList(containers.get(0)), starvingApp);
        schedulerNode.releaseContainer(containers.get(0).getContainerId(), true);
        // Stop the application then try to satisfy the reservation
        // and observe that there are still free resources not allocated to
        // the deleted app
        Mockito.when(starvingApp.isStopped()).thenReturn(true);
        allocateContainers(schedulerNode);
        Assert.assertNotEquals("Container should be allocated", schedulerNode.getTotalResource(), schedulerNode.getAllocatedResource());
        // Release all containers
        for (int i = 1; i < (containers.size()); ++i) {
            schedulerNode.releaseContainer(containers.get(i).getContainerId(), true);
        }
        finalValidation(schedulerNode);
    }

    /**
     * Preempt a bigger container than the preemption request.
     */
    @Test
    public void testPartialReservedPreemption() {
        RMNode node = createNode();
        FSSchedulerNode schedulerNode = new FSSchedulerNode(node, false);
        // Launch containers and saturate the cluster
        saturateCluster(schedulerNode);
        Assert.assertEquals("Container should be allocated", Resources.multiply(containers.get(0).getContainer().getResource(), containers.size()), schedulerNode.getAllocatedResource());
        // Preempt a container
        Resource originalStarvingAppDemand = Resource.newInstance(512, 1);
        FSAppAttempt starvingApp = createStarvingApp(schedulerNode, originalStarvingAppDemand);
        schedulerNode.addContainersForPreemption(Collections.singletonList(containers.get(0)), starvingApp);
        // Preemption occurs
        schedulerNode.releaseContainer(containers.get(0).getContainerId(), true);
        // Container partially reassigned
        allocateContainers(schedulerNode);
        Assert.assertEquals("Container should be allocated", Resources.subtract(schedulerNode.getTotalResource(), Resource.newInstance(512, 0)), schedulerNode.getAllocatedResource());
        // Cleanup simulating node update
        schedulerNode.getPreemptionList();
        // Release all containers
        for (int i = 1; i < (containers.size()); ++i) {
            schedulerNode.releaseContainer(containers.get(i).getContainerId(), true);
        }
        finalValidation(schedulerNode);
    }
}

