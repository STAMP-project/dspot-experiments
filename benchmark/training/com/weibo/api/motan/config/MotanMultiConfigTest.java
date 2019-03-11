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


import com.weibo.api.motan.BaseTestCase;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.mock.MockClient;
import com.weibo.api.motan.protocol.example.IHello;
import junit.framework.TestCase;
import org.junit.Test;


/**
 *
 *
 * @author maijunsheng
 * @author zhanlgei
 * @version ?????2013-6-23
 */
public class MotanMultiConfigTest extends BaseTestCase {
    ServiceConfig<IHello> serviceConfig1 = null;

    ServiceConfig<IHello> serviceConfig2 = null;

    RefererConfig<IHello> refererConfig1 = null;

    RefererConfig<IHello> refererConfig2 = null;

    RegistryConfig registryConfig = null;

    int port1 = 18080;

    int port2 = 18081;

    @Test
    public void testMultiService() {
        try {
            serviceConfig1.export();
            serviceConfig2.export();
            IHello hello = refererConfig1.getRef();
            TestCase.assertNotNull(hello);
            TestCase.assertEquals(2, MockClient.urlMap.size());
        } catch (Exception e) {
            e.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    @Test
    public void testMultiVersion() {
        try {
            serviceConfig1.setVersion("1.0");
            serviceConfig1.setExport((((MotanConstants.PROTOCOL_MOTAN) + ":") + (port1)));
            serviceConfig1.export();
            serviceConfig2.setVersion("2.0");
            serviceConfig2.setExport((((MotanConstants.PROTOCOL_MOTAN) + ":") + (port2)));
            serviceConfig2.export();
            refererConfig1.setVersion("1.0");
            IHello hello1 = refererConfig1.getRef();
            validateCall(port1, 3, hello1);
            refererConfig2.setVersion("2.0");
            IHello hello2 = refererConfig2.getRef();
            validateCall(port2, 2, hello2);
        } catch (Exception e) {
            e.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    @Test
    public void testMultiGroup() {
        try {
            // ???????group??rpc??????????group?????
            serviceConfig1.setGroup("group1");
            serviceConfig1.export();
            refererConfig1.setGroup("group2");
            IHello hello1 = refererConfig1.getRef();
            validateCall(port1, 3, hello1);
            serviceConfig1.unexport();
            refererConfig1.destroy();
            MockClient.urlMap.clear();
            serviceConfig2.setGroup("group2");
            serviceConfig2.export();
            refererConfig2.setGroup("group1");
            IHello hello2 = refererConfig2.getRef();
            validateCall(port2, 3, hello2);
        } catch (Exception e) {
            e.printStackTrace();
            TestCase.assertTrue(false);
        }
    }
}

