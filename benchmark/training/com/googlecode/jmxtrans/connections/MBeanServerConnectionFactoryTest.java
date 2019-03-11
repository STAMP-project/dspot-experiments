/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.connections;


import java.io.IOException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import org.junit.Test;
import org.mockito.Mockito;


public class MBeanServerConnectionFactoryTest {
    private MBeanServerConnectionFactory factory = new MBeanServerConnectionFactory();

    @Test
    public void connectionIsCreatedForRemoteServer() throws IOException {
        JmxConnectionProvider server = Mockito.mock(JmxConnectionProvider.class);
        JMXConnector jmxConnector = Mockito.mock(JMXConnector.class);
        MBeanServerConnection mBeanServerConnection = Mockito.mock(MBeanServerConnection.class);
        Mockito.when(server.isLocal()).thenReturn(false);
        Mockito.when(server.getServerConnection()).thenReturn(jmxConnector);
        Mockito.when(jmxConnector.getMBeanServerConnection()).thenReturn(mBeanServerConnection);
        JMXConnection jmxConnection = factory.makeObject(server);
        assertThat(jmxConnection.getMBeanServerConnection()).isSameAs(mBeanServerConnection);
    }

    @Test
    public void connectionIsCreatedForLocalServer() throws IOException {
        JmxConnectionProvider server = Mockito.mock(JmxConnectionProvider.class);
        MBeanServer mBeanServerConnection = Mockito.mock(MBeanServer.class);
        Mockito.when(server.isLocal()).thenReturn(true);
        Mockito.when(server.getLocalMBeanServer()).thenReturn(mBeanServerConnection);
        JMXConnection jmxConnection = factory.makeObject(server);
        assertThat(jmxConnection.getMBeanServerConnection()).isSameAs(mBeanServerConnection);
    }

    @Test
    public void connectionIsClosedOnDestroy() throws IOException {
        JmxConnectionProvider server = Mockito.mock(JmxConnectionProvider.class);
        JMXConnection connection = Mockito.mock(JMXConnection.class);
        Mockito.when(connection.isAlive()).thenReturn(true);
        factory.destroyObject(server, connection);
        Mockito.verify(connection).setMarkedAsDestroyed();
        Mockito.verify(connection, Mockito.timeout(2000)).close();
    }
}

