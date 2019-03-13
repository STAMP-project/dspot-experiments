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


import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 * CAMEL-6455
 */
public class BeanMethodWithEmptyParameterAndNoMethodWithNoParameterIssueTest extends ContextTestSupport {
    @Test
    public void testBean() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        try {
            template.sendBody("direct:start", "Camel");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            MethodNotFoundException cause = TestSupport.assertIsInstanceOf(MethodNotFoundException.class, e.getCause());
            Assert.assertEquals("doSomething()", cause.getMethodName());
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testOtherBean() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        try {
            template.sendBody("direct:other", "Camel");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            MethodNotFoundException cause = TestSupport.assertIsInstanceOf(MethodNotFoundException.class, e.getCause());
            Assert.assertEquals("doSomething()", cause.getMethodName());
        }
        assertMockEndpointsSatisfied();
    }

    public static final class MyBean {
        public static void doSomething(Exchange exchange) {
            exchange.getIn().setHeader("foo", "bar");
        }
    }

    public static final class MyOtherBean {
        public static void doSomething(Exchange exchange) {
            exchange.getIn().setHeader("foo", "bar");
        }

        public static void doSomething(Exchange exchange, String foo, String bar) {
            exchange.getIn().setHeader(foo, bar);
        }
    }
}

