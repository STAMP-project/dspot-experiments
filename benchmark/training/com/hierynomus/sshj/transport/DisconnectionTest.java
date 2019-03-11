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
package com.hierynomus.sshj.transport;


import com.hierynomus.sshj.test.SshFixture;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class DisconnectionTest {
    private AtomicBoolean disconnected = null;

    @Rule
    public SshFixture fixture = new SshFixture();

    @Test
    public void listenerNotifiedOnClientDisconnect() throws IOException {
        fixture.getClient().disconnect();
        Assert.assertTrue(disconnected.get());
    }

    @Test
    public void listenerNotifiedOnServerDisconnect() throws IOException, InterruptedException {
        fixture.stopServer();
        joinToClientTransport(2);
        Assert.assertTrue(disconnected.get());
    }

    @Test
    public void joinNotifiedOnClientDisconnect() throws IOException {
        fixture.getClient().disconnect();
        Assert.assertTrue(joinToClientTransport(2));
    }

    @Test
    public void joinNotifiedOnServerDisconnect() throws InterruptedException, TransportException {
        fixture.stopServer();
        Assert.assertFalse(joinToClientTransport(2));
    }

    @Test
    public void shouldNotThrowTimeoutOnDisconnect() throws IOException {
        fixture.getClient().authPassword("u", "u");
        Session session = fixture.getClient().startSession();
        session.allocateDefaultPTY();
        Session.Shell shell = session.startShell();
        session.close();
        fixture.getClient().disconnect();
    }
}

