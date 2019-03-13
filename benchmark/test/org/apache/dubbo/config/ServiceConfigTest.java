/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config;


import Constants.ANYHOST_KEY;
import Constants.APPLICATION_KEY;
import Constants.BIND_IP_KEY;
import Constants.BIND_PORT_KEY;
import Constants.GENERIC_KEY;
import Constants.INTERFACE_KEY;
import Constants.METHODS_KEY;
import Constants.PROVIDER;
import Constants.SIDE_KEY;
import java.util.concurrent.TimeUnit;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.config.api.DemoService;
import org.apache.dubbo.config.api.Greeting;
import org.apache.dubbo.config.mock.TestProxyFactory;
import org.apache.dubbo.config.provider.impl.DemoServiceImpl;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.service.GenericService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ServiceConfigTest {
    private Protocol protocolDelegate = Mockito.mock(Protocol.class);

    private Registry registryDelegate = Mockito.mock(Registry.class);

    private Exporter exporter = Mockito.mock(Exporter.class);

    private ServiceConfig<DemoServiceImpl> service = new ServiceConfig<DemoServiceImpl>();

    private ServiceConfig<DemoServiceImpl> service2 = new ServiceConfig<DemoServiceImpl>();

    private ServiceConfig<DemoServiceImpl> delayService = new ServiceConfig<DemoServiceImpl>();

    @Test
    public void testExport() throws Exception {
        service.export();
        MatcherAssert.assertThat(service.getExportedUrls(), Matchers.hasSize(1));
        URL url = service.toUrl();
        MatcherAssert.assertThat(url.getProtocol(), CoreMatchers.equalTo("mockprotocol2"));
        MatcherAssert.assertThat(url.getPath(), CoreMatchers.equalTo(DemoService.class.getName()));
        MatcherAssert.assertThat(url.getParameters(), Matchers.hasEntry(ANYHOST_KEY, "true"));
        MatcherAssert.assertThat(url.getParameters(), Matchers.hasEntry(APPLICATION_KEY, "app"));
        MatcherAssert.assertThat(url.getParameters(), Matchers.hasKey(BIND_IP_KEY));
        MatcherAssert.assertThat(url.getParameters(), Matchers.hasKey(BIND_PORT_KEY));
        MatcherAssert.assertThat(url.getParameters(), Matchers.hasEntry((((Constants.DEFAULT_KEY) + ".") + (Constants.EXPORT_KEY)), "true"));
        MatcherAssert.assertThat(url.getParameters(), Matchers.hasEntry("echo.0.callback", "false"));
        MatcherAssert.assertThat(url.getParameters(), Matchers.hasEntry(GENERIC_KEY, "false"));
        MatcherAssert.assertThat(url.getParameters(), Matchers.hasEntry(INTERFACE_KEY, DemoService.class.getName()));
        MatcherAssert.assertThat(url.getParameters(), Matchers.hasKey(METHODS_KEY));
        MatcherAssert.assertThat(url.getParameters().get(METHODS_KEY), CoreMatchers.containsString("echo"));
        MatcherAssert.assertThat(url.getParameters(), Matchers.hasEntry(SIDE_KEY, PROVIDER));
        Mockito.verify(protocolDelegate).export(Mockito.any(Invoker.class));
    }

    @Test
    public void testProxy() throws Exception {
        service2.export();
        MatcherAssert.assertThat(service2.getExportedUrls(), Matchers.hasSize(1));
        Assertions.assertEquals(2, TestProxyFactory.count);// local injvm and registry protocol, so expected is 2

    }

    @Test
    public void testDelayExport() throws Exception {
        delayService.export();
        Assertions.assertTrue(delayService.getExportedUrls().isEmpty());
        // add 300ms to ensure that the delayService has been exported
        TimeUnit.MILLISECONDS.sleep(((delayService.getDelay()) + 300));
        MatcherAssert.assertThat(delayService.getExportedUrls(), Matchers.hasSize(1));
    }

    @Test
    public void testInterfaceClass() throws Exception {
        ServiceConfig<Greeting> service = new ServiceConfig<Greeting>();
        service.setInterface(Greeting.class.getName());
        service.setRef(Mockito.mock(Greeting.class));
        MatcherAssert.assertThat(((service.getInterfaceClass()) == (Greeting.class)), CoreMatchers.is(true));
        service = new ServiceConfig<Greeting>();
        service.setRef(Mockito.mock(Greeting.class, Mockito.withSettings().extraInterfaces(GenericService.class)));
        MatcherAssert.assertThat(((service.getInterfaceClass()) == (GenericService.class)), CoreMatchers.is(true));
    }

    @Test
    public void testInterface1() throws Exception {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            ServiceConfig<DemoService> service = new ServiceConfig<DemoService>();
            service.setInterface(DemoServiceImpl.class);
        });
    }

    @Test
    public void testInterface2() throws Exception {
        ServiceConfig<DemoService> service = new ServiceConfig<DemoService>();
        service.setInterface(DemoService.class);
        MatcherAssert.assertThat(service.getInterface(), CoreMatchers.equalTo(DemoService.class.getName()));
    }

    @Test
    public void testProvider() throws Exception {
        ServiceConfig service = new ServiceConfig();
        ProviderConfig provider = new ProviderConfig();
        service.setProvider(provider);
        MatcherAssert.assertThat(service.getProvider(), CoreMatchers.is(provider));
    }

    @Test
    public void testGeneric1() throws Exception {
        ServiceConfig service = new ServiceConfig();
        service.setGeneric(GENERIC_SERIALIZATION_DEFAULT);
        MatcherAssert.assertThat(service.getGeneric(), CoreMatchers.equalTo(GENERIC_SERIALIZATION_DEFAULT));
        service.setGeneric(GENERIC_SERIALIZATION_NATIVE_JAVA);
        MatcherAssert.assertThat(service.getGeneric(), CoreMatchers.equalTo(GENERIC_SERIALIZATION_NATIVE_JAVA));
        service.setGeneric(GENERIC_SERIALIZATION_BEAN);
        MatcherAssert.assertThat(service.getGeneric(), CoreMatchers.equalTo(GENERIC_SERIALIZATION_BEAN));
    }

    @Test
    public void testGeneric2() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ServiceConfig service = new ServiceConfig();
            service.setGeneric("illegal");
        });
    }

    @Test
    public void testMock() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ServiceConfig service = new ServiceConfig();
            service.setMock("true");
        });
    }

    @Test
    public void testMock2() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ServiceConfig service = new ServiceConfig();
            service.setMock(true);
        });
    }

    @Test
    public void testUniqueServiceName() throws Exception {
        ServiceConfig<Greeting> service = new ServiceConfig<Greeting>();
        service.setGroup("dubbo");
        service.setInterface(Greeting.class);
        service.setVersion("1.0.0");
        MatcherAssert.assertThat(service.getUniqueServiceName(), CoreMatchers.equalTo((("dubbo/" + (Greeting.class.getName())) + ":1.0.0")));
    }
}

