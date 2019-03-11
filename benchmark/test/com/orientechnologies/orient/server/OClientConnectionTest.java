package com.orientechnologies.orient.server;


import OGlobalConfiguration.NETWORK_TOKEN_EXPIRE_TIMEOUT;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.enterprise.channel.binary.OTokenSecurityException;
import com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Created by tglman on 27/12/15.
 */
public class OClientConnectionTest {
    private ODatabaseDocumentInternal db;

    @Mock
    private ONetworkProtocolBinary protocol;

    @Mock
    private ONetworkProtocolBinary protocol1;

    @Mock
    private OClientConnectionManager manager;

    @Mock
    private OServer server;

    @Test
    public void testValidToken() throws IOException {
        OClientConnection conn = new OClientConnection(1, protocol);
        OTokenHandler handler = new com.orientechnologies.orient.server.token.OTokenHandlerImpl(server);
        byte[] tokenBytes = handler.getSignedBinaryToken(db, db.getUser(), conn.getData());
        conn.validateSession(tokenBytes, handler, null);
        Assert.assertTrue(conn.getTokenBased());
        Assert.assertEquals(tokenBytes, conn.getTokenBytes());
        Assert.assertNotNull(conn.getToken());
    }

    @Test(expected = OTokenSecurityException.class)
    public void testExpiredToken() throws IOException, InterruptedException {
        OClientConnection conn = new OClientConnection(1, protocol);
        long sessionTimeout = NETWORK_TOKEN_EXPIRE_TIMEOUT.getValueAsLong();
        NETWORK_TOKEN_EXPIRE_TIMEOUT.setValue(0);
        OTokenHandler handler = new com.orientechnologies.orient.server.token.OTokenHandlerImpl(server);
        NETWORK_TOKEN_EXPIRE_TIMEOUT.setValue(sessionTimeout);
        byte[] tokenBytes = handler.getSignedBinaryToken(db, db.getUser(), conn.getData());
        Thread.sleep(1);
        conn.validateSession(tokenBytes, handler, protocol);
    }

    @Test(expected = OTokenSecurityException.class)
    public void testWrongToken() throws IOException {
        OClientConnection conn = new OClientConnection(1, protocol);
        OTokenHandler handler = new com.orientechnologies.orient.server.token.OTokenHandlerImpl(server);
        byte[] tokenBytes = new byte[120];
        conn.validateSession(tokenBytes, handler, protocol);
    }

    @Test
    public void testAlreadyAuthenticatedOnConnection() throws IOException {
        OClientConnection conn = new OClientConnection(1, protocol);
        OTokenHandler handler = new com.orientechnologies.orient.server.token.OTokenHandlerImpl(server);
        byte[] tokenBytes = handler.getSignedBinaryToken(db, db.getUser(), conn.getData());
        conn.validateSession(tokenBytes, handler, protocol);
        Assert.assertTrue(conn.getTokenBased());
        Assert.assertEquals(tokenBytes, conn.getTokenBytes());
        Assert.assertNotNull(conn.getToken());
        // second validation don't need token
        conn.validateSession(null, handler, protocol);
        Assert.assertTrue(conn.getTokenBased());
        Assert.assertEquals(tokenBytes, conn.getTokenBytes());
        Assert.assertNotNull(conn.getToken());
    }

    @Test(expected = OTokenSecurityException.class)
    public void testNotAlreadyAuthenticated() throws IOException {
        OClientConnection conn = new OClientConnection(1, protocol);
        OTokenHandler handler = new com.orientechnologies.orient.server.token.OTokenHandlerImpl(server);
        // second validation don't need token
        conn.validateSession(null, handler, protocol1);
    }

    @Test(expected = OTokenSecurityException.class)
    public void testAlreadyAuthenticatedButNotOnSpecificConnection() throws IOException {
        OClientConnection conn = new OClientConnection(1, protocol);
        OTokenHandler handler = new com.orientechnologies.orient.server.token.OTokenHandlerImpl(server);
        byte[] tokenBytes = handler.getSignedBinaryToken(db, db.getUser(), conn.getData());
        conn.validateSession(tokenBytes, handler, protocol);
        Assert.assertTrue(conn.getTokenBased());
        Assert.assertEquals(tokenBytes, conn.getTokenBytes());
        Assert.assertNotNull(conn.getToken());
        // second validation don't need token
        ONetworkProtocolBinary otherConn = Mockito.mock(ONetworkProtocolBinary.class);
        conn.validateSession(null, handler, otherConn);
    }
}

