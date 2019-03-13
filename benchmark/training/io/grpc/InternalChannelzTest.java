/**
 * Copyright 2018 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc;


import io.grpc.InternalChannelz.ChannelStats;
import io.grpc.InternalChannelz.RootChannelList;
import io.grpc.InternalChannelz.ServerList;
import io.grpc.InternalChannelz.ServerSocketsList;
import io.grpc.InternalChannelz.ServerStats;
import io.grpc.InternalChannelz.SocketStats;
import io.grpc.InternalChannelz.Tls;
import io.grpc.internal.testing.TestUtils;
import java.security.cert.Certificate;
import javax.net.ssl.SSLSession;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public final class InternalChannelzTest {
    private final InternalChannelz channelz = new InternalChannelz();

    @Test
    public void getRootChannels_empty() {
        RootChannelList rootChannels = /* fromId= */
        /* maxPageSize= */
        channelz.getRootChannels(0, 1);
        TestCase.assertTrue(rootChannels.end);
        assertThat(rootChannels.channels).isEmpty();
    }

    @Test
    public void getRootChannels_onePage() {
        InternalInstrumented<ChannelStats> root1 = InternalChannelzTest.create();
        channelz.addRootChannel(root1);
        RootChannelList page = /* fromId= */
        /* maxPageSize= */
        channelz.getRootChannels(0, 1);
        TestCase.assertTrue(page.end);
        assertThat(page.channels).containsExactly(root1);
    }

    @Test
    public void getRootChannels_onePage_multi() {
        InternalInstrumented<ChannelStats> root1 = InternalChannelzTest.create();
        InternalInstrumented<ChannelStats> root2 = InternalChannelzTest.create();
        channelz.addRootChannel(root1);
        channelz.addRootChannel(root2);
        RootChannelList page = /* fromId= */
        /* maxPageSize= */
        channelz.getRootChannels(0, 2);
        TestCase.assertTrue(page.end);
        assertThat(page.channels).containsExactly(root1, root2);
    }

    @Test
    public void getRootChannels_paginate() {
        InternalInstrumented<ChannelStats> root1 = InternalChannelzTest.create();
        InternalInstrumented<ChannelStats> root2 = InternalChannelzTest.create();
        channelz.addRootChannel(root1);
        channelz.addRootChannel(root2);
        RootChannelList page1 = /* fromId= */
        /* maxPageSize= */
        channelz.getRootChannels(0, 1);
        Assert.assertFalse(page1.end);
        assertThat(page1.channels).containsExactly(root1);
        RootChannelList page2 = /* fromId= */
        /* maxPageSize= */
        channelz.getRootChannels(((InternalChannelz.id(root1)) + 1), 1);
        TestCase.assertTrue(page2.end);
        assertThat(page2.channels).containsExactly(root2);
    }

    @Test
    public void getRootChannels_remove() {
        InternalInstrumented<ChannelStats> root1 = InternalChannelzTest.create();
        channelz.addRootChannel(root1);
        channelz.removeRootChannel(root1);
        RootChannelList page = /* fromId= */
        /* maxPageSize= */
        channelz.getRootChannels(0, 1);
        TestCase.assertTrue(page.end);
        assertThat(page.channels).isEmpty();
    }

    @Test
    public void getRootChannels_addAfterLastPage() {
        InternalInstrumented<ChannelStats> root1 = InternalChannelzTest.create();
        {
            channelz.addRootChannel(root1);
            RootChannelList page1 = /* fromId= */
            /* maxPageSize= */
            channelz.getRootChannels(0, 1);
            TestCase.assertTrue(page1.end);
            assertThat(page1.channels).containsExactly(root1);
        }
        InternalInstrumented<ChannelStats> root2 = InternalChannelzTest.create();
        {
            channelz.addRootChannel(root2);
            RootChannelList page2 = /* fromId= */
            /* maxPageSize= */
            channelz.getRootChannels(((InternalChannelz.id(root1)) + 1), 1);
            TestCase.assertTrue(page2.end);
            assertThat(page2.channels).containsExactly(root2);
        }
    }

    @Test
    public void getServers_empty() {
        ServerList servers = /* fromId= */
        /* maxPageSize= */
        channelz.getServers(0, 1);
        TestCase.assertTrue(servers.end);
        assertThat(servers.servers).isEmpty();
    }

    @Test
    public void getServers_onePage() {
        InternalInstrumented<ServerStats> server1 = InternalChannelzTest.create();
        channelz.addServer(server1);
        ServerList page = /* fromId= */
        /* maxPageSize= */
        channelz.getServers(0, 1);
        TestCase.assertTrue(page.end);
        assertThat(page.servers).containsExactly(server1);
    }

    @Test
    public void getServers_onePage_multi() {
        InternalInstrumented<ServerStats> server1 = InternalChannelzTest.create();
        InternalInstrumented<ServerStats> server2 = InternalChannelzTest.create();
        channelz.addServer(server1);
        channelz.addServer(server2);
        ServerList page = /* fromId= */
        /* maxPageSize= */
        channelz.getServers(0, 2);
        TestCase.assertTrue(page.end);
        assertThat(page.servers).containsExactly(server1, server2);
    }

    @Test
    public void getServers_paginate() {
        InternalInstrumented<ServerStats> server1 = InternalChannelzTest.create();
        InternalInstrumented<ServerStats> server2 = InternalChannelzTest.create();
        channelz.addServer(server1);
        channelz.addServer(server2);
        ServerList page1 = /* fromId= */
        /* maxPageSize= */
        channelz.getServers(0, 1);
        Assert.assertFalse(page1.end);
        assertThat(page1.servers).containsExactly(server1);
        ServerList page2 = /* fromId= */
        /* maxPageSize= */
        channelz.getServers(((InternalChannelz.id(server1)) + 1), 1);
        TestCase.assertTrue(page2.end);
        assertThat(page2.servers).containsExactly(server2);
    }

    @Test
    public void getServers_remove() {
        InternalInstrumented<ServerStats> server1 = InternalChannelzTest.create();
        channelz.addServer(server1);
        channelz.removeServer(server1);
        ServerList page = /* fromId= */
        /* maxPageSize= */
        channelz.getServers(0, 1);
        TestCase.assertTrue(page.end);
        assertThat(page.servers).isEmpty();
    }

    @Test
    public void getServers_addAfterLastPage() {
        InternalInstrumented<ServerStats> server1 = InternalChannelzTest.create();
        {
            channelz.addServer(server1);
            ServerList page = /* fromId= */
            /* maxPageSize= */
            channelz.getServers(0, 1);
            TestCase.assertTrue(page.end);
            assertThat(page.servers).containsExactly(server1);
        }
        InternalInstrumented<ServerStats> server2 = InternalChannelzTest.create();
        {
            channelz.addServer(server2);
            ServerList page = /* fromId= */
            /* maxPageSize= */
            channelz.getServers(((InternalChannelz.id(server1)) + 1), 1);
            TestCase.assertTrue(page.end);
            assertThat(page.servers).containsExactly(server2);
        }
    }

    @Test
    public void getChannel() {
        InternalInstrumented<ChannelStats> root = InternalChannelzTest.create();
        Assert.assertNull(channelz.getChannel(InternalChannelz.id(root)));
        channelz.addRootChannel(root);
        Assert.assertSame(root, channelz.getChannel(InternalChannelz.id(root)));
        Assert.assertNull(channelz.getSubchannel(InternalChannelz.id(root)));
        channelz.removeRootChannel(root);
        Assert.assertNull(channelz.getRootChannel(InternalChannelz.id(root)));
    }

    @Test
    public void getSubchannel() {
        InternalInstrumented<ChannelStats> sub = InternalChannelzTest.create();
        Assert.assertNull(channelz.getSubchannel(InternalChannelz.id(sub)));
        channelz.addSubchannel(sub);
        Assert.assertSame(sub, channelz.getSubchannel(InternalChannelz.id(sub)));
        Assert.assertNull(channelz.getChannel(InternalChannelz.id(sub)));
        channelz.removeSubchannel(sub);
        Assert.assertNull(channelz.getSubchannel(InternalChannelz.id(sub)));
    }

    @Test
    public void getSocket() {
        InternalInstrumented<SocketStats> socket = InternalChannelzTest.create();
        Assert.assertNull(channelz.getSocket(InternalChannelz.id(socket)));
        channelz.addClientSocket(socket);
        Assert.assertSame(socket, channelz.getSocket(InternalChannelz.id(socket)));
        channelz.removeClientSocket(socket);
        Assert.assertNull(channelz.getSocket(InternalChannelz.id(socket)));
    }

    @Test
    public void serverSocket_noServer() {
        Assert.assertNull(/* serverId= */
        /* fromId= */
        /* maxPageSize= */
        channelz.getServerSockets(1, 0, 1));
    }

    @Test
    public void serverSocket() {
        InternalInstrumented<ServerStats> server = InternalChannelzTest.create();
        channelz.addServer(server);
        InternalInstrumented<SocketStats> socket = InternalChannelzTest.create();
        assertEmptyServerSocketsPage(InternalChannelz.id(server), InternalChannelz.id(socket));
        channelz.addServerSocket(server, socket);
        ServerSocketsList page = /* maxPageSize= */
        channelz.getServerSockets(InternalChannelz.id(server), InternalChannelz.id(socket), 1);
        Assert.assertNotNull(page);
        TestCase.assertTrue(page.end);
        assertThat(page.sockets).containsExactly(socket);
        channelz.removeServerSocket(server, socket);
        assertEmptyServerSocketsPage(InternalChannelz.id(server), InternalChannelz.id(socket));
    }

    @Test
    public void serverSocket_eachServerSeparate() {
        InternalInstrumented<ServerStats> server1 = InternalChannelzTest.create();
        InternalInstrumented<ServerStats> server2 = InternalChannelzTest.create();
        InternalInstrumented<SocketStats> socket1 = InternalChannelzTest.create();
        InternalInstrumented<SocketStats> socket2 = InternalChannelzTest.create();
        channelz.addServer(server1);
        channelz.addServer(server2);
        channelz.addServerSocket(server1, socket1);
        channelz.addServerSocket(server2, socket2);
        ServerSocketsList list1 = /* fromId= */
        /* maxPageSize= */
        channelz.getServerSockets(InternalChannelz.id(server1), 0, 2);
        Assert.assertNotNull(list1);
        TestCase.assertTrue(list1.end);
        assertThat(list1.sockets).containsExactly(socket1);
        ServerSocketsList list2 = /* fromId= */
        /* maxPageSize= */
        channelz.getServerSockets(InternalChannelz.id(server2), 0, 2);
        Assert.assertNotNull(list2);
        TestCase.assertTrue(list2.end);
        assertThat(list2.sockets).containsExactly(socket2);
    }

    @Test
    public void tlsSecurityInfo() throws Exception {
        Certificate local = TestUtils.loadX509Cert("client.pem");
        Certificate remote = TestUtils.loadX509Cert("server0.pem");
        final SSLSession session = Mockito.mock(SSLSession.class);
        Mockito.when(session.getCipherSuite()).thenReturn("TLS_NULL_WITH_NULL_NULL");
        Mockito.when(session.getLocalCertificates()).thenReturn(new Certificate[]{ local });
        Mockito.when(session.getPeerCertificates()).thenReturn(new Certificate[]{ remote });
        Tls tls = new Tls(session);
        Assert.assertEquals(local, tls.localCert);
        Assert.assertEquals(remote, tls.remoteCert);
        Assert.assertEquals("TLS_NULL_WITH_NULL_NULL", tls.cipherSuiteStandardName);
    }
}

