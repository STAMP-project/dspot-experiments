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
 * Unit test for bridged methods.
 */
public class BeanInfoWithBridgedMethodTest extends ContextTestSupport {
    @Test
    public void testBridgedMethod() throws Exception {
        BeanInfo beanInfo = new BeanInfo(context, BeanInfoWithBridgedMethodTest.MyService.class);
        DefaultExchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new BeanInfoWithBridgedMethodTest.Request(1));
        try {
            BeanInfoWithBridgedMethodTest.MyService myService = new BeanInfoWithBridgedMethodTest.MyService();
            MethodInvocation mi = beanInfo.createInvocation(null, exchange);
            Assert.assertEquals("MyService", mi.getMethod().getDeclaringClass().getSimpleName());
            Assert.assertEquals(2, mi.getMethod().invoke(myService, new BeanInfoWithBridgedMethodTest.Request(1)));
        } catch (AmbiguousMethodCallException e) {
            Assert.fail("This should not be ambiguous!");
        }
    }

    @Test
    public void testPackagePrivate() throws Exception {
        BeanInfo beanInfo = new BeanInfo(context, BeanInfoWithBridgedMethodTest.MyPackagePrivateService.class);
        DefaultExchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new BeanInfoWithBridgedMethodTest.Request(1));
        try {
            BeanInfoWithBridgedMethodTest.MyPackagePrivateService myService = new BeanInfoWithBridgedMethodTest.MyPackagePrivateService();
            MethodInvocation mi = beanInfo.createInvocation(null, exchange);
            Assert.assertEquals("Service", mi.getMethod().getDeclaringClass().getSimpleName());
            Assert.assertEquals(4, mi.getMethod().invoke(myService, new BeanInfoWithBridgedMethodTest.Request(2)));
        } catch (AmbiguousMethodCallException e) {
            Assert.fail("This should not be ambiguous!");
        }
    }

    public static class Request {
        int x;

        public Request(int x) {
            this.x = x;
        }
    }

    public interface Service<R> {
        int process(R request);
    }

    public static class MyService implements BeanInfoWithBridgedMethodTest.Service<BeanInfoWithBridgedMethodTest.Request> {
        public int process(BeanInfoWithBridgedMethodTest.Request request) {
            return (request.x) + 1;
        }
    }

    static class MyPackagePrivateService implements BeanInfoWithBridgedMethodTest.Service<BeanInfoWithBridgedMethodTest.Request> {
        public int process(BeanInfoWithBridgedMethodTest.Request request) {
            return (request.x) + 2;
        }
    }
}

