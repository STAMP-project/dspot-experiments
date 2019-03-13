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
package org.apache.dubbo.config.spring.schema;


import java.util.Map;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.spring.ConfigTest;
import org.apache.dubbo.config.spring.ServiceBean;
import org.apache.dubbo.config.spring.api.DemoService;
import org.apache.dubbo.config.spring.impl.DemoServiceImpl;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class DubboNamespaceHandlerTest {
    @Test
    public void testProviderXml() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((ConfigTest.class.getPackage().getName().replace('.', '/')) + "/demo-provider.xml"));
        ctx.start();
        ProtocolConfig protocolConfig = ctx.getBean(ProtocolConfig.class);
        MatcherAssert.assertThat(protocolConfig, CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(protocolConfig.getName(), CoreMatchers.is("dubbo"));
        MatcherAssert.assertThat(protocolConfig.getPort(), CoreMatchers.is(20813));
        ApplicationConfig applicationConfig = ctx.getBean(ApplicationConfig.class);
        MatcherAssert.assertThat(applicationConfig, CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(applicationConfig.getName(), CoreMatchers.is("demo-provider"));
        DemoService service = ctx.getBean(DemoService.class);
        MatcherAssert.assertThat(service, CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void testMultiProtocol() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((ConfigTest.class.getPackage().getName().replace('.', '/')) + "/multi-protocol.xml"));
        ctx.start();
        Map<String, ProtocolConfig> protocolConfigMap = ctx.getBeansOfType(ProtocolConfig.class);
        MatcherAssert.assertThat(protocolConfigMap.size(), CoreMatchers.is(2));
        ProtocolConfig rmiProtocolConfig = protocolConfigMap.get("rmi");
        MatcherAssert.assertThat(rmiProtocolConfig.getPort(), CoreMatchers.is(10991));
        ProtocolConfig dubboProtocolConfig = protocolConfigMap.get("dubbo");
        MatcherAssert.assertThat(dubboProtocolConfig.getPort(), CoreMatchers.is(20881));
    }

    @Test
    public void testDefaultProtocol() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((ConfigTest.class.getPackage().getName().replace('.', '/')) + "/override-protocol.xml"));
        ctx.start();
        ProtocolConfig protocolConfig = ctx.getBean(ProtocolConfig.class);
        MatcherAssert.assertThat(protocolConfig.getName(), CoreMatchers.is("dubbo"));
    }

    @Test
    public void testCustomParameter() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((ConfigTest.class.getPackage().getName().replace('.', '/')) + "/customize-parameter.xml"));
        ctx.start();
        ProtocolConfig protocolConfig = ctx.getBean(ProtocolConfig.class);
        MatcherAssert.assertThat(protocolConfig.getParameters().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(protocolConfig.getParameters().get("protocol-paramA"), CoreMatchers.is("protocol-paramA"));
        ServiceBean serviceBean = ctx.getBean(ServiceBean.class);
        MatcherAssert.assertThat(serviceBean.getParameters().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(serviceBean.getParameters().get("service-paramA"), CoreMatchers.is("service-paramA"));
    }

    @Test
    public void testDelayFixedTime() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext((("classpath:/" + (ConfigTest.class.getPackage().getName().replace('.', '/'))) + "/delay-fixed-time.xml"));
        ctx.start();
        MatcherAssert.assertThat(ctx.getBean(ServiceBean.class).getDelay(), CoreMatchers.is(300));
    }

    @Test
    public void testTimeoutConfig() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((ConfigTest.class.getPackage().getName().replace('.', '/')) + "/provider-nested-service.xml"));
        ctx.start();
        Map<String, ProviderConfig> providerConfigMap = ctx.getBeansOfType(ProviderConfig.class);
        MatcherAssert.assertThat(providerConfigMap.get("org.apache.dubbo.config.ProviderConfig").getTimeout(), CoreMatchers.is(2000));
    }

    @Test
    public void testMonitor() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((ConfigTest.class.getPackage().getName().replace('.', '/')) + "/provider-with-monitor.xml"));
        ctx.start();
        MatcherAssert.assertThat(ctx.getBean(MonitorConfig.class), CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void testMultiMonitor() {
        Assertions.assertThrows(BeanCreationException.class, () -> {
            ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((ConfigTest.class.getPackage().getName().replace('.', '/')) + "/multi-monitor.xml"));
            ctx.start();
        });
    }

    @Test
    public void testMultiProviderConfig() {
        Assertions.assertThrows(BeanCreationException.class, () -> {
            ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((ConfigTest.class.getPackage().getName().replace('.', '/')) + "/provider-multi.xml"));
            ctx.start();
        });
    }

    @Test
    public void testModuleInfo() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((ConfigTest.class.getPackage().getName().replace('.', '/')) + "/provider-with-module.xml"));
        ctx.start();
        ModuleConfig moduleConfig = ctx.getBean(ModuleConfig.class);
        MatcherAssert.assertThat(moduleConfig.getName(), CoreMatchers.is("test-module"));
    }

    @Test
    public void testNotificationWithWrongBean() {
        Assertions.assertThrows(BeanCreationException.class, () -> {
            ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((ConfigTest.class.getPackage().getName().replace('.', '/')) + "/consumer-notification.xml"));
            ctx.start();
        });
    }

    @Test
    public void testProperty() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((ConfigTest.class.getPackage().getName().replace('.', '/')) + "/service-class.xml"));
        ctx.start();
        ServiceBean serviceBean = ctx.getBean(ServiceBean.class);
        String prefix = ((DemoServiceImpl) (serviceBean.getRef())).getPrefix();
        MatcherAssert.assertThat(prefix, CoreMatchers.is("welcome:"));
    }
}

