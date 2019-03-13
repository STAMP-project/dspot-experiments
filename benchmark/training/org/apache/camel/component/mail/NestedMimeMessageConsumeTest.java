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


import java.util.Set;
import javax.activation.DataHandler;
import org.apache.camel.Attachment;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;


public class NestedMimeMessageConsumeTest extends CamelTestSupport {
    @Test
    public void testNestedMultipart() throws Exception {
        Mailbox.clearAll();
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedMinimumMessageCount(1);
        prepareMailbox("james3");
        resultEndpoint.assertIsSatisfied();
        Exchange exchange = resultEndpoint.getReceivedExchanges().get(0);
        String text = exchange.getIn().getBody(String.class);
        assertThat(text, StringContains.containsString("Test with bold face, pictures and attachments"));
        assertEquals("text/plain; charset=us-ascii", exchange.getIn().getHeader("Content-Type"));
        Set<String> attachmentNames = exchange.getIn().getAttachmentNames();
        assertNotNull("attachments got lost", attachmentNames);
        assertEquals(2, attachmentNames.size());
        for (String s : attachmentNames) {
            Attachment att = exchange.getIn().getAttachmentObject(s);
            DataHandler dh = att.getDataHandler();
            Object content = dh.getContent();
            assertNotNull("Content should not be empty", content);
            assertThat(dh.getName(), AnyOf.anyOf(IsEqual.equalTo("image001.png"), IsEqual.equalTo("test.txt")));
        }
    }
}

