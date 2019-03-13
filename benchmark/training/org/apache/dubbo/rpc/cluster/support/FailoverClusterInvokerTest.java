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
import java.util.concurrent.Callable;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.RpcResult;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.directory.StaticDirectory;
import org.apache.dubbo.rpc.protocol.AbstractInvoker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * FailoverClusterInvokerTest
 */
@SuppressWarnings("unchecked")
public class FailoverClusterInvokerTest {
    private List<Invoker<FailoverClusterInvokerTest>> invokers = new ArrayList<Invoker<FailoverClusterInvokerTest>>();

    private int retries = 5;

    private URL url = URL.valueOf(("test://test:11/test?retries=" + (retries)));

    private Invoker<FailoverClusterInvokerTest> invoker1 = Mockito.mock(Invoker.class);

    private Invoker<FailoverClusterInvokerTest> invoker2 = Mockito.mock(Invoker.class);

    private RpcInvocation invocation = new RpcInvocation();

    private Directory<FailoverClusterInvokerTest> dic;

    private Result result = new RpcResult();

    @Test
    public void testInvokeWithRuntimeException() {
        BDDMockito.given(invoker1.invoke(invocation)).willThrow(new RuntimeException());
        BDDMockito.given(invoker1.isAvailable()).willReturn(true);
        BDDMockito.given(invoker1.getUrl()).willReturn(url);
        BDDMockito.given(invoker1.getInterface()).willReturn(FailoverClusterInvokerTest.class);
        BDDMockito.given(invoker2.invoke(invocation)).willThrow(new RuntimeException());
        BDDMockito.given(invoker2.isAvailable()).willReturn(true);
        BDDMockito.given(invoker2.getUrl()).willReturn(url);
        BDDMockito.given(invoker2.getInterface()).willReturn(FailoverClusterInvokerTest.class);
        FailoverClusterInvoker<FailoverClusterInvokerTest> invoker = new FailoverClusterInvoker<FailoverClusterInvokerTest>(dic);
        try {
            invoker.invoke(invocation);
            Assertions.fail();
        } catch (RpcException expected) {
            Assertions.assertEquals(0, expected.getCode());
            Assertions.assertFalse(((expected.getCause()) instanceof RpcException));
        }
    }

    @Test
    public void testInvokeWithRPCException() {
        BDDMockito.given(invoker1.invoke(invocation)).willThrow(new RpcException());
        BDDMockito.given(invoker1.isAvailable()).willReturn(true);
        BDDMockito.given(invoker1.getUrl()).willReturn(url);
        BDDMockito.given(invoker1.getInterface()).willReturn(FailoverClusterInvokerTest.class);
        BDDMockito.given(invoker2.invoke(invocation)).willReturn(result);
        BDDMockito.given(invoker2.isAvailable()).willReturn(true);
        BDDMockito.given(invoker2.getUrl()).willReturn(url);
        BDDMockito.given(invoker2.getInterface()).willReturn(FailoverClusterInvokerTest.class);
        FailoverClusterInvoker<FailoverClusterInvokerTest> invoker = new FailoverClusterInvoker<FailoverClusterInvokerTest>(dic);
        for (int i = 0; i < 100; i++) {
            Result ret = invoker.invoke(invocation);
            Assertions.assertSame(result, ret);
        }
    }

    @Test
    public void testInvoke_retryTimes() {
        BDDMockito.given(invoker1.invoke(invocation)).willThrow(new RpcException(RpcException.TIMEOUT_EXCEPTION));
        BDDMockito.given(invoker1.isAvailable()).willReturn(false);
        BDDMockito.given(invoker1.getUrl()).willReturn(url);
        BDDMockito.given(invoker1.getInterface()).willReturn(FailoverClusterInvokerTest.class);
        BDDMockito.given(invoker2.invoke(invocation)).willThrow(new RpcException());
        BDDMockito.given(invoker2.isAvailable()).willReturn(false);
        BDDMockito.given(invoker2.getUrl()).willReturn(url);
        BDDMockito.given(invoker2.getInterface()).willReturn(FailoverClusterInvokerTest.class);
        FailoverClusterInvoker<FailoverClusterInvokerTest> invoker = new FailoverClusterInvoker<FailoverClusterInvokerTest>(dic);
        try {
            Result ret = invoker.invoke(invocation);
            Assertions.assertSame(result, ret);
            Assertions.fail();
        } catch (RpcException expected) {
            Assertions.assertTrue(((expected.isTimeout()) || ((expected.getCode()) == 0)));
            Assertions.assertTrue(((expected.getMessage().indexOf((((retries) + 1) + " times"))) > 0));
        }
    }

    @Test
    public void testNoInvoke() {
        dic = Mockito.mock(Directory.class);
        BDDMockito.given(dic.getUrl()).willReturn(url);
        BDDMockito.given(dic.list(invocation)).willReturn(null);
        BDDMockito.given(dic.getInterface()).willReturn(FailoverClusterInvokerTest.class);
        invocation.setMethodName("method1");
        invokers.add(invoker1);
        FailoverClusterInvoker<FailoverClusterInvokerTest> invoker = new FailoverClusterInvoker<FailoverClusterInvokerTest>(dic);
        try {
            invoker.invoke(invocation);
            Assertions.fail();
        } catch (RpcException expected) {
            Assertions.assertFalse(((expected.getCause()) instanceof RpcException));
        }
    }

    /**
     * When invokers in directory changes after a failed request but just before a retry effort,
     * then we should reselect from the latest invokers before retry.
     */
    @Test
    public void testInvokerDestroyAndReList() {
        final URL url = URL.valueOf(((("test://localhost/" + (FailoverClusterInvokerTest.Demo.class.getName())) + "?loadbalance=roundrobin&retries=") + (retries)));
        RpcException exception = new RpcException(RpcException.TIMEOUT_EXCEPTION);
        FailoverClusterInvokerTest.MockInvoker<FailoverClusterInvokerTest.Demo> invoker1 = new FailoverClusterInvokerTest.MockInvoker<FailoverClusterInvokerTest.Demo>(FailoverClusterInvokerTest.Demo.class, url);
        invoker1.setException(exception);
        FailoverClusterInvokerTest.MockInvoker<FailoverClusterInvokerTest.Demo> invoker2 = new FailoverClusterInvokerTest.MockInvoker<FailoverClusterInvokerTest.Demo>(FailoverClusterInvokerTest.Demo.class, url);
        invoker2.setException(exception);
        final List<Invoker<FailoverClusterInvokerTest.Demo>> invokers = new ArrayList<Invoker<FailoverClusterInvokerTest.Demo>>();
        invokers.add(invoker1);
        invokers.add(invoker2);
        Callable<Object> callable = new Callable<Object>() {
            public Object call() throws Exception {
                // Simulation: all invokers are destroyed
                for (Invoker<FailoverClusterInvokerTest.Demo> invoker : invokers) {
                    invoker.destroy();
                }
                invokers.clear();
                FailoverClusterInvokerTest.MockInvoker<FailoverClusterInvokerTest.Demo> invoker3 = new FailoverClusterInvokerTest.MockInvoker<FailoverClusterInvokerTest.Demo>(FailoverClusterInvokerTest.Demo.class, url);
                invokers.add(invoker3);
                return null;
            }
        };
        invoker1.setCallable(callable);
        invoker2.setCallable(callable);
        RpcInvocation inv = new RpcInvocation();
        inv.setMethodName("test");
        Directory<FailoverClusterInvokerTest.Demo> dic = new FailoverClusterInvokerTest.MockDirectory<FailoverClusterInvokerTest.Demo>(url, invokers);
        FailoverClusterInvoker<FailoverClusterInvokerTest.Demo> clusterinvoker = new FailoverClusterInvoker<FailoverClusterInvokerTest.Demo>(dic);
        clusterinvoker.invoke(inv);
    }

    public static interface Demo {}

    public static class MockInvoker<T> extends AbstractInvoker<T> {
        URL url;

        boolean available = true;

        boolean destoryed = false;

        Result result;

        RpcException exception;

        Callable<?> callable;

        public MockInvoker(Class<T> type, URL url) {
            super(type, url);
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public void setException(RpcException exception) {
            this.exception = exception;
        }

        public void setCallable(Callable<?> callable) {
            this.callable = callable;
        }

        @Override
        protected Result doInvoke(Invocation invocation) throws Throwable {
            if ((callable) != null) {
                try {
                    callable.call();
                } catch (Exception e) {
                    throw new RpcException(e);
                }
            }
            if ((exception) != null) {
                throw exception;
            } else {
                return result;
            }
        }
    }

    public class MockDirectory<T> extends StaticDirectory<T> {
        public MockDirectory(URL url, List<Invoker<T>> invokers) {
            super(url, invokers);
        }

        @Override
        protected List<Invoker<T>> doList(Invocation invocation) throws RpcException {
            return new ArrayList<Invoker<T>>(super.doList(invocation));
        }
    }
}

