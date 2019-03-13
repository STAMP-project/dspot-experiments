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
package org.apache.camel.component.jgroups;


import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.junit.Test;


public class JGroupsComponentTest extends CamelTestSupport {
    // Constants
    static final String CLUSTER_NAME = "CLUSTER_NAME";

    static final String MESSAGE = "MESSAGE";

    static final String SAMPLE_CHANNEL_PROPERTY = "enable_diagnostics=true";

    static final String SAMPLE_CHANNEL_PROPERTIES = String.format("UDP(%s)", JGroupsComponentTest.SAMPLE_CHANNEL_PROPERTY);

    static final String CONFIGURED_ENDPOINT_URI = String.format("jgroups:%s", JGroupsComponentTest.CLUSTER_NAME);

    // Fixtures
    JChannel clientChannel;

    JChannel defaultComponentChannel;

    @Test
    public void shouldConsumeMulticastedMessage() throws Exception {
        // Given
        MockEndpoint mockEndpoint = getMockEndpoint("mock:default");
        mockEndpoint.setExpectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived(JGroupsComponentTest.MESSAGE);
        // When
        Message message = new Message(null, JGroupsComponentTest.MESSAGE);
        message.setSrc(null);
        clientChannel.send(message);
        // Then
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldConfigureChannelWithProperties() throws Exception {
        // When
        JGroupsEndpoint endpoint = getMandatoryEndpoint(JGroupsComponentTest.CONFIGURED_ENDPOINT_URI, JGroupsEndpoint.class);
        // Then
        assertTrue(endpoint.getResolvedChannel().getProperties().contains(JGroupsComponentTest.SAMPLE_CHANNEL_PROPERTY));
    }

    @Test
    public void shouldCreateChannel() throws Exception {
        // When
        JGroupsEndpoint endpoint = getMandatoryEndpoint(("my-default-jgroups:" + (JGroupsComponentTest.CLUSTER_NAME)), JGroupsEndpoint.class);
        JGroupsComponent component = ((JGroupsComponent) (endpoint.getComponent()));
        // Then
        assertNotNull(component.getChannel());
    }
}

