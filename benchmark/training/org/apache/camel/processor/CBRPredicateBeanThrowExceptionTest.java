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
package org.apache.camel.processor;


import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Assert;
import org.junit.Test;


public class CBRPredicateBeanThrowExceptionTest extends ContextTestSupport {
    private static AtomicBoolean check = new AtomicBoolean();

    private static AtomicBoolean check2 = new AtomicBoolean();

    @Test
    public void testCBR() throws Exception {
        CBRPredicateBeanThrowExceptionTest.check.set(false);
        CBRPredicateBeanThrowExceptionTest.check2.set(false);
        getMockEndpoint("mock:dead").expectedMessageCount(0);
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello Foo");
        getMockEndpoint("mock:bar").expectedBodiesReceived("Hello Bar");
        template.sendBodyAndHeader("direct:start", "Hello Foo", "foo", "bar");
        template.sendBodyAndHeader("direct:start", "Hello Bar", "foo", "other");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(CBRPredicateBeanThrowExceptionTest.check.get());
        Assert.assertTrue(CBRPredicateBeanThrowExceptionTest.check2.get());
    }

    @Test
    public void testCBRKaboom() throws Exception {
        CBRPredicateBeanThrowExceptionTest.check.set(false);
        CBRPredicateBeanThrowExceptionTest.check2.set(false);
        getMockEndpoint("mock:foo").expectedMessageCount(0);
        getMockEndpoint("mock:foo2").expectedMessageCount(0);
        getMockEndpoint("mock:bar").expectedMessageCount(0);
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        template.sendBodyAndHeader("direct:start", "Hello Foo", "foo", "Kaboom");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(CBRPredicateBeanThrowExceptionTest.check.get());
        Assert.assertFalse(CBRPredicateBeanThrowExceptionTest.check2.get());
    }

    public static class MyCBRBean {
        public boolean checkHeader(Exchange exchange) {
            CBRPredicateBeanThrowExceptionTest.check.set(true);
            Message inMsg = exchange.getIn();
            String foo = ((String) (inMsg.getHeader("foo")));
            if ("Kaboom".equalsIgnoreCase(foo)) {
                throw new IllegalArgumentException("Forced");
            }
            return foo.equals("bar");
        }

        public boolean checkHeader2(Exchange exchange) {
            CBRPredicateBeanThrowExceptionTest.check2.set(true);
            Message inMsg = exchange.getIn();
            String foo = ((String) (inMsg.getHeader("foo")));
            if ("Kaboom".equalsIgnoreCase(foo)) {
                throw new IllegalArgumentException("Forced");
            }
            return foo.equals("bar");
        }
    }
}

