package com.orientechnologies.orient.server.distributed;


import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.server.OServer;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;


public class SimpleLiveQueryDistributedIT {
    private OServer server0;

    private OServer server1;

    @Test
    public void testLiveQueryDifferentNode() throws InterruptedException {
        OrientDB remote1 = new OrientDB("remote:localhost", "root", "test", OrientDBConfig.defaultConfig());
        ODatabaseSession session = remote1.open(SimpleLiveQueryDistributedIT.class.getSimpleName(), "admin", "admin");
        SimpleLiveQueryDistributedIT.EventListener listener = new SimpleLiveQueryDistributedIT.EventListener();
        OLiveQueryMonitor monitor = session.live("select from test", listener);
        OrientDB remote2 = new OrientDB("remote:localhost:2425", "root", "test", OrientDBConfig.defaultConfig());
        ODatabaseSession session2 = remote1.open(SimpleLiveQueryDistributedIT.class.getSimpleName(), "admin", "admin");
        OElement el = session2.save(session2.newElement("test"));
        el.setProperty("name", "name");
        session2.save(el);
        session2.delete(el);
        session2.close();
        session.activateOnCurrentThread();
        monitor.unSubscribe();
        session.close();
        listener.latch.await();
        Assert.assertEquals(1, listener.create);
        Assert.assertEquals(1, listener.delete);
        Assert.assertEquals(1, listener.update);
        remote1.close();
        remote2.close();
    }

    private static class EventListener implements OLiveQueryResultListener {
        public int create = 0;

        public int update = 0;

        public int delete = 0;

        public CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void onCreate(ODatabaseDocument database, OResult data) {
            (create)++;
        }

        @Override
        public void onUpdate(ODatabaseDocument database, OResult before, OResult after) {
            (update)++;
        }

        @Override
        public void onDelete(ODatabaseDocument database, OResult data) {
            (delete)++;
        }

        @Override
        public void onError(ODatabaseDocument database, OException exception) {
        }

        @Override
        public void onEnd(ODatabaseDocument database) {
            latch.countDown();
        }
    }
}

