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
package com.weibo.api.motan.config.springsupport;


import com.weibo.api.motan.config.ServiceConfig;
import com.weibo.api.motan.rpc.Exporter;
import com.weibo.api.motan.rpc.URL;
import com.weibo.api.motan.util.ConcurrentHashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ServiceConfigBeanTest extends BaseTest {
    ServiceConfig<ITest> serviceTest;

    ServiceConfig<ITest> serviceTest2;

    ServiceConfig<ITest> serviceTest3;

    @Test
    public void testGetRef() {
        ITest test = serviceTest.getRef();
        Assert.assertTrue((test instanceof TestImpl));
        Assert.assertEquals(test, serviceTest2.getRef());
        Assert.assertNotSame(test, serviceTest3.getRef());
    }

    @Test
    public void testExport() {
        Assert.assertTrue(serviceTest.getExported().get());
        Assert.assertTrue(serviceTest2.getExported().get());
        Assert.assertTrue(serviceTest3.getExported().get());
    }

    @Test
    public void testGetProtocolAndPort() {
        List<Exporter<ITest>> exporters = serviceTest.getExporters();
        Assert.assertEquals(2, exporters.size());
        boolean injvm = false;
        boolean motan = false;
        for (Exporter<ITest> exporter : exporters) {
            URL url = exporter.getUrl();
            if (("injvm".equals(url.getProtocol())) && ((url.getPort()) == 0)) {
                injvm = true;
            } else
                if (("motan".equals(url.getProtocol())) && ((url.getPort()) == 7888)) {
                    motan = true;
                }

        }
        Assert.assertTrue((injvm && motan));
        exporters = serviceTest2.getExporters();
        URL url = exporters.get(0).getUrl();
        Assert.assertEquals(1, exporters.size());
        Assert.assertEquals("motan", url.getProtocol());
        Assert.assertEquals(18080, url.getPort().intValue());
    }

    @Test
    public void testGetRegistereUrls() {
        ConcurrentHashSet<URL> registries = serviceTest.getRegistereUrls();
        Assert.assertEquals(3, registries.size());// ????????????????injvm?????localregistry?

        boolean local = false;
        boolean mock = false;
        for (URL url : registries) {
            if ("local".equals(url.getProtocol())) {
                local = true;
            }
            if ("mockRegistry".equals(url.getProtocol())) {
                mock = true;
            }
        }
        Assert.assertTrue((local && mock));
    }
}

