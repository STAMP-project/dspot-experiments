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
package org.apache.hadoop.yarn.service;


import ServiceState.EXPRESS_UPGRADING;
import ServiceState.STABLE;
import ServiceState.UPGRADING;
import ServiceState.UPGRADING_AUTO_FINALIZE;
import java.util.List;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.service.api.records.Service;
import org.apache.hadoop.yarn.service.utils.ServiceApiUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static ServiceEventType.CANCEL_UPGRADE;
import static ServiceEventType.START;


/**
 * Tests for {@link ServiceManager}.
 */
public class TestServiceManager {
    @Rule
    public ServiceTestUtils.ServiceFSWatcher rule = new ServiceTestUtils.ServiceFSWatcher();

    @Test(timeout = TestServiceManager.TIMEOUT)
    public void testUpgrade() throws Exception {
        ServiceContext context = createServiceContext("testUpgrade");
        initUpgrade(context, "v2", false, false, false);
        Assert.assertEquals("service not upgraded", UPGRADING, context.getServiceManager().getServiceSpec().getState());
    }

    @Test(timeout = TestServiceManager.TIMEOUT)
    public void testRestartNothingToUpgrade() throws Exception {
        ServiceContext context = createServiceContext("testRestartNothingToUpgrade");
        initUpgrade(context, "v2", false, false, false);
        ServiceManager manager = context.getServiceManager();
        // make components stable by upgrading all instances
        upgradeAndReadyAllInstances(context);
        context.scheduler.getDispatcher().getEventHandler().handle(new ServiceEvent(START));
        GenericTestUtils.waitFor(() -> context.service.getState().equals(ServiceState.STABLE), TestServiceManager.CHECK_EVERY_MILLIS, TestServiceManager.TIMEOUT);
        Assert.assertEquals("service not re-started", STABLE, manager.getServiceSpec().getState());
    }

    @Test(timeout = TestServiceManager.TIMEOUT)
    public void testAutoFinalizeNothingToUpgrade() throws Exception {
        ServiceContext context = createServiceContext("testAutoFinalizeNothingToUpgrade");
        initUpgrade(context, "v2", false, true, false);
        ServiceManager manager = context.getServiceManager();
        // make components stable by upgrading all instances
        upgradeAndReadyAllInstances(context);
        GenericTestUtils.waitFor(() -> context.service.getState().equals(ServiceState.STABLE), TestServiceManager.CHECK_EVERY_MILLIS, TestServiceManager.TIMEOUT);
        Assert.assertEquals("service stable", STABLE, manager.getServiceSpec().getState());
    }

    @Test(timeout = TestServiceManager.TIMEOUT)
    public void testRestartWithPendingUpgrade() throws Exception {
        ServiceContext context = createServiceContext("testRestart");
        initUpgrade(context, "v2", true, false, false);
        ServiceManager manager = context.getServiceManager();
        context.scheduler.getDispatcher().getEventHandler().handle(new ServiceEvent(START));
        context.scheduler.getDispatcher().stop();
        Assert.assertEquals("service should still be upgrading", UPGRADING, manager.getServiceSpec().getState());
    }

    @Test(timeout = TestServiceManager.TIMEOUT)
    public void testFinalize() throws Exception {
        ServiceContext context = createServiceContext("testCheckState");
        initUpgrade(context, "v2", true, false, false);
        ServiceManager manager = context.getServiceManager();
        Assert.assertEquals("service not upgrading", UPGRADING, manager.getServiceSpec().getState());
        // make components stable by upgrading all instances
        upgradeAndReadyAllInstances(context);
        // finalize service
        context.scheduler.getDispatcher().getEventHandler().handle(new ServiceEvent(START));
        GenericTestUtils.waitFor(() -> context.service.getState().equals(ServiceState.STABLE), TestServiceManager.CHECK_EVERY_MILLIS, TestServiceManager.TIMEOUT);
        Assert.assertEquals("service not re-started", STABLE, manager.getServiceSpec().getState());
        validateUpgradeFinalization(manager.getName(), "v2");
    }

    @Test(timeout = TestServiceManager.TIMEOUT)
    public void testAutoFinalize() throws Exception {
        ServiceContext context = createServiceContext("testCheckStateAutoFinalize");
        ServiceManager manager = context.getServiceManager();
        manager.getServiceSpec().setState(UPGRADING_AUTO_FINALIZE);
        initUpgrade(context, "v2", true, true, false);
        // make components stable
        upgradeAndReadyAllInstances(context);
        GenericTestUtils.waitFor(() -> context.service.getState().equals(ServiceState.STABLE), TestServiceManager.CHECK_EVERY_MILLIS, TestServiceManager.TIMEOUT);
        Assert.assertEquals("service not stable", STABLE, manager.getServiceSpec().getState());
        validateUpgradeFinalization(manager.getName(), "v2");
    }

    @Test
    public void testInvalidUpgrade() throws Exception {
        ServiceContext serviceContext = createServiceContext("testInvalidUpgrade");
        ServiceManager manager = serviceContext.getServiceManager();
        manager.getServiceSpec().setState(UPGRADING_AUTO_FINALIZE);
        Service upgradedDef = ServiceTestUtils.createExampleApplication();
        upgradedDef.setName(manager.getName());
        upgradedDef.setVersion("v2");
        upgradedDef.setLifetime(2L);
        writeUpgradedDef(upgradedDef);
        try {
            manager.processUpgradeRequest("v2", true, false);
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof UnsupportedOperationException));
            return;
        }
        Assert.fail();
    }

    @Test(timeout = TestServiceManager.TIMEOUT)
    public void testExpressUpgrade() throws Exception {
        ServiceContext context = createServiceContext("testExpressUpgrade");
        ServiceManager manager = context.getServiceManager();
        manager.getServiceSpec().setState(EXPRESS_UPGRADING);
        initUpgrade(context, "v2", true, true, true);
        List<String> comps = ServiceApiUtil.resolveCompsDependency(context.service);
        // wait till instances of first component are upgraded and ready
        String compA = comps.get(0);
        makeInstancesReadyAfterUpgrade(context, compA);
        // wait till instances of second component are upgraded and ready
        String compB = comps.get(1);
        makeInstancesReadyAfterUpgrade(context, compB);
        GenericTestUtils.waitFor(() -> context.service.getState().equals(ServiceState.STABLE), TestServiceManager.CHECK_EVERY_MILLIS, TestServiceManager.TIMEOUT);
        Assert.assertEquals("service not stable", STABLE, manager.getServiceSpec().getState());
        validateUpgradeFinalization(manager.getName(), "v2");
    }

    @Test(timeout = TestServiceManager.TIMEOUT)
    public void testCancelUpgrade() throws Exception {
        ServiceContext context = createServiceContext("testCancelUpgrade");
        writeInitialDef(context.service);
        initUpgrade(context, "v2", true, false, false);
        ServiceManager manager = context.getServiceManager();
        Assert.assertEquals("service not upgrading", UPGRADING, manager.getServiceSpec().getState());
        List<String> comps = ServiceApiUtil.resolveCompsDependency(context.service);
        // wait till instances of first component are upgraded and ready
        String compA = comps.get(0);
        // upgrade the instances
        upgradeInstances(context, compA);
        makeInstancesReadyAfterUpgrade(context, compA);
        // cancel upgrade
        context.scheduler.getDispatcher().getEventHandler().handle(new ServiceEvent(CANCEL_UPGRADE));
        makeInstancesReadyAfterUpgrade(context, compA);
        GenericTestUtils.waitFor(() -> context.service.getState().equals(ServiceState.STABLE), TestServiceManager.CHECK_EVERY_MILLIS, TestServiceManager.TIMEOUT);
        Assert.assertEquals("service upgrade not cancelled", STABLE, manager.getServiceSpec().getState());
        validateUpgradeFinalization(manager.getName(), "v1");
    }

    @Test(timeout = TestServiceManager.TIMEOUT)
    public void testCancelUpgradeAfterInitiate() throws Exception {
        ServiceContext context = createServiceContext("testCancelUpgrade");
        writeInitialDef(context.service);
        initUpgrade(context, "v2", true, false, false);
        ServiceManager manager = context.getServiceManager();
        Assert.assertEquals("service not upgrading", UPGRADING, manager.getServiceSpec().getState());
        // cancel upgrade
        context.scheduler.getDispatcher().getEventHandler().handle(new ServiceEvent(CANCEL_UPGRADE));
        GenericTestUtils.waitFor(() -> context.service.getState().equals(ServiceState.STABLE), TestServiceManager.CHECK_EVERY_MILLIS, TestServiceManager.TIMEOUT);
        Assert.assertEquals("service upgrade not cancelled", STABLE, manager.getServiceSpec().getState());
        validateUpgradeFinalization(manager.getName(), "v1");
    }

    private static final int TIMEOUT = 10000;

    private static final int CHECK_EVERY_MILLIS = 100;
}

