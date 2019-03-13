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
package org.apache.camel.component.aws.kinesis.integration;


import ExchangePattern.InOnly;
import ExchangePattern.InOut;
import KinesisConstants.PARTITION_KEY;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Must be manually tested.")
public class KinesisComponentIntegrationTest extends CamelTestSupport {
    @EndpointInject(uri = "direct:start")
    private ProducerTemplate template;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;

    @Test
    public void send() throws Exception {
        result.expectedMessageCount(2);
        template.send("direct:start", InOnly, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(PARTITION_KEY, "partition-1");
                exchange.getIn().setBody("Kinesis Event 1.");
            }
        });
        template.send("direct:start", InOut, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(PARTITION_KEY, "partition-1");
                exchange.getIn().setBody("Kinesis Event 2.");
            }
        });
        assertMockEndpointsSatisfied();
        assertResultExchange(result.getExchanges().get(0), "Kinesis Event 1.", "partition-1");
        assertResultExchange(result.getExchanges().get(1), "Kinesis Event 2.", "partition-1");
    }
}

