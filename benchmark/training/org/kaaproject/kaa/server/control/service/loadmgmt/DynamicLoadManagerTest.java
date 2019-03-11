/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */
package org.kaaproject.kaa.server.control.service.loadmgmt;


import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.zk.control.ControlNode;
import org.kaaproject.kaa.server.common.zk.operations.OperationsNodeListener;
import org.kaaproject.kaa.server.control.service.zk.ControlZkService;
import org.mockito.Mockito;


/**
 *
 *
 * @author Andrey Panasenko <apanasenko@cybervisiontech.com>
 */
public class DynamicLoadManagerTest {
    private static LoadDistributionService ldServiceMock;

    private static ControlZkService zkServiceMock;

    private static ControlNode pNodeMock;

    /**
     * Test method for {@link org.kaaproject.kaa.server.control.service.loadmgmt.DynamicLoadManager#DynamicLoadManager(org.kaaproject.kaa.server.control.service.loadmgmt.LoadDistributionService)}.
     */
    @Test
    public void testDynamicLoadManager() {
        DynamicLoadManager dm = new DynamicLoadManager(DynamicLoadManagerTest.ldServiceMock);
        Assert.assertNotNull(dm);
        Assert.assertNotNull(dm.getLoadDistributionService());
        Assert.assertNotNull(dm.getDynamicRebalancer());
        Mockito.verify(DynamicLoadManagerTest.ldServiceMock, Mockito.atLeast(1)).getOpsServerHistoryTtl();
        Assert.assertEquals(300000, dm.getOpsServerHistoryTtl());
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.control.service.loadmgmt.DynamicLoadManager#recalculate()}.
     */
    @Test
    public void testRecalculate() {
        DynamicLoadManager dm = new DynamicLoadManager(DynamicLoadManagerTest.ldServiceMock);
        Assert.assertNotNull(dm);
        dm.recalculate();
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.control.service.loadmgmt.DynamicLoadManager#registerListeners()}.
     */
    @Test
    public void testRegisterListeners() {
        DynamicLoadManager dm = new DynamicLoadManager(DynamicLoadManagerTest.ldServiceMock);
        Assert.assertNotNull(dm);
        dm.registerListeners();
        Mockito.verify(DynamicLoadManagerTest.pNodeMock, Mockito.atLeast(1)).addListener(((OperationsNodeListener) (dm)));
        // verify(pNodeMock, times(1)).addListener((BootstrapNodeListener)dm);
    }

    /**
     * Test methods for {@link org.kaaproject.kaa.server.control.service.loadmgmt.DynamicLoadManager#setOpsServerHistoryTtl(long)}.
     * and {@link org.kaaproject.kaa.server.control.service.loadmgmt.DynamicLoadManager#getOpsServerHistoryTTL(long)}
     */
    @Test
    public void testEndpointHistoryTTL() {
        DynamicLoadManager dm = new DynamicLoadManager(DynamicLoadManagerTest.ldServiceMock);
        Assert.assertNotNull(dm);
        long opsServerHistoryTTL = 123456;
        dm.setOpsServerHistoryTtl(opsServerHistoryTTL);
        Assert.assertEquals(opsServerHistoryTTL, dm.getOpsServerHistoryTtl());
    }
}

