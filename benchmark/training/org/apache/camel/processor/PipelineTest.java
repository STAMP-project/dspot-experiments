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


import ExchangePattern.InOut;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class PipelineTest extends ContextTestSupport {
    /**
     * Simple processor the copies the in to the out and increments a counter.
     * Used to verify that the pipeline actually takes the output of one stage of
     * the pipe and feeds it in as input into the next stage.
     */
    private static final class InToOut implements Processor {
        public void process(Exchange exchange) throws Exception {
            exchange.getOut().copyFrom(exchange.getIn());
            Integer counter = exchange.getIn().getHeader("copy-counter", Integer.class);
            if (counter == null) {
                counter = 0;
            }
            exchange.getOut().setHeader("copy-counter", (counter + 1));
        }
    }

    /**
     * Simple processor the copies the in to the fault and increments a counter.
     */
    private static final class InToFault implements Processor {
        public void process(Exchange exchange) throws Exception {
            exchange.getOut().setFault(true);
            exchange.getOut().setBody(exchange.getIn().getBody());
            Integer counter = exchange.getIn().getHeader("copy-counter", Integer.class);
            if (counter == null) {
                counter = 0;
            }
            exchange.getOut().setHeader("copy-counter", (counter + 1));
        }
    }

    protected MockEndpoint resultEndpoint;

    @Test
    public void testSendMessageThroughAPipeline() throws Exception {
        resultEndpoint.expectedBodiesReceived(4);
        Exchange results = template.request("direct:a", new Processor() {
            public void process(Exchange exchange) {
                // now lets fire in a message
                Message in = exchange.getIn();
                in.setBody(1);
                in.setHeader("foo", "bar");
            }
        });
        resultEndpoint.assertIsSatisfied();
        Assert.assertEquals("Result body", 4, results.getOut().getBody());
    }

    @Test
    public void testResultsReturned() throws Exception {
        Exchange exchange = template.request("direct:b", new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setBody("Hello World");
            }
        });
        Assert.assertEquals("Hello World", exchange.getOut().getBody());
        Assert.assertEquals(3, exchange.getOut().getHeader("copy-counter"));
    }

    /**
     * Disabled for now until we figure out fault processing in the pipeline.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFaultStopsPipeline() throws Exception {
        Exchange exchange = template.request("direct:c", new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setBody("Fault Message");
            }
        });
        // Check the fault..
        Assert.assertTrue((((exchange.getOut()) != null) && (exchange.getOut().isFault())));
        Assert.assertEquals("Fault Message", exchange.getOut().getBody());
        Assert.assertEquals(2, exchange.getOut().getHeader("copy-counter"));
    }

    @Test
    public void testOnlyProperties() {
        Exchange exchange = template.request("direct:b", new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setHeader("header", "headerValue");
            }
        });
        Assert.assertEquals("headerValue", exchange.getOut().getHeader("header"));
        Assert.assertEquals(3, exchange.getOut().getHeader("copy-counter"));
    }

    @Test
    public void testCopyInOutExchange() {
        Exchange exchange = template.request("direct:start", new Processor() {
            public void process(Exchange exchange) {
                exchange.setPattern(InOut);
                exchange.getIn().setBody("test");
            }
        });
        Assert.assertEquals("There should have no message header", 0, exchange.getOut().getHeaders().size());
        Assert.assertEquals("There should have no attachments", 0, exchange.getOut().getAttachmentObjects().size());
        Assert.assertEquals("There should have no attachments", 0, exchange.getOut().getAttachments().size());
        Assert.assertEquals("Get a wrong message body", "test", exchange.getOut().getBody());
        Assert.assertNull(exchange.getOut().getHeader("test"));
        Assert.assertNull(exchange.getOut().getAttachmentObject("test1.xml"));
        Assert.assertNull(exchange.getOut().getAttachment("test1.xml"));
    }
}

