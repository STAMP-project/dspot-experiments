/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2018 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.docker.client;


import com.spotify.docker.client.npipe.NamedPipeSocket;
import com.spotify.docker.client.npipe.NpipeConnectionSocketFactory;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.channels.SocketChannel;
import javax.net.ssl.SSLSocket;
import org.apache.http.protocol.HttpContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class NpipeConnectionSocketFactoryTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private NpipeConnectionSocketFactory sut;

    @Test
    public void testSanitizeUri() throws Exception {
        final URI npipeUri = NpipeConnectionSocketFactory.sanitizeUri(URI.create("npipe://localhost"));
        MatcherAssert.assertThat(npipeUri, Matchers.equalTo(URI.create("npipe://localhost:80")));
        final URI nonNpipeUri = URI.create("http://127.0.0.1");
        final URI uri = NpipeConnectionSocketFactory.sanitizeUri(nonNpipeUri);
        MatcherAssert.assertThat(uri, Matchers.equalTo(nonNpipeUri));
    }

    @Test
    public void testConnectSocket() throws Exception {
        final NamedPipeSocket npipeSocket = Mockito.mock(NamedPipeSocket.class);
        Mockito.when(npipeSocket.getChannel()).thenReturn(Mockito.mock(SocketChannel.class));
        final Socket socket = sut.connectSocket(10, npipeSocket, create("http://foo.com"), Mockito.mock(InetSocketAddress.class), Mockito.mock(InetSocketAddress.class), Mockito.mock(HttpContext.class));
        MatcherAssert.assertThat(socket, IsInstanceOf.instanceOf(NamedPipeSocket.class));
        MatcherAssert.assertThat(((NamedPipeSocket) (socket)), Matchers.equalTo(npipeSocket));
    }

    @Test(expected = AssertionError.class)
    public void testConnectSocketNotUnixSocket() throws Exception {
        sut.connectSocket(10, Mockito.mock(SSLSocket.class), create("http://foo.com"), Mockito.mock(InetSocketAddress.class), Mockito.mock(InetSocketAddress.class), Mockito.mock(HttpContext.class));
    }
}

