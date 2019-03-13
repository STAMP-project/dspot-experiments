/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl.servicemanager.impl;


import MapService.SERVICE_NAME;
import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.spi.ConfigurableService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.servicemanager.ServiceInfo;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import java.util.Properties;
import javax.swing.JPanel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ServiceManagerImplTest extends HazelcastTestSupport {
    private ServiceManagerImpl serviceManager;

    static class FooService implements ConfigurableService , ManagedService {
        volatile boolean initCalled;

        volatile boolean configureCalled;

        @Override
        public void configure(Object configObject) {
            this.configureCalled = true;
        }

        @Override
        public void init(NodeEngine nodeEngine, Properties properties) {
            this.initCalled = true;
        }

        @Override
        public void reset() {
        }

        @Override
        public void shutdown(boolean terminate) {
        }
    }

    // ===================== getServiceInfo ================================
    @Test
    public void getServiceInfo() {
        ServiceInfo result = serviceManager.getServiceInfo(SERVICE_NAME);
        Assert.assertNotNull(result);
        Assert.assertEquals(SERVICE_NAME, result.getName());
        HazelcastTestSupport.assertInstanceOf(MapService.class, result.getService());
    }

    @Test
    public void getServiceInfo_notExisting() {
        ServiceInfo result = serviceManager.getServiceInfo("notexisting");
        Assert.assertNull(result);
    }

    // ===================== getServiceInfos ================================
    @Test
    public void getServiceInfos() {
        List<ServiceInfo> result = serviceManager.getServiceInfos(MapService.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        ServiceInfo serviceInfo = result.get(0);
        Assert.assertEquals(SERVICE_NAME, serviceInfo.getName());
        HazelcastTestSupport.assertInstanceOf(MapService.class, serviceInfo.getService());
    }

    @Test
    public void getServiceInfos_notExisting() {
        List<ServiceInfo> result = serviceManager.getServiceInfos(JPanel.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    // ==================== getService =====================================
    @Test
    public void getService() {
        Object result = serviceManager.getService(SERVICE_NAME);
        HazelcastTestSupport.assertInstanceOf(MapService.class, result);
    }

    @Test
    public void getService_notExisting() {
        Object result = serviceManager.getService("notexisting");
        Assert.assertNull(result);
    }

    // ======================== getSharedService ============================
    @Test
    public void getSharedService() {
        Object result = serviceManager.getSharedService(LockService.SERVICE_NAME);
        HazelcastTestSupport.assertInstanceOf(LockService.class, result);
    }

    @Test
    public void getSharedService_notExisting() {
        Object result = serviceManager.getSharedService("notexisting");
        Assert.assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSharedService_notSharedService() {
        serviceManager.getSharedService(SERVICE_NAME);
    }

    // ========================= getServices =====================================
    @Test
    public void getServices() {
        List<MapService> result = serviceManager.getServices(MapService.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        HazelcastTestSupport.assertInstanceOf(MapService.class, result.get(0));
    }

    @Test
    public void getServices_notExisting() {
        List<JPanel> result = serviceManager.getServices(JPanel.class);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    // ========================= userService =====================================
    @Test
    public void userService() {
        Object result = serviceManager.getService("fooService");
        ServiceManagerImplTest.FooService fooService = HazelcastTestSupport.assertInstanceOf(ServiceManagerImplTest.FooService.class, result);
        Assert.assertTrue(fooService.initCalled);
        Assert.assertTrue(fooService.configureCalled);
    }
}

