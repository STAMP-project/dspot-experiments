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
import URLParamType.requestTimeout;
import URLParamType.retries;
import com.weibo.api.motan.BaseTestCase;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.protocol.example.IWorld;
import com.weibo.api.motan.rpc.URL;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Service config test
 *
 * @author fishermen zhanglei
 * @version V1.0 created at: 2013-6-17
 */
public class ServiceConfigTest extends BaseTestCase {
    private ServiceConfig<IWorld> serviceConfig = null;

    @Test
    public void testExport() {
        serviceConfig.export();
        TestCase.assertTrue(serviceConfig.getExported().get());
        TestCase.assertEquals(serviceConfig.getExporters().size(), 1);
        TestCase.assertEquals(serviceConfig.getRegistereUrls().size(), 1);
    }

    @Test
    public void testExportException() {
        // registry null
        serviceConfig = BaseTestCase.mockIWorldServiceConfig();
        try {
            serviceConfig.export();
        } catch (Exception e) {
            TestCase.assertTrue(e.getMessage().contains("Should set registry"));
        }
        serviceConfig.setRegistry(BaseTestCase.mockLocalRegistryConfig());
        // export null
        try {
            serviceConfig.export();
            TestCase.assertTrue(false);
        } catch (Exception e) {
            TestCase.assertTrue(e.getMessage().contains("export should not empty"));
        }
        // protocol not exist
        serviceConfig.setProtocol(BaseTestCase.mockProtocolConfig("notExist"));
        serviceConfig.setExport((("notExist" + ":") + 0));
        try {
            serviceConfig.export();
            TestCase.assertTrue(false);
        } catch (Exception e) {
            TestCase.assertTrue(e.getMessage().contains("Protocol is null"));
        }
        // service already exist
        serviceConfig.setProtocol(BaseTestCase.mockProtocolConfig(PROTOCOL_INJVM));
        serviceConfig.setExport((((MotanConstants.PROTOCOL_INJVM) + ":") + 0));
        serviceConfig.export();
        TestCase.assertTrue(serviceConfig.getExported().get());
        ServiceConfig<IWorld> newServiceConfig = BaseTestCase.mockIWorldServiceConfig();
        newServiceConfig.setProtocol(BaseTestCase.mockProtocolConfig(PROTOCOL_INJVM));
        newServiceConfig.setRegistry(BaseTestCase.mockLocalRegistryConfig());
        newServiceConfig.setExport((((MotanConstants.PROTOCOL_INJVM) + ":") + 0));
        try {
            newServiceConfig.export();
            TestCase.assertTrue(false);
        } catch (Exception e) {
            TestCase.assertTrue(e.getMessage().contains("for same service"));
        }
    }

    @Test
    public void testMethodConfig() {
        List<MethodConfig> methods = new ArrayList<MethodConfig>();
        MethodConfig mc = new MethodConfig();
        mc.setName("world");
        mc.setRetries(1);
        mc.setArgumentTypes("void");
        mc.setRequestTimeout(123);
        methods.add(mc);
        mc = new MethodConfig();
        mc.setName("worldSleep");
        mc.setRetries(2);
        mc.setArgumentTypes("java.lang.String,int");
        mc.setRequestTimeout(456);
        methods.add(mc);
        serviceConfig.setRetries(10);
        serviceConfig.setMethods(methods);
        serviceConfig.export();
        TestCase.assertEquals(serviceConfig.getExporters().size(), 1);
        TestCase.assertEquals(serviceConfig.getRegistereUrls().size(), 1);
        URL serviceUrl = serviceConfig.getExporters().get(0).getUrl();
        TestCase.assertEquals(123, serviceUrl.getMethodParameter("world", "void", requestTimeout.getName(), requestTimeout.getIntValue()).intValue());
        TestCase.assertEquals(456, serviceUrl.getMethodParameter("worldSleep", "java.lang.String,int", requestTimeout.getName(), requestTimeout.getIntValue()).intValue());
        TestCase.assertEquals(1, serviceUrl.getMethodParameter("world", "void", retries.getName(), retries.getIntValue()).intValue());
        TestCase.assertEquals(2, serviceUrl.getMethodParameter("worldSleep", "java.lang.String,int", retries.getName(), retries.getIntValue()).intValue());
    }

    @Test
    public void testMultiProtocol() {
        serviceConfig.setProtocols(BaseTestCase.getMultiProtocols(PROTOCOL_INJVM, PROTOCOL_MOTAN));
        serviceConfig.setExport(((((((MotanConstants.PROTOCOL_INJVM) + ":") + 0) + ",") + (MotanConstants.PROTOCOL_MOTAN)) + ":8002"));
        serviceConfig.export();
        TestCase.assertEquals(serviceConfig.getExporters().size(), 2);
    }

    @Test
    public void testMultiRegitstry() {
        serviceConfig.setRegistries(BaseTestCase.getMultiRegister(REGISTRY_PROTOCOL_LOCAL, REGISTRY_PROTOCOL_ZOOKEEPER));
        List<URL> registryUrls = serviceConfig.loadRegistryUrls();
        TestCase.assertEquals(2, registryUrls.size());
    }

    @Test
    public void testUnexport() {
        testExport();
        serviceConfig.unexport();
        TestCase.assertFalse(serviceConfig.getExported().get());
        TestCase.assertEquals(serviceConfig.getExporters().size(), 0);
        TestCase.assertEquals(serviceConfig.getRegistereUrls().size(), 0);
    }
}

