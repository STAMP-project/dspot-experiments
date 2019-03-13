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
package org.apache.camel.component.jms;


import ExchangePattern.InOnly;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.ConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.Processor;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class JmsRouteRequestReplyTest extends CamelTestSupport {
    protected static final String REPLY_TO_DESTINATION_SELECTOR_NAME = "camelProducer";

    protected static String componentName = "amq";

    protected static String componentName1 = "amq1";

    protected static String endpointUriA = (JmsRouteRequestReplyTest.componentName) + ":queue:test.a";

    protected static String endpointUriB = (JmsRouteRequestReplyTest.componentName) + ":queue:test.b";

    protected static String endpointUriB1 = (JmsRouteRequestReplyTest.componentName1) + ":queue:test.b";

    // note that the replyTo both A and B endpoints share the persistent replyTo queue,
    // which is one more way to verify that reply listeners of A and B endpoints don't steal each other messages
    protected static String endpointReplyToUriA = (JmsRouteRequestReplyTest.componentName) + ":queue:test.a?replyTo=queue:test.a.reply";

    protected static String endpointReplyToUriB = (JmsRouteRequestReplyTest.componentName) + ":queue:test.b?replyTo=queue:test.a.reply";

    protected static String request = "Hello World";

    protected static String expectedReply = "Re: " + (JmsRouteRequestReplyTest.request);

    protected static int maxTasks = 20;

    protected static int maxServerTasks = 1;

    protected static int maxCalls = 5;

    protected static AtomicBoolean inited = new AtomicBoolean(false);

    protected static Map<String, JmsRouteRequestReplyTest.ContextBuilder> contextBuilders = new HashMap<>();

    protected static Map<String, RouteBuilder> routeBuilders = new HashMap<>();

    private interface ContextBuilder {
        CamelContext buildContext(CamelContext context) throws Exception;
    }

    public static class SingleNodeDeadEndRouteBuilder extends RouteBuilder {
        public void configure() throws Exception {
            // We are not expect the response here
            from(JmsRouteRequestReplyTest.endpointUriA).setExchangePattern(InOnly).process(new Processor() {
                public void process(org.apache.camel.Exchange e) {
                    // do nothing
                }
            });
        }
    }

    public static class SingleNodeRouteBuilder extends RouteBuilder {
        public void configure() throws Exception {
            from(JmsRouteRequestReplyTest.endpointUriA).process(new Processor() {
                public void process(org.apache.camel.Exchange e) {
                    String request = e.getIn().getBody(String.class);
                    e.getOut().setBody(((JmsRouteRequestReplyTest.expectedReply) + (request.substring(request.indexOf('-')))));
                }
            });
        }
    }

    public static class MultiNodeRouteBuilder extends RouteBuilder {
        public void configure() throws Exception {
            from(JmsRouteRequestReplyTest.endpointUriA).to(JmsRouteRequestReplyTest.endpointUriB);
            from(JmsRouteRequestReplyTest.endpointUriB).process(new Processor() {
                public void process(org.apache.camel.Exchange e) {
                    String request = e.getIn().getBody(String.class);
                    e.getOut().setBody(((JmsRouteRequestReplyTest.expectedReply) + (request.substring(request.indexOf('-')))));
                }
            });
        }
    }

    public static class MultiNodeReplyToRouteBuilder extends RouteBuilder {
        public void configure() throws Exception {
            from(JmsRouteRequestReplyTest.endpointUriA).to(JmsRouteRequestReplyTest.endpointReplyToUriB);
            from(JmsRouteRequestReplyTest.endpointUriB).process(new Processor() {
                public void process(org.apache.camel.Exchange e) {
                    org.apache.camel.Message in = e.getIn();
                    org.apache.camel.Message out = e.getOut();
                    String selectorValue = in.getHeader(JmsRouteRequestReplyTest.REPLY_TO_DESTINATION_SELECTOR_NAME, String.class);
                    String request = in.getBody(String.class);
                    out.setHeader(JmsRouteRequestReplyTest.REPLY_TO_DESTINATION_SELECTOR_NAME, selectorValue);
                    out.setBody(((JmsRouteRequestReplyTest.expectedReply) + (request.substring(request.indexOf('-')))));
                }
            });
        }
    }

    public static class MultiNodeDiffCompRouteBuilder extends RouteBuilder {
        public void configure() throws Exception {
            from(JmsRouteRequestReplyTest.endpointUriA).to(JmsRouteRequestReplyTest.endpointUriB1);
            from(JmsRouteRequestReplyTest.endpointUriB1).process(new Processor() {
                public void process(org.apache.camel.Exchange e) {
                    String request = e.getIn().getBody(String.class);
                    e.getOut().setBody(((JmsRouteRequestReplyTest.expectedReply) + (request.substring(request.indexOf('-')))));
                }
            });
        }
    }

    public static class ContextBuilderMessageID implements JmsRouteRequestReplyTest.ContextBuilder {
        public CamelContext buildContext(CamelContext context) throws Exception {
            ConnectionFactory connectionFactory = CamelJmsTestHelper.createConnectionFactory();
            JmsComponent jmsComponent = JmsComponent.jmsComponentAutoAcknowledge(connectionFactory);
            jmsComponent.setUseMessageIDAsCorrelationID(true);
            jmsComponent.setConcurrentConsumers(JmsRouteRequestReplyTest.maxServerTasks);
            context.addComponent(JmsRouteRequestReplyTest.componentName, jmsComponent);
            return context;
        }
    }

    public class Task implements Callable<JmsRouteRequestReplyTest.Task> {
        private AtomicInteger counter;

        private String fromUri;

        private volatile boolean ok = true;

        private volatile String message = "";

        public Task(AtomicInteger counter, String fromUri) {
            this.counter = counter;
            this.fromUri = fromUri;
        }

        public JmsRouteRequestReplyTest.Task call() throws Exception {
            for (int i = 0; i < (JmsRouteRequestReplyTest.maxCalls); i++) {
                int callId = counter.incrementAndGet();
                Object reply = "";
                try {
                    reply = template.requestBody(fromUri, (((JmsRouteRequestReplyTest.request) + "-") + callId));
                } catch (RuntimeCamelException e) {
                    // expected in some cases
                }
                if (!(reply.equals((((JmsRouteRequestReplyTest.expectedReply) + "-") + callId)))) {
                    ok = false;
                    message = ((((("Unexpected reply. Expected: '" + (JmsRouteRequestReplyTest.expectedReply)) + "-") + callId) + "'; Received: '") + reply) + "'";
                }
            }
            return this;
        }

        public void assertSuccess() {
            assertTrue(message, ok);
        }
    }

    @Test
    public void testUseMessageIDAsCorrelationID() throws Exception {
        runRequestReplyThreaded(JmsRouteRequestReplyTest.endpointUriA);
    }

    @Test
    public void testUseCorrelationID() throws Exception {
        runRequestReplyThreaded(JmsRouteRequestReplyTest.endpointUriA);
    }

    @Test
    public void testUseMessageIDAsCorrelationIDMultiNode() throws Exception {
        runRequestReplyThreaded(JmsRouteRequestReplyTest.endpointUriA);
    }

    @Test
    public void testUseCorrelationIDMultiNode() throws Exception {
        runRequestReplyThreaded(JmsRouteRequestReplyTest.endpointUriA);
    }

    @Test
    public void testUseMessageIDAsCorrelationIDPersistReplyToMultiNode() throws Exception {
        runRequestReplyThreaded(JmsRouteRequestReplyTest.endpointReplyToUriA);
    }

    @Test
    public void testUseCorrelationIDPersistReplyToMultiNode() throws Exception {
        runRequestReplyThreaded(JmsRouteRequestReplyTest.endpointUriA);
    }

    // (1)
    // note this is an inefficient way of correlating replies to a persistent queue
    // a consumer will have to be created for each reply message
    // see testUseMessageIDAsCorrelationIDPersistMultiReplyToWithNamedSelectorMultiNode
    // or testCorrelationIDPersistMultiReplyToWithNamedSelectorMultiNode
    // for a faster way to do this. Note however that in this case the message copy has to occur
    // between consumer -> producer as the selector value needs to be propagated to the ultimate
    // destination, which in turn will copy this value back into the reply message
    @Test
    public void testUseMessageIDAsCorrelationIDPersistMultiReplyToMultiNode() throws Exception {
        int oldMaxTasks = JmsRouteRequestReplyTest.maxTasks;
        int oldMaxServerTasks = JmsRouteRequestReplyTest.maxServerTasks;
        int oldMaxCalls = JmsRouteRequestReplyTest.maxCalls;
        JmsRouteRequestReplyTest.maxTasks = 10;
        JmsRouteRequestReplyTest.maxServerTasks = 1;
        JmsRouteRequestReplyTest.maxCalls = 2;
        try {
            runRequestReplyThreaded(JmsRouteRequestReplyTest.endpointUriA);
        } finally {
            JmsRouteRequestReplyTest.maxTasks = oldMaxTasks;
            JmsRouteRequestReplyTest.maxServerTasks = oldMaxServerTasks;
            JmsRouteRequestReplyTest.maxCalls = oldMaxCalls;
        }
    }

    @Test
    public void testUseMessageIDAsCorrelationIDPersistMultiReplyToWithNamedSelectorMultiNode() throws Exception {
        runRequestReplyThreaded(JmsRouteRequestReplyTest.endpointUriA);
    }

    @Test
    public void testUseCorrelationIDPersistMultiReplyToWithNamedSelectorMultiNode() throws Exception {
        runRequestReplyThreaded(JmsRouteRequestReplyTest.endpointUriA);
    }

    @Test
    public void testUseCorrelationIDTimeout() throws Exception {
        JmsComponent c = ((JmsComponent) (context.getComponent(JmsRouteRequestReplyTest.componentName)));
        c.getConfiguration().setRequestTimeout(1000);
        Object reply = "";
        try {
            reply = template.requestBody(JmsRouteRequestReplyTest.endpointUriA, JmsRouteRequestReplyTest.request);
            fail("Should have thrown exception");
        } catch (RuntimeCamelException e) {
            assertIsInstanceOf(ExchangeTimedOutException.class, e.getCause());
        }
        assertEquals("", reply);
    }

    @Test
    public void testUseMessageIDAsCorrelationIDTimeout() throws Exception {
        JmsComponent c = ((JmsComponent) (context.getComponent(JmsRouteRequestReplyTest.componentName)));
        c.getConfiguration().setRequestTimeout(1000);
        Object reply = "";
        try {
            reply = template.requestBody(JmsRouteRequestReplyTest.endpointUriA, JmsRouteRequestReplyTest.request);
            fail("Should have thrown exception");
        } catch (RuntimeCamelException e) {
            assertIsInstanceOf(ExchangeTimedOutException.class, e.getCause());
        }
        assertEquals("", reply);
    }

    @Test
    public void testUseCorrelationIDMultiNodeDiffComponents() throws Exception {
        runRequestReplyThreaded(JmsRouteRequestReplyTest.endpointUriA);
    }

    @Test
    public void testUseMessageIDAsCorrelationIDMultiNodeDiffComponents() throws Exception {
        runRequestReplyThreaded(JmsRouteRequestReplyTest.endpointUriA);
    }
}

