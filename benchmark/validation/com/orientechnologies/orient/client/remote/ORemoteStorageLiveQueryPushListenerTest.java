package com.orientechnologies.orient.client.remote;


import com.orientechnologies.orient.core.sql.query.OLiveResultListener;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Created by tglman on 26/10/15.
 */
public class ORemoteStorageLiveQueryPushListenerTest {
    @Mock
    private OStorageRemote storage;

    @Mock
    private ORemoteConnectionPool pool;

    @Mock
    private OLiveResultListener listener;

    @Test
    public void testErrorOnConectionClose() {
        OStorageRemoteAsynchEventListener storageListener = new OStorageRemoteAsynchEventListener(storage);
        storageListener.registerLiveListener(pool, 10, listener);
        storageListener.onEndUsedConnections(pool);
        Mockito.verify(listener, Mockito.only()).onError(10);
    }
}

