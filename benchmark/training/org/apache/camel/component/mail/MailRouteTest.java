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
package org.apache.camel.component.mail;


import Exchange.CHARSET_NAME;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;


public class MailRouteTest extends CamelTestSupport {
    @Test
    public void testSendAndReceiveMails() throws Exception {
        Mailbox.clearAll();
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedBodiesReceived("hello world!");
        Map<String, Object> headers = new HashMap<>();
        headers.put("reply-to", "route-test-reply@localhost");
        template.sendBodyAndHeaders("smtp://route-test-james@localhost", "hello world!", headers);
        // lets test the first sent worked
        assertMailboxReceivedMessages("route-test-james@localhost");
        // lets test the receive worked
        resultEndpoint.assertIsSatisfied();
        // Validate that the headers were preserved.
        Exchange exchange = resultEndpoint.getReceivedExchanges().get(0);
        String replyTo = ((String) (exchange.getIn().getHeader("reply-to")));
        assertEquals("route-test-reply@localhost", replyTo);
        assertMailboxReceivedMessages("route-test-copy@localhost");
    }

    @Test
    public void testMailSubjectWithUnicode() throws Exception {
        Mailbox.clearAll();
        final String body = "Hello Camel Riders!";
        final String subject = "My Camel \u2122";
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        // now we don't use the UTF-8 encoding
        mock.expectedHeaderReceived("subject", "=?US-ASCII?Q?My_Camel_=3F?=");
        mock.expectedBodiesReceived(body);
        template.send("direct:a", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(body);
                exchange.getIn().setHeader("subject", subject);
                exchange.setProperty(CHARSET_NAME, "US-ASCII");
            }
        });
        mock.assertIsSatisfied();
        assertFalse("Should not have attachements", mock.getExchanges().get(0).getIn().hasAttachments());
    }
}

