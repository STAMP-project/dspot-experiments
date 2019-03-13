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
package org.apache.camel.component.mllp;


import MllpConstants.MLLP_EVENT_TYPE;
import MllpConstants.MLLP_LOCAL_ADDRESS;
import MllpConstants.MLLP_MESSAGE_CONTROL;
import MllpConstants.MLLP_MESSAGE_TYPE;
import MllpConstants.MLLP_PROCESSING_ID;
import MllpConstants.MLLP_RECEIVING_APPLICATION;
import MllpConstants.MLLP_REMOTE_ADDRESS;
import MllpConstants.MLLP_SECURITY;
import MllpConstants.MLLP_SENDING_APPLICATION;
import MllpConstants.MLLP_SENDING_FACILITY;
import MllpConstants.MLLP_TIMESTAMP;
import MllpConstants.MLLP_TRIGGER_EVENT;
import MllpConstants.MLLP_VERSION_ID;
import java.util.concurrent.TimeUnit;
import org.apache.camel.EndpointInject;
import org.apache.camel.Message;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit.rule.mllp.MllpClientResource;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Rule;
import org.junit.Test;

import static MllpConstants.MLLP_EVENT_TYPE;
import static MllpConstants.MLLP_LOCAL_ADDRESS;
import static MllpConstants.MLLP_MESSAGE_CONTROL;
import static MllpConstants.MLLP_MESSAGE_TYPE;
import static MllpConstants.MLLP_PROCESSING_ID;
import static MllpConstants.MLLP_RECEIVING_APPLICATION;
import static MllpConstants.MLLP_REMOTE_ADDRESS;
import static MllpConstants.MLLP_SECURITY;
import static MllpConstants.MLLP_SENDING_APPLICATION;
import static MllpConstants.MLLP_SENDING_FACILITY;
import static MllpConstants.MLLP_TIMESTAMP;
import static MllpConstants.MLLP_VERSION_ID;


public class MllpTcpServerConsumerMessageHeadersTest extends CamelTestSupport {
    @Rule
    public MllpClientResource mllpClient = new MllpClientResource();

    @EndpointInject(uri = "mock://result")
    MockEndpoint result;

    @EndpointInject(uri = "mock://on-completion-result")
    MockEndpoint onCompletionResult;

    @Test
    public void testHl7HeadersEnabled() throws Exception {
        String testMessage = ("MSH|^~\\&|ADT|EPIC|JCAPS|CC|20160902123950|RISTECH|ADT^A08|00001|D|2.3|||||||" + '\r') + '\n';
        addTestRoute(true);
        result.expectedMessageCount(1);
        result.expectedHeaderReceived(MLLP_SENDING_APPLICATION, "ADT");
        result.expectedHeaderReceived(MLLP_SENDING_FACILITY, "EPIC");
        result.expectedHeaderReceived(MLLP_RECEIVING_APPLICATION, "JCAPS");
        result.expectedHeaderReceived(MLLP_TIMESTAMP, "20160902123950");
        result.expectedHeaderReceived(MLLP_SECURITY, "RISTECH");
        result.expectedHeaderReceived(MLLP_MESSAGE_TYPE, "ADT^A08");
        result.expectedHeaderReceived(MLLP_EVENT_TYPE, "ADT");
        result.expectedHeaderReceived(MLLP_TRIGGER_EVENT, "A08");
        result.expectedHeaderReceived(MLLP_MESSAGE_CONTROL, "00001");
        result.expectedHeaderReceived(MLLP_PROCESSING_ID, "D");
        result.expectedHeaderReceived(MLLP_VERSION_ID, "2.3");
        mllpClient.connect();
        mllpClient.sendMessageAndWaitForAcknowledgement(testMessage, 10000);
        assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
        Message message = result.getExchanges().get(0).getIn();
        assertNotNull(("Should have header" + (MLLP_LOCAL_ADDRESS)), message.getHeader(MLLP_LOCAL_ADDRESS));
        assertNotNull(("Should have header" + (MLLP_REMOTE_ADDRESS)), message.getHeader(MLLP_REMOTE_ADDRESS));
    }

    @Test
    public void testHl7HeadersDisabled() throws Exception {
        String testMessage = ("MSH|^~\\&|ADT|EPIC|JCAPS|CC|20160902123950|RISTECH|ADT^A08|00001|D|2.3|||||||" + '\r') + '\n';
        addTestRoute(false);
        result.expectedMessageCount(1);
        mllpClient.connect();
        mllpClient.sendMessageAndWaitForAcknowledgement(testMessage, 10000);
        assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
        Message message = result.getExchanges().get(0).getIn();
        assertNotNull(("Should have header" + (MLLP_LOCAL_ADDRESS)), message.getHeader(MLLP_LOCAL_ADDRESS));
        assertNotNull(("Should have header" + (MLLP_REMOTE_ADDRESS)), message.getHeader(MLLP_REMOTE_ADDRESS));
        assertNull(("Should NOT have header" + (MLLP_SENDING_APPLICATION)), message.getHeader(MLLP_SENDING_APPLICATION));
        assertNull(("Should NOT have header" + (MLLP_SENDING_FACILITY)), message.getHeader(MLLP_SENDING_FACILITY));
        assertNull(("Should NOT have header" + (MLLP_RECEIVING_APPLICATION)), message.getHeader(MLLP_RECEIVING_APPLICATION));
        assertNull(("Should NOT have header" + (MLLP_TIMESTAMP)), message.getHeader(MLLP_TIMESTAMP));
        assertNull(("Should NOT have header" + (MLLP_SECURITY)), message.getHeader(MLLP_SECURITY));
        assertNull(("Should NOT have header" + (MLLP_MESSAGE_TYPE)), message.getHeader(MLLP_MESSAGE_TYPE));
        assertNull(("Should NOT have header" + (MLLP_EVENT_TYPE)), message.getHeader(MLLP_EVENT_TYPE));
        assertNull(("Should NOT have header" + (MLLP_MESSAGE_CONTROL)), message.getHeader(MLLP_MESSAGE_CONTROL));
        assertNull(("Should NOT have header" + (MLLP_PROCESSING_ID)), message.getHeader(MLLP_PROCESSING_ID));
        assertNull(("Should NOT have header" + (MLLP_VERSION_ID)), message.getHeader(MLLP_VERSION_ID));
    }
}

