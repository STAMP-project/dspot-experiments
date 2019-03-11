/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.config;


import MotanConstants.PROTOCOL_INJVM;
import MotanConstants.PROTOCOL_MOTAN;
import MotanConstants.REGISTRY_PROTOCOL_LOCAL;
import MotanConstants.REGISTRY_PROTOCOL_ZOOKEEPER;
import com.weibo.api.motan.BaseTestCase;
import com.weibo.api.motan.protocol.example.IWorld;
import com.weibo.api.motan.protocol.example.MockWorld;
import com.weibo.api.motan.rpc.URL;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * refererConfig unit test.
 *
 * @author fishermen
 * @version V1.0 created at: 2013-6-18
 */
public class RefererConfigTest extends BaseTestCase {
    private RefererConfig<IWorld> refererConfig = null;

    private ServiceConfig<IWorld> serviceConfig = null;

    @Test
    public void testGetRef() {
        MockWorld mWorld = new MockWorld();
        serviceConfig.setRef(mWorld);
        serviceConfig.export();
        IWorld ref = refererConfig.getRef();
        TestCase.assertNotNull(ref);
        TestCase.assertEquals(refererConfig.getClusterSupports().size(), 1);
        int times = 3;
        for (int i = 0; i < times; i++) {
            ref.world("test");
        }
        TestCase.assertEquals(times, mWorld.stringCount.get());
        serviceConfig.unexport();
        // destroy
        refererConfig.destroy();
        TestCase.assertFalse(refererConfig.getInitialized().get());
    }

    @Test
    public void testException() {
        IWorld ref = null;
        // protocol empty
        List<ProtocolConfig> protocols = new ArrayList<ProtocolConfig>();
        refererConfig.setProtocols(protocols);
        try {
            ref = refererConfig.getRef();
            TestCase.assertTrue(false);
        } catch (Exception e) {
            TestCase.assertTrue(e.getMessage().contains("protocol not set correctly"));
        }
        // protocol not exists
        protocols.add(BaseTestCase.mockProtocolConfig("notExist"));
        try {
            ref = refererConfig.getRef();
            TestCase.assertTrue(false);
        } catch (Exception e) {
            TestCase.assertTrue(e.getMessage().contains("Protocol is null"));
        }
        protocols.add(BaseTestCase.mockProtocolConfig("notExist"));
        // method config wrong
        MethodConfig mConfig = new MethodConfig();
        mConfig.setName("notExist");
        refererConfig.setMethods(mConfig);
        try {
            ref = refererConfig.getRef();
            TestCase.assertTrue(false);
        } catch (Exception e) {
            TestCase.assertTrue(e.getMessage().contains("not found method"));
        }
    }

    @Test
    public void testMultiProtocol() {
        List<ProtocolConfig> protocols = BaseTestCase.getMultiProtocols(PROTOCOL_INJVM, PROTOCOL_MOTAN);
        refererConfig.setProtocols(protocols);
        IWorld ref = refererConfig.getRef();
        TestCase.assertNotNull(ref);
        TestCase.assertEquals(protocols.size(), refererConfig.getClusterSupports().size());
    }

    @Test
    public void testMultiRegitstry() {
        List<RegistryConfig> registries = BaseTestCase.getMultiRegister(REGISTRY_PROTOCOL_LOCAL, REGISTRY_PROTOCOL_ZOOKEEPER);
        refererConfig.setRegistries(registries);
        List<URL> registryUrls = refererConfig.loadRegistryUrls();
        TestCase.assertEquals(registries.size(), registryUrls.size());
    }
}

