/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.clustering.jgroups;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import org.jboss.as.network.SocketBindingManager;
import org.jgroups.util.SocketFactory;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Paul Ferraro
 */
public class ManagedSocketFactoryTest {
    private SocketBindingManager manager = Mockito.mock(SocketBindingManager.class);

    private SocketFactory subject = new org.jboss.as.network.org.jboss.as.clustering.jgroups.ManagedSocketFactory(this.manager, Collections.singletonMap("known-service", new org.jboss.as.network.SocketBinding("binding", 0, false, null, 0, null, this.manager, Collections.emptyList())));

    @Test
    public void createSocket() throws IOException {
        this.createSocket("known-service", "binding");
        this.createSocket("unknown-service", "unknown-service");
    }

    @Test
    public void createServerSocket() throws IOException {
        this.createServerSocket("known-service", "binding");
        this.createServerSocket("unknown-service", "unknown-service");
    }

    @Test
    public void createDatagramSocket() throws IOException {
        this.createDatagramSocket("known-service", "binding");
        this.createDatagramSocket("unknown-service", "unknown-service");
    }

    @Test
    public void createMulticastSocket() throws IOException {
        this.createMulticastSocket("known-service", "binding");
        this.createMulticastSocket("unknown-service", "unknown-service");
    }

    @Test
    public void closeSocket() throws IOException {
        Socket socket = Mockito.mock(Socket.class);
        this.subject.close(socket);
        Mockito.verify(socket).close();
    }

    @Test
    public void closeServerSocket() throws IOException {
        ServerSocket socket = Mockito.mock(ServerSocket.class);
        this.subject.close(socket);
        Mockito.verify(socket).close();
    }

    @Test
    public void closeDatagramSocket() {
        DatagramSocket socket = Mockito.mock(DatagramSocket.class);
        this.subject.close(socket);
        Mockito.verify(socket).close();
    }

    @Test
    public void closeMulticastSocket() {
        MulticastSocket socket = Mockito.mock(MulticastSocket.class);
        this.subject.close(socket);
        Mockito.verify(socket).close();
    }
}

