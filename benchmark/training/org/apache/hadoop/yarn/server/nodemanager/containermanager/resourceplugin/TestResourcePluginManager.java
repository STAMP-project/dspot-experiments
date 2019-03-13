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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin;


import YarnConfiguration.NM_PLUGGABLE_DEVICE_FRAMEWORK_DEVICE_CLASSES;
import YarnConfiguration.NM_PLUGGABLE_DEVICE_FRAMEWORK_ENABLED;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.server.nodemanager.ContainerExecutor;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.DeletionService;
import org.apache.hadoop.yarn.server.nodemanager.LinuxContainerExecutor;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeHealthCheckerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager;
import org.apache.hadoop.yarn.server.nodemanager.NodeManagerTestBase;
import org.apache.hadoop.yarn.server.nodemanager.NodeStatusUpdater;
import org.apache.hadoop.yarn.server.nodemanager.api.deviceplugin.DevicePlugin;
import org.apache.hadoop.yarn.server.nodemanager.api.deviceplugin.DevicePluginScheduler;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.ContainerManagerImpl;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperation;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationExecutor;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.ResourceHandler;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.ResourceHandlerChain;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.ResourceHandlerException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.deviceframework.FakeTestDevicePlugin1;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.deviceframework.FakeTestDevicePlugin2;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.deviceframework.FakeTestDevicePlugin3;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.deviceframework.FakeTestDevicePlugin4;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.deviceframework.FakeTestDevicePlugin5;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestResourcePluginManager extends NodeManagerTestBase {
    private NodeManager nm;

    private YarnConfiguration conf;

    private String tempResourceTypesFile;

    private class CustomizedResourceHandler implements ResourceHandler {
        @Override
        public List<PrivilegedOperation> bootstrap(Configuration configuration) throws ResourceHandlerException {
            return null;
        }

        @Override
        public List<PrivilegedOperation> preStart(Container container) throws ResourceHandlerException {
            return null;
        }

        @Override
        public List<PrivilegedOperation> reacquireContainer(ContainerId containerId) throws ResourceHandlerException {
            return null;
        }

        @Override
        public List<PrivilegedOperation> updateContainer(Container container) throws ResourceHandlerException {
            return null;
        }

        @Override
        public List<PrivilegedOperation> postComplete(ContainerId containerId) throws ResourceHandlerException {
            return null;
        }

        @Override
        public List<PrivilegedOperation> teardown() throws ResourceHandlerException {
            return null;
        }
    }

    private class MyMockNM extends NodeManager {
        private final ResourcePluginManager rpm;

        public MyMockNM(ResourcePluginManager rpm) {
            this.rpm = rpm;
        }

        @Override
        protected NodeStatusUpdater createNodeStatusUpdater(Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker) {
            ((NodeManager.NMContext) (context)).setResourcePluginManager(rpm);
            return new NodeManagerTestBase.BaseNodeStatusUpdaterForTest(context, dispatcher, healthChecker, metrics, new NodeManagerTestBase.BaseResourceTrackerForTest());
        }

        @Override
        protected ContainerManagerImpl createContainerManager(Context context, ContainerExecutor exec, DeletionService del, NodeStatusUpdater nodeStatusUpdater, ApplicationACLsManager aclsManager, LocalDirsHandlerService diskhandler) {
            return new NodeManagerTestBase.MyContainerManager(context, exec, del, nodeStatusUpdater, metrics, diskhandler);
        }

        @Override
        protected ResourcePluginManager createResourcePluginManager() {
            return rpm;
        }
    }

    public class MyLCE extends LinuxContainerExecutor {
        private PrivilegedOperationExecutor poe = Mockito.mock(PrivilegedOperationExecutor.class);

        @Override
        protected PrivilegedOperationExecutor getPrivilegedOperationExecutor() {
            return poe;
        }
    }

    /* Make sure ResourcePluginManager is initialized during NM start up. */
    @Test(timeout = 30000)
    public void testResourcePluginManagerInitialization() throws Exception {
        final ResourcePluginManager rpm = stubResourcePluginmanager();
        nm = new TestResourcePluginManager.MyMockNM(rpm);
        nm.init(conf);
        Mockito.verify(rpm, Mockito.times(1)).initialize(ArgumentMatchers.any(Context.class));
    }

    /* Make sure ResourcePluginManager is invoked during NM update. */
    @Test(timeout = 30000)
    public void testNodeStatusUpdaterWithResourcePluginsEnabled() throws Exception {
        final ResourcePluginManager rpm = stubResourcePluginmanager();
        nm = new TestResourcePluginManager.MyMockNM(rpm);
        nm.init(conf);
        nm.start();
        NodeResourceUpdaterPlugin nodeResourceUpdaterPlugin = rpm.getNameToPlugins().get("resource1").getNodeResourceHandlerInstance();
        Mockito.verify(nodeResourceUpdaterPlugin, Mockito.times(1)).updateConfiguredResource(ArgumentMatchers.any(Resource.class));
    }

    /* Make sure ResourcePluginManager is used to initialize ResourceHandlerChain */
    @Test(timeout = 30000)
    public void testLinuxContainerExecutorWithResourcePluginsEnabled() throws Exception {
        final ResourcePluginManager rpm = stubResourcePluginmanager();
        final LinuxContainerExecutor lce = new TestResourcePluginManager.MyLCE();
        nm = new NodeManager() {
            @Override
            protected NodeStatusUpdater createNodeStatusUpdater(Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker) {
                ((NMContext) (context)).setResourcePluginManager(rpm);
                return new NodeManagerTestBase.BaseNodeStatusUpdaterForTest(context, dispatcher, healthChecker, metrics, new NodeManagerTestBase.BaseResourceTrackerForTest());
            }

            @Override
            protected ContainerManagerImpl createContainerManager(Context context, ContainerExecutor exec, DeletionService del, NodeStatusUpdater nodeStatusUpdater, ApplicationACLsManager aclsManager, LocalDirsHandlerService diskhandler) {
                return new NodeManagerTestBase.MyContainerManager(context, exec, del, nodeStatusUpdater, metrics, diskhandler);
            }

            @Override
            protected ContainerExecutor createContainerExecutor(Configuration configuration) {
                ((NMContext) (getNMContext())).setResourcePluginManager(rpm);
                lce.setConf(configuration);
                return lce;
            }
        };
        nm.init(conf);
        nm.start();
        ResourceHandler handler = lce.getResourceHandler();
        Assert.assertNotNull(handler);
        Assert.assertTrue((handler instanceof ResourceHandlerChain));
        boolean newHandlerAdded = false;
        for (ResourceHandler h : getResourceHandlerList()) {
            if (h instanceof DevicePluginAdapter) {
                Assert.assertTrue(false);
            }
            if (h instanceof TestResourcePluginManager.CustomizedResourceHandler) {
                newHandlerAdded = true;
                break;
            }
        }
        Assert.assertTrue("New ResourceHandler should be added", newHandlerAdded);
    }

    // Disabled pluggable framework in configuration.
    // We use spy object of real rpm to verify "initializePluggableDevicePlugins"
    // because use mock rpm will not working
    @Test(timeout = 30000)
    public void testInitializationWithPluggableDeviceFrameworkDisabled() throws Exception {
        ResourcePluginManager rpm = new ResourcePluginManager();
        ResourcePluginManager rpmSpy = Mockito.spy(rpm);
        nm = new TestResourcePluginManager.MyMockNM(rpmSpy);
        conf.setBoolean(NM_PLUGGABLE_DEVICE_FRAMEWORK_ENABLED, false);
        nm.init(conf);
        nm.start();
        Mockito.verify(rpmSpy, Mockito.times(1)).initialize(ArgumentMatchers.any(Context.class));
        Mockito.verify(rpmSpy, Mockito.times(0)).initializePluggableDevicePlugins(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(Map.class));
    }

    // No related configuration set.
    @Test(timeout = 30000)
    public void testInitializationWithPluggableDeviceFrameworkDisabled2() throws Exception {
        ResourcePluginManager rpm = new ResourcePluginManager();
        ResourcePluginManager rpmSpy = Mockito.spy(rpm);
        nm = new TestResourcePluginManager.MyMockNM(rpmSpy);
        nm.init(conf);
        nm.start();
        Mockito.verify(rpmSpy, Mockito.times(1)).initialize(ArgumentMatchers.any(Context.class));
        Mockito.verify(rpmSpy, Mockito.times(0)).initializePluggableDevicePlugins(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(Map.class));
    }

    // Enable framework and configure pluggable device classes
    @Test(timeout = 30000)
    public void testInitializationWithPluggableDeviceFrameworkEnabled() throws Exception {
        ResourcePluginManager rpm = new ResourcePluginManager();
        ResourcePluginManager rpmSpy = Mockito.spy(rpm);
        nm = new TestResourcePluginManager.MyMockNM(rpmSpy);
        conf.setBoolean(NM_PLUGGABLE_DEVICE_FRAMEWORK_ENABLED, true);
        conf.setStrings(NM_PLUGGABLE_DEVICE_FRAMEWORK_DEVICE_CLASSES, FakeTestDevicePlugin1.class.getCanonicalName());
        nm.init(conf);
        nm.start();
        Mockito.verify(rpmSpy, Mockito.times(1)).initialize(ArgumentMatchers.any(Context.class));
        Mockito.verify(rpmSpy, Mockito.times(1)).initializePluggableDevicePlugins(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(Map.class));
    }

    // Enable pluggable framework, but leave device classes un-configured
    // initializePluggableDevicePlugins invoked but it should throw an exception
    @Test(timeout = 30000)
    public void testInitializationWithPluggableDeviceFrameworkEnabled2() throws ClassNotFoundException {
        ResourcePluginManager rpm = new ResourcePluginManager();
        ResourcePluginManager rpmSpy = Mockito.spy(rpm);
        nm = new TestResourcePluginManager.MyMockNM(rpmSpy);
        Boolean fail = false;
        try {
            conf.setBoolean(NM_PLUGGABLE_DEVICE_FRAMEWORK_ENABLED, true);
            nm.init(conf);
            nm.start();
        } catch (YarnRuntimeException e) {
            fail = true;
        } catch (Exception e) {
        }
        Mockito.verify(rpmSpy, Mockito.times(1)).initializePluggableDevicePlugins(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(Map.class));
        Assert.assertTrue(fail);
    }

    @Test(timeout = 30000)
    public void testNormalInitializationOfPluggableDeviceClasses() throws Exception {
        ResourcePluginManager rpm = new ResourcePluginManager();
        ResourcePluginManager rpmSpy = Mockito.spy(rpm);
        nm = new TestResourcePluginManager.MyMockNM(rpmSpy);
        conf.setBoolean(NM_PLUGGABLE_DEVICE_FRAMEWORK_ENABLED, true);
        conf.setStrings(NM_PLUGGABLE_DEVICE_FRAMEWORK_DEVICE_CLASSES, FakeTestDevicePlugin1.class.getCanonicalName());
        nm.init(conf);
        nm.start();
        Map<String, ResourcePlugin> pluginMap = rpmSpy.getNameToPlugins();
        Assert.assertEquals(1, pluginMap.size());
        ResourcePlugin rp = pluginMap.get("cmpA.com/hdwA");
        if (!(rp instanceof DevicePluginAdapter)) {
            Assert.assertTrue(false);
        }
        Mockito.verify(rpmSpy, Mockito.times(1)).checkInterfaceCompatibility(DevicePlugin.class, FakeTestDevicePlugin1.class);
    }

    // Fail to load a class which doesn't implement interface DevicePlugin
    @Test(timeout = 30000)
    public void testLoadInvalidPluggableDeviceClasses() throws Exception {
        ResourcePluginManager rpm = new ResourcePluginManager();
        ResourcePluginManager rpmSpy = Mockito.spy(rpm);
        nm = new TestResourcePluginManager.MyMockNM(rpmSpy);
        conf.setBoolean(NM_PLUGGABLE_DEVICE_FRAMEWORK_ENABLED, true);
        conf.setStrings(NM_PLUGGABLE_DEVICE_FRAMEWORK_DEVICE_CLASSES, FakeTestDevicePlugin2.class.getCanonicalName());
        String expectedMessage = (("Class: " + (FakeTestDevicePlugin2.class.getCanonicalName())) + " not instance of ") + (DevicePlugin.class.getCanonicalName());
        String actualMessage = "";
        try {
            nm.init(conf);
            nm.start();
        } catch (YarnRuntimeException e) {
            actualMessage = e.getMessage();
        }
        Assert.assertEquals(expectedMessage, actualMessage);
    }

    // Fail to register duplicated resource name.
    @Test(timeout = 30000)
    public void testLoadDuplicateResourceNameDevicePlugin() throws Exception {
        ResourcePluginManager rpm = new ResourcePluginManager();
        ResourcePluginManager rpmSpy = Mockito.spy(rpm);
        nm = new TestResourcePluginManager.MyMockNM(rpmSpy);
        conf.setBoolean(NM_PLUGGABLE_DEVICE_FRAMEWORK_ENABLED, true);
        conf.setStrings(NM_PLUGGABLE_DEVICE_FRAMEWORK_DEVICE_CLASSES, (((FakeTestDevicePlugin1.class.getCanonicalName()) + ",") + (FakeTestDevicePlugin3.class.getCanonicalName())));
        String expectedMessage = ("cmpA.com/hdwA" + ((" already registered! Please change resource type name" + " or configure correct resource type name") + " in resource-types.xml for ")) + (FakeTestDevicePlugin3.class.getCanonicalName());
        String actualMessage = "";
        try {
            nm.init(conf);
            nm.start();
        } catch (YarnRuntimeException e) {
            actualMessage = e.getMessage();
        }
        Assert.assertEquals(expectedMessage, actualMessage);
    }

    /**
     * Fail a plugin due to incompatible interface implemented.
     * It doesn't implement the "getRegisterRequestInfo"
     */
    @Test(timeout = 30000)
    public void testIncompatibleDevicePlugin() throws Exception {
        ResourcePluginManager rpm = new ResourcePluginManager();
        ResourcePluginManager rpmSpy = Mockito.spy(rpm);
        nm = new TestResourcePluginManager.MyMockNM(rpmSpy);
        conf.setBoolean(NM_PLUGGABLE_DEVICE_FRAMEWORK_ENABLED, true);
        conf.setStrings(NM_PLUGGABLE_DEVICE_FRAMEWORK_DEVICE_CLASSES, FakeTestDevicePlugin4.class.getCanonicalName());
        String expectedMessage = ("Method getRegisterRequestInfo" + " is expected but not implemented in ") + (FakeTestDevicePlugin4.class.getCanonicalName());
        String actualMessage = "";
        try {
            nm.init(conf);
            nm.start();
        } catch (YarnRuntimeException e) {
            actualMessage = e.getMessage();
        }
        Assert.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testLoadPluginWithCustomizedScheduler() {
        ResourcePluginManager rpm = new ResourcePluginManager();
        DeviceMappingManager dmm = new DeviceMappingManager(Mockito.mock(Context.class));
        DeviceMappingManager dmmSpy = Mockito.spy(dmm);
        ResourcePluginManager rpmSpy = Mockito.spy(rpm);
        rpmSpy.setDeviceMappingManager(dmmSpy);
        nm = new TestResourcePluginManager.MyMockNM(rpmSpy);
        conf.setBoolean(NM_PLUGGABLE_DEVICE_FRAMEWORK_ENABLED, true);
        conf.setStrings(NM_PLUGGABLE_DEVICE_FRAMEWORK_DEVICE_CLASSES, (((FakeTestDevicePlugin1.class.getCanonicalName()) + ",") + (FakeTestDevicePlugin5.class.getCanonicalName())));
        nm.init(conf);
        nm.start();
        // only 1 plugin has the customized scheduler
        Mockito.verify(rpmSpy, Mockito.times(1)).checkInterfaceCompatibility(DevicePlugin.class, FakeTestDevicePlugin1.class);
        Mockito.verify(dmmSpy, Mockito.times(1)).addDevicePluginScheduler(ArgumentMatchers.any(String.class), ArgumentMatchers.any(DevicePluginScheduler.class));
        Assert.assertEquals(1, dmm.getDevicePluginSchedulers().size());
    }

    @Test(timeout = 30000)
    public void testRequestedResourceNameIsConfigured() throws Exception {
        ResourcePluginManager rpm = new ResourcePluginManager();
        String resourceName = "a.com/a";
        Assert.assertFalse(rpm.isConfiguredResourceName(resourceName));
        resourceName = "cmp.com/cmp";
        Assert.assertTrue(rpm.isConfiguredResourceName(resourceName));
    }
}

