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


public class PollEnricherTest extends ContextTestSupport {
    private static SampleAggregator aggregationStrategy = new SampleAggregator();

    protected MockEndpoint mock;

    // -------------------------------------------------------------
    // InOnly routes
    // -------------------------------------------------------------
    @Test
    public void testPollEnrichInOnly() throws InterruptedException {
        template.sendBody("seda:foo1", "blah");
        mock.expectedBodiesReceived("test:blah");
        mock.expectedHeaderReceived(TO_ENDPOINT, "seda://foo1");
        template.sendBody("direct:enricher-test-1", "test");
        mock.assertIsSatisfied();
    }

    @Test
    public void testPollEnrichInOnlyWaitWithTimeout() throws InterruptedException {
        // this first try there is no data so we timeout
        mock.expectedBodiesReceived("test:blah");
        mock.expectedHeaderReceived(TO_ENDPOINT, "seda://foo2");
        template.sendBody("direct:enricher-test-2", "test");
        // not expected data so we are not happy
        mock.assertIsNotSatisfied();
        // now send it and try again
        mock.reset();
        template.sendBody("seda:foo2", "blah");
        template.sendBody("direct:enricher-test-2", "test");
        mock.assertIsSatisfied();
    }

    @Test
    public void testPollEnrichInOnlyWaitNoTimeout() throws InterruptedException {
        // use another thread to send it a bit later
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    // ignore
                }
                template.sendBody("seda:foo3", "blah");
            }
        });
        long start = System.currentTimeMillis();
        mock.expectedBodiesReceived("test:blah");
        mock.expectedHeaderReceived(TO_ENDPOINT, "seda://foo3");
        t.start();
        template.sendBody("direct:enricher-test-3", "test");
        // should take approx 1 sec to complete as the other thread is sending a bit later and we wait
        mock.assertIsSatisfied();
        long delta = (System.currentTimeMillis()) - start;
        Assert.assertTrue(("Should take approx 0.25 sec: was " + delta), (delta > 150));
    }

    // -------------------------------------------------------------
    // InOut routes
    // -------------------------------------------------------------
    @Test
    public void testPollEnrichInOut() throws InterruptedException {
        template.sendBody("seda:foo4", "blah");
        String result = ((String) (template.sendBody("direct:enricher-test-4", InOut, "test")));
        Assert.assertEquals("test:blah", result);
    }

    @Test
    public void testPollEnrichInOutPlusHeader() throws InterruptedException {
        template.sendBody("seda:foo4", "blah");
        Exchange exchange = template.request("direct:enricher-test-4", new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setHeader("foo", "bar");
                exchange.getIn().setBody("test");
            }
        });
        Assert.assertEquals("bar", exchange.getIn().getHeader("foo"));
        Assert.assertEquals("test:blah", exchange.getIn().getBody());
        Assert.assertFalse(((exchange.hasOut()) && (exchange.getOut().isFault())));
        Assert.assertEquals("seda://foo4", exchange.getOut().getHeader(TO_ENDPOINT));
        Assert.assertNull(exchange.getException());
    }
}

