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
package org.apache.dubbo.rpc.filter;


import Constants.GENERIC_KEY;
import Constants.GENERIC_SERIALIZATION_NATIVE_JAVA;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.service.GenericService;
import org.apache.dubbo.rpc.support.DemoService;
import org.apache.dubbo.rpc.support.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GenericFilterTest {
    GenericFilter genericFilter = new GenericFilter();

    @Test
    public void testInvokeWithDefault() throws Exception {
        Method genericInvoke = GenericService.class.getMethods()[0];
        Map<String, Object> person = new HashMap<String, Object>();
        person.put("name", "dubbo");
        person.put("age", 10);
        RpcInvocation invocation = new RpcInvocation(Constants.$INVOKE, genericInvoke.getParameterTypes(), new Object[]{ "getPerson", new String[]{ Person.class.getCanonicalName() }, new Object[]{ person } });
        URL url = URL.valueOf(("test://test:11/org.apache.dubbo.rpc.support.DemoService?" + "accesslog=true&group=dubbo&version=1.1"));
        Invoker invoker = Mockito.mock(Invoker.class);
        Mockito.when(invoker.invoke(ArgumentMatchers.any(Invocation.class))).thenReturn(new RpcResult(new Person("person", 10)));
        Mockito.when(invoker.getUrl()).thenReturn(url);
        Mockito.when(invoker.getInterface()).thenReturn(DemoService.class);
        Result result = genericFilter.invoke(invoker, invocation);
        Assertions.assertEquals(HashMap.class, result.getValue().getClass());
        Assertions.assertEquals(10, ((HashMap) (result.getValue())).get("age"));
    }

    @Test
    public void testInvokeWithJavaException() throws Exception {
        Assertions.assertThrows(RpcException.class, () -> {
            Method genericInvoke = GenericService.class.getMethods()[0];
            Map<String, Object> person = new HashMap<String, Object>();
            person.put("name", "dubbo");
            person.put("age", 10);
            RpcInvocation invocation = new RpcInvocation(Constants.$INVOKE, genericInvoke.getParameterTypes(), new Object[]{ "getPerson", new String[]{ Person.class.getCanonicalName() }, new Object[]{ person } });
            invocation.setAttachment(GENERIC_KEY, GENERIC_SERIALIZATION_NATIVE_JAVA);
            URL url = URL.valueOf(("test://test:11/org.apache.dubbo.rpc.support.DemoService?" + "accesslog=true&group=dubbo&version=1.1"));
            Invoker invoker = Mockito.mock(Invoker.class);
            Mockito.when(invoker.invoke(ArgumentMatchers.any(Invocation.class))).thenReturn(new RpcResult(new Person("person", 10)));
            Mockito.when(invoker.getUrl()).thenReturn(url);
            Mockito.when(invoker.getInterface()).thenReturn(DemoService.class);
            genericFilter.invoke(invoker, invocation);
        });
    }

    @Test
    public void testInvokeWithMethodNamtNot$Invoke() {
        Method genericInvoke = GenericService.class.getMethods()[0];
        Map<String, Object> person = new HashMap<String, Object>();
        person.put("name", "dubbo");
        person.put("age", 10);
        RpcInvocation invocation = new RpcInvocation("sayHi", genericInvoke.getParameterTypes(), new Object[]{ "getPerson", new String[]{ Person.class.getCanonicalName() }, new Object[]{ person } });
        URL url = URL.valueOf(("test://test:11/org.apache.dubbo.rpc.support.DemoService?" + "accesslog=true&group=dubbo&version=1.1"));
        Invoker invoker = Mockito.mock(Invoker.class);
        Mockito.when(invoker.invoke(ArgumentMatchers.any(Invocation.class))).thenReturn(new RpcResult(new Person("person", 10)));
        Mockito.when(invoker.getUrl()).thenReturn(url);
        Mockito.when(invoker.getInterface()).thenReturn(DemoService.class);
        Result result = genericFilter.invoke(invoker, invocation);
        Assertions.assertEquals(Person.class, result.getValue().getClass());
        Assertions.assertEquals(10, ((Person) (result.getValue())).getAge());
    }

    @Test
    public void testInvokeWithMethodArgumentSizeIsNot3() {
        Method genericInvoke = GenericService.class.getMethods()[0];
        Map<String, Object> person = new HashMap<String, Object>();
        person.put("name", "dubbo");
        person.put("age", 10);
        RpcInvocation invocation = new RpcInvocation(Constants.$INVOKE, genericInvoke.getParameterTypes(), new Object[]{ "getPerson", new String[]{ Person.class.getCanonicalName() } });
        URL url = URL.valueOf(("test://test:11/org.apache.dubbo.rpc.support.DemoService?" + "accesslog=true&group=dubbo&version=1.1"));
        Invoker invoker = Mockito.mock(Invoker.class);
        Mockito.when(invoker.invoke(ArgumentMatchers.any(Invocation.class))).thenReturn(new RpcResult(new Person("person", 10)));
        Mockito.when(invoker.getUrl()).thenReturn(url);
        Mockito.when(invoker.getInterface()).thenReturn(DemoService.class);
        Result result = genericFilter.invoke(invoker, invocation);
        Assertions.assertEquals(Person.class, result.getValue().getClass());
        Assertions.assertEquals(10, ((Person) (result.getValue())).getAge());
    }
}

