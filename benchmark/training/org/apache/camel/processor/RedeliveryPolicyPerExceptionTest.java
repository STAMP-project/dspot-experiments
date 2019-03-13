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
import Exchange.REDELIVERY_MAX_COUNTER;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class RedeliveryPolicyPerExceptionTest extends ContextTestSupport {
    protected MockEndpoint a;

    protected MockEndpoint b;

    @Test
    public void testUsingCustomExceptionHandlerAndOneRedelivery() throws Exception {
        a.expectedMessageCount(1);
        sendBody("direct:start", "a");
        MockEndpoint.assertIsSatisfied(a, b);
        List<Exchange> list = a.getReceivedExchanges();
        Assert.assertTrue("List should not be empty!", (!(list.isEmpty())));
        Exchange exchange = list.get(0);
        Message in = exchange.getIn();
        log.info(("Found message with headers: " + (in.getHeaders())));
        TestSupport.assertMessageHeader(in, REDELIVERY_COUNTER, 2);
        TestSupport.assertMessageHeader(in, REDELIVERY_MAX_COUNTER, 2);
        TestSupport.assertMessageHeader(in, REDELIVERED, true);
    }

    @Test
    public void testUsingCustomExceptionHandlerWithNoRedeliveries() throws Exception {
        b.expectedMessageCount(1);
        sendBody("direct:start", "b");
        MockEndpoint.assertIsSatisfied(a, b);
        List<Exchange> list = b.getReceivedExchanges();
        Assert.assertTrue("List should not be empty!", (!(list.isEmpty())));
        Exchange exchange = list.get(0);
        Message in = exchange.getIn();
        log.info(("Found message with headers: " + (in.getHeaders())));
        TestSupport.assertMessageHeader(in, REDELIVERY_COUNTER, 0);
        TestSupport.assertMessageHeader(in, REDELIVERY_MAX_COUNTER, null);
        TestSupport.assertMessageHeader(in, REDELIVERED, false);
    }
}

