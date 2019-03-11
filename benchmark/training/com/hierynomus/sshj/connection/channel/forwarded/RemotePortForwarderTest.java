/**
 * Copyright (C)2009 - SSHJ Contributors
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
package com.hierynomus.sshj.connection.channel.forwarded;


import RemotePortForwarder.Forward;
import com.hierynomus.sshj.test.HttpServer;
import com.hierynomus.sshj.test.SshFixture;
import java.io.IOException;
import java.net.InetSocketAddress;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.forwarded.RemotePortForwarder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RemotePortForwarderTest {
    private static final Logger log = LoggerFactory.getLogger(RemotePortForwarderTest.class);

    private static final RemotePortForwarderTest.PortRange RANGE = new RemotePortForwarderTest.PortRange(9000, 9999);

    private static final InetSocketAddress HTTP_SERVER_SOCKET_ADDR = new InetSocketAddress("127.0.0.1", 8080);

    @Rule
    public SshFixture fixture = new SshFixture();

    @Rule
    public HttpServer httpServer = new HttpServer();

    @Test
    public void shouldHaveWorkingHttpServer() throws IOException {
        // Just to check that we have a working http server...
        Assert.assertThat(httpGet("127.0.0.1", 8080), CoreMatchers.equalTo(200));
    }

    @Test
    public void shouldDynamicallyForwardPortForLocalhost() throws IOException {
        SSHClient sshClient = getFixtureClient();
        RemotePortForwarder.Forward bind = forwardPort(sshClient, "127.0.0.1", new RemotePortForwarderTest.SinglePort(0));
        Assert.assertThat(httpGet("127.0.0.1", bind.getPort()), CoreMatchers.equalTo(200));
    }

    @Test
    public void shouldDynamicallyForwardPortForAllIPv4() throws IOException {
        SSHClient sshClient = getFixtureClient();
        RemotePortForwarder.Forward bind = forwardPort(sshClient, "0.0.0.0", new RemotePortForwarderTest.SinglePort(0));
        Assert.assertThat(httpGet("127.0.0.1", bind.getPort()), CoreMatchers.equalTo(200));
    }

    @Test
    public void shouldDynamicallyForwardPortForAllProtocols() throws IOException {
        SSHClient sshClient = getFixtureClient();
        RemotePortForwarder.Forward bind = forwardPort(sshClient, "", new RemotePortForwarderTest.SinglePort(0));
        Assert.assertThat(httpGet("127.0.0.1", bind.getPort()), CoreMatchers.equalTo(200));
    }

    @Test
    public void shouldForwardPortForLocalhost() throws IOException {
        SSHClient sshClient = getFixtureClient();
        RemotePortForwarder.Forward bind = forwardPort(sshClient, "127.0.0.1", RemotePortForwarderTest.RANGE);
        Assert.assertThat(httpGet("127.0.0.1", bind.getPort()), CoreMatchers.equalTo(200));
    }

    @Test
    public void shouldForwardPortForAllIPv4() throws IOException {
        SSHClient sshClient = getFixtureClient();
        RemotePortForwarder.Forward bind = forwardPort(sshClient, "0.0.0.0", RemotePortForwarderTest.RANGE);
        Assert.assertThat(httpGet("127.0.0.1", bind.getPort()), CoreMatchers.equalTo(200));
    }

    @Test
    public void shouldForwardPortForAllProtocols() throws IOException {
        SSHClient sshClient = getFixtureClient();
        RemotePortForwarder.Forward bind = forwardPort(sshClient, "", RemotePortForwarderTest.RANGE);
        Assert.assertThat(httpGet("127.0.0.1", bind.getPort()), CoreMatchers.equalTo(200));
    }

    private static class PortRange {
        private int upper;

        private int current;

        public PortRange(int lower, int upper) {
            this.upper = upper;
            this.current = lower;
        }

        public int nextPort() {
            if ((current) < (upper)) {
                return (current)++;
            }
            throw new IllegalStateException("Out of ports!");
        }

        public boolean hasNext() {
            return (current) < (upper);
        }
    }

    private static class SinglePort extends RemotePortForwarderTest.PortRange {
        private final int port;

        public SinglePort(int port) {
            super(port, port);
            this.port = port;
        }

        @Override
        public int nextPort() {
            return port;
        }
    }
}

