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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.RpcResult;
import org.apache.dubbo.rpc.cluster.Directory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * ForkingClusterInvokerTest
 */
@SuppressWarnings("unchecked")
public class ForkingClusterInvokerTest {
    private List<Invoker<ForkingClusterInvokerTest>> invokers = new ArrayList<Invoker<ForkingClusterInvokerTest>>();

    private URL url = URL.valueOf("test://test:11/test?forks=2");

    private Invoker<ForkingClusterInvokerTest> invoker1 = Mockito.mock(Invoker.class);

    private Invoker<ForkingClusterInvokerTest> invoker2 = Mockito.mock(Invoker.class);

    private Invoker<ForkingClusterInvokerTest> invoker3 = Mockito.mock(Invoker.class);

    private RpcInvocation invocation = new RpcInvocation();

    private Directory<ForkingClusterInvokerTest> dic;

    private Result result = new RpcResult();

    @Test
    public void testInvokeException() {
        resetInvokerToException();
        ForkingClusterInvoker<ForkingClusterInvokerTest> invoker = new ForkingClusterInvoker<ForkingClusterInvokerTest>(dic);
        try {
            invoker.invoke(invocation);
            Assertions.fail();
        } catch (RpcException expected) {
            Assertions.assertTrue(expected.getMessage().contains("Failed to forking invoke provider"));
            Assertions.assertFalse(((expected.getCause()) instanceof RpcException));
        }
    }

    @Test
    public void testClearRpcContext() {
        resetInvokerToException();
        ForkingClusterInvoker<ForkingClusterInvokerTest> invoker = new ForkingClusterInvoker<ForkingClusterInvokerTest>(dic);
        String attachKey = "attach";
        String attachValue = "value";
        RpcContext.getContext().setAttachment(attachKey, attachValue);
        Map<String, String> attachments = RpcContext.getContext().getAttachments();
        Assertions.assertTrue(((attachments != null) && ((attachments.size()) == 1)), "set attachment failed!");
        try {
            invoker.invoke(invocation);
            Assertions.fail();
        } catch (RpcException expected) {
            Assertions.assertTrue(expected.getMessage().contains("Failed to forking invoke provider"), "Succeeded to forking invoke provider !");
            Assertions.assertFalse(((expected.getCause()) instanceof RpcException));
        }
        Map<String, String> afterInvoke = RpcContext.getContext().getAttachments();
        Assertions.assertTrue(((afterInvoke != null) && ((afterInvoke.size()) == 0)), "clear attachment failed!");
    }

    @Test
    public void testInvokeNoException() {
        resetInvokerToNoException();
        ForkingClusterInvoker<ForkingClusterInvokerTest> invoker = new ForkingClusterInvoker<ForkingClusterInvokerTest>(dic);
        Result ret = invoker.invoke(invocation);
        Assertions.assertSame(result, ret);
    }
}

