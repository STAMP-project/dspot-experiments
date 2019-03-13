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


import Exchange.REDELIVERED;
import Exchange.REDELIVERY_COUNTER;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RollbackExchangeException;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class RollbackTest extends ContextTestSupport {
    @Test
    public void testOk() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        getMockEndpoint("mock:rollback").expectedMessageCount(0);
        template.requestBody("direct:start", "ok");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRollback() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:rollback").expectedMessageCount(1);
        try {
            template.requestBody("direct:start", "bad");
            Assert.fail("Should have thrown a RollbackExchangeException");
        } catch (RuntimeCamelException e) {
            Assert.assertTrue(((e.getCause()) instanceof RollbackExchangeException));
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRollbackWithExchange() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:rollback").expectedMessageCount(1);
        Exchange out = template.request("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("bad");
            }
        });
        assertMockEndpointsSatisfied();
        Assert.assertNotNull(out.getException());
        TestSupport.assertIsInstanceOf(RollbackExchangeException.class, out.getException());
        Assert.assertEquals("Should be marked as rollback", true, out.isRollbackOnly());
        // should not try to redeliver if exchange was marked as rollback only
        Assert.assertEquals(0, out.getIn().getHeader(REDELIVERY_COUNTER));
        Assert.assertEquals(false, out.getIn().getHeader(REDELIVERED));
    }
}

