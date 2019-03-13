package com.orientechnologies.orient.client.remote;


import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.client.remote.message.OLiveQueryPushRequest;
import com.orientechnologies.orient.client.remote.message.live.OLiveQueryResult;
import com.orientechnologies.orient.core.db.OLiveQueryResultListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultInternal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 * Created by tglman on 17/05/17.
 */
public class ORemoteLiveQueryPushTest {
    private static class MockLiveListener implements OLiveQueryResultListener {
        public int countCreate = 0;

        public int countUpdate = 0;

        public int countDelete = 0;

        public boolean end;

        @Override
        public void onCreate(ODatabaseDocument database, OResult data) {
            (countCreate)++;
        }

        @Override
        public void onUpdate(ODatabaseDocument database, OResult before, OResult after) {
            (countUpdate)++;
        }

        @Override
        public void onDelete(ODatabaseDocument database, OResult data) {
            (countDelete)++;
        }

        @Override
        public void onError(ODatabaseDocument database, OException exception) {
        }

        @Override
        public void onEnd(ODatabaseDocument database) {
            Assert.assertFalse(end);
            end = true;
        }
    }

    private OStorageRemote storage;

    @Mock
    private ORemoteConnectionManager connectionManager;

    @Mock
    private ODatabaseDocument database;

    @Test
    public void testLiveEvents() {
        ORemoteLiveQueryPushTest.MockLiveListener mock = new ORemoteLiveQueryPushTest.MockLiveListener();
        storage.registerLiveListener(10, new OLiveQueryClientListener(database, mock));
        List<OLiveQueryResult> events = new ArrayList<>();
        events.add(new OLiveQueryResult(OLiveQueryResult.CREATE_EVENT, new OResultInternal(), null));
        events.add(new OLiveQueryResult(OLiveQueryResult.UPDATE_EVENT, new OResultInternal(), new OResultInternal()));
        events.add(new OLiveQueryResult(OLiveQueryResult.DELETE_EVENT, new OResultInternal(), null));
        OLiveQueryPushRequest request = new OLiveQueryPushRequest(10, OLiveQueryPushRequest.END, events);
        request.execute(storage);
        Assert.assertEquals(mock.countCreate, 1);
        Assert.assertEquals(mock.countUpdate, 1);
        Assert.assertEquals(mock.countDelete, 1);
    }
}

