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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.gpu;


import CGroupsHandler.CGroupController.DEVICES;
import ResourceInformation.GPU_URI;
import ResourceMappings.AssignedResources;
import YarnConfiguration.NM_GPU_ALLOWED_DEVICES;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ResourceMappings;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationExecutor;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.CGroupsHandler;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.ResourceHandlerException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu.GpuDevice;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu.GpuDiscoverer;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMNullStateStoreService;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestGpuResourceHandler {
    private CGroupsHandler mockCGroupsHandler;

    private PrivilegedOperationExecutor mockPrivilegedExecutor;

    private GpuResourceHandlerImpl gpuResourceHandler;

    private NMStateStoreService mockNMStateStore;

    private ConcurrentHashMap<ContainerId, Container> runningContainersMap;

    private GpuDiscoverer gpuDiscoverer;

    private File testDataDirectory;

    @Test
    public void testBootStrap() throws Exception {
        Configuration conf = createDefaultConfig();
        conf.set(NM_GPU_ALLOWED_DEVICES, "0:0");
        gpuDiscoverer.initialize(conf);
        gpuResourceHandler.bootstrap(conf);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).initializeCGroupController(DEVICES);
    }

    @Test
    public void testAllocationWhenDockerContainerEnabled() throws Exception {
        // When docker container is enabled, no devices should be written to
        // devices.deny.
        commonTestAllocation(true);
    }

    @Test
    public void testAllocation() throws Exception {
        commonTestAllocation(false);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAssignedGpuWillBeCleanedupWhenStoreOpFails() throws Exception {
        Configuration conf = createDefaultConfig();
        conf.set(NM_GPU_ALLOWED_DEVICES, "0:0,1:1,2:3,3:4");
        gpuDiscoverer.initialize(conf);
        gpuResourceHandler.bootstrap(conf);
        Assert.assertEquals(4, gpuResourceHandler.getGpuAllocator().getAvailableGpus());
        Mockito.doThrow(new IOException("Exception ...")).when(mockNMStateStore).storeAssignedResources(ArgumentMatchers.any(Container.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyList());
        boolean exception = false;
        /* Start container 1, asks 3 containers */
        try {
            gpuResourceHandler.preStart(TestGpuResourceHandler.mockContainerWithGpuRequest(1, 3));
        } catch (ResourceHandlerException e) {
            exception = true;
        }
        Assert.assertTrue("preStart should throw exception", exception);
        // After preStart, we still have 4 available GPU since the store op fails.
        Assert.assertEquals(4, gpuResourceHandler.getGpuAllocator().getAvailableGpus());
    }

    @Test
    public void testAllocationWithoutAllowedGpus() throws Exception {
        Configuration conf = createDefaultConfig();
        conf.set(NM_GPU_ALLOWED_DEVICES, " ");
        gpuDiscoverer.initialize(conf);
        try {
            gpuResourceHandler.bootstrap(conf);
            Assert.fail("Should fail because no GPU available");
        } catch (ResourceHandlerException e) {
            // Expected because of no resource available
        }
        /* Start container 1, asks 0 containers */
        gpuResourceHandler.preStart(TestGpuResourceHandler.mockContainerWithGpuRequest(1, 0));
        verifyDeniedDevices(TestGpuResourceHandler.getContainerId(1), Collections.emptyList());
        /* Start container 2, asks 1 containers. Excepted to fail */
        boolean failedToAllocate = false;
        try {
            gpuResourceHandler.preStart(TestGpuResourceHandler.mockContainerWithGpuRequest(2, 1));
        } catch (ResourceHandlerException e) {
            failedToAllocate = true;
        }
        Assert.assertTrue(failedToAllocate);
        /* Release container 1, expect cgroups deleted */
        gpuResourceHandler.postComplete(TestGpuResourceHandler.getContainerId(1));
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).createCGroup(DEVICES, TestGpuResourceHandler.getContainerId(1).toString());
        Assert.assertEquals(0, gpuResourceHandler.getGpuAllocator().getAvailableGpus());
    }

    @Test
    public void testAllocationStored() throws Exception {
        Configuration conf = createDefaultConfig();
        conf.set(NM_GPU_ALLOWED_DEVICES, "0:0,1:1,2:3,3:4");
        gpuDiscoverer.initialize(conf);
        gpuResourceHandler.bootstrap(conf);
        Assert.assertEquals(4, gpuResourceHandler.getGpuAllocator().getAvailableGpus());
        /* Start container 1, asks 3 containers */
        Container container = TestGpuResourceHandler.mockContainerWithGpuRequest(1, 3);
        gpuResourceHandler.preStart(container);
        Mockito.verify(mockNMStateStore).storeAssignedResources(container, GPU_URI, Arrays.asList(new GpuDevice(0, 0), new GpuDevice(1, 1), new GpuDevice(2, 3)));
        // Only device=4 will be blocked.
        verifyDeniedDevices(TestGpuResourceHandler.getContainerId(1), Arrays.asList(new GpuDevice(3, 4)));
        /* Start container 2, ask 0 container, succeeded */
        container = TestGpuResourceHandler.mockContainerWithGpuRequest(2, 0);
        gpuResourceHandler.preStart(container);
        verifyDeniedDevices(TestGpuResourceHandler.getContainerId(2), Arrays.asList(new GpuDevice(0, 0), new GpuDevice(1, 1), new GpuDevice(2, 3), new GpuDevice(3, 4)));
        Assert.assertEquals(0, container.getResourceMappings().getAssignedResources(GPU_URI).size());
        // Store assigned resource will not be invoked.
        Mockito.verify(mockNMStateStore, Mockito.never()).storeAssignedResources(ArgumentMatchers.eq(container), ArgumentMatchers.eq(GPU_URI), ArgumentMatchers.anyList());
    }

    @Test
    public void testAllocationStoredWithNULLStateStore() throws Exception {
        NMNullStateStoreService mockNMNULLStateStore = Mockito.mock(NMNullStateStoreService.class);
        Configuration conf = createDefaultConfig();
        conf.set(NM_GPU_ALLOWED_DEVICES, "0:0,1:1,2:3,3:4");
        Context nmnctx = Mockito.mock(Context.class);
        Mockito.when(nmnctx.getNMStateStore()).thenReturn(mockNMNULLStateStore);
        Mockito.when(nmnctx.getConf()).thenReturn(conf);
        GpuResourceHandlerImpl gpuNULLStateResourceHandler = new GpuResourceHandlerImpl(nmnctx, mockCGroupsHandler, mockPrivilegedExecutor, gpuDiscoverer);
        gpuDiscoverer.initialize(conf);
        gpuNULLStateResourceHandler.bootstrap(conf);
        Assert.assertEquals(4, gpuNULLStateResourceHandler.getGpuAllocator().getAvailableGpus());
        /* Start container 1, asks 3 containers */
        Container container = TestGpuResourceHandler.mockContainerWithGpuRequest(1, 3);
        gpuNULLStateResourceHandler.preStart(container);
        Mockito.verify(nmnctx.getNMStateStore()).storeAssignedResources(container, GPU_URI, Arrays.asList(new GpuDevice(0, 0), new GpuDevice(1, 1), new GpuDevice(2, 3)));
    }

    @Test
    public void testRecoverResourceAllocation() throws Exception {
        Configuration conf = createDefaultConfig();
        conf.set(NM_GPU_ALLOWED_DEVICES, "0:0,1:1,2:3,3:4");
        gpuDiscoverer.initialize(conf);
        gpuResourceHandler.bootstrap(conf);
        Assert.assertEquals(4, gpuResourceHandler.getGpuAllocator().getAvailableGpus());
        Container nmContainer = Mockito.mock(Container.class);
        ResourceMappings rmap = new ResourceMappings();
        ResourceMappings.AssignedResources ar = new ResourceMappings.AssignedResources();
        ar.updateAssignedResources(Arrays.asList(new GpuDevice(1, 1), new GpuDevice(2, 3)));
        rmap.addAssignedResources(GPU_URI, ar);
        Mockito.when(nmContainer.getResourceMappings()).thenReturn(rmap);
        runningContainersMap.put(TestGpuResourceHandler.getContainerId(1), nmContainer);
        // TEST CASE
        // Reacquire container restore state of GPU Resource Allocator.
        gpuResourceHandler.reacquireContainer(TestGpuResourceHandler.getContainerId(1));
        Map<GpuDevice, ContainerId> deviceAllocationMapping = gpuResourceHandler.getGpuAllocator().getDeviceAllocationMappingCopy();
        Assert.assertEquals(2, deviceAllocationMapping.size());
        Assert.assertTrue(deviceAllocationMapping.keySet().contains(new GpuDevice(1, 1)));
        Assert.assertTrue(deviceAllocationMapping.keySet().contains(new GpuDevice(2, 3)));
        Assert.assertEquals(deviceAllocationMapping.get(new GpuDevice(1, 1)), TestGpuResourceHandler.getContainerId(1));
        // TEST CASE
        // Try to reacquire a container but requested device is not in allowed list.
        nmContainer = Mockito.mock(Container.class);
        rmap = new ResourceMappings();
        ar = new ResourceMappings.AssignedResources();
        // id=5 is not in allowed list.
        ar.updateAssignedResources(Arrays.asList(new GpuDevice(3, 4), new GpuDevice(4, 5)));
        rmap.addAssignedResources(GPU_URI, ar);
        Mockito.when(nmContainer.getResourceMappings()).thenReturn(rmap);
        runningContainersMap.put(TestGpuResourceHandler.getContainerId(2), nmContainer);
        boolean caughtException = false;
        try {
            gpuResourceHandler.reacquireContainer(TestGpuResourceHandler.getContainerId(1));
        } catch (ResourceHandlerException e) {
            caughtException = true;
        }
        Assert.assertTrue("Should fail since requested device Id is not in allowed list", caughtException);
        // Make sure internal state not changed.
        deviceAllocationMapping = gpuResourceHandler.getGpuAllocator().getDeviceAllocationMappingCopy();
        Assert.assertEquals(2, deviceAllocationMapping.size());
        Assert.assertTrue(deviceAllocationMapping.keySet().containsAll(Arrays.asList(new GpuDevice(1, 1), new GpuDevice(2, 3))));
        Assert.assertEquals(deviceAllocationMapping.get(new GpuDevice(1, 1)), TestGpuResourceHandler.getContainerId(1));
        // TEST CASE
        // Try to reacquire a container but requested device is already assigned.
        nmContainer = Mockito.mock(Container.class);
        rmap = new ResourceMappings();
        ar = new ResourceMappings.AssignedResources();
        // id=3 is already assigned
        ar.updateAssignedResources(Arrays.asList(new GpuDevice(3, 4), new GpuDevice(2, 3)));
        rmap.addAssignedResources("gpu", ar);
        Mockito.when(nmContainer.getResourceMappings()).thenReturn(rmap);
        runningContainersMap.put(TestGpuResourceHandler.getContainerId(2), nmContainer);
        caughtException = false;
        try {
            gpuResourceHandler.reacquireContainer(TestGpuResourceHandler.getContainerId(1));
        } catch (ResourceHandlerException e) {
            caughtException = true;
        }
        Assert.assertTrue("Should fail since requested device Id is already assigned", caughtException);
        // Make sure internal state not changed.
        deviceAllocationMapping = gpuResourceHandler.getGpuAllocator().getDeviceAllocationMappingCopy();
        Assert.assertEquals(2, deviceAllocationMapping.size());
        Assert.assertTrue(deviceAllocationMapping.keySet().containsAll(Arrays.asList(new GpuDevice(1, 1), new GpuDevice(2, 3))));
        Assert.assertEquals(deviceAllocationMapping.get(new GpuDevice(1, 1)), TestGpuResourceHandler.getContainerId(1));
    }
}

