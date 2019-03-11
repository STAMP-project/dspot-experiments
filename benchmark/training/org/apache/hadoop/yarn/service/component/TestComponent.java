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
package org.apache.hadoop.yarn.service.component;


import ComponentState.FLEXING;
import ComponentState.NEEDS_UPGRADE;
import ComponentState.STABLE;
import ComponentState.SUCCEEDED;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerState.COMPLETE;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.service.MockRunningServiceContext;
import org.apache.hadoop.yarn.service.ServiceContext;
import org.apache.hadoop.yarn.service.ServiceTestUtils;
import org.apache.hadoop.yarn.service.TestServiceManager;
import org.apache.hadoop.yarn.service.api.records.ComponentState;
import org.apache.hadoop.yarn.service.api.records.Service;
import org.apache.hadoop.yarn.service.api.records.ServiceState;
import org.apache.hadoop.yarn.service.api.records.org.apache.hadoop.yarn.service.component.ComponentState;
import org.apache.hadoop.yarn.service.component.ComponentState.CANCEL_UPGRADING;
import org.apache.hadoop.yarn.service.component.instance.ComponentInstance;
import org.apache.hadoop.yarn.service.component.instance.ComponentInstanceEvent;
import org.apache.hadoop.yarn.service.component.instance.ComponentInstanceEventType;
import org.apache.hadoop.yarn.service.conf.YarnServiceConstants;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static ComponentEventType.CANCEL_UPGRADE;
import static ComponentEventType.CHECK_STABLE;
import static ComponentEventType.CONTAINER_COMPLETED;
import static ComponentEventType.UPGRADE;


/**
 * Tests for {@link Component}.
 */
public class TestComponent {
    static final Logger LOG = Logger.getLogger(TestComponent.class);

    @Rule
    public ServiceTestUtils.ServiceFSWatcher rule = new ServiceTestUtils.ServiceFSWatcher();

    @Test
    public void testComponentUpgrade() throws Exception {
        ServiceContext context = TestComponent.createTestContext(rule, "testComponentUpgrade");
        Component comp = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        ComponentEvent upgradeEvent = new ComponentEvent(comp.getName(), UPGRADE);
        comp.handle(upgradeEvent);
        Assert.assertEquals("component not in need upgrade state", NEEDS_UPGRADE, comp.getComponentSpec().getState());
    }

    @Test
    public void testCheckState() throws Exception {
        String serviceName = "testCheckState";
        ServiceContext context = TestComponent.createTestContext(rule, serviceName);
        Component comp = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        comp.handle(new ComponentEvent(comp.getName(), UPGRADE).setTargetSpec(TestComponent.createSpecWithEnv(serviceName, comp.getName(), "key1", "val1")).setUpgradeVersion("v2"));
        // one instance finished upgrading
        comp.getUpgradeStatus().decContainersThatNeedUpgrade();
        comp.handle(new ComponentEvent(comp.getName(), CHECK_STABLE));
        Assert.assertEquals("component not in need upgrade state", NEEDS_UPGRADE, comp.getComponentSpec().getState());
        // second instance finished upgrading
        comp.getUpgradeStatus().decContainersThatNeedUpgrade();
        comp.handle(new ComponentEvent(comp.getName(), CHECK_STABLE));
        Assert.assertEquals("component not in stable state", STABLE, comp.getComponentSpec().getState());
        Assert.assertEquals("component did not upgrade successfully", "val1", comp.getComponentSpec().getConfiguration().getEnv("key1"));
    }

    @Test
    public void testContainerCompletedWhenUpgrading() throws Exception {
        String serviceName = "testContainerCompletedWhenUpgrading";
        MockRunningServiceContext context = TestComponent.createTestContext(rule, serviceName);
        Component comp = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        comp.handle(new ComponentEvent(comp.getName(), UPGRADE).setTargetSpec(TestComponent.createSpecWithEnv(serviceName, comp.getName(), "key1", "val1")).setUpgradeVersion("v2"));
        comp.getAllComponentInstances().forEach(( instance) -> instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), ComponentInstanceEventType.UPGRADE)));
        // reinitialization of a container failed
        for (ComponentInstance instance : comp.getAllComponentInstances()) {
            ComponentEvent stopEvent = new ComponentEvent(comp.getName(), CONTAINER_COMPLETED).setInstance(instance).setContainerId(instance.getContainer().getId());
            comp.handle(stopEvent);
            instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), ComponentInstanceEventType.STOP));
        }
        comp.handle(new ComponentEvent(comp.getName(), CHECK_STABLE));
        Assert.assertEquals("component not in needs upgrade state", NEEDS_UPGRADE, comp.getComponentSpec().getState());
    }

    @Test
    public void testCancelUpgrade() throws Exception {
        ServiceContext context = TestComponent.createTestContext(rule, "testCancelUpgrade");
        Component comp = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        ComponentEvent upgradeEvent = new ComponentEvent(comp.getName(), CANCEL_UPGRADE);
        comp.handle(upgradeEvent);
        Assert.assertEquals("component not in need upgrade state", NEEDS_UPGRADE, comp.getComponentSpec().getState());
        Assert.assertEquals(CANCEL_UPGRADING, comp.getState());
    }

    @Test
    public void testContainerCompletedCancelUpgrade() throws Exception {
        String serviceName = "testContainerCompletedCancelUpgrade";
        MockRunningServiceContext context = TestComponent.createTestContext(rule, serviceName);
        Component comp = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        // upgrade completes
        comp.handle(new ComponentEvent(comp.getName(), UPGRADE).setTargetSpec(TestComponent.createSpecWithEnv(serviceName, comp.getName(), "key1", "val1")).setUpgradeVersion("v2"));
        comp.getAllComponentInstances().forEach(( instance) -> instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), ComponentInstanceEventType.UPGRADE)));
        // reinitialization of a container done
        for (ComponentInstance instance : comp.getAllComponentInstances()) {
            instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), ComponentInstanceEventType.START));
            instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), ComponentInstanceEventType.BECOME_READY));
        }
        comp.handle(new ComponentEvent(comp.getName(), CANCEL_UPGRADE).setTargetSpec(TestComponent.createSpecWithEnv(serviceName, comp.getName(), "key1", "val0")).setUpgradeVersion("v1"));
        comp.getAllComponentInstances().forEach(( instance) -> instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), ComponentInstanceEventType.CANCEL_UPGRADE)));
        Iterator<ComponentInstance> iter = comp.getAllComponentInstances().iterator();
        // cancel upgrade failed of a container
        ComponentInstance instance1 = iter.next();
        ComponentEvent stopEvent = new ComponentEvent(comp.getName(), CONTAINER_COMPLETED).setInstance(instance1).setContainerId(instance1.getContainer().getId());
        comp.handle(stopEvent);
        instance1.handle(new ComponentInstanceEvent(instance1.getContainer().getId(), ComponentInstanceEventType.STOP));
        Assert.assertEquals(CANCEL_UPGRADING, comp.getState());
        comp.handle(new ComponentEvent(comp.getName(), CHECK_STABLE));
        Assert.assertEquals("component not in needs upgrade state", NEEDS_UPGRADE, comp.getComponentSpec().getState());
        Assert.assertEquals(CANCEL_UPGRADING, comp.getState());
        // second instance finished upgrading
        ComponentInstance instance2 = iter.next();
        instance2.handle(new ComponentInstanceEvent(instance2.getContainer().getId(), ComponentInstanceEventType.START));
        instance2.handle(new ComponentInstanceEvent(instance2.getContainer().getId(), ComponentInstanceEventType.BECOME_READY));
        comp.handle(new ComponentEvent(comp.getName(), CHECK_STABLE));
        Assert.assertEquals("component not in flexing state", FLEXING, comp.getComponentSpec().getState());
        // new container get allocated
        context.assignNewContainer(context.attemptId, 10, comp);
        comp.handle(new ComponentEvent(comp.getName(), CHECK_STABLE));
        Assert.assertEquals("component not in stable state", STABLE, comp.getComponentSpec().getState());
        Assert.assertEquals("cancel upgrade failed", "val0", comp.getComponentSpec().getConfiguration().getEnv("key1"));
    }

    @Test
    public void testCancelUpgradeSuccessWhileUpgrading() throws Exception {
        String serviceName = "testCancelUpgradeWhileUpgrading";
        MockRunningServiceContext context = TestComponent.createTestContext(rule, serviceName);
        Component comp = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        cancelUpgradeWhileUpgrading(context, comp);
        // cancel upgrade successful for both instances
        for (ComponentInstance instance : comp.getAllComponentInstances()) {
            instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), ComponentInstanceEventType.START));
            instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), ComponentInstanceEventType.BECOME_READY));
        }
        comp.handle(new ComponentEvent(comp.getName(), CHECK_STABLE));
        Assert.assertEquals("component not in stable state", STABLE, comp.getComponentSpec().getState());
        Assert.assertEquals("cancel upgrade failed", "val0", comp.getComponentSpec().getConfiguration().getEnv("key1"));
    }

    @Test
    public void testCancelUpgradeFailureWhileUpgrading() throws Exception {
        String serviceName = "testCancelUpgradeFailureWhileUpgrading";
        MockRunningServiceContext context = TestComponent.createTestContext(rule, serviceName);
        Component comp = context.scheduler.getAllComponents().entrySet().iterator().next().getValue();
        cancelUpgradeWhileUpgrading(context, comp);
        // cancel upgrade failed for both instances
        for (ComponentInstance instance : comp.getAllComponentInstances()) {
            instance.handle(new ComponentInstanceEvent(instance.getContainer().getId(), ComponentInstanceEventType.STOP));
        }
        comp.handle(new ComponentEvent(comp.getName(), CHECK_STABLE));
        Assert.assertEquals("component not in flexing state", FLEXING, comp.getComponentSpec().getState());
        for (ComponentInstance instance : comp.getAllComponentInstances()) {
            // new container get allocated
            context.assignNewContainer(context.attemptId, 10, comp);
        }
        comp.handle(new ComponentEvent(comp.getName(), CHECK_STABLE));
        Assert.assertEquals("component not in stable state", STABLE, comp.getComponentSpec().getState());
        Assert.assertEquals("cancel upgrade failed", "val0", comp.getComponentSpec().getConfiguration().getEnv("key1"));
    }

    @Test
    public void testComponentStateReachesStableStateWithTerminatingComponents() throws Exception {
        final String serviceName = "testComponentStateUpdatesWithTerminatingComponents";
        Service testService = ServiceTestUtils.createTerminatingJobExample(serviceName);
        TestServiceManager.createDef(serviceName, testService);
        ServiceContext context = new MockRunningServiceContext(rule, testService);
        for (Component comp : context.scheduler.getAllComponents().values()) {
            Iterator<ComponentInstance> instanceIter = comp.getAllComponentInstances().iterator();
            ComponentInstance componentInstance = instanceIter.next();
            Container instanceContainer = componentInstance.getContainer();
            Assert.assertEquals(0, comp.getNumSucceededInstances());
            Assert.assertEquals(0, comp.getNumFailedInstances());
            Assert.assertEquals(2, comp.getNumRunningInstances());
            Assert.assertEquals(2, comp.getNumReadyInstances());
            Assert.assertEquals(0, comp.getPendingInstances().size());
            // stop 1 container
            ContainerStatus containerStatus = ContainerStatus.newInstance(instanceContainer.getId(), COMPLETE, "successful", 0);
            comp.handle(new ComponentEvent(comp.getName(), CONTAINER_COMPLETED).setStatus(containerStatus).setContainerId(instanceContainer.getId()));
            componentInstance.handle(new ComponentInstanceEvent(componentInstance.getContainer().getId(), ComponentInstanceEventType.STOP).setStatus(containerStatus));
            Assert.assertEquals(1, comp.getNumSucceededInstances());
            Assert.assertEquals(0, comp.getNumFailedInstances());
            Assert.assertEquals(1, comp.getNumRunningInstances());
            Assert.assertEquals(1, comp.getNumReadyInstances());
            Assert.assertEquals(0, comp.getPendingInstances().size());
            org.apache.hadoop.yarn.service.component.ComponentState componentState = Component.checkIfStable(comp);
            Assert.assertEquals(org.apache.hadoop.yarn.service.component.ComponentState.STABLE, componentState);
        }
    }

    @Test
    public void testComponentStateUpdatesWithTerminatingComponents() throws Exception {
        final String serviceName = "testComponentStateUpdatesWithTerminatingComponents";
        Service testService = ServiceTestUtils.createTerminatingJobExample(serviceName);
        TestServiceManager.createDef(serviceName, testService);
        ServiceContext context = new MockRunningServiceContext(rule, testService);
        for (Component comp : context.scheduler.getAllComponents().values()) {
            Iterator<ComponentInstance> instanceIter = comp.getAllComponentInstances().iterator();
            while (instanceIter.hasNext()) {
                ComponentInstance componentInstance = instanceIter.next();
                Container instanceContainer = componentInstance.getContainer();
                // stop 1 container
                ContainerStatus containerStatus = ContainerStatus.newInstance(instanceContainer.getId(), COMPLETE, "successful", 0);
                comp.handle(new ComponentEvent(comp.getName(), CONTAINER_COMPLETED).setStatus(containerStatus).setContainerId(instanceContainer.getId()));
                componentInstance.handle(new ComponentInstanceEvent(componentInstance.getContainer().getId(), ComponentInstanceEventType.STOP).setStatus(containerStatus));
            } 
            ComponentState componentState = comp.getComponentSpec().getState();
            Assert.assertEquals(SUCCEEDED, componentState);
        }
        ServiceState serviceState = testService.getState();
        Assert.assertEquals(ServiceState.SUCCEEDED, serviceState);
    }

    @Test
    public void testComponentStateUpdatesWithTerminatingDominantComponents() throws Exception {
        final String serviceName = "testComponentStateUpdatesWithTerminatingServiceStateComponents";
        Service testService = ServiceTestUtils.createTerminatingDominantComponentJobExample(serviceName);
        TestServiceManager.createDef(serviceName, testService);
        ServiceContext context = new MockRunningServiceContext(rule, testService);
        for (Component comp : context.scheduler.getAllComponents().values()) {
            boolean componentIsDominant = comp.getComponentSpec().getConfiguration().getPropertyBool(YarnServiceConstants.CONTAINER_STATE_REPORT_AS_SERVICE_STATE, false);
            if (componentIsDominant) {
                Iterator<ComponentInstance> instanceIter = comp.getAllComponentInstances().iterator();
                while (instanceIter.hasNext()) {
                    ComponentInstance componentInstance = instanceIter.next();
                    Container instanceContainer = componentInstance.getContainer();
                    // stop 1 container
                    ContainerStatus containerStatus = ContainerStatus.newInstance(instanceContainer.getId(), COMPLETE, "successful", 0);
                    comp.handle(new ComponentEvent(comp.getName(), CONTAINER_COMPLETED).setStatus(containerStatus).setContainerId(instanceContainer.getId()));
                    componentInstance.handle(new ComponentInstanceEvent(componentInstance.getContainer().getId(), ComponentInstanceEventType.STOP).setStatus(containerStatus));
                } 
                ComponentState componentState = comp.getComponentSpec().getState();
                Assert.assertEquals(SUCCEEDED, componentState);
            }
        }
        ServiceState serviceState = testService.getState();
        Assert.assertEquals(ServiceState.SUCCEEDED, serviceState);
    }
}

