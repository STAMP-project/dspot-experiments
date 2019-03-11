/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller.service;


import ControllerServiceState.DISABLED;
import ControllerServiceState.ENABLED;
import ControllerServiceState.ENABLING;
import ScheduledState.STOPPED;
import ValidationStatus.VALID;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.nifi.bundle.Bundle;
import org.apache.nifi.components.state.StateManager;
import org.apache.nifi.components.state.StateManagerProvider;
import org.apache.nifi.controller.FlowController;
import org.apache.nifi.controller.ProcessorNode;
import org.apache.nifi.controller.flow.FlowManager;
import org.apache.nifi.controller.scheduling.StandardProcessScheduler;
import org.apache.nifi.controller.service.mock.MockProcessGroup;
import org.apache.nifi.controller.service.mock.ServiceA;
import org.apache.nifi.controller.service.mock.ServiceB;
import org.apache.nifi.controller.service.mock.ServiceC;
import org.apache.nifi.groups.ProcessGroup;
import org.apache.nifi.nar.ExtensionDiscoveringManager;
import org.apache.nifi.registry.VariableRegistry;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static ControllerServiceState.ENABLED;


public class TestStandardControllerServiceProvider {
    private static StateManagerProvider stateManagerProvider = new StateManagerProvider() {
        @Override
        public StateManager getStateManager(final String componentId) {
            return Mockito.mock(StateManager.class);
        }

        @Override
        public void shutdown() {
        }

        @Override
        public void enableClusterProvider() {
        }

        @Override
        public void disableClusterProvider() {
        }

        @Override
        public void onComponentRemoved(final String componentId) {
        }
    };

    private static VariableRegistry variableRegistry = VariableRegistry.ENVIRONMENT_SYSTEM_REGISTRY;

    private static NiFiProperties niFiProperties;

    private static ExtensionDiscoveringManager extensionManager;

    private static Bundle systemBundle;

    private FlowController controller;

    @Test
    public void testDisableControllerService() {
        final ProcessGroup procGroup = new MockProcessGroup(controller);
        final FlowController controller = Mockito.mock(FlowController.class);
        final FlowManager flowManager = Mockito.mock(FlowManager.class);
        Mockito.when(controller.getFlowManager()).thenReturn(flowManager);
        Mockito.when(flowManager.getGroup(Mockito.anyString())).thenReturn(procGroup);
        Mockito.when(controller.getExtensionManager()).thenReturn(TestStandardControllerServiceProvider.extensionManager);
        final StandardProcessScheduler scheduler = createScheduler();
        final StandardControllerServiceProvider provider = new StandardControllerServiceProvider(controller, scheduler, null);
        final ControllerServiceNode serviceNode = createControllerService(ServiceB.class.getName(), "B", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        serviceNode.performValidation();
        serviceNode.getValidationStatus(5, TimeUnit.SECONDS);
        provider.enableControllerService(serviceNode);
        provider.disableControllerService(serviceNode);
    }

    @Test(timeout = 10000)
    public void testEnableDisableWithReference() throws InterruptedException {
        final ProcessGroup group = new MockProcessGroup(controller);
        final FlowController controller = Mockito.mock(FlowController.class);
        final FlowManager flowManager = Mockito.mock(FlowManager.class);
        Mockito.when(controller.getFlowManager()).thenReturn(flowManager);
        Mockito.when(flowManager.getGroup(Mockito.anyString())).thenReturn(group);
        Mockito.when(controller.getExtensionManager()).thenReturn(TestStandardControllerServiceProvider.extensionManager);
        final StandardProcessScheduler scheduler = createScheduler();
        final StandardControllerServiceProvider provider = new StandardControllerServiceProvider(controller, scheduler, null);
        Mockito.when(controller.getControllerServiceProvider()).thenReturn(provider);
        final ControllerServiceNode serviceNodeB = createControllerService(ServiceB.class.getName(), "B", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        final ControllerServiceNode serviceNodeA = createControllerService(ServiceA.class.getName(), "A", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        group.addControllerService(serviceNodeA);
        group.addControllerService(serviceNodeB);
        setProperty(serviceNodeA, ServiceA.OTHER_SERVICE.getName(), "B");
        try {
            provider.enableControllerService(serviceNodeA);
        } catch (final IllegalStateException expected) {
        }
        Assert.assertSame(ENABLING, serviceNodeA.getState());
        serviceNodeB.performValidation();
        Assert.assertSame(VALID, serviceNodeB.getValidationStatus(5, TimeUnit.SECONDS));
        provider.enableControllerService(serviceNodeB);
        serviceNodeA.performValidation();
        Assert.assertSame(VALID, serviceNodeA.getValidationStatus(5, TimeUnit.SECONDS));
        final long maxTime = (System.nanoTime()) + (TimeUnit.SECONDS.toNanos(10));
        // Wait for Service A to become ENABLED. This will happen in a background thread after approximately 5 seconds, now that Service A is valid.
        while (((serviceNodeA.getState()) != (ENABLED)) && ((System.nanoTime()) <= maxTime)) {
            Thread.sleep(5L);
        } 
        Assert.assertSame(ENABLED, serviceNodeA.getState());
        try {
            provider.disableControllerService(serviceNodeB);
            Assert.fail("Was able to disable Service B but Service A is enabled and references B");
        } catch (final IllegalStateException expected) {
        }
        provider.disableControllerService(serviceNodeA);
        waitForServiceState(serviceNodeA, DISABLED);
        provider.disableControllerService(serviceNodeB);
        waitForServiceState(serviceNodeB, DISABLED);
    }

    @Test
    public void testOrderingOfServices() {
        final ProcessGroup procGroup = new MockProcessGroup(controller);
        final FlowController controller = Mockito.mock(FlowController.class);
        final FlowManager flowManager = Mockito.mock(FlowManager.class);
        Mockito.when(controller.getFlowManager()).thenReturn(flowManager);
        Mockito.when(flowManager.getGroup(Mockito.anyString())).thenReturn(procGroup);
        Mockito.when(controller.getExtensionManager()).thenReturn(TestStandardControllerServiceProvider.extensionManager);
        final StandardControllerServiceProvider provider = new StandardControllerServiceProvider(controller, null, null);
        final ControllerServiceNode serviceNode1 = createControllerService(ServiceA.class.getName(), "1", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        final ControllerServiceNode serviceNode2 = createControllerService(ServiceB.class.getName(), "2", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        setProperty(serviceNode1, ServiceA.OTHER_SERVICE.getName(), "2");
        final Map<String, ControllerServiceNode> nodeMap = new LinkedHashMap<>();
        nodeMap.put("1", serviceNode1);
        nodeMap.put("2", serviceNode2);
        List<List<ControllerServiceNode>> branches = StandardControllerServiceProvider.determineEnablingOrder(nodeMap);
        Assert.assertEquals(2, branches.size());
        List<ControllerServiceNode> ordered = branches.get(0);
        Assert.assertEquals(2, ordered.size());
        Assert.assertTrue(((ordered.get(0)) == serviceNode2));
        Assert.assertTrue(((ordered.get(1)) == serviceNode1));
        Assert.assertEquals(1, branches.get(1).size());
        Assert.assertTrue(((branches.get(1).get(0)) == serviceNode2));
        nodeMap.clear();
        nodeMap.put("2", serviceNode2);
        nodeMap.put("1", serviceNode1);
        branches = StandardControllerServiceProvider.determineEnablingOrder(nodeMap);
        Assert.assertEquals(2, branches.size());
        ordered = branches.get(1);
        Assert.assertEquals(2, ordered.size());
        Assert.assertTrue(((ordered.get(0)) == serviceNode2));
        Assert.assertTrue(((ordered.get(1)) == serviceNode1));
        Assert.assertEquals(1, branches.get(0).size());
        Assert.assertTrue(((branches.get(0).get(0)) == serviceNode2));
        // add circular dependency on self.
        nodeMap.clear();
        setProperty(serviceNode1, ServiceA.OTHER_SERVICE_2.getName(), "1");
        nodeMap.put("1", serviceNode1);
        nodeMap.put("2", serviceNode2);
        branches = StandardControllerServiceProvider.determineEnablingOrder(nodeMap);
        Assert.assertEquals(2, branches.size());
        ordered = branches.get(0);
        Assert.assertEquals(2, ordered.size());
        Assert.assertTrue(((ordered.get(0)) == serviceNode2));
        Assert.assertTrue(((ordered.get(1)) == serviceNode1));
        nodeMap.clear();
        nodeMap.put("2", serviceNode2);
        nodeMap.put("1", serviceNode1);
        branches = StandardControllerServiceProvider.determineEnablingOrder(nodeMap);
        Assert.assertEquals(2, branches.size());
        ordered = branches.get(1);
        Assert.assertEquals(2, ordered.size());
        Assert.assertTrue(((ordered.get(0)) == serviceNode2));
        Assert.assertTrue(((ordered.get(1)) == serviceNode1));
        // add circular dependency once removed. In this case, we won't actually be able to enable these because of the
        // circular dependency because they will never be valid because they will always depend on a disabled service.
        // But we want to ensure that the method returns successfully without throwing a StackOverflowException or anything
        // like that.
        nodeMap.clear();
        final ControllerServiceNode serviceNode3 = createControllerService(ServiceA.class.getName(), "3", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        setProperty(serviceNode1, ServiceA.OTHER_SERVICE.getName(), "3");
        setProperty(serviceNode3, ServiceA.OTHER_SERVICE.getName(), "1");
        nodeMap.put("1", serviceNode1);
        nodeMap.put("3", serviceNode3);
        branches = StandardControllerServiceProvider.determineEnablingOrder(nodeMap);
        Assert.assertEquals(2, branches.size());
        ordered = branches.get(0);
        Assert.assertEquals(2, ordered.size());
        Assert.assertTrue(((ordered.get(0)) == serviceNode3));
        Assert.assertTrue(((ordered.get(1)) == serviceNode1));
        nodeMap.clear();
        nodeMap.put("3", serviceNode3);
        nodeMap.put("1", serviceNode1);
        branches = StandardControllerServiceProvider.determineEnablingOrder(nodeMap);
        Assert.assertEquals(2, branches.size());
        ordered = branches.get(1);
        Assert.assertEquals(2, ordered.size());
        Assert.assertTrue(((ordered.get(0)) == serviceNode3));
        Assert.assertTrue(((ordered.get(1)) == serviceNode1));
        // Add multiple completely disparate branches.
        nodeMap.clear();
        setProperty(serviceNode1, ServiceA.OTHER_SERVICE.getName(), "2");
        final ControllerServiceNode serviceNode4 = createControllerService(ServiceB.class.getName(), "4", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        final ControllerServiceNode serviceNode5 = createControllerService(ServiceB.class.getName(), "5", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        setProperty(serviceNode3, ServiceA.OTHER_SERVICE.getName(), "4");
        nodeMap.put("1", serviceNode1);
        nodeMap.put("2", serviceNode2);
        nodeMap.put("3", serviceNode3);
        nodeMap.put("4", serviceNode4);
        nodeMap.put("5", serviceNode5);
        branches = StandardControllerServiceProvider.determineEnablingOrder(nodeMap);
        Assert.assertEquals(5, branches.size());
        ordered = branches.get(0);
        Assert.assertEquals(2, ordered.size());
        Assert.assertTrue(((ordered.get(0)) == serviceNode2));
        Assert.assertTrue(((ordered.get(1)) == serviceNode1));
        Assert.assertEquals(1, branches.get(1).size());
        Assert.assertTrue(((branches.get(1).get(0)) == serviceNode2));
        ordered = branches.get(2);
        Assert.assertEquals(2, ordered.size());
        Assert.assertTrue(((ordered.get(0)) == serviceNode4));
        Assert.assertTrue(((ordered.get(1)) == serviceNode3));
        Assert.assertEquals(1, branches.get(3).size());
        Assert.assertTrue(((branches.get(3).get(0)) == serviceNode4));
        Assert.assertEquals(1, branches.get(4).size());
        Assert.assertTrue(((branches.get(4).get(0)) == serviceNode5));
        // create 2 branches both dependent on the same service
        nodeMap.clear();
        setProperty(serviceNode1, ServiceA.OTHER_SERVICE.getName(), "2");
        setProperty(serviceNode3, ServiceA.OTHER_SERVICE.getName(), "2");
        nodeMap.put("1", serviceNode1);
        nodeMap.put("2", serviceNode2);
        nodeMap.put("3", serviceNode3);
        branches = StandardControllerServiceProvider.determineEnablingOrder(nodeMap);
        Assert.assertEquals(3, branches.size());
        ordered = branches.get(0);
        Assert.assertEquals(2, ordered.size());
        Assert.assertTrue(((ordered.get(0)) == serviceNode2));
        Assert.assertTrue(((ordered.get(1)) == serviceNode1));
        ordered = branches.get(1);
        Assert.assertEquals(1, ordered.size());
        Assert.assertTrue(((ordered.get(0)) == serviceNode2));
        ordered = branches.get(2);
        Assert.assertEquals(2, ordered.size());
        Assert.assertTrue(((ordered.get(0)) == serviceNode2));
        Assert.assertTrue(((ordered.get(1)) == serviceNode3));
    }

    @Test
    public void testEnableReferencingComponents() {
        final ProcessGroup procGroup = new MockProcessGroup(controller);
        final FlowController controller = Mockito.mock(FlowController.class);
        final FlowManager flowManager = Mockito.mock(FlowManager.class);
        Mockito.when(controller.getFlowManager()).thenReturn(flowManager);
        Mockito.when(flowManager.getGroup(Mockito.anyString())).thenReturn(procGroup);
        Mockito.when(controller.getExtensionManager()).thenReturn(TestStandardControllerServiceProvider.extensionManager);
        final StandardProcessScheduler scheduler = createScheduler();
        final StandardControllerServiceProvider provider = new StandardControllerServiceProvider(controller, null, null);
        final ControllerServiceNode serviceNode = createControllerService(ServiceA.class.getName(), "1", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        final ProcessorNode procNode = createProcessor(scheduler, provider);
        serviceNode.addReference(procNode);
        // procNode.setScheduledState(ScheduledState.STOPPED);
        provider.unscheduleReferencingComponents(serviceNode);
        Assert.assertEquals(STOPPED, procNode.getScheduledState());
        // procNode.setScheduledState(ScheduledState.RUNNING);
        provider.unscheduleReferencingComponents(serviceNode);
        Assert.assertEquals(STOPPED, procNode.getScheduledState());
    }

    @Test
    public void validateEnableServices() {
        final FlowManager flowManager = Mockito.mock(FlowManager.class);
        StandardProcessScheduler scheduler = createScheduler();
        FlowController controller = Mockito.mock(FlowController.class);
        Mockito.when(controller.getFlowManager()).thenReturn(flowManager);
        StandardControllerServiceProvider provider = new StandardControllerServiceProvider(controller, scheduler, null);
        ProcessGroup procGroup = new MockProcessGroup(controller);
        Mockito.when(flowManager.getGroup(Mockito.anyString())).thenReturn(procGroup);
        Mockito.when(controller.getExtensionManager()).thenReturn(TestStandardControllerServiceProvider.extensionManager);
        ControllerServiceNode A = createControllerService(ServiceA.class.getName(), "A", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode B = createControllerService(ServiceA.class.getName(), "B", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode C = createControllerService(ServiceA.class.getName(), "C", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode D = createControllerService(ServiceB.class.getName(), "D", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode E = createControllerService(ServiceA.class.getName(), "E", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode F = createControllerService(ServiceB.class.getName(), "F", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        procGroup.addControllerService(A);
        procGroup.addControllerService(B);
        procGroup.addControllerService(C);
        procGroup.addControllerService(D);
        procGroup.addControllerService(E);
        procGroup.addControllerService(F);
        setProperty(A, ServiceA.OTHER_SERVICE.getName(), "B");
        setProperty(B, ServiceA.OTHER_SERVICE.getName(), "D");
        setProperty(C, ServiceA.OTHER_SERVICE.getName(), "B");
        setProperty(C, ServiceA.OTHER_SERVICE_2.getName(), "D");
        setProperty(E, ServiceA.OTHER_SERVICE.getName(), "A");
        setProperty(E, ServiceA.OTHER_SERVICE_2.getName(), "F");
        final List<ControllerServiceNode> serviceNodes = Arrays.asList(A, B, C, D, E, F);
        serviceNodes.stream().forEach(ControllerServiceNode::performValidation);
        provider.enableControllerServices(serviceNodes);
        Assert.assertTrue(A.isActive());
        Assert.assertTrue(B.isActive());
        Assert.assertTrue(C.isActive());
        Assert.assertTrue(D.isActive());
        Assert.assertTrue(E.isActive());
        Assert.assertTrue(F.isActive());
    }

    /**
     * This test is similar to the above, but different combination of service
     * dependencies
     */
    @Test
    public void validateEnableServices2() {
        final FlowManager flowManager = Mockito.mock(FlowManager.class);
        StandardProcessScheduler scheduler = createScheduler();
        FlowController controller = Mockito.mock(FlowController.class);
        Mockito.when(controller.getFlowManager()).thenReturn(flowManager);
        StandardControllerServiceProvider provider = new StandardControllerServiceProvider(controller, scheduler, null);
        ProcessGroup procGroup = new MockProcessGroup(controller);
        Mockito.when(flowManager.getGroup(Mockito.anyString())).thenReturn(procGroup);
        Mockito.when(controller.getExtensionManager()).thenReturn(TestStandardControllerServiceProvider.extensionManager);
        ControllerServiceNode A = createControllerService(ServiceC.class.getName(), "A", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode B = createControllerService(ServiceA.class.getName(), "B", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode C = createControllerService(ServiceB.class.getName(), "C", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode D = createControllerService(ServiceA.class.getName(), "D", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode F = createControllerService(ServiceA.class.getName(), "F", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        procGroup.addControllerService(A);
        procGroup.addControllerService(B);
        procGroup.addControllerService(C);
        procGroup.addControllerService(D);
        procGroup.addControllerService(F);
        setProperty(A, ServiceC.REQ_SERVICE_1.getName(), "B");
        setProperty(A, ServiceC.REQ_SERVICE_2.getName(), "D");
        setProperty(B, ServiceA.OTHER_SERVICE.getName(), "C");
        setProperty(F, ServiceA.OTHER_SERVICE.getName(), "D");
        setProperty(D, ServiceA.OTHER_SERVICE.getName(), "C");
        final List<ControllerServiceNode> services = Arrays.asList(C, F, A, B, D);
        services.forEach(ControllerServiceNode::performValidation);
        provider.enableControllerServices(services);
        Assert.assertTrue(A.isActive());
        Assert.assertTrue(B.isActive());
        Assert.assertTrue(C.isActive());
        Assert.assertTrue(D.isActive());
        Assert.assertTrue(F.isActive());
    }

    @Test
    public void validateEnableServicesWithDisabledMissingService() {
        final FlowManager flowManager = Mockito.mock(FlowManager.class);
        StandardProcessScheduler scheduler = createScheduler();
        FlowController controller = Mockito.mock(FlowController.class);
        Mockito.when(controller.getFlowManager()).thenReturn(flowManager);
        StandardControllerServiceProvider provider = new StandardControllerServiceProvider(controller, scheduler, null);
        ProcessGroup procGroup = new MockProcessGroup(controller);
        Mockito.when(flowManager.getGroup(Mockito.anyString())).thenReturn(procGroup);
        Mockito.when(controller.getExtensionManager()).thenReturn(TestStandardControllerServiceProvider.extensionManager);
        ControllerServiceNode serviceNode1 = createControllerService(ServiceA.class.getName(), "1", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode serviceNode2 = createControllerService(ServiceA.class.getName(), "2", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode serviceNode3 = createControllerService(ServiceA.class.getName(), "3", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode serviceNode4 = createControllerService(ServiceB.class.getName(), "4", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode serviceNode5 = createControllerService(ServiceA.class.getName(), "5", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode serviceNode6 = createControllerService(ServiceB.class.getName(), "6", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        ControllerServiceNode serviceNode7 = createControllerService(ServiceC.class.getName(), "7", TestStandardControllerServiceProvider.systemBundle.getBundleDetails().getCoordinate(), provider);
        procGroup.addControllerService(serviceNode1);
        procGroup.addControllerService(serviceNode2);
        procGroup.addControllerService(serviceNode3);
        procGroup.addControllerService(serviceNode4);
        procGroup.addControllerService(serviceNode5);
        procGroup.addControllerService(serviceNode6);
        procGroup.addControllerService(serviceNode7);
        setProperty(serviceNode1, ServiceA.OTHER_SERVICE.getName(), "2");
        setProperty(serviceNode2, ServiceA.OTHER_SERVICE.getName(), "4");
        setProperty(serviceNode3, ServiceA.OTHER_SERVICE.getName(), "2");
        setProperty(serviceNode3, ServiceA.OTHER_SERVICE_2.getName(), "4");
        setProperty(serviceNode5, ServiceA.OTHER_SERVICE.getName(), "6");
        setProperty(serviceNode7, ServiceC.REQ_SERVICE_1.getName(), "2");
        setProperty(serviceNode7, ServiceC.REQ_SERVICE_2.getName(), "3");
        final List<ControllerServiceNode> allBut6 = Arrays.asList(serviceNode1, serviceNode2, serviceNode3, serviceNode4, serviceNode5, serviceNode7);
        allBut6.stream().forEach(ControllerServiceNode::performValidation);
        provider.enableControllerServices(allBut6);
        Assert.assertFalse(serviceNode1.isActive());
        Assert.assertFalse(serviceNode2.isActive());
        Assert.assertFalse(serviceNode3.isActive());
        Assert.assertFalse(serviceNode4.isActive());
        Assert.assertFalse(serviceNode5.isActive());
        Assert.assertFalse(serviceNode6.isActive());
        serviceNode6.performValidation();
        provider.enableControllerService(serviceNode6);
        provider.enableControllerServices(Arrays.asList(serviceNode1, serviceNode2, serviceNode3, serviceNode4, serviceNode5));
        Assert.assertTrue(serviceNode1.isActive());
        Assert.assertTrue(serviceNode2.isActive());
        Assert.assertTrue(serviceNode3.isActive());
        Assert.assertTrue(serviceNode4.isActive());
        Assert.assertTrue(serviceNode5.isActive());
        Assert.assertTrue(serviceNode6.isActive());
    }
}

