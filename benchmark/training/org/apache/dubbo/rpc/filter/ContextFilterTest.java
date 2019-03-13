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
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcResult;
import org.apache.dubbo.rpc.support.DemoService;
import org.apache.dubbo.rpc.support.MockInvocation;
import org.apache.dubbo.rpc.support.MyInvoker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * ContextFilterTest.java
 * TODO need to enhance assertion
 */
public class ContextFilterTest {
    Filter contextFilter = new ContextFilter();

    Invoker<DemoService> invoker;

    Invocation invocation;

    @SuppressWarnings("unchecked")
    @Test
    public void testSetContext() {
        invocation = Mockito.mock(Invocation.class);
        BDDMockito.given(invocation.getMethodName()).willReturn("$enumlength");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ Enum.class });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ "hello" });
        BDDMockito.given(invocation.getAttachments()).willReturn(null);
        invoker = Mockito.mock(Invoker.class);
        BDDMockito.given(invoker.isAvailable()).willReturn(true);
        BDDMockito.given(invoker.getInterface()).willReturn(DemoService.class);
        RpcResult result = new RpcResult();
        result.setValue("High");
        BDDMockito.given(invoker.invoke(invocation)).willReturn(result);
        URL url = URL.valueOf("test://test:11/test?group=dubbo&version=1.1");
        BDDMockito.given(invoker.getUrl()).willReturn(url);
        contextFilter.invoke(invoker, invocation);
        Assertions.assertNull(RpcContext.getContext().getInvoker());
    }

    @Test
    public void testWithAttachments() {
        URL url = URL.valueOf("test://test:11/test?group=dubbo&version=1.1");
        Invoker<DemoService> invoker = new MyInvoker<DemoService>(url);
        Invocation invocation = new MockInvocation();
        Result result = contextFilter.invoke(invoker, invocation);
        Assertions.assertNull(RpcContext.getContext().getInvoker());
    }
}

