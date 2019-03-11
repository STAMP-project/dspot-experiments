package com.orientechnologies.orient.server;


import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.query.live.OLiveQueryHook;
import com.orientechnologies.orient.core.query.live.OLiveQueryListener;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.enterprise.channel.binary.OChannelBinaryServer;
import com.orientechnologies.orient.server.network.protocol.binary.OLiveCommandResultListener;
import com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Created by tglman on 07/06/16.
 */
public class OLiveCommandResultListenerTest {
    @Mock
    private OServer server;

    @Mock
    private OChannelBinaryServer channelBinary;

    @Mock
    private OLiveQueryListener rawListener;

    private ONetworkProtocolBinary protocol;

    private OClientConnection connection;

    private ODatabaseDocumentInternal db;

    private static class TestResultListener implements OCommandResultListener {
        @Override
        public boolean result(Object iRecord) {
            return false;
        }

        @Override
        public void end() {
        }

        @Override
        public Object getResult() {
            return null;
        }
    }

    @Test
    public void testSimpleMessageSend() throws IOException {
        OLiveCommandResultListener listener = new OLiveCommandResultListener(server, connection, new OLiveCommandResultListenerTest.TestResultListener());
        ORecordOperation op = new ORecordOperation(new ODocument(), ORecordOperation.CREATED);
        listener.onLiveResult(10, op);
        Mockito.verify(channelBinary, Mockito.atLeastOnce()).writeBytes(Mockito.any(byte[].class));
    }

    @Test
    public void testNetworkError() throws IOException {
        Mockito.when(channelBinary.writeInt(Mockito.anyInt())).thenThrow(new IOException("Mock Exception"));
        OLiveCommandResultListener listener = new OLiveCommandResultListener(server, connection, new OLiveCommandResultListenerTest.TestResultListener());
        OLiveQueryHook.subscribe(10, rawListener, db);
        Assert.assertTrue(OLiveQueryHook.getOpsReference(db).getQueueThread().hasToken(10));
        ORecordOperation op = new ORecordOperation(new ODocument(), ORecordOperation.CREATED);
        listener.onLiveResult(10, op);
        Assert.assertFalse(OLiveQueryHook.getOpsReference(db).getQueueThread().hasToken(10));
    }
}

