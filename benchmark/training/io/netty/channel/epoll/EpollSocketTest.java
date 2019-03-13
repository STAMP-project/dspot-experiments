/**
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel.epoll;


import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.tests.SocketTest;
import io.netty.channel.unix.tests.UnixTestUtils;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class EpollSocketTest extends SocketTest<LinuxSocket> {
    @Test
    public void testTcpCork() throws Exception {
        Assert.assertFalse(socket.isTcpCork());
        socket.setTcpCork(true);
        Assert.assertTrue(socket.isTcpCork());
    }

    @Test
    public void testPeerCreds() throws IOException {
        LinuxSocket s1 = LinuxSocket.newSocketDomain();
        LinuxSocket s2 = LinuxSocket.newSocketDomain();
        try {
            DomainSocketAddress dsa = UnixTestUtils.newSocketAddress();
            s1.bind(dsa);
            s1.listen(1);
            Assert.assertTrue(s2.connect(dsa));
            byte[] addr = new byte[64];
            s1.accept(addr);
            PeerCredentials pc = s1.getPeerCredentials();
            Assert.assertNotEquals(pc.uid(), (-1));
        } finally {
            s1.close();
            s2.close();
        }
    }
}

