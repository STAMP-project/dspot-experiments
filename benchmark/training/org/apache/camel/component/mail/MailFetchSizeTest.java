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


import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;


/**
 * Unit test for fetch size.
 */
public class MailFetchSizeTest extends CamelTestSupport {
    @Test
    public void testFetchSize() throws Exception {
        Mailbox mailbox = Mailbox.get("jones@localhost");
        assertEquals(5, mailbox.size());
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(2);
        mock.expectedBodiesReceived("Message 0", "Message 1");
        // should be done within 2 seconds as no delay when started
        mock.setResultWaitTime(2000L);
        mock.assertIsSatisfied();
        Thread.sleep(500);
        assertEquals(3, mailbox.size());
        // reset mock to assert the next batch of 2 messages polled
        mock.reset();
        mock.expectedMessageCount(2);
        mock.expectedBodiesReceived("Message 2", "Message 3");
        // should be done within 2 (delay) + 1 seconds (polling)
        mock.setResultWaitTime(3000L);
        mock.assertIsSatisfied();
        Thread.sleep(500);
        assertEquals(1, mailbox.size());
        // reset mock to assert the last message polled
        mock.reset();
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("Message 4");
        mock.assertIsSatisfied();
    }
}

