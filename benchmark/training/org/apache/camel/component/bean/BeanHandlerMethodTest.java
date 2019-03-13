/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.bean;


import org.apache.camel.Body;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.junit.Assert;
import org.junit.Test;


public class BeanHandlerMethodTest extends ContextTestSupport {
    @Test
    public void testInterfaceBeanMethod() throws Exception {
        BeanInfo info = new BeanInfo(context, BeanHandlerMethodTest.MyConcreteBean.class);
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        BeanHandlerMethodTest.MyConcreteBean pojo = new BeanHandlerMethodTest.MyConcreteBean();
        MethodInvocation mi = info.createInvocation(pojo, exchange);
        Assert.assertNotNull(mi);
        Assert.assertEquals("hello", mi.getMethod().getName());
    }

    @Test
    public void testNoHandleMethod() throws Exception {
        BeanInfo info = new BeanInfo(context, BeanHandlerMethodTest.MyNoDummyBean.class);
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        BeanHandlerMethodTest.MyNoDummyBean pojo = new BeanHandlerMethodTest.MyNoDummyBean();
        MethodInvocation mi = info.createInvocation(pojo, exchange);
        Assert.assertNotNull(mi);
        Assert.assertEquals("hello", mi.getMethod().getName());
    }

    @Test
    public void testAmbigiousMethod() throws Exception {
        BeanInfo info = new BeanInfo(context, BeanHandlerMethodTest.MyAmbigiousBean.class);
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        BeanHandlerMethodTest.MyAmbigiousBean pojo = new BeanHandlerMethodTest.MyAmbigiousBean();
        try {
            info.createInvocation(pojo, exchange);
            Assert.fail("Should have thrown an exception");
        } catch (AmbiguousMethodCallException e) {
            Assert.assertEquals(2, e.getMethods().size());
        }
    }

    @Test
    public void testHandleMethod() throws Exception {
        BeanInfo info = new BeanInfo(context, BeanHandlerMethodTest.MyDummyBean.class);
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        BeanHandlerMethodTest.MyDummyBean pojo = new BeanHandlerMethodTest.MyDummyBean();
        MethodInvocation mi = info.createInvocation(pojo, exchange);
        Assert.assertNotNull(mi);
        Assert.assertEquals("hello", mi.getMethod().getName());
    }

    @Test
    public void testHandleAndBodyMethod() throws Exception {
        BeanInfo info = new BeanInfo(context, BeanHandlerMethodTest.MyOtherDummyBean.class);
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        BeanHandlerMethodTest.MyOtherDummyBean pojo = new BeanHandlerMethodTest.MyOtherDummyBean();
        MethodInvocation mi = info.createInvocation(pojo, exchange);
        Assert.assertNotNull(mi);
        Assert.assertEquals("hello", mi.getMethod().getName());
    }

    @Test
    public void testHandleAmbigious() throws Exception {
        BeanInfo info = new BeanInfo(context, BeanHandlerMethodTest.MyReallyDummyBean.class);
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        BeanHandlerMethodTest.MyReallyDummyBean pojo = new BeanHandlerMethodTest.MyReallyDummyBean();
        try {
            info.createInvocation(pojo, exchange);
            Assert.fail("Should throw exception");
        } catch (AmbiguousMethodCallException e) {
            Assert.assertEquals(2, e.getMethods().size());
        }
    }

    @Test
    public void testNoHandlerAmbigious() throws Exception {
        BeanInfo info = new BeanInfo(context, BeanHandlerMethodTest.MyNoHandlerBean.class);
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        BeanHandlerMethodTest.MyNoHandlerBean pojo = new BeanHandlerMethodTest.MyNoHandlerBean();
        try {
            info.createInvocation(pojo, exchange);
            Assert.fail("Should throw exception");
        } catch (AmbiguousMethodCallException e) {
            Assert.assertEquals(3, e.getMethods().size());
        }
    }

    public interface MyBaseInterface {
        @Handler
        String hello(@Body
        String hi);
    }

    public abstract static class MyAbstractBean implements BeanHandlerMethodTest.MyBaseInterface {
        public String hello(@Body
        String hi) {
            return "Hello " + hi;
        }

        public String doCompute(String input) {
            Assert.fail("Should not invoke me");
            return null;
        }
    }

    public static class MyConcreteBean extends BeanHandlerMethodTest.MyAbstractBean {}

    public static class MyNoDummyBean {
        public String hello(@Body
        String hi) {
            return "Hello " + hi;
        }

        public String doCompute(String input) {
            Assert.fail("Should not invoke me");
            return null;
        }
    }

    public static class MyAmbigiousBean {
        public String hello(String hi) {
            Assert.fail("Should not invoke me");
            return "Hello " + hi;
        }

        public String doCompute(String input) {
            Assert.fail("Should not invoke me");
            return null;
        }
    }

    public static class MyDummyBean {
        @Handler
        public String hello(String hi) {
            return "Hello " + hi;
        }

        public String doCompute(String input) {
            Assert.fail("Should not invoke me");
            return null;
        }
    }

    public static class MyOtherDummyBean {
        @Handler
        public String hello(String hi) {
            return "Hello " + hi;
        }

        public String bye(@Body
        String input) {
            Assert.fail("Should not invoke me");
            return null;
        }
    }

    public static class MyNoHandlerBean {
        public String hello(@Body
        String input, @Header("name")
        String name, @Header("age")
        int age) {
            Assert.fail("Should not invoke me");
            return null;
        }

        public String greeting(@Body
        String input, @Header("name")
        String name) {
            Assert.fail("Should not invoke me");
            return null;
        }

        public String bye(String input) {
            Assert.fail("Should not invoke me");
            return null;
        }
    }

    public static class MyReallyDummyBean {
        @Handler
        public String hello(String hi) {
            return "Hello " + hi;
        }

        @Handler
        public String bye(@Body
        String input) {
            Assert.fail("Should not invoke me");
            return null;
        }
    }
}

