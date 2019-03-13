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
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.SynchronizationAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


public class MDCOnCompletionOnCompletionTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(MDCOnCompletionOnCompletionTest.class);

    @Test
    public void testMDC() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        assertMockEndpointsSatisfied();
    }

    private class MyOnCompletion extends SynchronizationAdapter {
        @Override
        public void onDone(Exchange exchange) {
            Assert.assertEquals("route-a", MDC.get("camel.routeId"));
            Assert.assertEquals(exchange.getExchangeId(), MDC.get("camel.exchangeId"));
            Assert.assertEquals(exchange.getIn().getMessageId(), MDC.get("camel.messageId"));
            Assert.assertEquals("1", MDC.get("custom.id"));
            MDCOnCompletionOnCompletionTest.LOG.info("From onCompletion after route-a");
        }
    }
}

