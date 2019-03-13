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


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Service;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class BeanLifecycleTest extends ContextTestSupport {
    private BeanLifecycleTest.MyBean statefulInstance;

    private BeanLifecycleTest.MyBean statefulInstanceInRegistry;

    private BeanLifecycleTest.MyBean statefulInstanceInRegistryNoCache;

    @Test
    public void testBeanLifecycle() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        Assert.assertEquals("started", statefulInstance.getStatus());
        Assert.assertEquals("started", statefulInstanceInRegistry.getStatus());
        Assert.assertNull(statefulInstanceInRegistryNoCache.getStatus());
        Assert.assertEquals(2, BeanLifecycleTest.MyStatefulBean.INSTANCES.get());
        template.sendBody("direct:foo", null);
        mock.assertIsSatisfied();
    }

    public static class MyBean implements Service {
        private String status;

        public String getStatus() {
            return status;
        }

        public void doSomething(Exchange exchange) {
            // noop
        }

        @Override
        public void start() throws Exception {
            status = "started";
        }

        @Override
        public void stop() throws Exception {
            status = "stopped";
        }
    }

    public static class MyStatelessBean implements Service {
        public void doSomething(Exchange exchange) {
            // noop
        }

        @Override
        public void start() throws Exception {
            Assert.fail("Should not be invoked");
        }

        @Override
        public void stop() throws Exception {
            Assert.fail("Should not be invoked");
        }
    }

    public static class MyStatefulBean implements Service {
        private static final AtomicInteger INSTANCES = new AtomicInteger(0);

        public MyStatefulBean() {
            BeanLifecycleTest.MyStatefulBean.INSTANCES.incrementAndGet();
        }

        public void doSomething(Exchange exchange) {
            // noop
        }

        @Override
        public void start() throws Exception {
        }

        @Override
        public void stop() throws Exception {
        }
    }
}

