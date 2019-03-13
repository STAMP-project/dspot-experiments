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
package org.apache.camel.component.aws.ses;


import SesConstants.FROM;
import SesConstants.MESSAGE_ID;
import SesConstants.REPLY_TO_ADDRESSES;
import SesConstants.RETURN_PATH;
import SesConstants.SUBJECT;
import SesConstants.TO;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import java.util.Arrays;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class SesComponentTest extends CamelTestSupport {
    private AmazonSESClientMock sesClient;

    @Test
    public void sendInOnlyMessageUsingUrlOptions() throws Exception {
        Exchange exchange = template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("This is my message text.");
            }
        });
        assertEquals("1", exchange.getIn().getHeader(MESSAGE_ID));
        SendEmailRequest sendEmailRequest = sesClient.getSendEmailRequest();
        assertEquals("from@example.com", sendEmailRequest.getSource());
        assertEquals(2, getTo(sendEmailRequest).size());
        assertTrue(getTo(sendEmailRequest).contains("to1@example.com"));
        assertTrue(getTo(sendEmailRequest).contains("to2@example.com"));
        assertEquals("bounce@example.com", sendEmailRequest.getReturnPath());
        assertEquals(2, sendEmailRequest.getReplyToAddresses().size());
        assertTrue(sendEmailRequest.getReplyToAddresses().contains("replyTo1@example.com"));
        assertTrue(sendEmailRequest.getReplyToAddresses().contains("replyTo2@example.com"));
        assertEquals("Subject", getSubject(sendEmailRequest));
        assertEquals("This is my message text.", getBody(sendEmailRequest));
    }

    @Test
    public void sendInOutMessageUsingUrlOptions() throws Exception {
        Exchange exchange = template.request("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("This is my message text.");
            }
        });
        assertEquals("1", exchange.getOut().getHeader(MESSAGE_ID));
    }

    @Test
    public void sendMessageUsingMessageHeaders() throws Exception {
        Exchange exchange = template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("This is my message text.");
                exchange.getIn().setHeader(FROM, "anotherFrom@example.com");
                exchange.getIn().setHeader(TO, Arrays.asList("anotherTo1@example.com", "anotherTo2@example.com"));
                exchange.getIn().setHeader(RETURN_PATH, "anotherBounce@example.com");
                exchange.getIn().setHeader(REPLY_TO_ADDRESSES, Arrays.asList("anotherReplyTo1@example.com", "anotherReplyTo2@example.com"));
                exchange.getIn().setHeader(SUBJECT, "anotherSubject");
            }
        });
        assertEquals("1", exchange.getIn().getHeader(MESSAGE_ID));
        SendEmailRequest sendEmailRequest = sesClient.getSendEmailRequest();
        assertEquals("anotherFrom@example.com", sendEmailRequest.getSource());
        assertEquals(2, getTo(sendEmailRequest).size());
        assertTrue(getTo(sendEmailRequest).contains("anotherTo1@example.com"));
        assertTrue(getTo(sendEmailRequest).contains("anotherTo2@example.com"));
        assertEquals("anotherBounce@example.com", sendEmailRequest.getReturnPath());
        assertEquals(2, sendEmailRequest.getReplyToAddresses().size());
        assertTrue(sendEmailRequest.getReplyToAddresses().contains("anotherReplyTo1@example.com"));
        assertTrue(sendEmailRequest.getReplyToAddresses().contains("anotherReplyTo2@example.com"));
        assertEquals("anotherSubject", getSubject(sendEmailRequest));
        assertEquals("This is my message text.", getBody(sendEmailRequest));
    }
}

