/**
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.samples.tcpclientserver;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.endpoint.AbstractEndpoint;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Shows an example of using the Stx/Etx stream framing serializers that are included with
 * Spring Integration. We can be confident that the streams are properly handled because we
 * explicitly send a stream with the Stx/Etx frame and the beginning and end of the actual
 * content and the Server is configured to be able to handle the frame. In the asserts, we
 * assert that the payload, once it reaches a component (in this case, the message listener
 * we create and attach to the incomingServerChannel), does not have any of the Stx/Etx bytes.
 *
 * @author Christian Posta
 * @author Gunnar Hillert
 * @author Artem Bilan
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/integration/tcpServerConnectionDeserialize-context.xml")
@DirtiesContext
@SpringIntegrationTest(noAutoStartup = "outGateway")
public class TcpServerConnectionDeserializeTest {
    @Autowired
    SimpleGateway gw;

    @Autowired
    @Qualifier("incomingServerChannel")
    MessageChannel incomingServerChannel;

    @Autowired
    AbstractServerConnectionFactory crLfServer;

    @Autowired
    AbstractClientConnectionFactory client;

    @Autowired
    AbstractEndpoint outGateway;

    @Test
    public void testHappyPath() {
        // add a listener to this channel, otherwise there is not one defined
        // the reason we use a listener here is so we can assert truths on the
        // message and/or payload
        SubscribableChannel channel = ((SubscribableChannel) (incomingServerChannel));
        channel.subscribe(new AbstractReplyProducingMessageHandler() {
            @Override
            protected Object handleRequestMessage(Message<?> requestMessage) {
                byte[] payload = ((byte[]) (requestMessage.getPayload()));
                // we assert during the processing of the messaging that the
                // payload is just the content we wanted to send without the
                // framing bytes (STX/ETX)
                Assert.assertEquals("Hello World!", new String(payload));
                return requestMessage;
            }
        });
        String sourceMessage = wrapWithStxEtx("Hello World!");
        String result = gw.send(sourceMessage);
        System.out.println(result);
        Assert.assertEquals("Hello World!", result);
    }
}

