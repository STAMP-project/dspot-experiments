package com.orientechnologies.orient.server.network;


import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.sql.query.OLiveQuery;
import com.orientechnologies.orient.core.sql.query.OLiveResultListener;
import com.orientechnologies.orient.server.OServer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class OLiveQueryShotdownTest {
    private OServer server;

    @Test
    public void testShutDown() throws Exception {
        bootServer();
        ODatabaseDocument db = new ODatabaseDocumentTx(("remote:localhost/" + (OLiveQueryShotdownTest.class.getSimpleName())));
        db.open("admin", "admin");
        db.getMetadata().getSchema().createClass("Test");
        final CountDownLatch error = new CountDownLatch(1);
        try {
            db.command(new OLiveQuery("live select from Test", new OLiveResultListener() {
                @Override
                public void onUnsubscribe(int iLiveToken) {
                }

                @Override
                public void onLiveResult(int iLiveToken, ORecordOperation iOp) throws OException {
                }

                @Override
                public void onError(int iLiveToken) {
                    error.countDown();
                }
            })).execute();
            shutdownServer();
            Assert.assertTrue("onError method never called on shutdow", error.await(2, TimeUnit.SECONDS));
        } finally {
            // db.close();
        }
    }
}

