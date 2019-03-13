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
package org.apache.camel.processor.enricher;


import Exchange.TO_ENDPOINT;
import ExchangePattern.InOut;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class EnricherTest extends ContextTestSupport {
    private static SampleAggregator aggregationStrategy = new SampleAggregator();

    protected MockEndpoint mock;

    // -------------------------------------------------------------
    // InOnly routes
    // -------------------------------------------------------------
    @Test
    public void testEnrichInOnly() throws InterruptedException {
        mock.expectedBodiesReceived("test:blah");
        mock.message(0).exchangeProperty(TO_ENDPOINT).isEqualTo("mock://mock");
        template.sendBody("direct:enricher-test-1", "test");
        mock.assertIsSatisfied();
    }

    @Test
    public void testEnrichFaultInOnly() throws InterruptedException {
        mock.expectedMessageCount(0);
        Exchange exchange = template.send("direct:enricher-test-3", new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setBody("test");
            }
        });
        mock.assertIsSatisfied();
        Assert.assertEquals("test", exchange.getIn().getBody());
        Assert.assertTrue((((exchange.getOut()) != null) && (exchange.getOut().isFault())));
        Assert.assertEquals("failed", exchange.getOut().getBody());
        Assert.assertEquals("direct://enricher-fault-resource", exchange.getProperty(TO_ENDPOINT));
        Assert.assertNull(exchange.getException());
    }

    @Test
    public void testEnrichErrorInOnly() throws InterruptedException {
        mock.expectedMessageCount(0);
        Exchange exchange = template.send("direct:enricher-test-4", new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setBody("test");
            }
        });
        mock.assertIsSatisfied();
        Assert.assertEquals("test", exchange.getIn().getBody());
        Assert.assertEquals("failed", exchange.getException().getMessage());
        Assert.assertFalse(exchange.hasOut());
    }

    // -------------------------------------------------------------
    // InOut routes
    // -------------------------------------------------------------
    @Test
    public void testEnrichInOut() throws InterruptedException {
        String result = ((String) (template.sendBody("direct:enricher-test-5", InOut, "test")));
        Assert.assertEquals("test:blah", result);
    }

    @Test
    public void testEnrichInOutPlusHeader() throws InterruptedException {
        Exchange exchange = template.send("direct:enricher-test-5", InOut, new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setHeader("foo", "bar");
                exchange.getIn().setBody("test");
            }
        });
        Assert.assertEquals("bar", exchange.getIn().getHeader("foo"));
        Assert.assertEquals("test:blah", exchange.getIn().getBody());
        Assert.assertTrue(exchange.hasOut());
        Assert.assertNull(exchange.getException());
    }

    @Test
    public void testEnrichFaultInOut() throws InterruptedException {
        Exchange exchange = template.send("direct:enricher-test-7", InOut, new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setBody("test");
            }
        });
        Assert.assertEquals("test", exchange.getIn().getBody());
        Assert.assertTrue((((exchange.getOut()) != null) && (exchange.getOut().isFault())));
        Assert.assertEquals("failed", exchange.getOut().getBody());
        Assert.assertNull(exchange.getException());
    }

    @Test
    public void testEnrichErrorInOut() throws InterruptedException {
        Exchange exchange = template.send("direct:enricher-test-8", InOut, new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setBody("test");
            }
        });
        Assert.assertEquals("test", exchange.getIn().getBody());
        Assert.assertEquals("failed", exchange.getException().getMessage());
        Assert.assertFalse(exchange.hasOut());
    }
}

