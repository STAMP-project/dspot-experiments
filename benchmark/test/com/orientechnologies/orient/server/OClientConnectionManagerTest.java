package com.orientechnologies.orient.server;


import com.orientechnologies.orient.core.metadata.security.OToken;
import com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


public class OClientConnectionManagerTest {
    @Mock
    private ONetworkProtocolBinary protocol;

    @Mock
    private OToken token;

    @Mock
    private OTokenHandler handler;

    @Mock
    private OServer server;

    @Test
    public void testSimpleConnectDisconnect() throws IOException {
        OClientConnectionManager manager = new OClientConnectionManager(server);
        OClientConnection ret = manager.connect(protocol);
        Assert.assertNotNull(ret);
        OClientConnection ret1 = manager.getConnection(ret.getId(), protocol);
        Assert.assertSame(ret, ret1);
        manager.disconnect(ret);
        OClientConnection ret2 = manager.getConnection(ret.getId(), protocol);
        Assert.assertNull(ret2);
    }

    @Test
    public void testTokenConnectDisconnect() throws IOException {
        byte[] atoken = new byte[]{  };
        OClientConnectionManager manager = new OClientConnectionManager(server);
        OClientConnection ret = manager.connect(protocol);
        manager.connect(protocol, ret, atoken, handler);
        Assert.assertNotNull(ret);
        OClientSessions sess = manager.getSession(ret);
        Assert.assertNotNull(sess);
        Assert.assertEquals(sess.getConnections().size(), 1);
        OClientConnection ret1 = manager.getConnection(ret.getId(), protocol);
        Assert.assertSame(ret, ret1);
        OClientConnection ret2 = manager.reConnect(protocol, atoken, token);
        Assert.assertNotSame(ret1, ret2);
        Assert.assertEquals(sess.getConnections().size(), 2);
        manager.disconnect(ret);
        Assert.assertEquals(sess.getConnections().size(), 1);
        OClientConnection ret3 = manager.getConnection(ret.getId(), protocol);
        Assert.assertNull(ret3);
        manager.disconnect(ret2);
        Assert.assertEquals(sess.getConnections().size(), 0);
        OClientConnection ret4 = manager.getConnection(ret2.getId(), protocol);
        Assert.assertNull(ret4);
    }
}

