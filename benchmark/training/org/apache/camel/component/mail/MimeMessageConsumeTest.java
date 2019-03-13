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


import Message.RecipientType.TO;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;


public class MimeMessageConsumeTest extends CamelTestSupport {
    private String body = "hello world!";

    @Test
    public void testSendAndReceiveMails() throws Exception {
        Mailbox.clearAll();
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedMinimumMessageCount(1);
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "localhost");
        Session session = Session.getInstance(properties, null);
        MimeMessage message = new MimeMessage(session);
        populateMimeMessageBody(message);
        message.setRecipients(TO, "james3@localhost");
        Transport.send(message);
        // lets test the receive worked
        resultEndpoint.assertIsSatisfied();
        Exchange exchange = resultEndpoint.getReceivedExchanges().get(0);
        String text = exchange.getIn().getBody(String.class);
        assertEquals("mail body", body, text);
        assertNotNull("attachments got lost", exchange.getIn().getAttachments());
        for (String s : exchange.getIn().getAttachmentNames()) {
            DataHandler dh = exchange.getIn().getAttachment(s);
            Object content = dh.getContent();
            assertNotNull("Content should not be empty", content);
            assertEquals("log4j2.properties", dh.getName());
        }
    }
}

