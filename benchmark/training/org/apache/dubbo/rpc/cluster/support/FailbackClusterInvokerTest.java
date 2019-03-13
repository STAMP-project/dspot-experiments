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
package org.apache.dubbo.rpc.cluster.support;


import MethodOrderer.OrderAnnotation;
import java.util.ArrayList;
import java.util.List;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.DubboAppender;
import org.apache.dubbo.common.utils.LogUtil;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.RpcResult;
import org.apache.dubbo.rpc.cluster.Directory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * FailbackClusterInvokerTest
 * <p>
 * add annotation @TestMethodOrder, the testARetryFailed Method must to first execution
 */
@TestMethodOrder(OrderAnnotation.class)
public class FailbackClusterInvokerTest {
    List<Invoker<FailbackClusterInvokerTest>> invokers = new ArrayList<Invoker<FailbackClusterInvokerTest>>();

    URL url = URL.valueOf("test://test:11/test?retries=2&failbacktasks=2");

    Invoker<FailbackClusterInvokerTest> invoker = Mockito.mock(Invoker.class);

    RpcInvocation invocation = new RpcInvocation();

    Directory<FailbackClusterInvokerTest> dic;

    Result result = new RpcResult();

    @Test
    @Order(1)
    public void testInvokeException() {
        resetInvokerToException();
        FailbackClusterInvoker<FailbackClusterInvokerTest> invoker = new FailbackClusterInvoker<FailbackClusterInvokerTest>(dic);
        invoker.invoke(invocation);
        Assertions.assertNull(RpcContext.getContext().getInvoker());
        DubboAppender.clear();
    }

    @Test
    @Order(2)
    public void testInvokeNoException() {
        resetInvokerToNoException();
        FailbackClusterInvoker<FailbackClusterInvokerTest> invoker = new FailbackClusterInvoker<FailbackClusterInvokerTest>(dic);
        Result ret = invoker.invoke(invocation);
        Assertions.assertSame(result, ret);
    }

    @Test
    @Order(3)
    public void testNoInvoke() {
        dic = Mockito.mock(Directory.class);
        BDDMockito.given(dic.getUrl()).willReturn(url);
        BDDMockito.given(dic.list(invocation)).willReturn(null);
        BDDMockito.given(dic.getInterface()).willReturn(FailbackClusterInvokerTest.class);
        invocation.setMethodName("method1");
        invokers.add(invoker);
        resetInvokerToNoException();
        FailbackClusterInvoker<FailbackClusterInvokerTest> invoker = new FailbackClusterInvoker<FailbackClusterInvokerTest>(dic);
        LogUtil.start();
        DubboAppender.clear();
        invoker.invoke(invocation);
        Assertions.assertEquals(1, LogUtil.findMessage("Failback to invoke"));
        LogUtil.stop();
    }
}

