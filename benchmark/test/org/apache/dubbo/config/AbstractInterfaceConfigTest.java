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
package org.apache.dubbo.config;


import Constants.SHUTDOWN_WAIT_KEY;
import Constants.SHUTDOWN_WAIT_SECONDS_KEY;
import java.io.File;
import java.util.Collections;
import java.util.List;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.ConfigUtils;
import org.apache.dubbo.config.api.Greeting;
import org.apache.dubbo.config.mock.GreetingLocal1;
import org.apache.dubbo.config.mock.GreetingLocal2;
import org.apache.dubbo.config.mock.GreetingLocal3;
import org.apache.dubbo.config.mock.GreetingMock1;
import org.apache.dubbo.config.mock.GreetingMock2;
import org.apache.dubbo.monitor.MonitorService;
import org.apache.dubbo.registry.RegistryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class AbstractInterfaceConfigTest {
    private static File dubboProperties;

    @Test
    public void testCheckRegistry1() {
        System.setProperty("dubbo.registry.address", "addr1|addr2");
        try {
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            checkRegistry();
            Assertions.assertEquals(2, getRegistries().size());
        } finally {
            System.clearProperty("dubbo.registry.address");
        }
    }

    @Test
    public void testCheckRegistry2() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            checkRegistry();
        });
    }

    @Test
    public void checkApplication1() {
        try {
            ConfigUtils.setProperties(null);
            System.clearProperty(SHUTDOWN_WAIT_KEY);
            System.clearProperty(SHUTDOWN_WAIT_SECONDS_KEY);
            writeDubboProperties(SHUTDOWN_WAIT_KEY, "100");
            System.setProperty("dubbo.application.name", "demo");
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            checkApplication();
            ApplicationConfig appConfig = getApplication();
            Assertions.assertEquals("demo", appConfig.getName());
            Assertions.assertEquals("100", System.getProperty(SHUTDOWN_WAIT_KEY));
            System.clearProperty(SHUTDOWN_WAIT_KEY);
            ConfigUtils.setProperties(null);
            writeDubboProperties(SHUTDOWN_WAIT_SECONDS_KEY, "1000");
            System.setProperty("dubbo.application.name", "demo");
            interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            checkApplication();
            Assertions.assertEquals("1000", System.getProperty(SHUTDOWN_WAIT_SECONDS_KEY));
        } finally {
            ConfigUtils.setProperties(null);
            System.clearProperty("dubbo.application.name");
            System.clearProperty(SHUTDOWN_WAIT_KEY);
            System.clearProperty(SHUTDOWN_WAIT_SECONDS_KEY);
        }
    }

    @Test
    public void checkApplication2() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            checkApplication();
        });
    }

    @Test
    public void testLoadRegistries() {
        System.setProperty("dubbo.registry.address", "addr1");
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        // FIXME: now we need to check first, then load
        checkRegistry();
        List<URL> urls = loadRegistries(true);
        Assertions.assertEquals(1, urls.size());
        URL url = urls.get(0);
        Assertions.assertEquals("registry", url.getProtocol());
        Assertions.assertEquals("addr1:9090", url.getAddress());
        Assertions.assertEquals(RegistryService.class.getName(), url.getPath());
        Assertions.assertTrue(url.getParameters().containsKey("timestamp"));
        Assertions.assertTrue(url.getParameters().containsKey("pid"));
        Assertions.assertTrue(url.getParameters().containsKey("registry"));
        Assertions.assertTrue(url.getParameters().containsKey("dubbo"));
    }

    @Test
    public void testLoadMonitor() {
        System.setProperty("dubbo.monitor.address", "monitor-addr:12080");
        System.setProperty("dubbo.monitor.protocol", "monitor");
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        URL url = interfaceConfig.loadMonitor(new URL("dubbo", "addr1", 9090));
        Assertions.assertEquals("monitor-addr:12080", url.getAddress());
        Assertions.assertEquals(MonitorService.class.getName(), url.getParameter("interface"));
        Assertions.assertNotNull(url.getParameter("dubbo"));
        Assertions.assertNotNull(url.getParameter("pid"));
        Assertions.assertNotNull(url.getParameter("timestamp"));
    }

    @Test
    public void checkInterfaceAndMethods1() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            interfaceConfig.checkInterfaceAndMethods(null, null);
        });
    }

    @Test
    public void checkInterfaceAndMethods2() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            checkInterfaceAndMethods(AbstractInterfaceConfigTest.class, null);
        });
    }

    @Test
    public void checkInterfaceAndMethod3() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            MethodConfig methodConfig = new MethodConfig();
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            interfaceConfig.checkInterfaceAndMethods(Greeting.class, Collections.singletonList(methodConfig));
        });
    }

    @Test
    public void checkInterfaceAndMethod4() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            MethodConfig methodConfig = new MethodConfig();
            methodConfig.setName("nihao");
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            interfaceConfig.checkInterfaceAndMethods(Greeting.class, Collections.singletonList(methodConfig));
        });
    }

    @Test
    public void checkInterfaceAndMethod5() {
        MethodConfig methodConfig = new MethodConfig();
        methodConfig.setName("hello");
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        interfaceConfig.checkInterfaceAndMethods(Greeting.class, Collections.singletonList(methodConfig));
    }

    @Test
    public void checkStubAndMock1() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            setLocal(GreetingLocal1.class.getName());
            checkStubAndLocal(Greeting.class);
            checkMock(Greeting.class);
        });
    }

    @Test
    public void checkStubAndMock2() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            setLocal(GreetingLocal2.class.getName());
            checkStubAndLocal(Greeting.class);
            checkMock(Greeting.class);
        });
    }

    @Test
    public void checkStubAndMock3() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setLocal(GreetingLocal3.class.getName());
        checkStubAndLocal(Greeting.class);
        checkMock(Greeting.class);
    }

    @Test
    public void checkStubAndMock4() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            setStub(GreetingLocal1.class.getName());
            checkStubAndLocal(Greeting.class);
            checkMock(Greeting.class);
        });
    }

    @Test
    public void checkStubAndMock5() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            setStub(GreetingLocal2.class.getName());
            checkStubAndLocal(Greeting.class);
            checkMock(Greeting.class);
        });
    }

    @Test
    public void checkStubAndMock6() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setStub(GreetingLocal3.class.getName());
        checkStubAndLocal(Greeting.class);
        checkMock(Greeting.class);
    }

    @Test
    public void checkStubAndMock7() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            setMock("return {a, b}");
            checkStubAndLocal(Greeting.class);
            checkMock(Greeting.class);
        });
    }

    @Test
    public void checkStubAndMock8() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            setMock(GreetingMock1.class.getName());
            checkStubAndLocal(Greeting.class);
            checkMock(Greeting.class);
        });
    }

    @Test
    public void checkStubAndMock9() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
            setMock(GreetingMock2.class.getName());
            checkStubAndLocal(Greeting.class);
            checkMock(Greeting.class);
        });
    }

    @Test
    public void testLocal() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        interfaceConfig.setLocal(((Boolean) (null)));
        Assertions.assertNull(getLocal());
        interfaceConfig.setLocal(true);
        Assertions.assertEquals("true", getLocal());
        setLocal("GreetingMock");
        Assertions.assertEquals("GreetingMock", getLocal());
    }

    @Test
    public void testStub() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        interfaceConfig.setStub(((Boolean) (null)));
        Assertions.assertNull(getStub());
        interfaceConfig.setStub(true);
        Assertions.assertEquals("true", getStub());
        setStub("GreetingMock");
        Assertions.assertEquals("GreetingMock", getStub());
    }

    @Test
    public void testCluster() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setCluster("mockcluster");
        Assertions.assertEquals("mockcluster", getCluster());
    }

    @Test
    public void testProxy() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setProxy("mockproxyfactory");
        Assertions.assertEquals("mockproxyfactory", getProxy());
    }

    @Test
    public void testConnections() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setConnections(1);
        Assertions.assertEquals(1, getConnections().intValue());
    }

    @Test
    public void testFilter() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setFilter("mockfilter");
        Assertions.assertEquals("mockfilter", getFilter());
    }

    @Test
    public void testListener() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setListener("mockinvokerlistener");
        Assertions.assertEquals("mockinvokerlistener", getListener());
    }

    @Test
    public void testLayer() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setLayer("layer");
        Assertions.assertEquals("layer", getLayer());
    }

    @Test
    public void testApplication() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        interfaceConfig.setApplication(applicationConfig);
        Assertions.assertSame(applicationConfig, interfaceConfig.getApplication());
    }

    @Test
    public void testModule() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        ModuleConfig moduleConfig = new ModuleConfig();
        interfaceConfig.setModule(moduleConfig);
        Assertions.assertSame(moduleConfig, getModule());
    }

    @Test
    public void testRegistry() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        RegistryConfig registryConfig = new RegistryConfig();
        interfaceConfig.setRegistry(registryConfig);
        Assertions.assertSame(registryConfig, getRegistry());
    }

    @Test
    public void testRegistries() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        RegistryConfig registryConfig = new RegistryConfig();
        interfaceConfig.setRegistries(Collections.singletonList(registryConfig));
        Assertions.assertEquals(1, getRegistries().size());
        Assertions.assertSame(registryConfig, getRegistries().get(0));
    }

    @Test
    public void testMonitor() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setMonitor("monitor-addr");
        Assertions.assertEquals("monitor-addr", getMonitor().getAddress());
        MonitorConfig monitorConfig = new MonitorConfig();
        interfaceConfig.setMonitor(monitorConfig);
        Assertions.assertSame(monitorConfig, getMonitor());
    }

    @Test
    public void testOwner() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setOwner("owner");
        Assertions.assertEquals("owner", getOwner());
    }

    @Test
    public void testCallbacks() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setCallbacks(2);
        Assertions.assertEquals(2, getCallbacks().intValue());
    }

    @Test
    public void testOnconnect() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setOnconnect("onConnect");
        Assertions.assertEquals("onConnect", getOnconnect());
    }

    @Test
    public void testOndisconnect() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setOndisconnect("onDisconnect");
        Assertions.assertEquals("onDisconnect", getOndisconnect());
    }

    @Test
    public void testScope() {
        AbstractInterfaceConfigTest.InterfaceConfig interfaceConfig = new AbstractInterfaceConfigTest.InterfaceConfig();
        setScope("scope");
        Assertions.assertEquals("scope", getScope());
    }

    private static class InterfaceConfig extends AbstractInterfaceConfig {}
}

