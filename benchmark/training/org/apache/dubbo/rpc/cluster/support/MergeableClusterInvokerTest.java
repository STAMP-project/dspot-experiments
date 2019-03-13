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


import Constants.GROUP_KEY;
import Constants.MERGER_KEY;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcResult;
import org.apache.dubbo.rpc.cluster.Directory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


public class MergeableClusterInvokerTest {
    private Directory directory = Mockito.mock(Directory.class);

    private Invoker firstInvoker = Mockito.mock(Invoker.class);

    private Invoker secondInvoker = Mockito.mock(Invoker.class);

    private Invocation invocation = Mockito.mock(Invocation.class);

    private MergeableClusterInvoker<MenuService> mergeableClusterInvoker;

    private String[] list1 = new String[]{ "10", "11", "12" };

    private String[] list2 = new String[]{ "20", "21", "22" };

    private String[] list3 = new String[]{ "23", "24", "25" };

    private String[] list4 = new String[]{ "30", "31", "32" };

    private Map<String, List<String>> firstMenuMap = new HashMap<String, List<String>>() {
        {
            put("1", Arrays.asList(list1));
            put("2", Arrays.asList(list2));
        }
    };

    private Map<String, List<String>> secondMenuMap = new HashMap<String, List<String>>() {
        {
            put("2", Arrays.asList(list3));
            put("3", Arrays.asList(list4));
        }
    };

    private Menu firstMenu = new Menu(firstMenuMap);

    private Menu secondMenu = new Menu(secondMenuMap);

    private URL url = URL.valueOf(("test://test/" + (MenuService.class.getName())));

    @Test
    public void testGetMenuSuccessfully() throws Exception {
        // setup
        url = url.addParameter(MERGER_KEY, ".merge");
        BDDMockito.given(invocation.getMethodName()).willReturn("getMenu");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{  });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{  });
        BDDMockito.given(invocation.getAttachments()).willReturn(new HashMap<String, String>());
        BDDMockito.given(invocation.getInvoker()).willReturn(firstInvoker);
        firstInvoker = ((Invoker) (Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ Invoker.class }, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("getUrl".equals(method.getName())) {
                    return url.addParameter(GROUP_KEY, "first");
                }
                if ("getInterface".equals(method.getName())) {
                    return MenuService.class;
                }
                if ("invoke".equals(method.getName())) {
                    return new RpcResult(firstMenu);
                }
                return null;
            }
        })));
        secondInvoker = ((Invoker) (Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ Invoker.class }, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("getUrl".equals(method.getName())) {
                    return url.addParameter(GROUP_KEY, "second");
                }
                if ("getInterface".equals(method.getName())) {
                    return MenuService.class;
                }
                if ("invoke".equals(method.getName())) {
                    return new RpcResult(secondMenu);
                }
                return null;
            }
        })));
        BDDMockito.given(directory.list(invocation)).willReturn(new ArrayList() {
            {
                add(firstInvoker);
                add(secondInvoker);
            }
        });
        BDDMockito.given(directory.getUrl()).willReturn(url);
        BDDMockito.given(directory.getInterface()).willReturn(MenuService.class);
        mergeableClusterInvoker = new MergeableClusterInvoker<MenuService>(directory);
        // invoke
        Result result = mergeableClusterInvoker.invoke(invocation);
        Assertions.assertTrue(((result.getValue()) instanceof Menu));
        Menu menu = ((Menu) (result.getValue()));
        Map<String, List<String>> expected = new HashMap<String, List<String>>();
        MergeableClusterInvokerTest.merge(expected, firstMenuMap);
        MergeableClusterInvokerTest.merge(expected, secondMenuMap);
        Assertions.assertEquals(expected.keySet(), menu.getMenus().keySet());
        for (Map.Entry<String, List<String>> entry : expected.entrySet()) {
            // FIXME: cannot guarantee the sequence of the merge result, check implementation in
            // MergeableClusterInvoker#invoke
            List<String> values1 = new ArrayList<String>(entry.getValue());
            List<String> values2 = new ArrayList<String>(menu.getMenus().get(entry.getKey()));
            Collections.sort(values1);
            Collections.sort(values2);
            Assertions.assertEquals(values1, values2);
        }
    }

    @Test
    public void testAddMenu() throws Exception {
        String menu = "first";
        List<String> menuItems = new ArrayList<String>() {
            {
                add("1");
                add("2");
            }
        };
        BDDMockito.given(invocation.getMethodName()).willReturn("addMenu");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ String.class, List.class });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ menu, menuItems });
        BDDMockito.given(invocation.getAttachments()).willReturn(new HashMap<String, String>());
        BDDMockito.given(invocation.getInvoker()).willReturn(firstInvoker);
        BDDMockito.given(firstInvoker.getUrl()).willReturn(url.addParameter(GROUP_KEY, "first"));
        BDDMockito.given(firstInvoker.getInterface()).willReturn(MenuService.class);
        BDDMockito.given(firstInvoker.invoke(invocation)).willReturn(new RpcResult());
        BDDMockito.given(firstInvoker.isAvailable()).willReturn(true);
        BDDMockito.given(secondInvoker.getUrl()).willReturn(url.addParameter(GROUP_KEY, "second"));
        BDDMockito.given(secondInvoker.getInterface()).willReturn(MenuService.class);
        BDDMockito.given(secondInvoker.invoke(invocation)).willReturn(new RpcResult());
        BDDMockito.given(secondInvoker.isAvailable()).willReturn(true);
        BDDMockito.given(directory.list(invocation)).willReturn(new ArrayList() {
            {
                add(firstInvoker);
                add(secondInvoker);
            }
        });
        BDDMockito.given(directory.getUrl()).willReturn(url);
        BDDMockito.given(directory.getInterface()).willReturn(MenuService.class);
        mergeableClusterInvoker = new MergeableClusterInvoker<MenuService>(directory);
        Result result = mergeableClusterInvoker.invoke(invocation);
        Assertions.assertNull(result.getValue());
    }
}

