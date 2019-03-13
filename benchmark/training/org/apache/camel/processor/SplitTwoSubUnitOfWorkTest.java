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


import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class SplitTwoSubUnitOfWorkTest extends ContextTestSupport {
    private static int counter;

    @Test
    public void testOK() throws Exception {
        SplitTwoSubUnitOfWorkTest.counter = 0;
        getMockEndpoint("mock:dead").expectedMessageCount(0);
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedBodiesReceived("Tiger", "Camel");
        getMockEndpoint("mock:c").expectedBodiesReceived("Elephant", "Lion");
        getMockEndpoint("mock:result").expectedMessageCount(1);
        getMockEndpoint("mock:line").expectedBodiesReceived("Tiger", "Camel", "Elephant", "Lion");
        SplitTwoSubUnitOfWorkTest.MyBody body = new SplitTwoSubUnitOfWorkTest.MyBody("Tiger,Camel", "Elephant,Lion");
        template.sendBody("direct:start", body);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testError() throws Exception {
        SplitTwoSubUnitOfWorkTest.counter = 0;
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        getMockEndpoint("mock:dead").message(0).body().isInstanceOf(SplitTwoSubUnitOfWorkTest.MyBody.class);
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedBodiesReceived("Tiger", "Camel");
        getMockEndpoint("mock:c").expectedBodiesReceived("Elephant", "Donkey");
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:line").expectedBodiesReceived("Tiger", "Camel", "Elephant");
        SplitTwoSubUnitOfWorkTest.MyBody body = new SplitTwoSubUnitOfWorkTest.MyBody("Tiger,Camel", "Elephant,Donkey");
        template.sendBody("direct:start", body);
        assertMockEndpointsSatisfied();
        Assert.assertEquals(4, SplitTwoSubUnitOfWorkTest.counter);// 1 first + 3 redeliveries

        SplitTwoSubUnitOfWorkTest.MyBody dead = getMockEndpoint("mock:dead").getReceivedExchanges().get(0).getIn().getBody(SplitTwoSubUnitOfWorkTest.MyBody.class);
        Assert.assertSame("Should be original message in DLC", body, dead);
    }

    public static final class MyBody {
        private String foo;

        private String bar;

        private MyBody(String foo, String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }
    }
}

