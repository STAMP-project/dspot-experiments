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
package org.apache.camel.component.spring.ws.addressing;


import java.net.URI;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.spring.ws.utils.OutputChannelReceiver;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.springframework.ws.soap.addressing.client.ActionCallback;


/**
 * Provides abstract test for fault and output params for spring-ws:to: and
 * spring-ws:action: endpoints
 */
public class CamelDirectSenderTest extends AbstractWSATests {
    private OutputChannelReceiver customChannel;

    @EndpointInject(uri = "mock:camelDirect")
    private MockEndpoint endpointCamelDirect;

    @Test
    public void endpointSender() throws Exception {
        ActionCallback requestCallback = channelIn("http://sender-default.com");
        webServiceTemplate.sendSourceAndReceiveToResult(source, requestCallback, result);
        Assertions.assertThat(channelOut().getTo()).isEqualTo(new URI("mailto:andrej@chocolatejar.eu"));
        Assertions.assertThat(endpointCamelDirect.getReceivedCounter()).isZero();
    }

    @Test
    public void customSender() throws Exception {
        ActionCallback requestCallback = channelIn("http://sender-custom.com");
        webServiceTemplate.sendSourceAndReceiveToResult(source, requestCallback, result);
        Assertions.assertThat(customChannelParams().getTo()).isEqualTo(new URI("mailto:andrej@chocolatejar.eu"));
        Assertions.assertThat(endpointCamelDirect.getReceivedCounter()).isZero();
    }

    @Test
    public void camelInvalid() throws Exception {
        ActionCallback requestCallback = toAndReplyTo("http://sender-camel.com", "mailto:not-mappped-address@chocolatejar.eu");
        webServiceTemplate.sendSourceAndReceiveToResult(source, requestCallback, result);
        Assertions.assertThat(endpointCamelDirect.getReceivedCounter()).isZero();
    }

    @Test
    public void camelReceivedReplyTo() throws Exception {
        ActionCallback requestCallback = channelIn("http://sender-camel.com");
        webServiceTemplate.sendSourceAndReceiveToResult(source, requestCallback, result);
        endpointCamelDirect.assertExchangeReceived(0);
        endpointCamelDirect.assertIsSatisfied();
    }

    @Test
    public void customMessageIdGenerator() throws Exception {
        ActionCallback requestCallback = channelIn("http://messageIdStrategy-custom.com");
        webServiceTemplate.sendSourceAndReceiveToResult(source, requestCallback, result);
        Assertions.assertThat(channelOut().getMessageId()).isEqualTo(new URI("staticTestId"));
    }

    @Test
    public void defaultMessageIdGenerator() throws Exception {
        ActionCallback requestCallback = channelIn("http://messageIdStrategy-default.com");
        webServiceTemplate.sendSourceAndReceiveToResult(source, requestCallback, result);
        Assertions.assertThat(channelOut().getMessageId()).isNotEqualTo(new URI("staticTestId"));
    }
}

