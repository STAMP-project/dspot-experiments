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
package org.apache.camel.component.activemq;


import java.util.HashMap;
import java.util.Map;
import javax.jms.Destination;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class InvokeRequestReplyUsingJmsReplyToHeaderTest extends CamelTestSupport {
    private static final transient Logger LOG = LoggerFactory.getLogger(InvokeRequestReplyUsingJmsReplyToHeaderTest.class);

    protected String replyQueueName = "queue://test.reply";

    protected Object correlationID = "ABC-123";

    protected Object groupID = "GROUP-XYZ";

    private InvokeRequestReplyUsingJmsReplyToHeaderTest.MyServer myBean = new InvokeRequestReplyUsingJmsReplyToHeaderTest.MyServer();

    @Test
    public void testPerformRequestReplyOverJms() throws Exception {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("cheese", 123);
        headers.put("JMSReplyTo", replyQueueName);
        headers.put("JMSCorrelationID", correlationID);
        headers.put("JMSXGroupID", groupID);
        Exchange reply = template.request("activemq:test.server?replyTo=queue:test.reply", new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setBody("James");
                Map<String, Object> headers = new HashMap<String, Object>();
                headers.put("cheese", 123);
                headers.put("JMSReplyTo", replyQueueName);
                headers.put("JMSCorrelationID", correlationID);
                headers.put("JMSXGroupID", groupID);
                exchange.getIn().setHeaders(headers);
            }
        });
        Message in = reply.getIn();
        Object replyTo = in.getHeader("JMSReplyTo");
        InvokeRequestReplyUsingJmsReplyToHeaderTest.LOG.info(("Reply to is: " + replyTo));
        InvokeRequestReplyUsingJmsReplyToHeaderTest.LOG.info(("Received headers: " + (in.getHeaders())));
        InvokeRequestReplyUsingJmsReplyToHeaderTest.LOG.info(("Received body: " + (in.getBody())));
        assertMessageHeader(in, "JMSCorrelationID", correlationID);
        Map<String, Object> receivedHeaders = myBean.getHeaders();
        assertThat(receivedHeaders, Matchers.hasKey("JMSReplyTo"));
        assertThat(receivedHeaders, Matchers.hasEntry("JMSXGroupID", groupID));
        assertThat(receivedHeaders, Matchers.hasEntry("JMSCorrelationID", correlationID));
        replyTo = receivedHeaders.get("JMSReplyTo");
        InvokeRequestReplyUsingJmsReplyToHeaderTest.LOG.info(("Reply to is: " + replyTo));
        Destination destination = assertIsInstanceOf(Destination.class, replyTo);
        assertEquals("ReplyTo", replyQueueName, destination.toString());
    }

    protected static class MyServer {
        private Map<String, Object> headers;

        public String process(@Headers
        Map<String, Object> headers, String body) {
            this.headers = headers;
            InvokeRequestReplyUsingJmsReplyToHeaderTest.LOG.info(("process() invoked with headers: " + headers));
            return "Hello " + body;
        }

        public Map<String, Object> getHeaders() {
            return headers;
        }
    }
}

