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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// END SNIPPET: e3
public class SplitAggregateInOutTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(SplitAggregateInOutTest.class);

    private String expectedBody = "Response[(id=1,item=A);(id=2,item=B);(id=3,item=C)]";

    @Test
    public void testSplitAndAggregateInOut() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived(expectedBody);
        // use requestBody as its InOut
        Object out = template.requestBody("direct:start", "A@B@C");
        Assert.assertEquals(expectedBody, out);
        SplitAggregateInOutTest.LOG.debug(("Response to caller: " + out));
        assertMockEndpointsSatisfied();
    }

    // START SNIPPET: e2
    public static class MyOrderService {
        private static int counter;

        /**
         * We just handle the order by returning a id line for the order
         */
        public String handleOrder(String line) {
            SplitAggregateInOutTest.LOG.debug(("HandleOrder: " + line));
            return ((("(id=" + (++(SplitAggregateInOutTest.MyOrderService.counter))) + ",item=") + line) + ")";
        }

        /**
         * We use the same bean for building the combined response to send
         * back to the original caller
         */
        public String buildCombinedResponse(String line) {
            SplitAggregateInOutTest.LOG.debug(("BuildCombinedResponse: " + line));
            return ("Response[" + line) + "]";
        }
    }

    // END SNIPPET: e2
    // START SNIPPET: e3
    /**
     * This is our own order aggregation strategy where we can control
     * how each splitted message should be combined. As we do not want to
     * loos any message we copy from the new to the old to preserve the
     * order lines as long we process them
     */
    public static class MyOrderStrategy implements AggregationStrategy {
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            // put order together in old exchange by adding the order from new exchange
            if (oldExchange == null) {
                // the first time we aggregate we only have the new exchange,
                // so we just return it
                return newExchange;
            }
            String orders = oldExchange.getIn().getBody(String.class);
            String newLine = newExchange.getIn().getBody(String.class);
            SplitAggregateInOutTest.LOG.debug(("Aggregate old orders: " + orders));
            SplitAggregateInOutTest.LOG.debug(("Aggregate new order: " + newLine));
            // put orders together separating by semi colon
            orders = (orders + ";") + newLine;
            // put combined order back on old to preserve it
            oldExchange.getIn().setBody(orders);
            // return old as this is the one that has all the orders gathered until now
            return oldExchange;
        }
    }
}

