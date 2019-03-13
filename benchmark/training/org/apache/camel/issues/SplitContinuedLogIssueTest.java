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
package org.apache.camel.issues;


import org.apache.camel.AggregationStrategy;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class SplitContinuedLogIssueTest extends ContextTestSupport {
    @Test
    public void testFooBar() throws Exception {
        getMockEndpoint("mock:error").expectedBodiesReceived("bar");
        getMockEndpoint("mock:line").expectedBodiesReceived("foo", "bar");
        getMockEndpoint("mock:result").expectedBodiesReceived("foo=bar");
        String out = template.requestBody("direct:start", "foo,bar", String.class);
        Assert.assertEquals("foo=bar", out);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBarFoo() throws Exception {
        getMockEndpoint("mock:error").expectedBodiesReceived("bar");
        getMockEndpoint("mock:line").expectedBodiesReceived("bar", "foo");
        getMockEndpoint("mock:result").expectedBodiesReceived("bar=foo");
        String out = template.requestBody("direct:start", "bar,foo", String.class);
        Assert.assertEquals("bar=foo", out);
        assertMockEndpointsSatisfied();
    }

    private class SplitAggregationStrategy implements AggregationStrategy {
        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null) {
                return newExchange;
            }
            String s1 = oldExchange.getIn().getBody(String.class);
            String s2 = newExchange.getIn().getBody(String.class);
            String body = (s1 + "=") + s2;
            oldExchange.getIn().setBody(body);
            return oldExchange;
        }
    }
}

