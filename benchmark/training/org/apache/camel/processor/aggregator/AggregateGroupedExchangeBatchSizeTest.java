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
package org.apache.camel.processor.aggregator;


import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for aggregate grouped exchanges.
 */
public class AggregateGroupedExchangeBatchSizeTest extends ContextTestSupport {
    @SuppressWarnings("unchecked")
    @Test
    public void testGrouped() throws Exception {
        MockEndpoint result = getMockEndpoint("mock:result");
        // we expect 1 or 2 messages since we group all we get in using the same correlation key
        result.expectedMinimumMessageCount(1);
        // then we sent all the message at once
        template.sendBody("direct:start", "100");
        template.sendBody("direct:start", "150");
        template.sendBody("direct:start", "130");
        template.sendBody("direct:start", "200");
        assertMockEndpointsSatisfied();
        Exchange out = result.getExchanges().get(0);
        List<Exchange> grouped = out.getIn().getBody(List.class);
        Assert.assertTrue(("Should be either 2 or 4, was " + (grouped.size())), (((grouped.size()) == 2) || ((grouped.size()) == 4)));
        Assert.assertEquals("100", grouped.get(0).getIn().getBody(String.class));
        Assert.assertEquals("150", grouped.get(1).getIn().getBody(String.class));
        // wait a bit for the remainder to come in
        Thread.sleep(1000);
        if ((result.getReceivedCounter()) == 2) {
            out = result.getExchanges().get(1);
            grouped = out.getIn().getBody(List.class);
            Assert.assertEquals(2, grouped.size());
            Assert.assertEquals("130", grouped.get(0).getIn().getBody(String.class));
            Assert.assertEquals("200", grouped.get(1).getIn().getBody(String.class));
        }
        // END SNIPPET: e2
    }
}

