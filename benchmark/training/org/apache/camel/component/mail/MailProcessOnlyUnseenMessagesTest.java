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


/**
 * Unit test for unseen option.
 */
public class MailProcessOnlyUnseenMessagesTest extends CamelTestSupport {
    @Test
    public void testProcessOnlyUnseenMessages() throws Exception {
        sendBody("direct:a", "Message 3");
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("Message 3");
        mock.assertIsSatisfied();
        // reset mock so we can make new assertions
        mock.reset();
        // send a new message, now we should only receive this new massages as all the others has been SEEN
        sendBody("direct:a", "Message 4");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("Message 4");
        mock.assertIsSatisfied();
    }
}

