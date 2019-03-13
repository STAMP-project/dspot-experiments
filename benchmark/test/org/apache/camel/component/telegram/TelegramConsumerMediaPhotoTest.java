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
package org.apache.camel.component.telegram;


import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.telegram.model.IncomingMessage;
import org.apache.camel.component.telegram.util.TelegramTestSupport;
import org.junit.Test;


/**
 * Tests the reception of messages without text having media content.
 */
public class TelegramConsumerMediaPhotoTest extends TelegramTestSupport {
    @EndpointInject(uri = "mock:telegram")
    private MockEndpoint endpoint;

    @Test
    public void testReceptionOfTwoMessagesOneWithMedia() throws Exception {
        endpoint.expectedMinimumMessageCount(2);
        endpoint.assertIsSatisfied();
        Exchange mediaExchange = endpoint.getExchanges().get(1);
        IncomingMessage msg = mediaExchange.getIn().getBody(IncomingMessage.class);
        assertNotNull(msg.getPhoto());
        assertCollectionSize(msg.getPhoto(), 4);
        assertEquals(4, msg.getPhoto().stream().map(( ph) -> ph.getFileId()).distinct().count());
    }
}

