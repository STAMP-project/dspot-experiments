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


import org.apache.camel.AggregationStrategy;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class MulticastParallelAllTimeoutAwareTest extends ContextTestSupport {
    private volatile Exchange receivedExchange;

    private volatile int receivedIndex;

    private volatile int receivedTotal;

    private volatile long receivedTimeout;

    @Test
    public void testMulticastParallelAllTimeoutAware() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        // ABC will timeout so we only get our canned response
        mock.expectedBodiesReceived("AllTimeout");
        template.sendBody("direct:start", "Hello");
        assertMockEndpointsSatisfied();
        Assert.assertNotNull(receivedExchange);
        // Just make sure the MyAggregationStrategy is called for all the exchange
        Assert.assertEquals(2, receivedIndex);
        Assert.assertEquals(3, receivedTotal);
        Assert.assertEquals(500, receivedTimeout);
    }

    private class MyAggregationStrategy implements AggregationStrategy {
        public void timeout(Exchange oldExchange, int index, int total, long timeout) {
            // we can't assert on the expected values here as the contract of this method doesn't
            // allow to throw any Throwable (including AssertionError) so that we assert
            // about the expected values directly inside the test method itself. other than that
            // asserting inside a thread other than the main thread dosen't make much sense as
            // junit would not realize the failed assertion!
            receivedExchange = oldExchange;
            receivedIndex = index;
            receivedTotal = total;
            receivedTimeout = timeout;
            oldExchange.getIn().setBody("AllTimeout");
        }

        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            // noop
            return oldExchange;
        }
    }
}

