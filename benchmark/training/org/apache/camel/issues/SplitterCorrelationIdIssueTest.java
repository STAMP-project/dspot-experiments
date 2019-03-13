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


import Exchange.CORRELATION_ID;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class SplitterCorrelationIdIssueTest extends ContextTestSupport {
    @Test
    public void testSplitCorrelationId() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:split");
        mock.expectedMessageCount(3);
        Exchange exchange = template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("A,B,C");
            }
        });
        assertMockEndpointsSatisfied();
        // match that all exchange id is unique
        String parent = exchange.getExchangeId();
        String split1 = mock.getReceivedExchanges().get(0).getExchangeId();
        String split2 = mock.getReceivedExchanges().get(1).getExchangeId();
        String split3 = mock.getReceivedExchanges().get(2).getExchangeId();
        Assert.assertNotSame(parent, split1);
        Assert.assertNotSame(parent, split2);
        Assert.assertNotSame(parent, split3);
        Assert.assertNotSame(split1, split2);
        Assert.assertNotSame(split2, split3);
        Assert.assertNotSame(split3, split1);
        // match correlation id from split -> parent
        String corr1 = mock.getReceivedExchanges().get(0).getProperty(CORRELATION_ID, String.class);
        String corr2 = mock.getReceivedExchanges().get(1).getProperty(CORRELATION_ID, String.class);
        String corr3 = mock.getReceivedExchanges().get(2).getProperty(CORRELATION_ID, String.class);
        Assert.assertEquals(parent, corr1);
        Assert.assertEquals(parent, corr2);
        Assert.assertEquals(parent, corr3);
    }
}

