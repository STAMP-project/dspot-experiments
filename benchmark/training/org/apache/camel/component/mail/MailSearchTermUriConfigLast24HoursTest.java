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


public class MailSearchTermUriConfigLast24HoursTest extends CamelTestSupport {
    @Test
    public void testSearchTerm() throws Exception {
        Mailbox mailbox = Mailbox.get("bill@localhost");
        assertEquals(6, mailbox.size());
        // should only get the 4 latest emails that was sent within the last 24 hours
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceivedInAnyOrder("Ordering ActiveMQ in Action", "This is spam", "We meet at 7pm the usual place", "I am attaching you");
        assertMockEndpointsSatisfied();
    }
}

