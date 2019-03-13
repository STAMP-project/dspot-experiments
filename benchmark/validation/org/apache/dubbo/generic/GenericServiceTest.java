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
package org.apache.dubbo.generic;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.metadata.definition.ServiceDefinitionBuilder;
import org.apache.dubbo.metadata.definition.model.FullServiceDefinition;
import org.apache.dubbo.metadata.definition.model.MethodDefinition;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.service.GenericService;
import org.apache.dubbo.service.ComplexObject;
import org.apache.dubbo.service.DemoService;
import org.apache.dubbo.service.DemoServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.apache.dubbo.service.ComplexObject.TestEnum.VALUE2;


public class GenericServiceTest {
    @Test
    public void testGeneric() {
        DemoService server = new DemoServiceImpl();
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
        URL url = URL.valueOf((("dubbo://127.0.0.1:5342/" + (DemoService.class.getName())) + "?version=1.0.0"));
        Exporter<DemoService> exporter = protocol.export(proxyFactory.getInvoker(server, DemoService.class, url));
        Invoker<DemoService> invoker = protocol.refer(DemoService.class, url);
        GenericService client = ((GenericService) (proxyFactory.getProxy(invoker, true)));
        Object result = client.$invoke("sayHello", new String[]{ "java.lang.String" }, new Object[]{ "haha" });
        Assertions.assertEquals("hello haha", result);
        GenericService newClient = ((GenericService) (proxyFactory.getProxy(invoker, true)));
        Object res = newClient.$invoke("sayHello", new String[]{ "java.lang.String" }, new Object[]{ "hehe" });
        Assertions.assertEquals("hello hehe", res);
        invoker.destroy();
        exporter.unexport();
    }

    @Test
    public void testGeneric2() {
        DemoService server = new DemoServiceImpl();
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
        URL url = URL.valueOf((("dubbo://127.0.0.1:5342/" + (DemoService.class.getName())) + "?version=1.0.0&generic=true$timeout=3000"));
        Exporter<DemoService> exporter = protocol.export(proxyFactory.getInvoker(server, DemoService.class, url));
        Invoker<GenericService> invoker = protocol.refer(GenericService.class, url);
        GenericService client = proxyFactory.getProxy(invoker, true);
        Object result = client.$invoke("sayHello", new String[]{ "java.lang.String" }, new Object[]{ "haha" });
        Assertions.assertEquals("hello haha", result);
        Invoker<DemoService> invoker2 = protocol.refer(DemoService.class, url);
        GenericService client2 = ((GenericService) (proxyFactory.getProxy(invoker2, true)));
        Object result2 = client2.$invoke("sayHello", new String[]{ "java.lang.String" }, new Object[]{ "haha" });
        Assertions.assertEquals("hello haha", result2);
        invoker.destroy();
        exporter.unexport();
    }

    @Test
    public void testGenericComplexCompute4FullServiceMetadata() {
        DemoService server = new DemoServiceImpl();
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
        URL url = URL.valueOf((("dubbo://127.0.0.1:5342/" + (DemoService.class.getName())) + "?version=1.0.0&generic=true$timeout=3000"));
        Exporter<DemoService> exporter = protocol.export(proxyFactory.getInvoker(server, DemoService.class, url));
        String var1 = "v1";
        int var2 = 234;
        long l = 555;
        String[] var3 = new String[]{ "var31", "var32" };
        List<Integer> var4 = Arrays.asList(2, 4, 8);
        ComplexObject.TestEnum testEnum = VALUE2;
        FullServiceDefinition fullServiceDefinition = ServiceDefinitionBuilder.buildFullDefinition(DemoService.class);
        MethodDefinition methodDefinition = getMethod("complexCompute", fullServiceDefinition.getMethods());
        Map mapObject = createComplexObject(fullServiceDefinition, var1, var2, l, var3, var4, testEnum);
        ComplexObject complexObject = map2bean(mapObject);
        Invoker<GenericService> invoker = protocol.refer(GenericService.class, url);
        GenericService client = proxyFactory.getProxy(invoker, true);
        Object result = client.$invoke(methodDefinition.getName(), methodDefinition.getParameterTypes(), new Object[]{ "haha", mapObject });
        Assertions.assertEquals(("haha###" + (complexObject.toString())), result);
        Invoker<DemoService> invoker2 = protocol.refer(DemoService.class, url);
        GenericService client2 = ((GenericService) (proxyFactory.getProxy(invoker2, true)));
        Object result2 = client2.$invoke("complexCompute", methodDefinition.getParameterTypes(), new Object[]{ "haha2", mapObject });
        Assertions.assertEquals(("haha2###" + (complexObject.toString())), result2);
        invoker.destroy();
        exporter.unexport();
    }

    @Test
    public void testGenericFindComplexObject4FullServiceMetadata() {
        DemoService server = new DemoServiceImpl();
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
        URL url = URL.valueOf((("dubbo://127.0.0.1:5342/" + (DemoService.class.getName())) + "?version=1.0.0&generic=true$timeout=3000"));
        Exporter<DemoService> exporter = protocol.export(proxyFactory.getInvoker(server, DemoService.class, url));
        String var1 = "v1";
        int var2 = 234;
        long l = 555;
        String[] var3 = new String[]{ "var31", "var32" };
        List<Integer> var4 = Arrays.asList(2, 4, 8);
        ComplexObject.TestEnum testEnum = VALUE2;
        // ComplexObject complexObject = createComplexObject(var1, var2, l, var3, var4, testEnum);
        Invoker<GenericService> invoker = protocol.refer(GenericService.class, url);
        GenericService client = proxyFactory.getProxy(invoker, true);
        Object result = client.$invoke("findComplexObject", new String[]{ "java.lang.String", "int", "long", "java.lang.String[]", "java.util.List", "org.apache.dubbo.service.ComplexObject$TestEnum" }, new Object[]{ var1, var2, l, var3, var4, testEnum });
        Assertions.assertNotNull(result);
        ComplexObject r = map2bean(((Map) (result)));
        Assertions.assertEquals(r, createComplexObject(var1, var2, l, var3, var4, testEnum));
        invoker.destroy();
        exporter.unexport();
    }
}

