/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.deviceframework;


import CGroupsHandler.CGroupController.DEVICES;
import Device.Builder;
import MountDeviceSpec.RW;
import NodeManager.NMContext;
import ResourceMappings.AssignedResources;
import VolumeSpec.CREATE;
import YarnRuntimeType.RUNTIME_DEFAULT;
import YarnRuntimeType.RUNTIME_DOCKER;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager;
import org.apache.hadoop.yarn.server.nodemanager.api.deviceplugin.Device;
import org.apache.hadoop.yarn.server.nodemanager.api.deviceplugin.DevicePlugin;
import org.apache.hadoop.yarn.server.nodemanager.api.deviceplugin.DevicePluginScheduler;
import org.apache.hadoop.yarn.server.nodemanager.api.deviceplugin.DeviceRegisterRequest;
import org.apache.hadoop.yarn.server.nodemanager.api.deviceplugin.DeviceRuntimeSpec;
import org.apache.hadoop.yarn.server.nodemanager.api.deviceplugin.YarnRuntimeType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ResourceMappings;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperation;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationExecutor;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.CGroupsHandler;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.ResourceHandlerException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker.DockerRunCommand;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker.DockerVolumeCommand;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.DockerCommandPlugin;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.ResourcePluginManager;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMMemoryStateStoreService;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService;
import org.apache.hadoop.yarn.server.nodemanager.webapp.dao.NMDeviceResourceInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit tests for DevicePluginAdapter.
 * About interaction with vendor plugin
 */
public class TestDevicePluginAdapter {
    protected static final Logger LOG = LoggerFactory.getLogger(TestDevicePluginAdapter.class);

    private YarnConfiguration conf;

    private String tempResourceTypesFile;

    private CGroupsHandler mockCGroupsHandler;

    private PrivilegedOperationExecutor mockPrivilegedExecutor;

    /**
     * Use the MyPlugin which implement {@code DevicePlugin}.
     * Plugin's initialization is tested in TestResourcePluginManager
     */
    @Test
    public void testBasicWorkflow() throws IOException, YarnException {
        NodeManager.NMContext context = Mockito.mock(NMContext.class);
        NMStateStoreService storeService = Mockito.mock(NMStateStoreService.class);
        Mockito.when(context.getNMStateStore()).thenReturn(storeService);
        Mockito.when(context.getConf()).thenReturn(this.conf);
        Mockito.doNothing().when(storeService).storeAssignedResources(ArgumentMatchers.isA(Container.class), ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(ArrayList.class));
        // Init scheduler manager
        DeviceMappingManager dmm = new DeviceMappingManager(context);
        ResourcePluginManager rpm = Mockito.mock(ResourcePluginManager.class);
        Mockito.when(rpm.getDeviceMappingManager()).thenReturn(dmm);
        // Init an plugin
        TestDevicePluginAdapter.MyPlugin plugin = new TestDevicePluginAdapter.MyPlugin();
        TestDevicePluginAdapter.MyPlugin spyPlugin = Mockito.spy(plugin);
        String resourceName = TestDevicePluginAdapter.MyPlugin.RESOURCE_NAME;
        // Init an adapter for the plugin
        DevicePluginAdapter adapter = new DevicePluginAdapter(resourceName, spyPlugin, dmm);
        // Bootstrap, adding device
        adapter.initialize(context);
        // Use mock shell when create resourceHandler
        ShellWrapper mockShellWrapper = Mockito.mock(ShellWrapper.class);
        Mockito.when(mockShellWrapper.existFile(ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(mockShellWrapper.getDeviceFileType(ArgumentMatchers.anyString())).thenReturn("c");
        DeviceResourceHandlerImpl drhl = new DeviceResourceHandlerImpl(resourceName, adapter, dmm, mockCGroupsHandler, mockPrivilegedExecutor, context, mockShellWrapper);
        adapter.setDeviceResourceHandler(drhl);
        adapter.getDeviceResourceHandler().bootstrap(conf);
        Mockito.verify(mockCGroupsHandler).initializeCGroupController(DEVICES);
        int size = dmm.getAvailableDevices(resourceName);
        Assert.assertEquals(3, size);
        // Case 1. A container c1 requests 1 device
        Container c1 = TestDevicePluginAdapter.mockContainerWithDeviceRequest(1, resourceName, 1, false);
        // preStart
        adapter.getDeviceResourceHandler().preStart(c1);
        // check book keeping
        Assert.assertEquals(2, dmm.getAvailableDevices(resourceName));
        Assert.assertEquals(1, dmm.getAllUsedDevices().get(resourceName).size());
        Assert.assertEquals(3, dmm.getAllAllowedDevices().get(resourceName).size());
        Assert.assertEquals(1, dmm.getAllocatedDevices(resourceName, c1.getContainerId()).size());
        Mockito.verify(mockShellWrapper, Mockito.times(2)).getDeviceFileType(ArgumentMatchers.anyString());
        // check device cgroup create operation
        checkCgroupOperation(c1.getContainerId().toString(), 1, "c-256:1-rwm,c-256:2-rwm", "256:0");
        // postComplete
        adapter.getDeviceResourceHandler().postComplete(TestDevicePluginAdapter.getContainerId(1));
        Assert.assertEquals(3, dmm.getAvailableDevices(resourceName));
        Assert.assertEquals(0, dmm.getAllUsedDevices().get(resourceName).size());
        Assert.assertEquals(3, dmm.getAllAllowedDevices().get(resourceName).size());
        // check cgroup delete operation
        Mockito.verify(mockCGroupsHandler).deleteCGroup(DEVICES, c1.getContainerId().toString());
        // Case 2. A container c2 requests 3 device
        Container c2 = TestDevicePluginAdapter.mockContainerWithDeviceRequest(2, resourceName, 3, false);
        Mockito.reset(mockShellWrapper);
        Mockito.reset(mockCGroupsHandler);
        Mockito.reset(mockPrivilegedExecutor);
        Mockito.when(mockShellWrapper.existFile(ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(mockShellWrapper.getDeviceFileType(ArgumentMatchers.anyString())).thenReturn("c");
        // preStart
        adapter.getDeviceResourceHandler().preStart(c2);
        // check book keeping
        Assert.assertEquals(0, dmm.getAvailableDevices(resourceName));
        Assert.assertEquals(3, dmm.getAllUsedDevices().get(resourceName).size());
        Assert.assertEquals(3, dmm.getAllAllowedDevices().get(resourceName).size());
        Assert.assertEquals(3, dmm.getAllocatedDevices(resourceName, c2.getContainerId()).size());
        Mockito.verify(mockShellWrapper, Mockito.times(0)).getDeviceFileType(ArgumentMatchers.anyString());
        // check device cgroup create operation
        Mockito.verify(mockCGroupsHandler).createCGroup(DEVICES, c2.getContainerId().toString());
        // check device cgroup update operation
        checkCgroupOperation(c2.getContainerId().toString(), 1, null, "256:0,256:1,256:2");
        // postComplete
        adapter.getDeviceResourceHandler().postComplete(TestDevicePluginAdapter.getContainerId(2));
        Assert.assertEquals(3, dmm.getAvailableDevices(resourceName));
        Assert.assertEquals(0, dmm.getAllUsedDevices().get(resourceName).size());
        Assert.assertEquals(3, dmm.getAllAllowedDevices().get(resourceName).size());
        // check cgroup delete operation
        Mockito.verify(mockCGroupsHandler).deleteCGroup(DEVICES, c2.getContainerId().toString());
        // Case 3. A container c3 request 0 device
        Container c3 = TestDevicePluginAdapter.mockContainerWithDeviceRequest(3, resourceName, 0, false);
        Mockito.reset(mockShellWrapper);
        Mockito.reset(mockCGroupsHandler);
        Mockito.reset(mockPrivilegedExecutor);
        Mockito.when(mockShellWrapper.existFile(ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(mockShellWrapper.getDeviceFileType(ArgumentMatchers.anyString())).thenReturn("c");
        // preStart
        adapter.getDeviceResourceHandler().preStart(c3);
        // check book keeping
        Assert.assertEquals(3, dmm.getAvailableDevices(resourceName));
        Assert.assertEquals(0, dmm.getAllUsedDevices().get(resourceName).size());
        Assert.assertEquals(3, dmm.getAllAllowedDevices().get(resourceName).size());
        Mockito.verify(mockShellWrapper, Mockito.times(3)).getDeviceFileType(ArgumentMatchers.anyString());
        // check device cgroup create operation
        Mockito.verify(mockCGroupsHandler).createCGroup(DEVICES, c3.getContainerId().toString());
        // check device cgroup update operation
        checkCgroupOperation(c3.getContainerId().toString(), 1, "c-256:0-rwm,c-256:1-rwm,c-256:2-rwm", null);
        // postComplete
        adapter.getDeviceResourceHandler().postComplete(TestDevicePluginAdapter.getContainerId(3));
        Assert.assertEquals(3, dmm.getAvailableDevices(resourceName));
        Assert.assertEquals(0, dmm.getAllUsedDevices().get(resourceName).size());
        Assert.assertEquals(3, dmm.getAllAllowedDevices().get(resourceName).size());
        Assert.assertEquals(0, dmm.getAllocatedDevices(resourceName, c3.getContainerId()).size());
        // check cgroup delete operation
        Mockito.verify(mockCGroupsHandler).deleteCGroup(DEVICES, c3.getContainerId().toString());
    }

    @Test
    public void testDeviceResourceUpdaterImpl() throws YarnException {
        Resource nodeResource = Mockito.mock(Resource.class);
        NodeManager.NMContext context = Mockito.mock(NMContext.class);
        // Init an plugin
        TestDevicePluginAdapter.MyPlugin plugin = new TestDevicePluginAdapter.MyPlugin();
        TestDevicePluginAdapter.MyPlugin spyPlugin = Mockito.spy(plugin);
        String resourceName = TestDevicePluginAdapter.MyPlugin.RESOURCE_NAME;
        // Init scheduler manager
        DeviceMappingManager dmm = new DeviceMappingManager(context);
        // Init an adapter for the plugin
        DevicePluginAdapter adapter = new DevicePluginAdapter(resourceName, spyPlugin, dmm);
        adapter.initialize(Mockito.mock(Context.class));
        adapter.getNodeResourceHandlerInstance().updateConfiguredResource(nodeResource);
        Mockito.verify(spyPlugin, Mockito.times(1)).getDevices();
        Mockito.verify(nodeResource, Mockito.times(1)).setResourceValue(resourceName, 3);
    }

    @Test
    public void testStoreDeviceSchedulerManagerState() throws IOException, YarnException {
        NodeManager.NMContext context = Mockito.mock(NMContext.class);
        NMStateStoreService realStoreService = new NMMemoryStateStoreService();
        NMStateStoreService storeService = Mockito.spy(realStoreService);
        Mockito.when(context.getNMStateStore()).thenReturn(storeService);
        Mockito.when(context.getConf()).thenReturn(this.conf);
        Mockito.doNothing().when(storeService).storeAssignedResources(ArgumentMatchers.isA(Container.class), ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(ArrayList.class));
        // Init scheduler manager
        DeviceMappingManager dmm = new DeviceMappingManager(context);
        ResourcePluginManager rpm = Mockito.mock(ResourcePluginManager.class);
        Mockito.when(rpm.getDeviceMappingManager()).thenReturn(dmm);
        // Init an plugin
        TestDevicePluginAdapter.MyPlugin plugin = new TestDevicePluginAdapter.MyPlugin();
        TestDevicePluginAdapter.MyPlugin spyPlugin = Mockito.spy(plugin);
        String resourceName = TestDevicePluginAdapter.MyPlugin.RESOURCE_NAME;
        // Init an adapter for the plugin
        DevicePluginAdapter adapter = new DevicePluginAdapter(resourceName, spyPlugin, dmm);
        // Bootstrap, adding device
        adapter.initialize(context);
        adapter.createResourceHandler(context, mockCGroupsHandler, mockPrivilegedExecutor);
        adapter.getDeviceResourceHandler().bootstrap(conf);
        // A container c0 requests 1 device
        Container c0 = TestDevicePluginAdapter.mockContainerWithDeviceRequest(0, resourceName, 1, false);
        // preStart
        adapter.getDeviceResourceHandler().preStart(c0);
        // ensure container1's resource is persistent
        Mockito.verify(storeService).storeAssignedResources(c0, resourceName, Arrays.asList(Builder.newInstance().setId(0).setDevPath("/dev/hdwA0").setMajorNumber(256).setMinorNumber(0).setBusID("0000:80:00.0").setHealthy(true).build()));
    }

    @Test
    public void testRecoverDeviceSchedulerManagerState() throws IOException, YarnException {
        NodeManager.NMContext context = Mockito.mock(NMContext.class);
        NMStateStoreService realStoreService = new NMMemoryStateStoreService();
        NMStateStoreService storeService = Mockito.spy(realStoreService);
        Mockito.when(context.getNMStateStore()).thenReturn(storeService);
        Mockito.doNothing().when(storeService).storeAssignedResources(ArgumentMatchers.isA(Container.class), ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(ArrayList.class));
        // Init scheduler manager
        DeviceMappingManager dmm = new DeviceMappingManager(context);
        ResourcePluginManager rpm = Mockito.mock(ResourcePluginManager.class);
        Mockito.when(rpm.getDeviceMappingManager()).thenReturn(dmm);
        // Init an plugin
        TestDevicePluginAdapter.MyPlugin plugin = new TestDevicePluginAdapter.MyPlugin();
        TestDevicePluginAdapter.MyPlugin spyPlugin = Mockito.spy(plugin);
        String resourceName = TestDevicePluginAdapter.MyPlugin.RESOURCE_NAME;
        // Init an adapter for the plugin
        DevicePluginAdapter adapter = new DevicePluginAdapter(resourceName, spyPlugin, dmm);
        // Bootstrap, adding device
        adapter.initialize(context);
        adapter.createResourceHandler(context, mockCGroupsHandler, mockPrivilegedExecutor);
        adapter.getDeviceResourceHandler().bootstrap(conf);
        Assert.assertEquals(3, dmm.getAllAllowedDevices().get(resourceName).size());
        // mock NMStateStore
        Device storedDevice = Builder.newInstance().setId(0).setDevPath("/dev/hdwA0").setMajorNumber(256).setMinorNumber(0).setBusID("0000:80:00.0").setHealthy(true).build();
        ConcurrentHashMap<ContainerId, Container> runningContainersMap = new ConcurrentHashMap<>();
        Container nmContainer = Mockito.mock(Container.class);
        ResourceMappings rmap = new ResourceMappings();
        ResourceMappings.AssignedResources ar = new ResourceMappings.AssignedResources();
        ar.updateAssignedResources(Arrays.asList(storedDevice));
        rmap.addAssignedResources(resourceName, ar);
        Mockito.when(nmContainer.getResourceMappings()).thenReturn(rmap);
        Mockito.when(context.getContainers()).thenReturn(runningContainersMap);
        // Test case 1. c0 get recovered. scheduler state restored
        runningContainersMap.put(TestDevicePluginAdapter.getContainerId(0), nmContainer);
        adapter.getDeviceResourceHandler().reacquireContainer(TestDevicePluginAdapter.getContainerId(0));
        Assert.assertEquals(3, dmm.getAllAllowedDevices().get(resourceName).size());
        Assert.assertEquals(1, dmm.getAllUsedDevices().get(resourceName).size());
        Assert.assertEquals(2, dmm.getAvailableDevices(resourceName));
        Map<Device, ContainerId> used = dmm.getAllUsedDevices().get(resourceName);
        Assert.assertTrue(used.keySet().contains(storedDevice));
        // Test case 2. c1 wants get recovered.
        // But stored device is already allocated to c2
        nmContainer = Mockito.mock(Container.class);
        rmap = new ResourceMappings();
        ar = new ResourceMappings.AssignedResources();
        ar.updateAssignedResources(Arrays.asList(storedDevice));
        rmap.addAssignedResources(resourceName, ar);
        // already assigned to c1
        runningContainersMap.put(TestDevicePluginAdapter.getContainerId(2), nmContainer);
        boolean caughtException = false;
        try {
            adapter.getDeviceResourceHandler().reacquireContainer(TestDevicePluginAdapter.getContainerId(1));
        } catch (ResourceHandlerException e) {
            caughtException = true;
        }
        Assert.assertTrue("Should fail since requested device is assigned already", caughtException);
        // don't affect c0 allocation state
        Assert.assertEquals(3, dmm.getAllAllowedDevices().get(resourceName).size());
        Assert.assertEquals(1, dmm.getAllUsedDevices().get(resourceName).size());
        Assert.assertEquals(2, dmm.getAvailableDevices(resourceName));
        used = dmm.getAllUsedDevices().get(resourceName);
        Assert.assertTrue(used.keySet().contains(storedDevice));
    }

    @Test
    public void testAssignedDeviceCleanupWhenStoreOpFails() throws IOException, YarnException {
        NodeManager.NMContext context = Mockito.mock(NMContext.class);
        NMStateStoreService realStoreService = new NMMemoryStateStoreService();
        NMStateStoreService storeService = Mockito.spy(realStoreService);
        Mockito.when(context.getConf()).thenReturn(this.conf);
        Mockito.when(context.getNMStateStore()).thenReturn(storeService);
        Mockito.doThrow(new IOException("Exception ...")).when(storeService).storeAssignedResources(ArgumentMatchers.isA(Container.class), ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(ArrayList.class));
        // Init scheduler manager
        DeviceMappingManager dmm = new DeviceMappingManager(context);
        ResourcePluginManager rpm = Mockito.mock(ResourcePluginManager.class);
        Mockito.when(rpm.getDeviceMappingManager()).thenReturn(dmm);
        // Init an plugin
        TestDevicePluginAdapter.MyPlugin plugin = new TestDevicePluginAdapter.MyPlugin();
        TestDevicePluginAdapter.MyPlugin spyPlugin = Mockito.spy(plugin);
        String resourceName = TestDevicePluginAdapter.MyPlugin.RESOURCE_NAME;
        // Init an adapter for the plugin
        DevicePluginAdapter adapter = new DevicePluginAdapter(resourceName, spyPlugin, dmm);
        // Bootstrap, adding device
        adapter.initialize(context);
        adapter.createResourceHandler(context, mockCGroupsHandler, mockPrivilegedExecutor);
        adapter.getDeviceResourceHandler().bootstrap(conf);
        // A container c0 requests 1 device
        Container c0 = TestDevicePluginAdapter.mockContainerWithDeviceRequest(0, resourceName, 1, false);
        // preStart
        boolean exception = false;
        try {
            adapter.getDeviceResourceHandler().preStart(c0);
        } catch (ResourceHandlerException e) {
            exception = true;
        }
        Assert.assertTrue("Should throw exception in preStart", exception);
        // no device assigned
        Assert.assertEquals(3, dmm.getAllAllowedDevices().get(resourceName).size());
        Assert.assertEquals(0, dmm.getAllUsedDevices().get(resourceName).size());
        Assert.assertEquals(3, dmm.getAvailableDevices(resourceName));
    }

    @Test
    public void testPreferPluginScheduler() throws IOException, YarnException {
        NodeManager.NMContext context = Mockito.mock(NMContext.class);
        NMStateStoreService storeService = Mockito.mock(NMStateStoreService.class);
        Mockito.when(context.getNMStateStore()).thenReturn(storeService);
        Mockito.when(context.getConf()).thenReturn(this.conf);
        Mockito.doNothing().when(storeService).storeAssignedResources(ArgumentMatchers.isA(Container.class), ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(ArrayList.class));
        // Init scheduler manager
        DeviceMappingManager dmm = new DeviceMappingManager(context);
        ResourcePluginManager rpm = Mockito.mock(ResourcePluginManager.class);
        Mockito.when(rpm.getDeviceMappingManager()).thenReturn(dmm);
        // Init an plugin
        TestDevicePluginAdapter.MyPlugin plugin = new TestDevicePluginAdapter.MyPlugin();
        TestDevicePluginAdapter.MyPlugin spyPlugin = Mockito.spy(plugin);
        String resourceName = TestDevicePluginAdapter.MyPlugin.RESOURCE_NAME;
        // Add plugin to DeviceMappingManager
        dmm.getDevicePluginSchedulers().put(TestDevicePluginAdapter.MyPlugin.RESOURCE_NAME, spyPlugin);
        // Init an adapter for the plugin
        DevicePluginAdapter adapter = new DevicePluginAdapter(resourceName, spyPlugin, dmm);
        // Bootstrap, adding device
        adapter.initialize(context);
        adapter.createResourceHandler(context, mockCGroupsHandler, mockPrivilegedExecutor);
        adapter.getDeviceResourceHandler().bootstrap(conf);
        int size = dmm.getAvailableDevices(resourceName);
        Assert.assertEquals(3, size);
        // A container c1 requests 1 device
        Container c1 = TestDevicePluginAdapter.mockContainerWithDeviceRequest(0, resourceName, 1, false);
        // preStart
        adapter.getDeviceResourceHandler().preStart(c1);
        // Use customized scheduler
        Mockito.verify(spyPlugin, Mockito.times(1)).allocateDevices(ArgumentMatchers.anySet(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyMap());
        Assert.assertEquals(2, dmm.getAvailableDevices(resourceName));
        Assert.assertEquals(1, dmm.getAllUsedDevices().get(resourceName).size());
        Assert.assertEquals(3, dmm.getAllAllowedDevices().get(resourceName).size());
    }

    /**
     * Ensure correct return value generated.
     */
    @Test
    public void testNMResourceInfoRESTAPI() throws IOException, YarnException {
        NodeManager.NMContext context = Mockito.mock(NMContext.class);
        NMStateStoreService storeService = Mockito.mock(NMStateStoreService.class);
        Mockito.when(context.getNMStateStore()).thenReturn(storeService);
        Mockito.when(context.getConf()).thenReturn(this.conf);
        Mockito.doNothing().when(storeService).storeAssignedResources(ArgumentMatchers.isA(Container.class), ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(ArrayList.class));
        // Init scheduler manager
        DeviceMappingManager dmm = new DeviceMappingManager(context);
        ResourcePluginManager rpm = Mockito.mock(ResourcePluginManager.class);
        Mockito.when(rpm.getDeviceMappingManager()).thenReturn(dmm);
        // Init an plugin
        TestDevicePluginAdapter.MyPlugin plugin = new TestDevicePluginAdapter.MyPlugin();
        TestDevicePluginAdapter.MyPlugin spyPlugin = Mockito.spy(plugin);
        String resourceName = TestDevicePluginAdapter.MyPlugin.RESOURCE_NAME;
        // Init an adapter for the plugin
        DevicePluginAdapter adapter = new DevicePluginAdapter(resourceName, spyPlugin, dmm);
        // Bootstrap, adding device
        adapter.initialize(context);
        adapter.createResourceHandler(context, mockCGroupsHandler, mockPrivilegedExecutor);
        adapter.getDeviceResourceHandler().bootstrap(conf);
        int size = dmm.getAvailableDevices(resourceName);
        Assert.assertEquals(3, size);
        // A container c1 requests 1 device
        Container c1 = TestDevicePluginAdapter.mockContainerWithDeviceRequest(0, resourceName, 1, false);
        // preStart
        adapter.getDeviceResourceHandler().preStart(c1);
        // check book keeping
        Assert.assertEquals(2, dmm.getAvailableDevices(resourceName));
        Assert.assertEquals(1, dmm.getAllUsedDevices().get(resourceName).size());
        Assert.assertEquals(3, dmm.getAllAllowedDevices().get(resourceName).size());
        // get REST return value
        NMDeviceResourceInfo response = ((NMDeviceResourceInfo) (adapter.getNMResourceInfo()));
        Assert.assertEquals(1, response.getAssignedDevices().size());
        Assert.assertEquals(3, response.getTotalDevices().size());
        Device device = response.getAssignedDevices().get(0).getDevice();
        String cId = response.getAssignedDevices().get(0).getContainerId();
        Assert.assertTrue(dmm.getAllAllowedDevices().get(resourceName).contains(device));
        Assert.assertTrue(dmm.getAllUsedDevices().get(resourceName).containsValue(ContainerId.fromString(cId)));
        // finish container
        adapter.getDeviceResourceHandler().postComplete(TestDevicePluginAdapter.getContainerId(0));
        response = ((NMDeviceResourceInfo) (adapter.getNMResourceInfo()));
        Assert.assertEquals(0, response.getAssignedDevices().size());
        Assert.assertEquals(3, response.getTotalDevices().size());
    }

    /**
     * Test a container run command update when using Docker runtime.
     * And the device plugin it uses is like Nvidia Docker v1.
     */
    @Test
    public void testDeviceResourceDockerRuntimePlugin1() throws Exception {
        NodeManager.NMContext context = Mockito.mock(NMContext.class);
        NMStateStoreService storeService = Mockito.mock(NMStateStoreService.class);
        Mockito.when(context.getNMStateStore()).thenReturn(storeService);
        Mockito.when(context.getConf()).thenReturn(this.conf);
        Mockito.doNothing().when(storeService).storeAssignedResources(ArgumentMatchers.isA(Container.class), ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(ArrayList.class));
        // Init scheduler manager
        DeviceMappingManager dmm = new DeviceMappingManager(context);
        DeviceMappingManager spyDmm = Mockito.spy(dmm);
        ResourcePluginManager rpm = Mockito.mock(ResourcePluginManager.class);
        Mockito.when(rpm.getDeviceMappingManager()).thenReturn(spyDmm);
        // Init a plugin
        TestDevicePluginAdapter.MyPlugin plugin = new TestDevicePluginAdapter.MyPlugin();
        TestDevicePluginAdapter.MyPlugin spyPlugin = Mockito.spy(plugin);
        String resourceName = TestDevicePluginAdapter.MyPlugin.RESOURCE_NAME;
        // Init an adapter for the plugin
        DevicePluginAdapter adapter = new DevicePluginAdapter(resourceName, spyPlugin, spyDmm);
        adapter.initialize(context);
        // Bootstrap, adding device
        adapter.initialize(context);
        adapter.createResourceHandler(context, mockCGroupsHandler, mockPrivilegedExecutor);
        adapter.getDeviceResourceHandler().bootstrap(conf);
        // Case 1. A container request Docker runtime and 1 device
        Container c1 = TestDevicePluginAdapter.mockContainerWithDeviceRequest(1, resourceName, 1, true);
        // generate spec based on v1
        spyPlugin.setDevicePluginVersion("v1");
        // preStart will do allocation
        adapter.getDeviceResourceHandler().preStart(c1);
        Set<Device> allocatedDevice = spyDmm.getAllocatedDevices(resourceName, c1.getContainerId());
        Mockito.reset(spyDmm);
        // c1 is requesting docker runtime.
        // it will create parent cgroup but no cgroups update operation needed.
        // check device cgroup create operation
        Mockito.verify(mockCGroupsHandler).createCGroup(DEVICES, c1.getContainerId().toString());
        // ensure no cgroups update operation
        Mockito.verify(mockPrivilegedExecutor, Mockito.times(0)).executePrivilegedOperation(ArgumentMatchers.any(PrivilegedOperation.class), ArgumentMatchers.anyBoolean());
        DockerCommandPlugin dcp = adapter.getDockerCommandPluginInstance();
        // When DockerLinuxContainerRuntime invoke the DockerCommandPluginInstance
        // First to create volume
        DockerVolumeCommand dvc = dcp.getCreateDockerVolumeCommand(c1);
        // ensure that allocation is get once from device mapping manager
        Mockito.verify(spyDmm).getAllocatedDevices(resourceName, c1.getContainerId());
        // ensure that plugin's onDeviceAllocated is invoked
        Mockito.verify(spyPlugin).onDevicesAllocated(allocatedDevice, RUNTIME_DEFAULT);
        Mockito.verify(spyPlugin).onDevicesAllocated(allocatedDevice, RUNTIME_DOCKER);
        Assert.assertEquals("nvidia-docker", dvc.getDriverName());
        Assert.assertEquals("create", dvc.getSubCommand());
        Assert.assertEquals("nvidia_driver_352.68", dvc.getVolumeName());
        // then the DockerLinuxContainerRuntime will update docker run command
        DockerRunCommand drc = new DockerRunCommand(c1.getContainerId().toString(), "user", "image/tensorflow");
        // reset to avoid count times in above invocation
        Mockito.reset(spyPlugin);
        Mockito.reset(spyDmm);
        // Second, update the run command.
        dcp.updateDockerRunCommand(drc, c1);
        // The spec is already generated in getCreateDockerVolumeCommand
        // and there should be a cache hit for DeviceRuntime spec.
        Mockito.verify(spyPlugin, Mockito.times(0)).onDevicesAllocated(allocatedDevice, RUNTIME_DOCKER);
        // ensure that allocation is get from cache instead of device mapping
        // manager
        Mockito.verify(spyDmm, Mockito.times(0)).getAllocatedDevices(resourceName, c1.getContainerId());
        String runStr = drc.toString();
        Assert.assertTrue(runStr.contains("nvidia_driver_352.68:/usr/local/nvidia:ro"));
        Assert.assertTrue(runStr.contains("/dev/hdwA0:/dev/hdwA0"));
        // Third, cleanup in getCleanupDockerVolumesCommand
        dcp.getCleanupDockerVolumesCommand(c1);
        // Ensure device plugin's onDeviceReleased is invoked
        Mockito.verify(spyPlugin).onDevicesReleased(allocatedDevice);
        // If we run the c1 again. No cache will be used for allocation and spec
        dcp.getCreateDockerVolumeCommand(c1);
        Mockito.verify(spyDmm).getAllocatedDevices(resourceName, c1.getContainerId());
        Mockito.verify(spyPlugin).onDevicesAllocated(allocatedDevice, RUNTIME_DOCKER);
    }

    /**
     * Test a container run command update when using Docker runtime.
     * And the device plugin it uses is like Nvidia Docker v2.
     */
    @Test
    public void testDeviceResourceDockerRuntimePlugin2() throws Exception {
        NodeManager.NMContext context = Mockito.mock(NMContext.class);
        NMStateStoreService storeService = Mockito.mock(NMStateStoreService.class);
        Mockito.when(context.getNMStateStore()).thenReturn(storeService);
        Mockito.when(context.getConf()).thenReturn(this.conf);
        Mockito.doNothing().when(storeService).storeAssignedResources(ArgumentMatchers.isA(Container.class), ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(ArrayList.class));
        // Init scheduler manager
        DeviceMappingManager dmm = new DeviceMappingManager(context);
        DeviceMappingManager spyDmm = Mockito.spy(dmm);
        ResourcePluginManager rpm = Mockito.mock(ResourcePluginManager.class);
        Mockito.when(rpm.getDeviceMappingManager()).thenReturn(spyDmm);
        // Init a plugin
        TestDevicePluginAdapter.MyPlugin plugin = new TestDevicePluginAdapter.MyPlugin();
        TestDevicePluginAdapter.MyPlugin spyPlugin = Mockito.spy(plugin);
        String resourceName = TestDevicePluginAdapter.MyPlugin.RESOURCE_NAME;
        // Init an adapter for the plugin
        DevicePluginAdapter adapter = new DevicePluginAdapter(resourceName, spyPlugin, spyDmm);
        adapter.initialize(context);
        // Bootstrap, adding device
        adapter.initialize(context);
        adapter.createResourceHandler(context, mockCGroupsHandler, mockPrivilegedExecutor);
        adapter.getDeviceResourceHandler().bootstrap(conf);
        // Case 1. A container request Docker runtime and 1 device
        Container c1 = TestDevicePluginAdapter.mockContainerWithDeviceRequest(1, resourceName, 2, true);
        // generate spec based on v2
        spyPlugin.setDevicePluginVersion("v2");
        // preStart will do allocation
        adapter.getDeviceResourceHandler().preStart(c1);
        Set<Device> allocatedDevice = spyDmm.getAllocatedDevices(resourceName, c1.getContainerId());
        Mockito.reset(spyDmm);
        // c1 is requesting docker runtime.
        // it will create parent cgroup but no cgroups update operation needed.
        // check device cgroup create operation
        Mockito.verify(mockCGroupsHandler).createCGroup(DEVICES, c1.getContainerId().toString());
        // ensure no cgroups update operation
        Mockito.verify(mockPrivilegedExecutor, Mockito.times(0)).executePrivilegedOperation(ArgumentMatchers.any(PrivilegedOperation.class), ArgumentMatchers.anyBoolean());
        DockerCommandPlugin dcp = adapter.getDockerCommandPluginInstance();
        // When DockerLinuxContainerRuntime invoke the DockerCommandPluginInstance
        // First to create volume
        DockerVolumeCommand dvc = dcp.getCreateDockerVolumeCommand(c1);
        // ensure that allocation is get once from device mapping manager
        Mockito.verify(spyDmm).getAllocatedDevices(resourceName, c1.getContainerId());
        // ensure that plugin's onDeviceAllocated is invoked
        Mockito.verify(spyPlugin).onDevicesAllocated(allocatedDevice, RUNTIME_DEFAULT);
        Mockito.verify(spyPlugin).onDevicesAllocated(allocatedDevice, RUNTIME_DOCKER);
        // No volume creation request
        Assert.assertNull(dvc);
        // then the DockerLinuxContainerRuntime will update docker run command
        DockerRunCommand drc = new DockerRunCommand(c1.getContainerId().toString(), "user", "image/tensorflow");
        // reset to avoid count times in above invocation
        Mockito.reset(spyPlugin);
        Mockito.reset(spyDmm);
        // Second, update the run command.
        dcp.updateDockerRunCommand(drc, c1);
        // The spec is already generated in getCreateDockerVolumeCommand
        // and there should be a cache hit for DeviceRuntime spec.
        Mockito.verify(spyPlugin, Mockito.times(0)).onDevicesAllocated(allocatedDevice, RUNTIME_DOCKER);
        // ensure that allocation is get once from device mapping manager
        Mockito.verify(spyDmm, Mockito.times(0)).getAllocatedDevices(resourceName, c1.getContainerId());
        Assert.assertEquals("0,1", drc.getEnv().get("NVIDIA_VISIBLE_DEVICES"));
        Assert.assertTrue(drc.toString().contains("runtime=nvidia"));
        // Third, cleanup in getCleanupDockerVolumesCommand
        dcp.getCleanupDockerVolumesCommand(c1);
        // Ensure device plugin's onDeviceReleased is invoked
        Mockito.verify(spyPlugin).onDevicesReleased(allocatedDevice);
        // If we run the c1 again. No cache will be used for allocation and spec
        dcp.getCreateDockerVolumeCommand(c1);
        Mockito.verify(spyDmm).getAllocatedDevices(resourceName, c1.getContainerId());
        Mockito.verify(spyPlugin).onDevicesAllocated(allocatedDevice, RUNTIME_DOCKER);
    }

    private class MyPlugin implements DevicePlugin , DevicePluginScheduler {
        private static final String RESOURCE_NAME = "cmpA.com/hdwA";

        // v1 means the vendor uses the similar way of Nvidia Docker v1
        // v2 means the vendor user the similar way of Nvidia Docker v2
        private String devicePluginVersion = "v2";

        public void setDevicePluginVersion(String version) {
            devicePluginVersion = version;
        }

        @Override
        public DeviceRegisterRequest getRegisterRequestInfo() {
            return DeviceRegisterRequest.Builder.newInstance().setResourceName(TestDevicePluginAdapter.MyPlugin.RESOURCE_NAME).setPluginVersion("v1.0").build();
        }

        @Override
        public Set<Device> getDevices() {
            TreeSet<Device> r = new TreeSet<>();
            r.add(Builder.newInstance().setId(0).setDevPath("/dev/hdwA0").setMajorNumber(256).setMinorNumber(0).setBusID("0000:80:00.0").setHealthy(true).build());
            r.add(Builder.newInstance().setId(1).setDevPath("/dev/hdwA1").setMajorNumber(256).setMinorNumber(1).setBusID("0000:80:01.0").setHealthy(true).build());
            r.add(Builder.newInstance().setId(2).setDevPath("/dev/hdwA2").setMajorNumber(256).setMinorNumber(2).setBusID("0000:80:02.0").setHealthy(true).build());
            return r;
        }

        @Override
        public DeviceRuntimeSpec onDevicesAllocated(Set<Device> allocatedDevices, YarnRuntimeType yarnRuntime) throws Exception {
            if (yarnRuntime == (YarnRuntimeType.RUNTIME_DEFAULT)) {
                return null;
            }
            if (yarnRuntime == (YarnRuntimeType.RUNTIME_DOCKER)) {
                return generateSpec(devicePluginVersion, allocatedDevices);
            }
            return null;
        }

        private DeviceRuntimeSpec generateSpec(String version, Set<Device> allocatedDevices) {
            DeviceRuntimeSpec.Builder builder = DeviceRuntimeSpec.Builder.newInstance();
            if (version.equals("v1")) {
                // Nvidia v1 examples like below. These info is get from Nvidia v1
                // RESTful.
                // --device=/dev/nvidiactl --device=/dev/nvidia-uvm
                // --device=/dev/nvidia0
                // --volume-driver=nvidia-docker
                // --volume=nvidia_driver_352.68:/usr/local/nvidia:ro
                String volumeDriverName = "nvidia-docker";
                String volumeToBeCreated = "nvidia_driver_352.68";
                String volumePathInContainer = "/usr/local/nvidia";
                // describe volumes to be created and mounted
                builder.addVolumeSpec(VolumeSpec.Builder.newInstance().setVolumeDriver(volumeDriverName).setVolumeName(volumeToBeCreated).setVolumeOperation(CREATE).build()).addMountVolumeSpec(MountVolumeSpec.Builder.newInstance().setHostPath(volumeToBeCreated).setMountPath(volumePathInContainer).setReadOnly(true).build());
                // describe devices to be mounted
                for (Device device : allocatedDevices) {
                    builder.addMountDeviceSpec(MountDeviceSpec.Builder.newInstance().setDevicePathInHost(device.getDevPath()).setDevicePathInContainer(device.getDevPath()).setDevicePermission(RW).build());
                }
            }
            if (version.equals("v2")) {
                String nvidiaRuntime = "nvidia";
                String nvidiaVisibleDevices = "NVIDIA_VISIBLE_DEVICES";
                StringBuffer gpuMinorNumbersSB = new StringBuffer();
                for (Device device : allocatedDevices) {
                    gpuMinorNumbersSB.append(((device.getMinorNumber()) + ","));
                }
                String minorNumbers = gpuMinorNumbersSB.toString();
                // set runtime and environment variable is enough for
                // plugin like Nvidia Docker v2
                builder.addEnv(nvidiaVisibleDevices, minorNumbers.substring(0, ((minorNumbers.length()) - 1))).setContainerRuntime(nvidiaRuntime);
            }
            return builder.build();
        }

        @Override
        public void onDevicesReleased(Set<Device> releasedDevices) {
            // nothing to do
        }

        @Override
        public Set<Device> allocateDevices(Set<Device> availableDevices, int count, Map<String, String> env) {
            Set<Device> allocated = new TreeSet<>();
            int number = 0;
            for (Device d : availableDevices) {
                allocated.add(d);
                number++;
                if (number == count) {
                    break;
                }
            }
            return allocated;
        }
    }
}

