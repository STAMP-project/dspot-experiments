/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.proxy;


import MotanConstants.ASYNC_SUFFIX;
import MotanConstants.NODE_TYPE_REFERER;
import URLParamType.nodeType;
import com.weibo.api.motan.BaseTestCase;
import com.weibo.api.motan.cluster.Cluster;
import com.weibo.api.motan.exception.MotanBizException;
import com.weibo.api.motan.exception.MotanServiceException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.jmock.Expectations;
import org.junit.Test;


public class RefererInvocationHandlerTest extends BaseTestCase {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testInvokeException() throws Throwable {
        final Cluster cluster = BaseTestCase.mockery.mock(Cluster.class);
        final URL u = new URL("motan", "local", 80, "test");
        u.addParameter(nodeType.getName(), NODE_TYPE_REFERER);
        BaseTestCase.mockery.checking(new Expectations() {
            {
                one(cluster).call(with(any(Request.class)));
                will(throwException(new MotanBizException("just test", new StackOverflowError())));
                allowing(cluster).getUrl();
                will(returnValue(u));
            }
        });
        List<Cluster> clus = new ArrayList<Cluster>();
        clus.add(cluster);
        RefererInvocationHandler handler = new RefererInvocationHandler(String.class, clus);
        Method[] methods = String.class.getMethods();
        try {
            handler.invoke(null, methods[1], null);
        } catch (Exception e) {
            TestCase.assertTrue((e instanceof MotanServiceException));
            TestCase.assertTrue(e.getMessage().contains("StackOverflowError"));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testLocalMehtod() throws Exception {
        final Cluster cluster = BaseTestCase.mockery.mock(Cluster.class);
        final URL u = new URL("motan", "local", 80, "test");
        final List<Referer> referers = new ArrayList<Referer>();
        final Referer referer = BaseTestCase.mockery.mock(Referer.class);
        referers.add(referer);
        BaseTestCase.mockery.checking(new Expectations() {
            {
                allowing(cluster).getUrl();
                will(returnValue(u));
                allowing(referer).getUrl();
                will(returnValue(u));
                allowing(referer).isAvailable();
                will(returnValue(true));
                allowing(cluster).getReferers();
                will(returnValue(referers));
            }
        });
        List<Cluster> clusters = new ArrayList<>();
        clusters.add(cluster);
        RefererInvocationHandler handler = new RefererInvocationHandler(RefererInvocationHandlerTest.TestService.class, clusters);
        // local method
        Method method = RefererInvocationHandlerTest.TestServiceImpl.class.getMethod("toString");
        TestCase.assertTrue(handler.isLocalMethod(method));
        try {
            String result = ((String) (handler.invoke(null, method, null)));
            TestCase.assertEquals("{protocol:motan[motan://local:80/test?group=default_rpc, available:true]}", result);
        } catch (Throwable e) {
            TestCase.assertTrue(false);
        }
        method = RefererInvocationHandlerTest.TestServiceImpl.class.getMethod("hashCode");
        TestCase.assertTrue(handler.isLocalMethod(method));
        // remote method
        method = RefererInvocationHandlerTest.TestServiceImpl.class.getMethod("hello");
        TestCase.assertFalse(handler.isLocalMethod(method));
        method = RefererInvocationHandlerTest.TestServiceImpl.class.getMethod("equals", Object.class);
        TestCase.assertFalse(handler.isLocalMethod(method));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testAsync() {
        final Cluster cluster = BaseTestCase.mockery.mock(Cluster.class);
        final URL u = new URL("motan", "local", 80, "test");
        u.addParameter(nodeType.getName(), NODE_TYPE_REFERER);
        final ResponseFuture response = BaseTestCase.mockery.mock(ResponseFuture.class);
        BaseTestCase.mockery.checking(new Expectations() {
            {
                one(cluster).call(with(any(Request.class)));
                will(returnValue(response));
                allowing(cluster).getUrl();
                will(returnValue(u));
                allowing(response).setReturnType(with(any(Class.class)));
            }
        });
        List<Cluster> clus = new ArrayList<Cluster>();
        clus.add(cluster);
        RefererInvocationHandler handler = new RefererInvocationHandler(String.class, clus);
        Method method;
        try {
            method = RefererInvocationHandlerTest.TestService.class.getMethod("helloAsync", new Class<?>[]{  });
            ResponseFuture res = ((ResponseFuture) (handler.invoke(null, method, null)));
            TestCase.assertEquals(response, res);
            TestCase.assertTrue(((Boolean) (RpcContext.getContext().getAttribute(ASYNC_SUFFIX))));
        } catch (Throwable e) {
            e.printStackTrace();
            TestCase.assertTrue(false);
        }
    }

    interface TestService {
        String hello();

        ResponseFuture helloAsync();

        boolean equals(Object o);
    }

    class TestServiceImpl implements RefererInvocationHandlerTest.TestService {
        @Override
        public String hello() {
            return "hello";
        }

        @Override
        public ResponseFuture helloAsync() {
            return null;
        }
    }
}

