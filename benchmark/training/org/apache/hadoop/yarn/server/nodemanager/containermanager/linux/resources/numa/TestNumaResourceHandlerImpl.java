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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.numa;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ResourceMappings;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ResourceMappings.AssignedResources;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperation;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.ResourceHandlerException;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test class for NumaResourceHandlerImpl.
 */
public class TestNumaResourceHandlerImpl {
    private YarnConfiguration conf;

    private NumaResourceHandlerImpl numaResourceHandler;

    private Container mockContainer;

    @Test
    public void testAllocateNumaMemoryResource() throws ResourceHandlerException {
        // allocates node 0 for memory and cpu
        testAllocateNumaResource("container_1481156246874_0001_01_000001", Resource.newInstance(2048, 2), "0", "0");
        // allocates node 1 for memory and cpu since allocator uses round
        // robin assignment
        testAllocateNumaResource("container_1481156246874_0001_01_000002", Resource.newInstance(60000, 2), "1", "1");
        // allocates node 0,1 for memory since there is no sufficient memory in any
        // one node
        testAllocateNumaResource("container_1481156246874_0001_01_000003", Resource.newInstance(80000, 2), "0,1", "0");
        // returns null since there are no sufficient resources available for the
        // request
        Mockito.when(mockContainer.getContainerId()).thenReturn(ContainerId.fromString("container_1481156246874_0001_01_000004"));
        Mockito.when(mockContainer.getResource()).thenReturn(Resource.newInstance(80000, 2));
        Assert.assertNull(numaResourceHandler.preStart(mockContainer));
        // allocates node 1 for memory and cpu
        testAllocateNumaResource("container_1481156246874_0001_01_000005", Resource.newInstance(1024, 2), "1", "1");
    }

    @Test
    public void testAllocateNumaCpusResource() throws ResourceHandlerException {
        // allocates node 0 for memory and cpu
        testAllocateNumaResource("container_1481156246874_0001_01_000001", Resource.newInstance(2048, 2), "0", "0");
        // allocates node 1 for memory and cpu since allocator uses round
        // robin assignment
        testAllocateNumaResource("container_1481156246874_0001_01_000002", Resource.newInstance(2048, 2), "1", "1");
        // allocates node 0,1 for cpus since there is are no sufficient cpus
        // available in any one node
        testAllocateNumaResource("container_1481156246874_0001_01_000003", Resource.newInstance(2048, 3), "0", "0,1");
        // returns null since there are no sufficient resources available for the
        // request
        Mockito.when(mockContainer.getContainerId()).thenReturn(ContainerId.fromString("container_1481156246874_0001_01_000004"));
        Mockito.when(mockContainer.getResource()).thenReturn(Resource.newInstance(2048, 2));
        Assert.assertNull(numaResourceHandler.preStart(mockContainer));
        // allocates node 1 for memory and cpu
        testAllocateNumaResource("container_1481156246874_0001_01_000005", Resource.newInstance(2048, 1), "1", "1");
    }

    @Test
    public void testReacquireContainer() throws Exception {
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<ContainerId, Container> mockContainers = Mockito.mock(ConcurrentHashMap.class);
        Context mockContext = Mockito.mock(Context.class);
        NMStateStoreService mock = Mockito.mock(NMStateStoreService.class);
        Mockito.when(mockContext.getNMStateStore()).thenReturn(mock);
        ResourceMappings resourceMappings = new ResourceMappings();
        AssignedResources assignedRscs = new AssignedResources();
        NumaResourceAllocation numaResourceAllocation = new NumaResourceAllocation("0", 70000, "0", 4);
        assignedRscs.updateAssignedResources(Arrays.asList(numaResourceAllocation));
        resourceMappings.addAssignedResources("numa", assignedRscs);
        Mockito.when(mockContainer.getResourceMappings()).thenReturn(resourceMappings);
        Mockito.when(mockContainers.get(ArgumentMatchers.any())).thenReturn(mockContainer);
        Mockito.when(mockContext.getContainers()).thenReturn(mockContainers);
        numaResourceHandler = new NumaResourceHandlerImpl(conf, mockContext);
        numaResourceHandler.bootstrap(conf);
        // recovered numa resources should be added to the used resources and
        // remaining will be available for further allocation.
        numaResourceHandler.reacquireContainer(ContainerId.fromString("container_1481156246874_0001_01_000001"));
        testAllocateNumaResource("container_1481156246874_0001_01_000005", Resource.newInstance(2048, 1), "1", "1");
        Mockito.when(mockContainer.getContainerId()).thenReturn(ContainerId.fromString("container_1481156246874_0001_01_000005"));
        Mockito.when(mockContainer.getResource()).thenReturn(Resource.newInstance(2048, 4));
        List<PrivilegedOperation> preStart = numaResourceHandler.preStart(mockContainer);
        Assert.assertNull(preStart);
    }
}

