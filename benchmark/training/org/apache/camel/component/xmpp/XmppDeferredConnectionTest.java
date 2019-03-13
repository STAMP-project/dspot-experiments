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
package org.apache.camel.component.xmpp;


import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Test to verify that the XMPP producer and consumer will create deferred / lazy connections
 * to the XMPP server when the server is not available upon route initialization. Also verify that
 * these endpoints will then deliver messages as expected.
 */
public class XmppDeferredConnectionTest extends CamelTestSupport {
    private EmbeddedXmppTestServer embeddedXmppTestServer;

    @Test
    public void testXmppChatWithDelayedConnection() throws Exception {
        MockEndpoint consumerEndpoint = context.getEndpoint("mock:out", MockEndpoint.class);
        MockEndpoint simpleEndpoint = context.getEndpoint("mock:simple", MockEndpoint.class);
        consumerEndpoint.setExpectedMessageCount(1);
        consumerEndpoint.expectedBodiesReceived("Hello again!");
        simpleEndpoint.setExpectedMessageCount(1);
        MockEndpoint errorEndpoint = context.getEndpoint("mock:error", MockEndpoint.class);
        errorEndpoint.setExpectedMessageCount(1);
        // this request should fail XMPP delivery because the server is not available
        template.sendBody("direct:start", "Hello!");
        consumerEndpoint.assertIsNotSatisfied();
        errorEndpoint.assertIsSatisfied();
        // this request should be received because it is not going through the XMPP endpoints
        // verifying that the non-xmpp routes are started
        template.sendBody("direct:simple", "Hello simple!");
        simpleEndpoint.assertIsSatisfied();
        embeddedXmppTestServer.startXmppEndpoint();
        // wait for the connection to be established
        Thread.sleep(2000);
        // this request should succeed now that the server is available
        template.sendBody("direct:start", "Hello again!");
        consumerEndpoint.assertIsSatisfied();
    }
}

