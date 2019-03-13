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


import Exchange.EXCEPTION_CAUGHT;
import Exchange.REDELIVERED;
import Exchange.REDELIVERY_COUNTER;
import Exchange.REDELIVERY_MAX_COUNTER;
import ExchangePattern.InOnly;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class DeadLetterChannelTest extends ContextTestSupport {
    protected Endpoint startEndpoint;

    protected MockEndpoint deadEndpoint;

    protected MockEndpoint successEndpoint;

    protected int failUntilAttempt = 2;

    protected String body = "<hello>world!</hello>";

    @Test
    public void testFirstFewAttemptsFail() throws Exception {
        successEndpoint.expectedBodiesReceived(body);
        successEndpoint.message(0).header(REDELIVERED).isEqualTo(true);
        successEndpoint.message(0).header(REDELIVERY_COUNTER).isEqualTo(1);
        successEndpoint.message(0).header(REDELIVERY_MAX_COUNTER).isEqualTo(2);
        deadEndpoint.expectedMessageCount(0);
        sendBody("direct:start", body);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testLotsOfAttemptsFail() throws Exception {
        failUntilAttempt = 5;
        deadEndpoint.expectedBodiesReceived(body);
        // no traces of redelivery as the dead letter channel will handle the exception when moving the DLQ
        deadEndpoint.message(0).header(REDELIVERED).isNull();
        deadEndpoint.message(0).header(REDELIVERY_COUNTER).isNull();
        deadEndpoint.message(0).header(REDELIVERY_MAX_COUNTER).isNull();
        successEndpoint.expectedMessageCount(0);
        sendBody("direct:start", body);
        assertMockEndpointsSatisfied();
        Throwable t = deadEndpoint.getExchanges().get(0).getProperty(EXCEPTION_CAUGHT, Throwable.class);
        Assert.assertNotNull("Should have been a cause property", t);
        Assert.assertTrue((t instanceof RuntimeException));
        Assert.assertEquals("Failed to process due to attempt: 3 being less than: 5", t.getMessage());
        // must be InOnly
        Exchange dead = deadEndpoint.getReceivedExchanges().get(0);
        Assert.assertEquals(InOnly, dead.getPattern());
    }

    @Test
    public void testLotsOfAttemptsFailInOut() throws Exception {
        failUntilAttempt = 5;
        deadEndpoint.expectedBodiesReceived(body);
        // no traces of redelivery as the dead letter channel will handle the exception when moving the DLQ
        deadEndpoint.message(0).header(REDELIVERED).isNull();
        deadEndpoint.message(0).header(REDELIVERY_COUNTER).isNull();
        deadEndpoint.message(0).header(REDELIVERY_MAX_COUNTER).isNull();
        successEndpoint.expectedMessageCount(0);
        template.requestBody("direct:start", body);
        assertMockEndpointsSatisfied();
        Throwable t = deadEndpoint.getExchanges().get(0).getProperty(EXCEPTION_CAUGHT, Throwable.class);
        Assert.assertNotNull("Should have been a cause property", t);
        Assert.assertTrue((t instanceof RuntimeException));
        Assert.assertEquals("Failed to process due to attempt: 3 being less than: 5", t.getMessage());
        // must be InOnly
        Exchange dead = deadEndpoint.getReceivedExchanges().get(0);
        Assert.assertEquals(InOnly, dead.getPattern());
    }
}

