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


import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcResult;
import org.apache.dubbo.rpc.support.DemoService;
import org.apache.dubbo.rpc.support.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * CompatibleFilterTest.java
 */
public class CompatibleFilterFilterTest {
    private Filter compatibleFilter = new CompatibleFilter();

    private Invocation invocation;

    private Invoker invoker;

    @Test
    public void testInvokerGeneric() {
        invocation = Mockito.mock(Invocation.class);
        BDDMockito.given(invocation.getMethodName()).willReturn("$enumlength");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ Enum.class });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ "hello" });
        invoker = Mockito.mock(Invoker.class);
        BDDMockito.given(invoker.isAvailable()).willReturn(true);
        BDDMockito.given(invoker.getInterface()).willReturn(DemoService.class);
        RpcResult result = new RpcResult();
        result.setValue("High");
        BDDMockito.given(invoker.invoke(invocation)).willReturn(result);
        URL url = URL.valueOf("test://test:11/test?group=dubbo&version=1.1");
        BDDMockito.given(invoker.getUrl()).willReturn(url);
        Result filterResult = compatibleFilter.invoke(invoker, invocation);
        Assertions.assertEquals(filterResult, result);
    }

    @Test
    public void testResulthasException() {
        invocation = Mockito.mock(Invocation.class);
        BDDMockito.given(invocation.getMethodName()).willReturn("enumlength");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ Enum.class });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ "hello" });
        invoker = Mockito.mock(Invoker.class);
        BDDMockito.given(invoker.isAvailable()).willReturn(true);
        BDDMockito.given(invoker.getInterface()).willReturn(DemoService.class);
        RpcResult result = new RpcResult();
        result.setException(new RuntimeException());
        result.setValue("High");
        BDDMockito.given(invoker.invoke(invocation)).willReturn(result);
        URL url = URL.valueOf("test://test:11/test?group=dubbo&version=1.1");
        BDDMockito.given(invoker.getUrl()).willReturn(url);
        Result filterResult = compatibleFilter.invoke(invoker, invocation);
        Assertions.assertEquals(filterResult, result);
    }

    @Test
    public void testInvokerJsonPojoSerialization() {
        invocation = Mockito.mock(Invocation.class);
        BDDMockito.given(invocation.getMethodName()).willReturn("enumlength");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ Type[].class });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ "hello" });
        invoker = Mockito.mock(Invoker.class);
        BDDMockito.given(invoker.isAvailable()).willReturn(true);
        BDDMockito.given(invoker.getInterface()).willReturn(DemoService.class);
        RpcResult result = new RpcResult();
        result.setValue("High");
        BDDMockito.given(invoker.invoke(invocation)).willReturn(result);
        URL url = URL.valueOf("test://test:11/test?group=dubbo&version=1.1&serialization=json");
        BDDMockito.given(invoker.getUrl()).willReturn(url);
        Result filterResult = compatibleFilter.invoke(invoker, invocation);
        Assertions.assertEquals(Type.High, filterResult.getValue());
    }

    @Test
    public void testInvokerNonJsonEnumSerialization() {
        invocation = Mockito.mock(Invocation.class);
        BDDMockito.given(invocation.getMethodName()).willReturn("enumlength");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ Type[].class });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ "hello" });
        invoker = Mockito.mock(Invoker.class);
        BDDMockito.given(invoker.isAvailable()).willReturn(true);
        BDDMockito.given(invoker.getInterface()).willReturn(DemoService.class);
        RpcResult result = new RpcResult();
        result.setValue("High");
        BDDMockito.given(invoker.invoke(invocation)).willReturn(result);
        URL url = URL.valueOf("test://test:11/test?group=dubbo&version=1.1");
        BDDMockito.given(invoker.getUrl()).willReturn(url);
        Result filterResult = compatibleFilter.invoke(invoker, invocation);
        Assertions.assertEquals(Type.High, filterResult.getValue());
    }

    @Test
    public void testInvokerNonJsonNonPojoSerialization() {
        invocation = Mockito.mock(Invocation.class);
        BDDMockito.given(invocation.getMethodName()).willReturn("echo");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ String.class });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ "hello" });
        invoker = Mockito.mock(Invoker.class);
        BDDMockito.given(invoker.isAvailable()).willReturn(true);
        BDDMockito.given(invoker.getInterface()).willReturn(DemoService.class);
        RpcResult result = new RpcResult();
        result.setValue(new String[]{ "High" });
        BDDMockito.given(invoker.invoke(invocation)).willReturn(result);
        URL url = URL.valueOf("test://test:11/test?group=dubbo&version=1.1");
        BDDMockito.given(invoker.getUrl()).willReturn(url);
        Result filterResult = compatibleFilter.invoke(invoker, invocation);
        Assertions.assertArrayEquals(new String[]{ "High" }, ((String[]) (filterResult.getValue())));
    }

    @Test
    public void testInvokerNonJsonPojoSerialization() {
        invocation = Mockito.mock(Invocation.class);
        BDDMockito.given(invocation.getMethodName()).willReturn("echo");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ String.class });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ "hello" });
        invoker = Mockito.mock(Invoker.class);
        BDDMockito.given(invoker.isAvailable()).willReturn(true);
        BDDMockito.given(invoker.getInterface()).willReturn(DemoService.class);
        RpcResult result = new RpcResult();
        result.setValue("hello");
        BDDMockito.given(invoker.invoke(invocation)).willReturn(result);
        URL url = URL.valueOf("test://test:11/test?group=dubbo&version=1.1");
        BDDMockito.given(invoker.getUrl()).willReturn(url);
        Result filterResult = compatibleFilter.invoke(invoker, invocation);
        Assertions.assertEquals("hello", filterResult.getValue());
    }
}

