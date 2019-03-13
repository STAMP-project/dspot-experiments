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
package org.apache.camel.component.file.remote;


import Exchange.EXCEPTION_CAUGHT;
import Exchange.REDELIVERY_EXHAUSTED;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.junit.Test;


public class FtpPollEnrichBridgeErrorHandlerTest extends BaseServerTestSupport {
    // we want to poll enrich from FTP and therefore want to fail fast if something is wrong
    // and then bridge that error to the Camel routing error handler
    // so we need to turn of reconnection attempts
    // and turn of auto create as that will pre-login to check if the directory exists
    // and in case of connection error then throw that as an exception
    private String uri = (("ftp://admin@localhost:" + (getPort())) + "/unknown/?password=admin") + "&maximumReconnectAttempts=0&autoCreate=false&throwExceptionOnConnectFailed=true&consumer.bridgeErrorHandler=true";

    @Test
    public void testPollEnrich() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        template.sendBody("seda:start", "Hello World");
        assertMockEndpointsSatisfied();
        Exchange out = getMockEndpoint("mock:dead").getExchanges().get(0);
        assertNotNull(out);
        Exception caught = out.getProperty(EXCEPTION_CAUGHT, Exception.class);
        assertNotNull("Should store caught exception", caught);
    }

    private class MyAggregationStrategy implements AggregationStrategy {
        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (newExchange != null) {
                copyResultsPreservePattern(oldExchange, newExchange);
            } else {
                // if no newExchange then there was no message from the external resource
                // and therefore we should set an empty body to indicate this fact
                // but keep headers/attachments as we want to propagate those
                oldExchange.getIn().setBody(null);
                oldExchange.setOut(null);
            }
            // in case of exception we are bridged then we want to perform redeliveries etc.
            // so we need to turn of exhausted redelivery
            oldExchange.removeProperties(REDELIVERY_EXHAUSTED);
            return oldExchange;
        }
    }
}

