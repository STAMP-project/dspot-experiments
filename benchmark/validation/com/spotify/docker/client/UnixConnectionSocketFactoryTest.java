/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
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


import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.channels.SocketChannel;
import javax.net.ssl.SSLSocket;
import jnr.unixsocket.UnixSocket;
import org.apache.http.protocol.HttpContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class UnixConnectionSocketFactoryTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private UnixConnectionSocketFactory sut;

    @Test
    public void testSanitizeUri() throws Exception {
        final URI unixUri = UnixConnectionSocketFactory.sanitizeUri(URI.create("unix://localhost"));
        MatcherAssert.assertThat(unixUri, Matchers.equalTo(URI.create("unix://localhost:80")));
        final URI nonUnixUri = URI.create("http://127.0.0.1");
        final URI uri = UnixConnectionSocketFactory.sanitizeUri(nonUnixUri);
        MatcherAssert.assertThat(uri, Matchers.equalTo(nonUnixUri));
    }

    @Test
    public void testConnectSocket() throws Exception {
        final UnixSocket unixSocket = Mockito.mock(UnixSocket.class);
        Mockito.when(unixSocket.getChannel()).thenReturn(Mockito.mock(SocketChannel.class));
        final Socket socket = sut.connectSocket(10, unixSocket, create("http://foo.com"), Mockito.mock(InetSocketAddress.class), Mockito.mock(InetSocketAddress.class), Mockito.mock(HttpContext.class));
        Mockito.verify(unixSocket).setSoTimeout(10);
        MatcherAssert.assertThat(socket, IsInstanceOf.instanceOf(UnixSocket.class));
        MatcherAssert.assertThat(((UnixSocket) (socket)), Matchers.equalTo(unixSocket));
    }

    @Test(expected = AssertionError.class)
    public void testConnectSocketNotUnixSocket() throws Exception {
        sut.connectSocket(10, Mockito.mock(SSLSocket.class), create("http://foo.com"), Mockito.mock(InetSocketAddress.class), Mockito.mock(InetSocketAddress.class), Mockito.mock(HttpContext.class));
    }
}

