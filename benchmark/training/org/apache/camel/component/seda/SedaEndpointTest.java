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
package org.apache.camel.component.seda;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.camel.Consumer;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.junit.Assert;
import org.junit.Test;


public class SedaEndpointTest extends ContextTestSupport {
    private BlockingQueue<Exchange> queue = new ArrayBlockingQueue<>(1000);

    @Test
    public void testSedaEndpointUnboundedQueue() throws Exception {
        BlockingQueue<Exchange> unbounded = new LinkedBlockingQueue<>();
        SedaEndpoint seda = new SedaEndpoint("seda://foo", context.getComponent("seda"), unbounded);
        Assert.assertNotNull(seda);
        Assert.assertEquals(Integer.MAX_VALUE, seda.getSize());
        Assert.assertSame(unbounded, seda.getQueue());
        Assert.assertEquals(1, seda.getConcurrentConsumers());
        Producer prod = seda.createProducer();
        seda.onStarted(((SedaProducer) (prod)));
        Assert.assertEquals(1, seda.getProducers().size());
        Consumer cons = seda.createConsumer(new Processor() {
            public void process(Exchange exchange) throws Exception {
                // do nothing
            }
        });
        seda.onStarted(((SedaConsumer) (cons)));
        Assert.assertEquals(1, seda.getConsumers().size());
        Assert.assertEquals(0, seda.getExchanges().size());
    }

    @Test
    public void testSedaEndpoint() throws Exception {
        SedaEndpoint seda = new SedaEndpoint("seda://foo", context.getComponent("seda"), queue);
        Assert.assertNotNull(seda);
        Assert.assertEquals(1000, seda.getSize());
        Assert.assertSame(queue, seda.getQueue());
        Assert.assertEquals(1, seda.getConcurrentConsumers());
        Producer prod = seda.createProducer();
        seda.onStarted(((SedaProducer) (prod)));
        Assert.assertEquals(1, seda.getProducers().size());
        Consumer cons = seda.createConsumer(new Processor() {
            public void process(Exchange exchange) throws Exception {
                // do nothing
            }
        });
        seda.onStarted(((SedaConsumer) (cons)));
        Assert.assertEquals(1, seda.getConsumers().size());
        Assert.assertEquals(0, seda.getExchanges().size());
    }

    @Test
    public void testSedaEndpointTwo() throws Exception {
        SedaEndpoint seda = new SedaEndpoint("seda://foo", context.getComponent("seda"), queue, 2);
        Assert.assertNotNull(seda);
        Assert.assertEquals(1000, seda.getSize());
        Assert.assertSame(queue, seda.getQueue());
        Assert.assertEquals(2, seda.getConcurrentConsumers());
        Producer prod = seda.createProducer();
        seda.onStarted(((SedaProducer) (prod)));
        Assert.assertEquals(1, seda.getProducers().size());
        Consumer cons = seda.createConsumer(new Processor() {
            public void process(Exchange exchange) throws Exception {
                // do nothing
            }
        });
        seda.onStarted(((SedaConsumer) (cons)));
        Assert.assertEquals(1, seda.getConsumers().size());
        Assert.assertEquals(0, seda.getExchanges().size());
    }

    @Test
    public void testSedaEndpointSetQueue() throws Exception {
        SedaEndpoint seda = new SedaEndpoint();
        Assert.assertNotNull(seda);
        seda.setCamelContext(context);
        seda.setEndpointUriIfNotSpecified("seda://bar");
        Assert.assertNotNull(seda.getQueue());
        // overwrite with a new queue
        seda.setQueue(new ArrayBlockingQueue<Exchange>(1000));
        seda.setConcurrentConsumers(2);
        Assert.assertEquals(1000, seda.getSize());
        Assert.assertNotSame(queue, seda.getQueue());
        Assert.assertEquals(2, seda.getConcurrentConsumers());
        Producer prod = seda.createProducer();
        seda.onStarted(((SedaProducer) (prod)));
        Assert.assertEquals(1, seda.getProducers().size());
        Consumer cons = seda.createConsumer(new Processor() {
            public void process(Exchange exchange) throws Exception {
                // do nothing
            }
        });
        seda.onStarted(((SedaConsumer) (cons)));
        Assert.assertEquals(1, seda.getConsumers().size());
        Assert.assertEquals(0, seda.getExchanges().size());
    }

    @Test
    public void testSedaConsumer() throws Exception {
        SedaEndpoint seda = context.getEndpoint("seda://foo", SedaEndpoint.class);
        Consumer consumer = seda.createConsumer(new Processor() {
            public void process(Exchange exchange) throws Exception {
                // do nothing
            }
        });
        Assert.assertSame(seda, consumer.getEndpoint());
        Assert.assertNotNull(consumer.toString());
    }

    @Test
    public void testSedaDefaultValue() throws Exception {
        SedaComponent sedaComponent = new SedaComponent();
        sedaComponent.setQueueSize(300);
        sedaComponent.setConcurrentConsumers(3);
        context.addComponent("seda", sedaComponent);
        SedaEndpoint seda = context.getEndpoint("seda://foo", SedaEndpoint.class);
        Assert.assertEquals(300, seda.getSize());
        Assert.assertEquals(3, seda.getConcurrentConsumers());
    }
}

