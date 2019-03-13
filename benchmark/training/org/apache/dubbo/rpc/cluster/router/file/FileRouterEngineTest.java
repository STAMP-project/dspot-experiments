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
package org.apache.dubbo.rpc.cluster.router.file;


import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngineManager;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcResult;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.RouterFactory;
import org.apache.dubbo.rpc.cluster.directory.StaticDirectory;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
public class FileRouterEngineTest {
    private static boolean isScriptUnsupported = (new ScriptEngineManager().getEngineByName("javascript")) == null;

    List<Invoker<FileRouterEngineTest>> invokers = new ArrayList<Invoker<FileRouterEngineTest>>();

    Invoker<FileRouterEngineTest> invoker1 = Mockito.mock(Invoker.class);

    Invoker<FileRouterEngineTest> invoker2 = Mockito.mock(Invoker.class);

    Invocation invocation;

    StaticDirectory<FileRouterEngineTest> dic;

    Result result = new RpcResult();

    private RouterFactory routerFactory = ExtensionLoader.getExtensionLoader(RouterFactory.class).getAdaptiveExtension();

    @Test
    public void testRouteNotAvailable() {
        if (FileRouterEngineTest.isScriptUnsupported)
            return;

        URL url = initUrl("notAvailablerule.javascript");
        initInvocation("method1");
        initInvokers(url, true, false);
        initDic(url);
        FileRouterEngineTest.MockClusterInvoker<FileRouterEngineTest> sinvoker = new FileRouterEngineTest.MockClusterInvoker<FileRouterEngineTest>(dic, url);
        for (int i = 0; i < 100; i++) {
            sinvoker.invoke(invocation);
            Invoker<FileRouterEngineTest> invoker = sinvoker.getSelectedInvoker();
            Assertions.assertEquals(invoker2, invoker);
        }
    }

    @Test
    public void testRouteAvailable() {
        if (FileRouterEngineTest.isScriptUnsupported)
            return;

        URL url = initUrl("availablerule.javascript");
        initInvocation("method1");
        initInvokers(url);
        initDic(url);
        FileRouterEngineTest.MockClusterInvoker<FileRouterEngineTest> sinvoker = new FileRouterEngineTest.MockClusterInvoker<FileRouterEngineTest>(dic, url);
        for (int i = 0; i < 100; i++) {
            sinvoker.invoke(invocation);
            Invoker<FileRouterEngineTest> invoker = sinvoker.getSelectedInvoker();
            Assertions.assertEquals(invoker1, invoker);
        }
    }

    @Test
    public void testRouteByMethodName() {
        if (FileRouterEngineTest.isScriptUnsupported)
            return;

        URL url = initUrl("methodrule.javascript");
        {
            initInvocation("method1");
            initInvokers(url, true, true);
            initDic(url);
            FileRouterEngineTest.MockClusterInvoker<FileRouterEngineTest> sinvoker = new FileRouterEngineTest.MockClusterInvoker<FileRouterEngineTest>(dic, url);
            for (int i = 0; i < 100; i++) {
                sinvoker.invoke(invocation);
                Invoker<FileRouterEngineTest> invoker = sinvoker.getSelectedInvoker();
                Assertions.assertEquals(invoker1, invoker);
            }
        }
        {
            initInvocation("method2");
            initInvokers(url, true, true);
            initDic(url);
            FileRouterEngineTest.MockClusterInvoker<FileRouterEngineTest> sinvoker = new FileRouterEngineTest.MockClusterInvoker<FileRouterEngineTest>(dic, url);
            for (int i = 0; i < 100; i++) {
                sinvoker.invoke(invocation);
                Invoker<FileRouterEngineTest> invoker = sinvoker.getSelectedInvoker();
                Assertions.assertEquals(invoker2, invoker);
            }
        }
    }

    static class MockClusterInvoker<T> extends AbstractClusterInvoker<T> {
        private Invoker<T> selectedInvoker;

        public MockClusterInvoker(Directory<T> directory) {
            super(directory);
        }

        public MockClusterInvoker(Directory<T> directory, URL url) {
            super(directory, url);
        }

        @Override
        protected Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
            Invoker<T> invoker = select(loadbalance, invocation, invokers, null);
            selectedInvoker = invoker;
            return null;
        }

        public Invoker<T> getSelectedInvoker() {
            return selectedInvoker;
        }
    }
}

