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


import Constants.INVOCATION_NEED_MOCK;
import Constants.MONITOR_KEY;
import Constants.REFER_KEY;
import RoundRobinLoadBalance.NAME;
import RpcException.TIMEOUT_EXCEPTION;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.directory.StaticDirectory;
import org.apache.dubbo.rpc.cluster.filter.DemoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * AbstractClusterInvokerTest
 */
@SuppressWarnings("rawtypes")
public class AbstractClusterInvokerTest {
    List<Invoker<AbstractClusterInvokerTest.IHelloService>> invokers = new ArrayList<Invoker<AbstractClusterInvokerTest.IHelloService>>();

    List<Invoker<AbstractClusterInvokerTest.IHelloService>> selectedInvokers = new ArrayList<Invoker<AbstractClusterInvokerTest.IHelloService>>();

    AbstractClusterInvoker<AbstractClusterInvokerTest.IHelloService> cluster;

    AbstractClusterInvoker<AbstractClusterInvokerTest.IHelloService> cluster_nocheck;

    StaticDirectory<AbstractClusterInvokerTest.IHelloService> dic;

    RpcInvocation invocation = new RpcInvocation();

    URL url = URL.valueOf(("registry://localhost:9090/org.apache.dubbo.rpc.cluster.support.AbstractClusterInvokerTest.IHelloService?refer=" + (URL.encode("application=abstractClusterInvokerTest"))));

    Invoker<AbstractClusterInvokerTest.IHelloService> invoker1;

    Invoker<AbstractClusterInvokerTest.IHelloService> invoker2;

    Invoker<AbstractClusterInvokerTest.IHelloService> invoker3;

    Invoker<AbstractClusterInvokerTest.IHelloService> invoker4;

    Invoker<AbstractClusterInvokerTest.IHelloService> invoker5;

    Invoker<AbstractClusterInvokerTest.IHelloService> mockedInvoker1;

    @Test
    public void testBindingAttachment() {
        final String attachKey = "attach";
        final String attachValue = "value";
        // setup attachment
        RpcContext.getContext().setAttachment(attachKey, attachValue);
        Map<String, String> attachments = RpcContext.getContext().getAttachments();
        Assertions.assertTrue(((attachments != null) && ((attachments.size()) == 1)), "set attachment failed!");
        cluster = new AbstractClusterInvoker(dic) {
            @Override
            protected Result doInvoke(Invocation invocation, List invokers, LoadBalance loadbalance) throws RpcException {
                // attachment will be bind to invocation
                String value = invocation.getAttachment(attachKey);
                Assertions.assertTrue(((value != null) && (value.equals(attachValue))), "binding attachment failed!");
                return null;
            }
        };
        // invoke
        cluster.invoke(invocation);
    }

    @Test
    public void testSelect_Invokersize0() throws Exception {
        LoadBalance l = cluster.initLoadBalance(invokers, invocation);
        Assertions.assertNotNull(l, "cluster.initLoadBalance returns null!");
        {
            Invoker invoker = cluster.select(l, null, null, null);
            Assertions.assertEquals(null, invoker);
        }
        {
            invokers.clear();
            selectedInvokers.clear();
            Invoker invoker = cluster.select(l, null, invokers, null);
            Assertions.assertEquals(null, invoker);
        }
    }

    @Test
    public void testSelect_Invokersize1() throws Exception {
        invokers.clear();
        invokers.add(invoker1);
        LoadBalance l = cluster.initLoadBalance(invokers, invocation);
        Assertions.assertNotNull(l, "cluster.initLoadBalance returns null!");
        Invoker invoker = cluster.select(l, null, invokers, null);
        Assertions.assertEquals(invoker1, invoker);
    }

    @Test
    public void testSelect_Invokersize2AndselectNotNull() throws Exception {
        invokers.clear();
        invokers.add(invoker2);
        invokers.add(invoker4);
        LoadBalance l = cluster.initLoadBalance(invokers, invocation);
        Assertions.assertNotNull(l, "cluster.initLoadBalance returns null!");
        {
            selectedInvokers.clear();
            selectedInvokers.add(invoker4);
            Invoker invoker = cluster.select(l, invocation, invokers, selectedInvokers);
            Assertions.assertEquals(invoker2, invoker);
        }
        {
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            Invoker invoker = cluster.select(l, invocation, invokers, selectedInvokers);
            Assertions.assertEquals(invoker4, invoker);
        }
    }

    @Test
    public void testSelect_multiInvokers() throws Exception {
        testSelect_multiInvokers(NAME);
        testSelect_multiInvokers(LeastActiveLoadBalance.NAME);
        testSelect_multiInvokers(RandomLoadBalance.NAME);
    }

    @Test
    public void testCloseAvailablecheck() {
        LoadBalance lb = Mockito.mock(LoadBalance.class);
        Map<String, String> queryMap = StringUtils.parseQueryString(url.getParameterAndDecoded(REFER_KEY));
        URL tmpUrl = url.addParameters(queryMap).removeParameter(MONITOR_KEY);
        BDDMockito.given(lb.select(invokers, tmpUrl, invocation)).willReturn(invoker1);
        initlistsize5();
        Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
        Assertions.assertEquals(false, sinvoker.isAvailable());
        Assertions.assertEquals(invoker1, sinvoker);
    }

    @Test
    public void testDonotSelectAgainAndNoCheckAvailable() {
        LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(NAME);
        initlistsize5();
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertSame(invoker1, sinvoker);
        }
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertSame(invoker2, sinvoker);
        }
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertSame(invoker3, sinvoker);
        }
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertSame(invoker5, sinvoker);
        }
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertTrue(invokers.contains(sinvoker));
        }
    }

    @Test
    public void testSelectAgainAndCheckAvailable() {
        LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(NAME);
        initlistsize5();
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertTrue((sinvoker == (invoker4)));
        }
        {
            // Boundary condition test .
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assertions.assertTrue(((sinvoker == (invoker2)) || (sinvoker == (invoker4))));
        }
        {
            // Boundary condition test .
            for (int i = 0; i < 100; i++) {
                selectedInvokers.clear();
                Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
                Assertions.assertTrue(((sinvoker == (invoker2)) || (sinvoker == (invoker4))));
            }
        }
        {
            // Boundary condition test .
            for (int i = 0; i < 100; i++) {
                selectedInvokers.clear();
                selectedInvokers.add(invoker1);
                selectedInvokers.add(invoker3);
                selectedInvokers.add(invoker5);
                Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
                Assertions.assertTrue(((sinvoker == (invoker2)) || (sinvoker == (invoker4))));
            }
        }
        {
            // Boundary condition test .
            for (int i = 0; i < 100; i++) {
                selectedInvokers.clear();
                selectedInvokers.add(invoker1);
                selectedInvokers.add(invoker3);
                selectedInvokers.add(invoker2);
                selectedInvokers.add(invoker4);
                selectedInvokers.add(invoker5);
                Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
                Assertions.assertTrue(((sinvoker == (invoker2)) || (sinvoker == (invoker4))));
            }
        }
    }

    /**
     * Test balance.
     */
    @Test
    public void testSelectBalance() {
        LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(NAME);
        initlistsize5();
        Map<Invoker, AtomicLong> counter = new ConcurrentHashMap<Invoker, AtomicLong>();
        for (Invoker invoker : invokers) {
            counter.put(invoker, new AtomicLong(0));
        }
        int runs = 1000;
        for (int i = 0; i < runs; i++) {
            selectedInvokers.clear();
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            counter.get(sinvoker).incrementAndGet();
        }
        for (Map.Entry<Invoker, AtomicLong> entry : counter.entrySet()) {
            Long count = entry.getValue().get();
            // System.out.println(count);
            if (entry.getKey().isAvailable())
                Assertions.assertTrue((count > (runs / (invokers.size()))), "count should > avg");

        }
        Assertions.assertEquals(runs, ((counter.get(invoker2).get()) + (counter.get(invoker4).get())));
    }

    @Test
    public void testTimeoutExceptionCode() {
        List<Invoker<DemoService>> invokers = new ArrayList<Invoker<DemoService>>();
        invokers.add(new Invoker<DemoService>() {
            @Override
            public Class<DemoService> getInterface() {
                return DemoService.class;
            }

            public URL getUrl() {
                return URL.valueOf(((("dubbo://" + (NetUtils.getLocalHost())) + ":20880/") + (DemoService.class.getName())));
            }

            @Override
            public boolean isAvailable() {
                return false;
            }

            public Result invoke(Invocation invocation) throws RpcException {
                throw new RpcException(RpcException.TIMEOUT_EXCEPTION, "test timeout");
            }

            @Override
            public void destroy() {
            }
        });
        Directory<DemoService> directory = new StaticDirectory<DemoService>(invokers);
        FailoverClusterInvoker<DemoService> failoverClusterInvoker = new FailoverClusterInvoker<DemoService>(directory);
        try {
            failoverClusterInvoker.invoke(new RpcInvocation("sayHello", new Class<?>[0], new Object[0]));
            Assertions.fail();
        } catch (RpcException e) {
            Assertions.assertEquals(TIMEOUT_EXCEPTION, e.getCode());
        }
        ForkingClusterInvoker<DemoService> forkingClusterInvoker = new ForkingClusterInvoker<DemoService>(directory);
        try {
            forkingClusterInvoker.invoke(new RpcInvocation("sayHello", new Class<?>[0], new Object[0]));
            Assertions.fail();
        } catch (RpcException e) {
            Assertions.assertEquals(TIMEOUT_EXCEPTION, e.getCode());
        }
        FailfastClusterInvoker<DemoService> failfastClusterInvoker = new FailfastClusterInvoker<DemoService>(directory);
        try {
            failfastClusterInvoker.invoke(new RpcInvocation("sayHello", new Class<?>[0], new Object[0]));
            Assertions.fail();
        } catch (RpcException e) {
            Assertions.assertEquals(TIMEOUT_EXCEPTION, e.getCode());
        }
    }

    /**
     * Test mock invoker selector works as expected
     */
    @Test
    public void testMockedInvokerSelect() {
        initlistsize5();
        invokers.add(mockedInvoker1);
        initDic();
        RpcInvocation mockedInvocation = new RpcInvocation();
        mockedInvocation.setMethodName("sayHello");
        mockedInvocation.setAttachment(INVOCATION_NEED_MOCK, "true");
        List<Invoker<AbstractClusterInvokerTest.IHelloService>> mockedInvokers = dic.list(mockedInvocation);
        Assertions.assertEquals(1, mockedInvokers.size());
        List<Invoker<AbstractClusterInvokerTest.IHelloService>> invokers = dic.list(invocation);
        Assertions.assertEquals(5, invokers.size());
    }

    public static interface IHelloService {}
}

