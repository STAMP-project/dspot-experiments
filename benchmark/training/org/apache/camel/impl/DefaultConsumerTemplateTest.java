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
package org.apache.camel.impl;


import Exchange.FILE_NAME;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class DefaultConsumerTemplateTest extends ContextTestSupport {
    private DefaultConsumerTemplate consumer;

    @Test
    public void testConsumeReceive() throws Exception {
        template.sendBody("seda:foo", "Hello");
        Exchange out = consumer.receive("seda:foo");
        Assert.assertNotNull(out);
        Assert.assertEquals("Hello", out.getIn().getBody());
        Assert.assertSame(context, consumer.getCamelContext());
    }

    @Test
    public void testConsumeTwiceReceive() throws Exception {
        template.sendBody("seda:foo", "Hello");
        Exchange out = consumer.receive("seda:foo");
        Assert.assertNotNull(out);
        Assert.assertEquals("Hello", out.getIn().getBody());
        template.sendBody("seda:foo", "Bye");
        out = consumer.receive("seda:foo");
        Assert.assertNotNull(out);
        Assert.assertEquals("Bye", out.getIn().getBody());
    }

    @Test
    public void testConsumeReceiveNoWait() throws Exception {
        Exchange out = consumer.receiveNoWait("seda:foo");
        Assert.assertNull(out);
        template.sendBody("seda:foo", "Hello");
        await().atMost(1, TimeUnit.SECONDS).until(() -> {
            Exchange foo = consumer.receiveNoWait("seda:foo");
            if (foo != null) {
                assertEquals("Hello", foo.getIn().getBody());
            }
            return foo != null;
        });
    }

    @Test
    public void testConsumeReceiveTimeout() throws Exception {
        long start = System.currentTimeMillis();
        Exchange out = consumer.receive("seda:foo", 1000);
        Assert.assertNull(out);
        long delta = (System.currentTimeMillis()) - start;
        Assert.assertTrue(("Should take about 1 sec: " + delta), (delta < 1500));
        template.sendBody("seda:foo", "Hello");
        out = consumer.receive("seda:foo");
        Assert.assertEquals("Hello", out.getIn().getBody());
    }

    @Test
    public void testConsumeReceiveBody() throws Exception {
        template.sendBody("seda:foo", "Hello");
        Object body = consumer.receiveBody("seda:foo");
        Assert.assertEquals("Hello", body);
    }

    @Test
    public void testConsumeTwiceReceiveBody() throws Exception {
        template.sendBody("seda:foo", "Hello");
        Object body = consumer.receiveBody("seda:foo");
        Assert.assertEquals("Hello", body);
        template.sendBody("seda:foo", "Bye");
        body = consumer.receiveBody("seda:foo");
        Assert.assertEquals("Bye", body);
    }

    @Test
    public void testConsumeReceiveBodyNoWait() throws Exception {
        Object body = consumer.receiveBodyNoWait("seda:foo");
        Assert.assertNull(body);
        template.sendBody("seda:foo", "Hello");
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            Object foo = consumer.receiveBodyNoWait("seda:foo");
            assertEquals("Hello", foo);
        });
    }

    @Test
    public void testConsumeReceiveBodyString() throws Exception {
        template.sendBody("seda:foo", "Hello");
        String body = consumer.receiveBody("seda:foo", String.class);
        Assert.assertEquals("Hello", body);
    }

    @Test
    public void testConsumeTwiceReceiveBodyString() throws Exception {
        template.sendBody("seda:foo", "Hello");
        String body = consumer.receiveBody("seda:foo", String.class);
        Assert.assertEquals("Hello", body);
        template.sendBody("seda:foo", "Bye");
        body = consumer.receiveBody("seda:foo", String.class);
        Assert.assertEquals("Bye", body);
    }

    @Test
    public void testConsumeReceiveBodyStringNoWait() throws Exception {
        String body = consumer.receiveBodyNoWait("seda:foo", String.class);
        Assert.assertNull(body);
        template.sendBody("seda:foo", "Hello");
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            String foo = consumer.receiveBodyNoWait("seda:foo", .class);
            assertEquals("Hello", foo);
        });
    }

    @Test
    public void testConsumeReceiveEndpoint() throws Exception {
        template.sendBody("seda:foo", "Hello");
        Assert.assertNotNull(consumer.getCamelContext());
        Endpoint endpoint = context.getEndpoint("seda:foo");
        Exchange out = consumer.receive(endpoint);
        Assert.assertEquals("Hello", out.getIn().getBody());
    }

    @Test
    public void testConsumeReceiveEndpointTimeout() throws Exception {
        template.sendBody("seda:foo", "Hello");
        Assert.assertNotNull(consumer.getCamelContext());
        Endpoint endpoint = context.getEndpoint("seda:foo");
        Exchange out = consumer.receive(endpoint, 1000);
        Assert.assertEquals("Hello", out.getIn().getBody());
    }

    @Test
    public void testConsumeReceiveEndpointNoWait() throws Exception {
        Assert.assertNotNull(consumer.getCamelContext());
        Endpoint endpoint = context.getEndpoint("seda:foo");
        Exchange out = consumer.receiveNoWait(endpoint);
        Assert.assertNull(out);
        template.sendBody("seda:foo", "Hello");
        await().atMost(1, TimeUnit.SECONDS).until(() -> {
            Exchange foo = consumer.receiveNoWait(endpoint);
            if (foo != null) {
                assertEquals("Hello", foo.getIn().getBody());
            }
            return foo != null;
        });
    }

    @Test
    public void testConsumeReceiveEndpointBody() throws Exception {
        template.sendBody("seda:foo", "Hello");
        Assert.assertNotNull(consumer.getCamelContext());
        Endpoint endpoint = context.getEndpoint("seda:foo");
        Object body = consumer.receiveBody(endpoint);
        Assert.assertEquals("Hello", body);
    }

    @Test
    public void testConsumeReceiveEndpointBodyTimeout() throws Exception {
        template.sendBody("seda:foo", "Hello");
        Assert.assertNotNull(consumer.getCamelContext());
        Endpoint endpoint = context.getEndpoint("seda:foo");
        Object body = consumer.receiveBody(endpoint, 1000);
        Assert.assertEquals("Hello", body);
    }

    @Test
    public void testConsumeReceiveEndpointBodyType() throws Exception {
        template.sendBody("seda:foo", "Hello");
        Assert.assertNotNull(consumer.getCamelContext());
        Endpoint endpoint = context.getEndpoint("seda:foo");
        String body = consumer.receiveBody(endpoint, String.class);
        Assert.assertEquals("Hello", body);
    }

    @Test
    public void testConsumeReceiveEndpointBodyTimeoutType() throws Exception {
        template.sendBody("seda:foo", "Hello");
        Assert.assertNotNull(consumer.getCamelContext());
        Endpoint endpoint = context.getEndpoint("seda:foo");
        String body = consumer.receiveBody(endpoint, 1000, String.class);
        Assert.assertEquals("Hello", body);
    }

    @Test
    public void testConsumeReceiveBodyTimeoutType() throws Exception {
        template.sendBody("seda:foo", "Hello");
        String body = consumer.receiveBody("seda:foo", 1000, String.class);
        Assert.assertEquals("Hello", body);
    }

    @Test
    public void testConsumeReceiveEndpointBodyTypeNoWait() throws Exception {
        Assert.assertNotNull(consumer.getCamelContext());
        Endpoint endpoint = context.getEndpoint("seda:foo");
        String out = consumer.receiveBodyNoWait(endpoint, String.class);
        Assert.assertNull(out);
        template.sendBody("seda:foo", "Hello");
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            String foo = consumer.receiveBodyNoWait(endpoint, .class);
            assertEquals("Hello", foo);
        });
    }

    @Test
    public void testConsumeReceiveEndpointBodyNoWait() throws Exception {
        Assert.assertNotNull(consumer.getCamelContext());
        Endpoint endpoint = context.getEndpoint("seda:foo");
        Object out = consumer.receiveBodyNoWait(endpoint);
        Assert.assertNull(out);
        template.sendBody("seda:foo", "Hello");
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            Object foo = consumer.receiveBodyNoWait(endpoint);
            assertEquals("Hello", foo);
        });
    }

    @Test
    public void testReceiveException() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.setException(new IllegalArgumentException("Damn"));
        Exchange out = template.send("seda:foo", exchange);
        Assert.assertTrue(out.isFailed());
        Assert.assertNotNull(out.getException());
        try {
            consumer.receiveBody("seda:foo", String.class);
            Assert.fail("Should have thrown an exception");
        } catch (RuntimeCamelException e) {
            TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals("Damn", e.getCause().getMessage());
        }
    }

    @Test
    public void testReceiveOut() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getOut().setBody("Bye World");
        template.send("seda:foo", exchange);
        String out = consumer.receiveBody("seda:foo", String.class);
        Assert.assertEquals("Bye World", out);
    }

    @Test
    public void testCacheConsumers() throws Exception {
        ConsumerTemplate template = new DefaultConsumerTemplate(context);
        template.setMaximumCacheSize(500);
        template.start();
        Assert.assertEquals("Size should be 0", 0, template.getCurrentCacheSize());
        // test that we cache at most 500 consumers to avoid it eating to much memory
        for (int i = 0; i < 503; i++) {
            Endpoint e = context.getEndpoint(("direct:queue:" + i));
            template.receiveNoWait(e);
        }
        // the eviction is async so force cleanup
        template.cleanUp();
        Assert.assertEquals("Size should be 500", 500, template.getCurrentCacheSize());
        template.stop();
        // should be 0
        Assert.assertEquals("Size should be 0", 0, template.getCurrentCacheSize());
    }

    @Test
    public void testCacheConsumersFromContext() throws Exception {
        ConsumerTemplate template = context.createConsumerTemplate(500);
        Assert.assertEquals("Size should be 0", 0, template.getCurrentCacheSize());
        // test that we cache at most 500 consumers to avoid it eating to much memory
        for (int i = 0; i < 503; i++) {
            Endpoint e = context.getEndpoint(("direct:queue:" + i));
            template.receiveNoWait(e);
        }
        // the eviction is async so force cleanup
        template.cleanUp();
        Assert.assertEquals("Size should be 500", 500, template.getCurrentCacheSize());
        template.stop();
        // should be 0
        Assert.assertEquals("Size should be 0", 0, template.getCurrentCacheSize());
    }

    @Test
    public void testDoneUoW() throws Exception {
        TestSupport.deleteDirectory("target/data/foo");
        template.sendBodyAndHeader("file:target/data/foo", "Hello World", FILE_NAME, "hello.txt");
        Exchange exchange = consumer.receive("file:target/data/foo?initialDelay=0&delay=10&delete=true");
        Assert.assertNotNull(exchange);
        Assert.assertEquals("Hello World", exchange.getIn().getBody(String.class));
        // file should still exists
        File file = new File("target/data/foo/hello.txt");
        Assert.assertTrue(("File should exist " + file), file.exists());
        // done the exchange
        consumer.doneUoW(exchange);
        Assert.assertFalse(("File should have been deleted " + file), file.exists());
    }
}

