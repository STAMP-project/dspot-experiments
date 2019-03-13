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


import org.apache.camel.ContextTestSupport;
import org.apache.camel.support.DefaultExchange;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for overridden methods in an inheritance.
 */
public class BeanInfoInheritanceTest extends ContextTestSupport {
    @Test
    public void testInheritance() throws Exception {
        BeanInfo beanInfo = new BeanInfo(context, BeanInfoInheritanceTest.Y.class);
        DefaultExchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new BeanInfoInheritanceTest.Request());
        try {
            MethodInvocation mi = beanInfo.createInvocation(null, exchange);
            Assert.assertNotNull(mi);
            Assert.assertEquals("process", mi.getMethod().getName());
            Assert.assertEquals("Y", mi.getMethod().getDeclaringClass().getSimpleName());
        } catch (AmbiguousMethodCallException e) {
            Assert.fail("This should not be ambiguous!");
        }
    }

    @Test
    public void testNoInheritance() throws Exception {
        BeanInfo beanInfo = new BeanInfo(context, BeanInfoInheritanceTest.A.class);
        DefaultExchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new BeanInfoInheritanceTest.Request());
        try {
            MethodInvocation mi = beanInfo.createInvocation(null, exchange);
            Assert.assertNotNull(mi);
            Assert.assertEquals("process", mi.getMethod().getName());
            Assert.assertEquals("A", mi.getMethod().getDeclaringClass().getSimpleName());
        } catch (AmbiguousMethodCallException e) {
            Assert.fail("This should not be ambiguous!");
        }
    }

    @Test
    public void testInheritanceAndOverload() throws Exception {
        BeanInfo beanInfo = new BeanInfo(context, BeanInfoInheritanceTest.Z.class);
        DefaultExchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new BeanInfoInheritanceTest.Request());
        try {
            beanInfo.createInvocation(null, exchange);
            Assert.fail("This should be ambiguous!");
        } catch (AmbiguousMethodCallException e) {
            // expected (currently not supported in camel)
        }
    }

    public static class Request {
        int x;
    }

    public static class X {
        public int process(BeanInfoInheritanceTest.Request request) {
            return 0;
        }
    }

    public static class Y extends BeanInfoInheritanceTest.X {
        public int process(BeanInfoInheritanceTest.Request request) {
            return 1;
        }

        public int compute(String body) {
            return 2;
        }
    }

    public static class Z extends BeanInfoInheritanceTest.Y {
        public int compute(BeanInfoInheritanceTest.Request request) {
            return 2;
        }

        public int process(BeanInfoInheritanceTest.Request request, String body) {
            return 3;
        }
    }

    public static class A {
        public void doSomething(String body) {
            // noop
        }

        public int process(BeanInfoInheritanceTest.Request request) {
            return 0;
        }
    }
}

