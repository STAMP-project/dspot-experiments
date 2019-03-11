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
package org.apache.hadoop.yarn.service.component.instance;


import ComponentInstanceState.INIT;
import ConfigFile.TypeEnum.STATIC;
import ContainerExitStatus.ABORTED;
import ContainerState.FAILED_UPGRADE;
import ContainerState.NEEDS_UPGRADE;
import ContainerState.READY;
import ContainerState.RUNNING_BUT_UNREADY;
import ContainerState.UPGRADING;
import LocalizationState.COMPLETED;
import LocalizationState.PENDING;
import com.google.common.collect.Lists;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerState.COMPLETE;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalizationStatus;
import org.apache.hadoop.yarn.service.MockRunningServiceContext;
import org.apache.hadoop.yarn.service.ServiceContext;
import org.apache.hadoop.yarn.service.ServiceScheduler;
import org.apache.hadoop.yarn.service.ServiceTestUtils;
import org.apache.hadoop.yarn.service.TestServiceManager;
import org.apache.hadoop.yarn.service.api.records.ConfigFile;
import org.apache.hadoop.yarn.service.api.records.Configuration;
import org.apache.hadoop.yarn.service.api.records.Container;
import org.apache.hadoop.yarn.service.api.records.Service;
import org.apache.hadoop.yarn.service.component.Component;
import org.apache.hadoop.yarn.service.component.TestComponent;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static ComponentInstanceEventType.BECOME_READY;
import static ComponentInstanceEventType.CANCEL_UPGRADE;
import static ComponentInstanceEventType.START;
import static ComponentInstanceEventType.STOP;
import static ComponentInstanceEventType.UPGRADE;


/**
 * Tests for {@link ComponentInstance}.
 */
public class TestComponentInstance {
    @Rule
    public ServiceTestUtils.ServiceFSWatcher rule = new ServiceTestUtils.ServiceFSWatcher();

    @Test
    public void testContainerUpgrade() throws Exception {
        ServiceContext context = TestComponent.createTestContext(rule, "testContainerUpgrade");
        Component component = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        upgradeComponent(component);
        ComponentInstance instance = component.getAllComponentInstances().iterator().next();
        ComponentInstanceEvent instanceEvent = new ComponentInstanceEvent(instance.getContainer().getId(), UPGRADE);
        instance.handle(instanceEvent);
        Container containerSpec = component.getComponentSpec().getContainer(instance.getContainer().getId().toString());
        Assert.assertEquals("instance not upgrading", UPGRADING, containerSpec.getState());
    }

    @Test
    public void testContainerReadyAfterUpgrade() throws Exception {
        ServiceContext context = TestComponent.createTestContext(rule, "testContainerReadyAfterUpgrade");
        Component component = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        upgradeComponent(component);
        ComponentInstance instance = component.getAllComponentInstances().iterator().next();
        ComponentInstanceEvent instanceEvent = new ComponentInstanceEvent(instance.getContainer().getId(), UPGRADE);
        instance.handle(instanceEvent);
        instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), START));
        Assert.assertEquals("instance not running", RUNNING_BUT_UNREADY, component.getComponentSpec().getContainer(instance.getContainer().getId().toString()).getState());
        instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), BECOME_READY));
        Assert.assertEquals("instance not ready", READY, component.getComponentSpec().getContainer(instance.getContainer().getId().toString()).getState());
    }

    @Test
    public void testContainerUpgradeFailed() throws Exception {
        ServiceContext context = TestComponent.createTestContext(rule, "testContainerUpgradeFailed");
        Component component = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        upgradeComponent(component);
        ComponentInstance instance = component.getAllComponentInstances().iterator().next();
        ComponentInstanceEvent upgradeEvent = new ComponentInstanceEvent(instance.getContainer().getId(), UPGRADE);
        instance.handle(upgradeEvent);
        ContainerStatus containerStatus = Mockito.mock(ContainerStatus.class);
        Mockito.when(containerStatus.getExitStatus()).thenReturn(ABORTED);
        ComponentInstanceEvent stopEvent = new ComponentInstanceEvent(instance.getContainer().getId(), STOP).setStatus(containerStatus);
        // this is the call back from NM for the upgrade
        instance.handle(stopEvent);
        Assert.assertEquals("instance did not fail", FAILED_UPGRADE, component.getComponentSpec().getContainer(instance.getContainer().getId().toString()).getState());
    }

    @Test
    public void testFailureAfterReinit() throws Exception {
        ServiceContext context = TestComponent.createTestContext(rule, "testContainerUpgradeFailed");
        Component component = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        upgradeComponent(component);
        ComponentInstance instance = component.getAllComponentInstances().iterator().next();
        ComponentInstanceEvent upgradeEvent = new ComponentInstanceEvent(instance.getContainer().getId(), UPGRADE);
        instance.handle(upgradeEvent);
        // NM finished updgrae
        instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), START));
        Assert.assertEquals("instance not running", RUNNING_BUT_UNREADY, component.getComponentSpec().getContainer(instance.getContainer().getId().toString()).getState());
        ContainerStatus containerStatus = Mockito.mock(ContainerStatus.class);
        Mockito.when(containerStatus.getExitStatus()).thenReturn(ABORTED);
        ComponentInstanceEvent stopEvent = new ComponentInstanceEvent(instance.getContainer().getId(), STOP).setStatus(containerStatus);
        // this is the call back from NM for the upgrade
        instance.handle(stopEvent);
        Assert.assertEquals("instance did not fail", FAILED_UPGRADE, component.getComponentSpec().getContainer(instance.getContainer().getId().toString()).getState());
    }

    @Test
    public void testCancelNothingToUpgrade() throws Exception {
        ServiceContext context = TestComponent.createTestContext(rule, "testCancelUpgradeWhenContainerReady");
        Component component = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        cancelCompUpgrade(component);
        ComponentInstance instance = component.getAllComponentInstances().iterator().next();
        ComponentInstanceEvent cancelEvent = new ComponentInstanceEvent(instance.getContainer().getId(), CANCEL_UPGRADE);
        instance.handle(cancelEvent);
        Assert.assertEquals("instance not ready", READY, component.getComponentSpec().getContainer(instance.getContainer().getId().toString()).getState());
    }

    @Test
    public void testCancelUpgradeFailed() throws Exception {
        ServiceContext context = TestComponent.createTestContext(rule, "testCancelUpgradeFailed");
        Component component = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        cancelCompUpgrade(component);
        ComponentInstance instance = component.getAllComponentInstances().iterator().next();
        ComponentInstanceEvent cancelEvent = new ComponentInstanceEvent(instance.getContainer().getId(), CANCEL_UPGRADE);
        instance.handle(cancelEvent);
        instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), STOP));
        Assert.assertEquals("instance not init", INIT, instance.getState());
    }

    @Test
    public void testCancelAfterCompProcessedCancel() throws Exception {
        ServiceContext context = TestComponent.createTestContext(rule, "testCancelAfterCompProcessedCancel");
        Component component = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        upgradeComponent(component);
        cancelCompUpgrade(component);
        ComponentInstance instance = component.getAllComponentInstances().iterator().next();
        ComponentInstanceEvent upgradeEvent = new ComponentInstanceEvent(instance.getContainer().getId(), UPGRADE);
        instance.handle(upgradeEvent);
        Assert.assertEquals("instance should start upgrading", NEEDS_UPGRADE, component.getComponentSpec().getContainer(instance.getContainer().getId().toString()).getState());
    }

    @Test
    public void testCancelWhileUpgradeWithSuccess() throws Exception {
        validateCancelWhileUpgrading(true, true);
    }

    @Test
    public void testCancelWhileUpgradeWithFailure() throws Exception {
        validateCancelWhileUpgrading(false, true);
    }

    @Test
    public void testCancelFailedWhileUpgradeWithSuccess() throws Exception {
        validateCancelWhileUpgrading(true, false);
    }

    @Test
    public void testCancelFailedWhileUpgradeWithFailure() throws Exception {
        validateCancelWhileUpgrading(false, false);
    }

    @Test
    public void testUpdateLocalizationStatuses() throws Exception {
        Service def = TestServiceManager.createBaseDef("testUpdateLocalizationStatuses");
        String file1 = (rule.getServiceBasePath().toString()) + "/file1";
        Files.write(Paths.get(file1), "test file".getBytes(), StandardOpenOption.CREATE_NEW);
        org.apache.hadoop.yarn.service.api.records.Component compDef = def.getComponents().iterator().next();
        ConfigFile configFile1 = new ConfigFile();
        configFile1.setType(STATIC);
        configFile1.setSrcFile(file1);
        compDef.setConfiguration(new Configuration().files(Lists.newArrayList(configFile1)));
        ServiceContext context = new MockRunningServiceContext(rule, def);
        Component component = context.scheduler.getAllComponents().get(compDef.getName());
        ComponentInstance instance = component.getAllComponentInstances().iterator().next();
        LocalizationStatus status = LocalizationStatus.newInstance("file1", PENDING);
        instance.updateLocalizationStatuses(Lists.newArrayList(status));
        Assert.assertTrue("retriever should still be active", instance.isLclRetrieverActive());
        Container container = instance.getContainerSpec();
        Assert.assertTrue(((container.getLocalizationStatuses()) != null));
        Assert.assertEquals("dest file", container.getLocalizationStatuses().get(0).getDestFile(), status.getResourceKey());
        Assert.assertEquals("state", container.getLocalizationStatuses().get(0).getState(), status.getLocalizationState());
        status = LocalizationStatus.newInstance("file1", COMPLETED);
        instance.updateLocalizationStatuses(Lists.newArrayList(status));
        Assert.assertTrue("retriever should not be active", (!(instance.isLclRetrieverActive())));
        Assert.assertTrue(((container.getLocalizationStatuses()) != null));
        Assert.assertEquals("dest file", container.getLocalizationStatuses().get(0).getDestFile(), status.getResourceKey());
        Assert.assertEquals("state", container.getLocalizationStatuses().get(0).getState(), status.getLocalizationState());
    }

    @Test
    public void testComponentRestartPolicy() {
        Map<String, Component> allComponents = new HashMap<>();
        Service mockService = Mockito.mock(Service.class);
        ServiceContext serviceContext = Mockito.mock(ServiceContext.class);
        Mockito.when(serviceContext.getService()).thenReturn(mockService);
        ServiceScheduler serviceSchedulerInstance = new ServiceScheduler(serviceContext);
        ServiceScheduler serviceScheduler = Mockito.spy(serviceSchedulerInstance);
        Mockito.when(serviceScheduler.getAllComponents()).thenReturn(allComponents);
        Mockito.doNothing().when(serviceScheduler).setGracefulStop(ArgumentMatchers.any(FinalApplicationStatus.class));
        final String containerDiag = "Container succeeded";
        ComponentInstanceEvent componentInstanceEvent = Mockito.mock(ComponentInstanceEvent.class);
        ContainerId containerId = ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(1234L, 1), 1), 1);
        ContainerStatus containerStatus = ContainerStatus.newInstance(containerId, COMPLETE, containerDiag, 0);
        Mockito.when(componentInstanceEvent.getStatus()).thenReturn(containerStatus);
        // Test case1: one component, one instance, restart policy = ALWAYS, exit=0
        Component comp = createComponent(serviceScheduler, org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 1, 0, 1, 0);
        ComponentInstance componentInstance = comp.getAllComponentInstances().iterator().next();
        ComponentInstance.handleComponentInstanceRelaunch(componentInstance, componentInstanceEvent, false, containerDiag);
        Mockito.verify(comp, Mockito.never()).markAsSucceeded(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.never()).markAsFailed(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.times(1)).reInsertPendingInstance(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(serviceScheduler.getTerminationHandler(), Mockito.never()).terminate(ArgumentMatchers.anyInt());
        // Test case2: one component, one instance, restart policy = ALWAYS, exit=1
        comp = createComponent(serviceScheduler, org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 0, 1, 1, 0);
        componentInstance = comp.getAllComponentInstances().iterator().next();
        containerStatus.setExitStatus(1);
        ComponentInstance.handleComponentInstanceRelaunch(componentInstance, componentInstanceEvent, false, containerDiag);
        Mockito.verify(comp, Mockito.never()).markAsSucceeded(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.never()).markAsFailed(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.times(1)).reInsertPendingInstance(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(serviceScheduler.getTerminationHandler(), Mockito.never()).terminate(ArgumentMatchers.anyInt());
        // Test case3: one component, one instance, restart policy = NEVER, exit=0
        // Should exit with code=0
        comp = createComponent(serviceScheduler, org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 1, 0, 1, 0);
        componentInstance = comp.getAllComponentInstances().iterator().next();
        containerStatus.setExitStatus(0);
        Map<String, ComponentInstance> succeededInstances = new HashMap<>();
        succeededInstances.put(componentInstance.getCompInstanceName(), componentInstance);
        Mockito.when(comp.getSucceededInstances()).thenReturn(succeededInstances.values());
        Mockito.when(comp.getNumSucceededInstances()).thenReturn(new Long(1));
        ComponentInstance.handleComponentInstanceRelaunch(componentInstance, componentInstanceEvent, false, containerDiag);
        Mockito.verify(comp, Mockito.times(1)).markAsSucceeded(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.never()).markAsFailed(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.times(0)).reInsertPendingInstance(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(serviceScheduler.getTerminationHandler(), Mockito.times(1)).terminate(ArgumentMatchers.eq(0));
        // Test case4: one component, one instance, restart policy = NEVER, exit=1
        // Should exit with code=-1
        comp = createComponent(serviceScheduler, org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 0, 1, 1, 0);
        componentInstance = comp.getAllComponentInstances().iterator().next();
        containerStatus.setExitStatus((-1));
        Mockito.when(comp.getNumFailedInstances()).thenReturn(new Long(1));
        ComponentInstance.handleComponentInstanceRelaunch(componentInstance, componentInstanceEvent, false, containerDiag);
        Mockito.verify(comp, Mockito.never()).markAsSucceeded(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.times(1)).markAsFailed(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.times(0)).reInsertPendingInstance(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(serviceScheduler.getTerminationHandler(), Mockito.times(1)).terminate(ArgumentMatchers.eq((-1)));
        // Test case5: one component, one instance, restart policy = ON_FAILURE,
        // exit=1
        // Should continue run.
        comp = createComponent(serviceScheduler, org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 0, 1, 1, 0);
        componentInstance = comp.getAllComponentInstances().iterator().next();
        containerStatus.setExitStatus(1);
        ComponentInstance.handleComponentInstanceRelaunch(componentInstance, componentInstanceEvent, false, containerDiag);
        Mockito.verify(comp, Mockito.never()).markAsSucceeded(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.never()).markAsFailed(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.times(1)).reInsertPendingInstance(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(serviceScheduler.getTerminationHandler(), Mockito.times(0)).terminate(ArgumentMatchers.anyInt());
        // Test case6: one component, 3 instances, restart policy = NEVER, exit=1
        // 2 of the instances not completed, it should continue run.
        comp = createComponent(serviceScheduler, org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 0, 1, 3, 0);
        componentInstance = comp.getAllComponentInstances().iterator().next();
        containerStatus.setExitStatus(1);
        ComponentInstance.handleComponentInstanceRelaunch(componentInstance, componentInstanceEvent, false, containerDiag);
        Mockito.verify(comp, Mockito.never()).markAsSucceeded(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.times(1)).markAsFailed(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.times(0)).reInsertPendingInstance(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(serviceScheduler.getTerminationHandler(), Mockito.times(0)).terminate(ArgumentMatchers.anyInt());
        // Test case7: one component, 3 instances, restart policy = ON_FAILURE,
        // exit=1
        // 2 of the instances completed, it should continue run.
        comp = createComponent(serviceScheduler, org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 0, 1, 3, 0);
        Iterator<ComponentInstance> iter = comp.getAllComponentInstances().iterator();
        containerStatus.setExitStatus(1);
        ComponentInstance commponentInstance = iter.next();
        ComponentInstance.handleComponentInstanceRelaunch(commponentInstance, componentInstanceEvent, false, containerDiag);
        Mockito.verify(comp, Mockito.never()).markAsSucceeded(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.never()).markAsFailed(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.times(1)).reInsertPendingInstance(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(serviceScheduler.getTerminationHandler(), Mockito.times(0)).terminate(ArgumentMatchers.anyInt());
        // Test case8: 2 components, 2 instances for each
        // comp2 already finished.
        // comp1 has a new instance finish, we should terminate the service
        comp = createComponent(serviceScheduler, org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 2, 0);
        Collection<ComponentInstance> component1Instances = comp.getAllComponentInstances();
        containerStatus.setExitStatus((-1));
        Component comp2 = createComponent(componentInstance.getComponent().getScheduler(), org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 2, 1);
        Collection<ComponentInstance> component2Instances = comp2.getAllComponentInstances();
        Map<String, ComponentInstance> failed2Instances = new HashMap<>();
        for (ComponentInstance component2Instance : component2Instances) {
            failed2Instances.put(component2Instance.getCompInstanceName(), component2Instance);
            Mockito.when(component2Instance.getComponent().getFailedInstances()).thenReturn(failed2Instances.values());
            Mockito.when(component2Instance.getComponent().getNumFailedInstances()).thenReturn(new Long(failed2Instances.size()));
            ComponentInstance.handleComponentInstanceRelaunch(component2Instance, componentInstanceEvent, false, containerDiag);
        }
        Map<String, ComponentInstance> failed1Instances = new HashMap<>();
        // 2nd component, already finished.
        for (ComponentInstance component1Instance : component1Instances) {
            failed1Instances.put(component1Instance.getCompInstanceName(), component1Instance);
            Mockito.when(component1Instance.getComponent().getFailedInstances()).thenReturn(failed1Instances.values());
            Mockito.when(component1Instance.getComponent().getNumFailedInstances()).thenReturn(new Long(failed1Instances.size()));
            ComponentInstance.handleComponentInstanceRelaunch(component1Instance, componentInstanceEvent, false, containerDiag);
        }
        Mockito.verify(comp, Mockito.never()).markAsSucceeded(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.times(2)).markAsFailed(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.times(0)).reInsertPendingInstance(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(serviceScheduler.getTerminationHandler(), Mockito.times(1)).terminate(ArgumentMatchers.eq((-1)));
        // Test case9: 2 components, 2 instances for each
        // comp2 already finished.
        // comp1 has a new instance finish, we should terminate the service
        // All instance finish with 0, service should exit with 0 as well.
        containerStatus.setExitStatus(0);
        comp = createComponent(serviceScheduler, org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 2, 0);
        component1Instances = comp.getAllComponentInstances();
        comp2 = createComponent(componentInstance.getComponent().getScheduler(), org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 2, 1);
        component2Instances = comp2.getAllComponentInstances();
        Map<String, ComponentInstance> succeeded2Instances = new HashMap<>();
        for (ComponentInstance component2Instance : component2Instances) {
            succeeded2Instances.put(component2Instance.getCompInstanceName(), component2Instance);
            Mockito.when(component2Instance.getComponent().getSucceededInstances()).thenReturn(succeeded2Instances.values());
            Mockito.when(component2Instance.getComponent().getNumSucceededInstances()).thenReturn(new Long(succeeded2Instances.size()));
            ComponentInstance.handleComponentInstanceRelaunch(component2Instance, componentInstanceEvent, false, containerDiag);
        }
        Map<String, ComponentInstance> succeeded1Instances = new HashMap<>();
        // 2nd component, already finished.
        for (ComponentInstance component1Instance : component1Instances) {
            succeeded1Instances.put(component1Instance.getCompInstanceName(), component1Instance);
            Mockito.when(component1Instance.getComponent().getSucceededInstances()).thenReturn(succeeded1Instances.values());
            Mockito.when(component1Instance.getComponent().getNumSucceededInstances()).thenReturn(new Long(succeeded1Instances.size()));
            ComponentInstance.handleComponentInstanceRelaunch(component1Instance, componentInstanceEvent, false, containerDiag);
        }
        Mockito.verify(comp, Mockito.times(2)).markAsSucceeded(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.never()).markAsFailed(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(componentInstance.getComponent(), Mockito.times(0)).reInsertPendingInstance(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(serviceScheduler.getTerminationHandler(), Mockito.times(1)).terminate(ArgumentMatchers.eq(0));
        // Test case10: 2 components, 2 instances for each
        // comp2 hasn't finished
        // comp1 finished.
        // Service should continue run.
        comp = createComponent(serviceScheduler, org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 2, 0);
        component1Instances = comp.getAllComponentInstances();
        comp2 = createComponent(componentInstance.getComponent().getScheduler(), org.apache.hadoop.yarn.service.api.records.Component.RestartPolicyEnum, 2, 1);
        component2Instances = comp2.getAllComponentInstances();
        for (ComponentInstance component2Instance : component2Instances) {
            ComponentInstance.handleComponentInstanceRelaunch(component2Instance, componentInstanceEvent, false, containerDiag);
        }
        succeeded1Instances = new HashMap();
        // 2nd component, already finished.
        for (ComponentInstance component1Instance : component1Instances) {
            succeeded1Instances.put(component1Instance.getCompInstanceName(), component1Instance);
            Mockito.when(component1Instance.getComponent().getSucceededInstances()).thenReturn(succeeded1Instances.values());
            ComponentInstance.handleComponentInstanceRelaunch(component1Instance, componentInstanceEvent, false, containerDiag);
        }
        Mockito.verify(comp, Mockito.times(2)).markAsSucceeded(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(comp, Mockito.never()).markAsFailed(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(componentInstance.getComponent(), Mockito.times(0)).reInsertPendingInstance(ArgumentMatchers.any(ComponentInstance.class));
        Mockito.verify(serviceScheduler.getTerminationHandler(), Mockito.never()).terminate(ArgumentMatchers.eq(0));
    }
}

