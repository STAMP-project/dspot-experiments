/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.test.udt.nio;


import NioUdtProvider.BYTE_ACCEPTOR;
import NioUdtProvider.BYTE_CONNECTOR;
import NioUdtProvider.BYTE_RENDEZVOUS;
import NioUdtProvider.MESSAGE_ACCEPTOR;
import NioUdtProvider.MESSAGE_CONNECTOR;
import NioUdtProvider.MESSAGE_RENDEZVOUS;
import io.netty.channel.udt.UdtServerChannel;
import io.netty.channel.udt.nio.NioUdtByteAcceptorChannel;
import io.netty.channel.udt.nio.NioUdtByteConnectorChannel;
import io.netty.channel.udt.nio.NioUdtByteRendezvousChannel;
import io.netty.channel.udt.nio.NioUdtMessageAcceptorChannel;
import io.netty.channel.udt.nio.NioUdtMessageConnectorChannel;
import io.netty.channel.udt.nio.NioUdtMessageRendezvousChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import org.junit.Assert;
import org.junit.Test;


public class NioUdtProviderTest extends AbstractUdtTest {
    /**
     * verify factory
     */
    @Test
    public void provideFactory() {
        NioUdtByteAcceptorChannel nioUdtByteAcceptorChannel = ((NioUdtByteAcceptorChannel) (BYTE_ACCEPTOR.newChannel()));
        NioUdtByteConnectorChannel nioUdtByteConnectorChannel = ((NioUdtByteConnectorChannel) (BYTE_CONNECTOR.newChannel()));
        NioUdtByteRendezvousChannel nioUdtByteRendezvousChannel = ((NioUdtByteRendezvousChannel) (BYTE_RENDEZVOUS.newChannel()));
        NioUdtMessageAcceptorChannel nioUdtMessageAcceptorChannel = ((NioUdtMessageAcceptorChannel) (MESSAGE_ACCEPTOR.newChannel()));
        NioUdtMessageConnectorChannel nioUdtMessageConnectorChannel = ((NioUdtMessageConnectorChannel) (MESSAGE_CONNECTOR.newChannel()));
        NioUdtMessageRendezvousChannel nioUdtMessageRendezvousChannel = ((NioUdtMessageRendezvousChannel) (MESSAGE_RENDEZVOUS.newChannel()));
        // bytes
        Assert.assertNotNull(nioUdtByteAcceptorChannel);
        Assert.assertNotNull(nioUdtByteConnectorChannel);
        Assert.assertNotNull(nioUdtByteRendezvousChannel);
        // message
        Assert.assertNotNull(nioUdtMessageAcceptorChannel);
        Assert.assertNotNull(nioUdtMessageConnectorChannel);
        Assert.assertNotNull(nioUdtMessageRendezvousChannel);
        // channel
        Assert.assertNotNull(NioUdtProvider.channelUDT(nioUdtByteAcceptorChannel));
        Assert.assertNotNull(NioUdtProvider.channelUDT(nioUdtByteConnectorChannel));
        Assert.assertNotNull(NioUdtProvider.channelUDT(nioUdtByteRendezvousChannel));
        Assert.assertNotNull(NioUdtProvider.channelUDT(nioUdtMessageAcceptorChannel));
        Assert.assertNotNull(NioUdtProvider.channelUDT(nioUdtMessageConnectorChannel));
        Assert.assertNotNull(NioUdtProvider.channelUDT(nioUdtMessageRendezvousChannel));
        // acceptor types
        Assert.assertTrue(((BYTE_ACCEPTOR.newChannel()) instanceof UdtServerChannel));
        Assert.assertTrue(((MESSAGE_ACCEPTOR.newChannel()) instanceof UdtServerChannel));
    }
}

